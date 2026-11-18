import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public final class MigrationProgram {
    private static final String INSERT_SQL = "INSERT INTO bus_pass_migrated "
            + "(pass_id, register_number, passenger_name, route_number, boarding_stop, validity_date, amount_paid) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private MigrationProgram() { }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: MigrationProgram <pass-file> <rejection-report>");
            System.exit(2);
        }
        Path input = Path.of(args[0]);
        Path rejected = Path.of(args[1]);
        String url = env("A5_DB_URL", "jdbc:mysql://127.0.0.1:3307/assignment5_test?rewriteBatchedStatements=true");
        String user = env("A5_DB_USER", "assignment5");
        String password = env("A5_DB_PASSWORD", "Assignment5Test");

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            createTable(connection);
            truncate(connection);
            long start = System.nanoTime();
            MigrationResult result = migrate(connection, input, rejected, 1_000);
            double millis = (System.nanoTime() - start) / 1_000_000.0;
            printMetadata(connection);
            System.out.println("input_records=" + result.inputRecords);
            System.out.println("loaded_records=" + result.loadedRecords);
            System.out.println("rejected_records=" + result.rejectedRecords);
            System.out.printf("migration_ms=%.3f%n", millis);
            System.out.println("database_rows=" + countRows(connection));
            System.out.println("rejection_report=" + rejected.toAbsolutePath());
            System.out.println("migration=" + (result.loadedRecords == countRows(connection) ? "PASS" : "FAIL"));
        }
    }

    static MigrationResult migrate(Connection connection, Path input, Path rejected, int batchSize) throws Exception {
        Path parent = rejected.toAbsolutePath().getParent();
        if (parent != null) Files.createDirectories(parent);
        connection.setAutoCommit(false);
        long inputRecords = 0;
        long loadedRecords = 0;
        long rejectedRecords = 0;
        int pending = 0;
        try (BufferedReader reader = Files.newBufferedReader(input, StandardCharsets.UTF_8);
             BufferedWriter report = Files.newBufferedWriter(rejected, StandardCharsets.UTF_8);
             PreparedStatement insert = connection.prepareStatement(INSERT_SQL)) {
            report.write("line_number,reason,raw_record");
            report.newLine();
            String line;
            long lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                inputRecords++;
                try {
                    ValidPass pass = parse(line);
                    bind(insert, pass);
                    insert.addBatch();
                    pending++;
                    loadedRecords++;
                    if (pending == batchSize) {
                        insert.executeBatch();
                        insert.clearBatch();
                        pending = 0;
                    }
                } catch (IllegalArgumentException error) {
                    rejectedRecords++;
                    report.write(lineNumber + ",\"" + csv(error.getMessage()) + "\",\"" + csv(line) + "\"");
                    report.newLine();
                }
            }
            if (pending > 0) insert.executeBatch();
            connection.commit();
            return new MigrationResult(inputRecords, loadedRecords, rejectedRecords);
        } catch (Exception error) {
            connection.rollback();
            throw error;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private static ValidPass parse(String line) {
        String[] fields = line.split("\\|", -1);
        if (fields.length != 7) throw new IllegalArgumentException("expected 7 fields, found " + fields.length);
        if (!fields[0].matches("[PM]\\w{4,}")) throw new IllegalArgumentException("invalid pass ID");
        if (!fields[1].matches("\\d{9}")) throw new IllegalArgumentException("invalid register number");
        if (fields[2].isBlank() || fields[3].isBlank() || fields[4].isBlank())
            throw new IllegalArgumentException("blank required text");
        LocalDate date;
        BigDecimal amount;
        try { date = LocalDate.parse(fields[5]); }
        catch (RuntimeException error) { throw new IllegalArgumentException("invalid ISO date"); }
        try { amount = new BigDecimal(fields[6]); }
        catch (NumberFormatException error) { throw new IllegalArgumentException("invalid amount"); }
        if (amount.signum() < 0) throw new IllegalArgumentException("negative amount");
        return new ValidPass(fields[0], fields[1], fields[2], fields[3], fields[4], date, amount);
    }

    private static void bind(PreparedStatement statement, ValidPass pass) throws SQLException {
        statement.setString(1, pass.passId);
        statement.setString(2, pass.registerNumber);
        statement.setString(3, pass.passengerName);
        statement.setString(4, pass.routeNumber);
        statement.setString(5, pass.boardingStop);
        statement.setObject(6, pass.validityDate);
        statement.setBigDecimal(7, pass.amount);
    }

    private static void printMetadata(Connection connection) throws SQLException {
        DatabaseMetaData metadata = connection.getMetaData();
        System.out.println("database_product=" + metadata.getDatabaseProductName());
        System.out.println("database_version=" + metadata.getDatabaseProductVersion());
        System.out.println("driver_name=" + metadata.getDriverName());
        System.out.println("driver_version=" + metadata.getDriverVersion());
        try (ResultSet tables = metadata.getTables(connection.getCatalog(), null, "%", new String[] {"TABLE"})) {
            StringBuilder names = new StringBuilder();
            while (tables.next()) {
                if (names.length() > 0) names.append(';');
                names.append(tables.getString("TABLE_NAME"));
            }
            System.out.println("available_tables=" + names);
        }
    }

    private static void createTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS bus_pass_migrated ("
                    + "pass_id VARCHAR(16) PRIMARY KEY, register_number CHAR(9) NOT NULL, "
                    + "passenger_name VARCHAR(100) NOT NULL, route_number VARCHAR(12) NOT NULL, "
                    + "boarding_stop VARCHAR(120) NOT NULL, validity_date DATE NOT NULL, "
                    + "amount_paid DECIMAL(10,2) NOT NULL CHECK (amount_paid >= 0), "
                    + "INDEX idx_migrated_route(route_number), INDEX idx_migrated_register(register_number)) ENGINE=InnoDB");
        }
    }

    private static void truncate(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("TRUNCATE TABLE bus_pass_migrated");
        }
    }

    private static long countRows(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet rows = statement.executeQuery("SELECT COUNT(*) FROM bus_pass_migrated")) {
            rows.next();
            return rows.getLong(1);
        }
    }

    private static String csv(String text) { return text.replace("\"", "\"\""); }
    private static String env(String name, String fallback) { return System.getenv().getOrDefault(name, fallback); }
    private record ValidPass(String passId, String registerNumber, String passengerName,
            String routeNumber, String boardingStop, LocalDate validityDate, BigDecimal amount) { }
    record MigrationResult(long inputRecords, long loadedRecords, long rejectedRecords) { }
}
