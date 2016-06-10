package Core.Database;

import Core.Singleton.ConfigSingleton;
import Core.Singleton.ServerSingleton;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by teddy on 03/04/2016.
 */
public class SQLite {
    public static String database = "sheol.fr";
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
                    if (result.getObject(i).getClass().getTypeName().equals("java.lang.String")) {
                        try {
                            data.put(metaData.getColumnName(i), URLDecoder.decode(result.getObject(i).toString(), ConfigSingleton.getInstance().getCharset()));
                        } catch (UnsupportedEncodingException e) {
                            ServerSingleton.getInstance().log("URLDecode : " + e, true);
                        }
                    } else {
                        data.put(metaData.getColumnName(i), result.getObject(i));
                    }
                }
                entities.add(data);
            }
        } catch (SQLException e) {
            ServerSingleton.getInstance().log("SELECT : " + e, true);
        }
        sql.closeDB();
    }

    public void insert() {
        SQLiteJDBC sql = new SQLiteJDBC(database);
        sql.insertDB(request);
    }

    public void update() {
        SQLiteJDBC sql = new SQLiteJDBC(database);
        sql.updateDB(request);
    }

    public void delete() {
        SQLiteJDBC sql = new SQLiteJDBC(database);
        sql.deleteDB(request);
    }
}
