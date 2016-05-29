package Core.Database;

import Core.Singleton.ServerSingleton;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

    public static String make(String subject, Object[] values) {
        for (int i = 0; i < values.length; i++) {
            if (values[i].getClass().getTypeName().equals("java.lang.String")) {
                try {
                    values[i] = "\"" + URLEncoder.encode(values[i].toString(), "UTF-8") + "\"";
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return String.format(subject.replace("%", "%%").replace("?", "%s"), values);
    }

    public void insertDB(String sql) {
        try {
            stmt.executeUpdate(sql);
            c.commit();
        } catch (SQLException e) {
            ServerSingleton.getInstance().log("SQLException: " + e.getMessage(), true);
        }
    }

    public void updateDB(String sql) {
        try {
            stmt.executeUpdate(sql);
            c.commit();
        } catch (SQLException e) {
            ServerSingleton.getInstance().log("SQLException: " + e.getMessage(), true);
        }
    }

    public void deleteDB(String sql) {
        try {
            stmt.executeUpdate(sql);
            c.commit();
        } catch (SQLException e) {
            ServerSingleton.getInstance().log("SQLException: " + e.getMessage(), true);
        }
    }

    public ResultSet selectDB(String sql) {
        try {
            ResultSet result = stmt.executeQuery(sql);
            c.commit();
            return result;
        } catch (SQLException e) {
            ServerSingleton.getInstance().log("SQLException: " + e.getMessage(), true);
        }
        return null;
    }

    public void closeDB() {
        try {
            stmt.close();
            c.close();
        } catch (SQLException e) {
            ServerSingleton.getInstance().log("SQLException: " + e.getMessage(), true);
        }
    }
}
