package Core.Database;

import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteJDBC extends SQL {
    public SQLiteJDBC(String dbName) {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:./db/" + dbName + ".db");
            stmt = c.createStatement();
            c.setAutoCommit(false);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
    }
}