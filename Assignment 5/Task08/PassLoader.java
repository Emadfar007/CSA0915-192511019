import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public final class PassLoader {
    private static final String INSERT_SQL = "INSERT INTO bus_pass "
            + "(pass_id, register_number, passenger_name, route_number, boarding_stop, validity_date, amount_paid) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private PassLoader() { }

    public static void main(String[] args) throws Exception {
        String url = env("A5_DB_URL", "jdbc:mysql://127.0.0.1:3307/assignment5_test?rewriteBatchedStatements=true");
        String user = env("A5_DB_USER", "assignment5");
        String password = env("A5_DB_PASSWORD", "Assignment5Test");

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            createTable(connection);
            DatabaseMetaData database = connection.getMetaData();
            System.out.println("database=" + database.getDatabaseProductName() + " " + database.getDatabaseProductVersion());
            runTenRowVerification(connection);

            System.out.println("PERFORMANCE_RESULTS");
            System.out.println("rows,statement_ms,prepared_ms,batch_1000_ms,verified_rows");
            for (int size : new int[] {1_000, 10_000, 100_000}) {
                double statement = timedStrategy(connection, size, Strategy.STATEMENT);
                double prepared = timedStrategy(connection, size, Strategy.PREPARED);
                double batch = timedStrategy(connection, size, Strategy.BATCH);
                long rows = countRows(connection);
                System.out.printf("%d,%.3f,%.3f,%.3f,%d%n", size, statement, prepared, batch, rows);
            }

            System.out.println("AUTOCOMMIT_COMPARISON");
            System.out.println("rows,auto_commit_ms,single_transaction_ms");
            int comparisonSize = 10_000;
            double autoCommit = timedAutoCommit(connection, comparisonSize, true);
            double oneTransaction = timedAutoCommit(connection, comparisonSize, false);
            System.out.printf("%d,%.3f,%.3f%n", comparisonSize, autoCommit, oneTransaction);

            System.out.println("ROLLBACK_TEST");
            runRollbackTest(connection, 10_000);
        }
    }

    private static void runTenRowVerification(Connection connection) throws SQLException {
        System.out.println("TEN_ROW_VERIFICATION");
        for (Strategy strategy : Strategy.values()) {
            double millis = timedStrategy(connection, 10, strategy);
            System.out.printf("strategy=%s,rows=%d,time_ms=%.3f%n", strategy, countRows(connection), millis);
        }
        try (Statement statement = connection.createStatement();
             ResultSet rows = statement.executeQuery("SELECT * FROM bus_pass ORDER BY pass_id LIMIT 2")) {
            ResultSetMetaData metadata = rows.getMetaData();
            for (int column = 1; column <= metadata.getColumnCount(); column++) {
                System.out.printf("column=%s,type=%s,java=%s%n", metadata.getColumnName(column),
                        metadata.getColumnTypeName(column), metadata.getColumnClassName(column));
            }
            while (rows.next()) {
                System.out.printf("sample=%s,%s,%s%n", rows.getString("pass_id"),
                        rows.getString("register_number"), rows.getBigDecimal("amount_paid"));
            }
        }
    }

    private static double timedStrategy(Connection connection, int count, Strategy strategy) throws SQLException {
        truncate(connection);
        connection.setAutoCommit(false);
        long start = System.nanoTime();
        try {
            switch (strategy) {
                case STATEMENT -> insertWithStatement(connection, count);
                case PREPARED -> insertWithPreparedStatement(connection, count);
                case BATCH -> insertWithBatch(connection, count, 1_000);
            }
            connection.commit();
            return (System.nanoTime() - start) / 1_000_000.0;
        } catch (SQLException error) {
            connection.rollback();
            throw error;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private static void insertWithStatement(Connection connection, int count) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            for (int index = 0; index < count; index++) {
                PassData pass = passAt(index);
                String sql = "INSERT INTO bus_pass "
                        + "(pass_id, register_number, passenger_name, route_number, boarding_stop, validity_date, amount_paid) VALUES ('"
                        + pass.passId + "','" + pass.registerNumber + "','" + pass.passengerName + "','"
                        + pass.routeNumber + "','" + pass.boardingStop + "','" + pass.validityDate + "',"
                        + pass.amount.toPlainString() + ")";
                statement.executeUpdate(sql);
            }
        }
    }

    private static void insertWithPreparedStatement(Connection connection, int count) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            for (int index = 0; index < count; index++) {
                bind(statement, passAt(index));
                statement.executeUpdate();
            }
        }
    }

    private static void insertWithBatch(Connection connection, int count, int batchSize) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            int pending = 0;
            for (int index = 0; index < count; index++) {
                bind(statement, passAt(index));
                statement.addBatch();
                pending++;
                if (pending == batchSize) {
                    statement.executeBatch();
                    statement.clearBatch();
                    pending = 0;
                }
            }
            if (pending > 0) {
                statement.executeBatch();
                statement.clearBatch();
            }
        }
    }

    private static double timedAutoCommit(Connection connection, int count, boolean autoCommit) throws SQLException {
        truncate(connection);
        connection.setAutoCommit(autoCommit);
        long start = System.nanoTime();
        try {
            insertWithPreparedStatement(connection, count);
            if (!autoCommit) connection.commit();
            return (System.nanoTime() - start) / 1_000_000.0;
        } catch (SQLException error) {
            if (!autoCommit) connection.rollback();
            throw error;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private static void runRollbackTest(Connection connection, int count) throws SQLException {
        truncate(connection);
        connection.setAutoCommit(false);
        boolean failed = false;
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            for (int index = 0; index < count; index++) {
                PassData pass = passAt(index);
                if (index == count / 2) {
                    pass = new PassData(pass.passId, pass.registerNumber, pass.passengerName,
                            pass.routeNumber, pass.boardingStop, pass.validityDate, new BigDecimal("-1.00"));
                }
                bind(statement, pass);
                statement.addBatch();
                if ((index + 1) % 1_000 == 0) statement.executeBatch();
            }
            connection.commit();
        } catch (SQLException expected) {
            failed = true;
            connection.rollback();
            System.out.println("forced_failure=" + expected.getSQLState());
        } finally {
            connection.setAutoCommit(true);
        }
        long rows = countRows(connection);
        System.out.println("failure_observed=" + failed);
        System.out.println("rows_after_rollback=" + rows);
        System.out.println("rollback_test=" + (failed && rows == 0 ? "PASS" : "FAIL"));
    }

    private static void bind(PreparedStatement statement, PassData pass) throws SQLException {
        statement.setString(1, pass.passId);
        statement.setString(2, pass.registerNumber);
        statement.setString(3, pass.passengerName);
        statement.setString(4, pass.routeNumber);
        statement.setString(5, pass.boardingStop);
        statement.setObject(6, pass.validityDate);
        statement.setBigDecimal(7, pass.amount);
    }

    private static PassData passAt(int index) {
        return new PassData("P" + String.format("%09d", index),
                String.format("%09d", 100_000_000 + index), "Student " + index,
                "R" + (index % 30), "Stop " + (index % 100), LocalDate.of(2027, 5, 31),
                new BigDecimal("1250.00"));
    }

    private static void createTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS bus_pass ("
                    + "pass_id VARCHAR(16) PRIMARY KEY, register_number CHAR(9) NOT NULL, "
                    + "passenger_name VARCHAR(100) NOT NULL, route_number VARCHAR(12) NOT NULL, "
                    + "boarding_stop VARCHAR(120) NOT NULL, validity_date DATE NOT NULL, "
                    + "amount_paid DECIMAL(10,2) NOT NULL CHECK (amount_paid >= 0), "
                    + "INDEX idx_bus_pass_route(route_number), INDEX idx_bus_pass_register(register_number)) ENGINE=InnoDB");
        }
    }

    private static void truncate(Connection connection) throws SQLException {
        boolean original = connection.getAutoCommit();
        if (!original) connection.setAutoCommit(true);
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("TRUNCATE TABLE bus_pass");
        }
        if (!original) connection.setAutoCommit(false);
    }

    private static long countRows(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM bus_pass")) {
            result.next();
            return result.getLong(1);
        }
    }

    private static String env(String name, String fallback) {
        return System.getenv().getOrDefault(name, fallback);
    }

    private enum Strategy { STATEMENT, PREPARED, BATCH }
    private record PassData(String passId, String registerNumber, String passengerName,
            String routeNumber, String boardingStop, LocalDate validityDate, BigDecimal amount) { }
}
