package Core.Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by teddy on 11/04/2016.
 */
public class SQL {
    protected Connection c = null;
    protected Statement stmt = null;

    public void insertDB(String sql) {
        try {
            stmt.executeUpdate(sql);
            c.commit();
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
    }

    public void updateDB(String sql) {
        try {
            stmt.executeUpdate(sql);
            c.commit();
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
    }

    public void deleteDB(String sql) {
        try {
            stmt.executeUpdate(sql);
            c.commit();
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
    }

    public ResultSet selectDB(String sql) {
        try {
            ResultSet result = stmt.executeQuery(sql);
            c.commit();
            return result;
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
        return null;
    }

    public void closeDB() {
        try {
            stmt.close();
            c.close();
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
    }
}
