package testing;



import java.sql.*;
import java.io.File;

public class Database {

    // Use relative path — make sure UMS.db is in the root directory of the project
    private static final String DB_PATH = "UMS.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;

    /**
     * Gets a connection to the UMS.db SQLite database.
     * Make sure UMS.db exists in the project root.
     */
    public static Connection getConnection() throws SQLException {
        File dbFile = new File(DB_PATH);
        if (!dbFile.exists()) {
            System.err.println("❌ Database file not found: " + dbFile.getAbsolutePath());
            throw new SQLException("Database file not found.");
        }

        System.out.println("✅ Connecting to database at: " + dbFile.getAbsolutePath());
        return DriverManager.getConnection(DB_URL);
    }

    /**
     * Simple main test method to list all institutions.
     */
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            String sql = "SELECT LEGAL_NAME FROM INSTITUTION LIMIT 5";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("LEGAL_NAME");
                System.out.println("Institution: " + name);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("❌ SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
