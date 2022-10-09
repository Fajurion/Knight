package ac.knight.util;

import java.lang.reflect.Field;

public class ReflectionUtil {

    public static void setField(String name, Object obj, Object value) {
        try {
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception ignored) {}
    }

    public static Object getField(String name, Object obj) {
        try {
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception ignored) {}
        return null;
    }

}
