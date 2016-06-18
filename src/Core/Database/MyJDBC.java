package Core.Database;

import Core.SQLDriver;
import Core.Singleton.ConfigSingleton;
import Core.Singleton.ServerSingleton;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by teddy on 18/06/2016.
 */
public class MyJDBC {
    public JDBCLib load() {
        for (Method methods : JDBCLib.class.getDeclaredMethods()) {
            if (methods.getAnnotation(SQLDriver.class).value().equals(ConfigSingleton.getInstance().getPropertie("database_type"))) {
                try {
                    return (JDBCLib) methods.invoke(JDBCLib.class.newInstance());
                } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                    ServerSingleton.getInstance().log("MyJDBC : " + e.getMessage(), true);
                }
            }
        }
        ServerSingleton.getInstance().log("MyJDBC : no database driver supported founded", true);
        return null;
    }
}
