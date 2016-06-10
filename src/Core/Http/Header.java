package Core.Http;

import Core.Singleton.ServerSingleton;

import java.util.HashMap;

/**
 * Created by teddy on 31/05/2016.
 */
public class Header extends HashMap {
    public int getInt(Object key) {
        try {
            return Integer.parseInt(get(key).toString());
        } catch (NumberFormatException e) {
            ServerSingleton.getInstance().log("[SERVER] -> Header.getInt : " + e, true);
        }
        return -1;
    }

    public String getString(Object key) {
        return String.valueOf(get(key).toString());
    }

    public double getDouble(Object key) {
        try {
            return Double.valueOf(get(key).toString());
        } catch (NumberFormatException e) {
            ServerSingleton.getInstance().log("[SERVER] -> Header.getDouble : " + e, true);
        }
        return -1;
    }

    public float getFloat(Object key) {
        try {
            return Float.valueOf(get(key).toString());
        } catch (NumberFormatException e) {
            ServerSingleton.getInstance().log("[SERVER] -> Header.getFloat : " + e, true);
        }
        return -1;
    }

    @Override
    public Object get(Object key) {
        return super.get(key);
    }

    @Override
    public boolean containsKey(Object key) {
        return super.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return super.containsValue(value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object put(Object key, Object value) {
        return super.put(key, value);
    }
}
