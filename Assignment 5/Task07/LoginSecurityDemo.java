import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class LoginSecurityDemo {
    private LoginSecurityDemo() { }

    public static void main(String[] args) throws Exception {
        String url = env("A5_DB_URL", "jdbc:mysql://127.0.0.1:3307/assignment5_test");
        String user = env("A5_DB_USER", "assignment5");
        String password = env("A5_DB_PASSWORD", "Assignment5Test");

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            resetTestTable(connection);
            String injectedUsername = "nobody' OR '1'='1' -- ";
            String invalidPassword = "wrong-password";

            boolean normalUnsafe = unsafeLogin(connection, "emad", "correct-test-password");
            boolean injectionUnsafe = unsafeLogin(connection, injectedUsername, invalidPassword);
            boolean injectionPrepared = preparedLogin(connection, injectedUsername, invalidPassword);

            System.out.println("database.product=" + connection.getMetaData().getDatabaseProductName());
            System.out.println("database.version=" + connection.getMetaData().getDatabaseProductVersion());
            System.out.println("normal.unsafe.accepted=" + normalUnsafe);
            System.out.println("injection.input=" + injectedUsername);
            System.out.println("injection.unsafe.accepted=" + injectionUnsafe);
            System.out.println("injection.prepared.accepted=" + injectionPrepared);
            System.out.println("expected=unsafe true, prepared false");
            System.out.println("tests=" + (normalUnsafe && injectionUnsafe && !injectionPrepared ? "PASS" : "FAIL"));
        }
    }

    static boolean unsafeLogin(Connection connection, String username, String password) throws SQLException {
        String sql = "SELECT username FROM app_user WHERE username = '" + username
                + "' AND password_text = '" + password + "'";
        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {
            return result.next();
        }
    }

    static boolean preparedLogin(Connection connection, String username, String password) throws SQLException {
        String sql = "SELECT username FROM app_user WHERE username = ? AND password_text = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    private static void resetTestTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP TABLE IF EXISTS app_user");
            statement.executeUpdate("CREATE TABLE app_user ("
                    + "username VARCHAR(80) PRIMARY KEY, password_text VARCHAR(120) NOT NULL) ENGINE=InnoDB");
        }
        try (PreparedStatement insert = connection.prepareStatement(
                "INSERT INTO app_user(username, password_text) VALUES (?, ?)")) {
            insert.setString(1, "emad");
            insert.setString(2, "correct-test-password");
            insert.executeUpdate();
        }
    }

    private static String env(String name, String fallback) {
        return System.getenv().getOrDefault(name, fallback);
    }
}
