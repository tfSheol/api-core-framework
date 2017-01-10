package Core.Database;

import Core.Http.Map;
import Core.Singleton.ConfigSingleton;
import Core.Singleton.ServerSingleton;
import com.google.gson.annotations.Since;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by teddy on 03/04/2016.
 */
@Since(1.8)
public class SQLRequest {
    private ArrayList<Map> entities = new ArrayList<>();
    private String request;
    private int accountId = -1;
    private JDBCLib sql = new MyJDBC().load();
    private int generatedId = -1;

    public SQLRequest(String request) {
        this.request = request;
    }

    public SQLRequest(int user_id, String request) {
        this.request = request;
        this.accountId = user_id;
    }

    public ArrayList<Map> getResultSet() {
        return entities;
    }

    public void select() {
        ResultSet result;
        try {
            while (sql.c == null) {
                sql = new MyJDBC().load();
                Thread.sleep(3000);
            }
            result = sql.selectDB(request);
            ResultSetMetaData metaData = result.getMetaData();
            while (result.next()) {
                Map data = new Map();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    if (result.getObject(i).getClass().getTypeName().equals("java.lang.String")) {
                        try {
                            String str = URLDecoder.decode(result.getObject(i).toString().replaceAll("%(?![0-9a-fA-F]{2})", "%25"), ConfigSingleton.getInstance().getCharset());
                            data.put(metaData.getTableName(i) + "." + metaData.getColumnLabel(i), str);
                            if (str.length() >= ConfigSingleton.getInstance().getInt("prev_length")) {
                                data.put(metaData.getTableName(i) + "." + metaData.getColumnLabel(i) + ".prev", str.substring(0, ConfigSingleton.getInstance().getInt("prev_length")));
                            } else {
                                data.put(metaData.getTableName(i) + "." + metaData.getColumnLabel(i) + ".prev", str);
                            }
                        } catch (UnsupportedEncodingException e) {
                            ServerSingleton.getInstance().log("URLDecode : " + e, e);
                        }
                    } else {
                        data.put(metaData.getTableName(i) + "." + metaData.getColumnLabel(i), result.getObject(i));
                    }
                }
                entities.add(data);
            }
        } catch (SQLException e) {
            ServerSingleton.getInstance().log("SELECT : " + e, e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sql.closeDB();
    }

    public void insert() {
        if (accountId != -1) {
            sql.requestDB("");
        }
        sql.requestDB(request);
        generatedId = sql.getGeneratedId();
    }

    public void update() {
        if (accountId != -1) {
            sql.requestDB("");
        }
        sql.requestDB(request);
        generatedId = sql.getGeneratedId();
    }

    public void delete() {
        if (accountId != -1) {
            sql.requestDB("");
        }
        sql.requestDB(request);
        generatedId = sql.getGeneratedId();
    }

    public int getGeneratedId() {
        return generatedId;
    }
}
