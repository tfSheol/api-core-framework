package Core.Database;

import Core.Singleton.ServerSingleton;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by teddy on 03/04/2016.
 */
public class SQLite {
    public static String database = "data";
    private ArrayList<HashMap<String, Object>> entities = new ArrayList<>();
    private String request;

    public SQLite(String request) {
        this.request = request;
    }

    public ArrayList<HashMap<String, Object>> getResultSet() {
        return entities;
    }

    public void select() {
        ResultSet result;
        SQLiteJDBC sql = new SQLiteJDBC(database);
        try {
            result = sql.selectDB(request);
            ResultSetMetaData metaData = result.getMetaData();
            while (result.next()) {
                HashMap<String, Object> data = new HashMap<>();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    data.put(metaData.getColumnName(i), result.getObject(i));
                }
                entities.add(data);
            }
        } catch (SQLException e) {
            System.err.println("SELECT : " + e);
        }
        sql.closeDB();
    }

    public void insert() {
        SQLiteJDBC sql = new SQLiteJDBC(database);
        ServerSingleton.getInstance().log("DEBUG INSERT " + request);
        sql.insertDB(request);
    }

    public void update() {
        SQLiteJDBC sql = new SQLiteJDBC(database);
        ServerSingleton.getInstance().log("DEBUG UPDATE " + request);
        sql.updateDB(request);
    }

    public void delete() {
        SQLiteJDBC sql = new SQLiteJDBC(database);
        ServerSingleton.getInstance().log("DEBUG DELETE " + request);
        sql.deleteDB(request);
    }
}
