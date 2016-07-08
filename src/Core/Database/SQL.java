package Core.Database;

import Core.Singleton.ConfigSingleton;
import Core.Singleton.ServerSingleton;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.*;

/**
 * Created by teddy on 11/04/2016.
 */
public class SQL {
    protected Connection c = null;
    protected Statement stmt = null;
    private int generatedId = -1;

    public static String make(String subject, Object[] values) {
        for (int i = 0; i < values.length; i++) {
            if (values[i].getClass().getTypeName().equals("java.lang.String")) {
                try {
                    values[i] = "\"" + URLEncoder.encode(values[i].toString(), ConfigSingleton.getInstance().getCharset()) + "\"";
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return String.format(subject.replace("%", "%%").replace("?", "%s"), values);
    }

    public void insertDB(String sql) {
        try {
            PreparedStatement preparedStatement = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.execute();
            c.commit();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            rs.next();
            generatedId = rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            ServerSingleton.getInstance().log("SQLException on insert: " + e.getMessage(), true);
        }
    }

    public void updateDB(String sql) {
        try {
            PreparedStatement preparedStatement = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.execute();
            c.commit();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            rs.next();
            generatedId = rs.getInt(1);
        } catch (SQLException e) {
            ServerSingleton.getInstance().log("SQLException on update: " + e.getMessage(), true);
        }
    }

    public void deleteDB(String sql) {
        try {
            PreparedStatement preparedStatement = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.execute();
            c.commit();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            rs.next();
            generatedId = rs.getInt(1);
        } catch (SQLException e) {
            ServerSingleton.getInstance().log("SQLException on delete: " + e.getMessage(), true);
        }
    }

    public ResultSet selectDB(String sql) {
        try {
            PreparedStatement preparedStatement = c.prepareStatement(sql);
            ResultSet result = preparedStatement.executeQuery();
            c.commit();
            return result;
        } catch (SQLException e) {
            ServerSingleton.getInstance().log("SQLException on select: " + e.getMessage(), true);
        }
        return null;
    }

    public void closeDB() {
        try {
            stmt.close();
            c.close();
        } catch (SQLException e) {
            ServerSingleton.getInstance().log("SQLException on close: " + e.getMessage(), true);
        }
    }

    public int getGeneratedId() {
        return generatedId;
    }
}
