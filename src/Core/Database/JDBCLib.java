package Core.Database;

import Core.SQLDriver;
import Core.Singleton.ConfigSingleton;
import Core.Singleton.ServerSingleton;

import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCLib extends SQL {
    private String url = ConfigSingleton.getInstance().getString("db_url");
    private String username = ConfigSingleton.getInstance().getString("db_username");
    private String password = ConfigSingleton.getInstance().getString("db_password");

    @SQLDriver("Sqlite")
    public JDBCLib sqlite() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + url + ".db");
            stmt = c.createStatement();
            c.setAutoCommit(false);
        } catch (ClassNotFoundException | SQLException e) {
            ServerSingleton.getInstance().log("Sqlite : " + e.getMessage(), e);
        }
        return this;
    }

    @SQLDriver("MySQL")
    public JDBCLib mySQL() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            c = DriverManager.getConnection("jdbc:mysql://" + url, username, password);
            stmt = c.createStatement();
            c.setAutoCommit(false);
        } catch (Exception e) {
            ServerSingleton.getInstance().log("MySQL : " + e.getMessage(), e);
        }
        return this;
    }

    @SQLDriver("PostgresSQL")
    public JDBCLib postgreSQL() {
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://" + url + "?user=" + username + "&password=" + password);
            stmt = c.createStatement();
            c.setAutoCommit(false);
        } catch (ClassNotFoundException | SQLException e) {
            ServerSingleton.getInstance().log("PostgreSQL : " + e.getMessage(), e);
        }
        return this;
    }
}