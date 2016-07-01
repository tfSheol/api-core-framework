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
public class SQLRequest {
    private ArrayList<HashMap<String, Object>> entities = new ArrayList<>();
    private String request;
    private JDBCLib sql = new MyJDBC().load();
    private int generatedId = -1;

    public SQLRequest(String request) {
        this.request = request;
    }

    public ArrayList<HashMap<String, Object>> getResultSet() {
        return entities;
    }

    public void select() {
        ResultSet result;
        try {
            result = sql.selectDB(request);
            ResultSetMetaData metaData = result.getMetaData();
            while (result.next()) {
                HashMap<String, Object> data = new HashMap<>();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    if (result.getObject(i).getClass().getTypeName().equals("java.lang.String")) {
                        try {
                            data.put(metaData.getColumnLabel(i), URLDecoder.decode(result.getObject(i).toString(), ConfigSingleton.getInstance().getCharset()));
                        } catch (UnsupportedEncodingException e) {
                            ServerSingleton.getInstance().log("URLDecode : " + e, true);
                        }
                    } else {
                        data.put(metaData.getColumnLabel(i), result.getObject(i));
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
        sql.insertDB(request);
        generatedId = sql.getGeneratedId();
    }

    public void update() {
        sql.updateDB(request);
        generatedId = sql.getGeneratedId();
    }

    public void delete() {
        sql.deleteDB(request);
        generatedId = sql.getGeneratedId();
    }

    public int getGeneratedId() {
        return generatedId;
    }
}
