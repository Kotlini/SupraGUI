package fr.kotlini.supragui.utils;


import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;

public class SupraReflection {

    private static final String OBC_PACKAGE = "org.bukkit.craftbukkit";

    private static final String NMS_PACKAGE = "net.minecraft.server";

    private static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().substring(OBC_PACKAGE.length() + 1);


    public static String nmsClassName(String className) {
        return NMS_PACKAGE + '.' + VERSION + '.' + className;
    }

    public static Class<?> nmsClass(String className) throws ClassNotFoundException {
        return Class.forName(nmsClassName(className));
    }

    public static String obcClassName(String className) {
        return OBC_PACKAGE + '.' + VERSION + '.' + className;
    }

    public static Class<?> obcClass(String className) throws ClassNotFoundException {
        return Class.forName(obcClassName(className));
    }

    public static Object fieldClass(Class<?> clazz1, Object ob, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        return clazz1.getDeclaredField(fieldName).get(ob);
    }

    public static Object fieldEnum(Class<?> clazz, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        return clazz.getField(fieldName).get(null);
    }

    public static Object instanceClass(Class<?> clazz, int constructor, Object... options) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return clazz.getDeclaredConstructors()[constructor].newInstance(options);
    }

    public static Object objectToArray(Class<?> clazz, Object... ob) {
        int length = ob.length;
        Object array = Array.newInstance(clazz, length);
        for (int o = 0; o < length; o++) {
            Array.set(array, o, ob[o]);
        }
        return array;
    }

    public static void sendPacket(Player player, Object packet) {
        try {
            final Object playerConnection = getPlayerConnection(player);
            playerConnection.getClass().getMethod("sendPacket", nmsClass("Packet")).invoke(playerConnection,
                    packet);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException |
                 ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getPlayerConnection(Player player) {
        final Object handle = getHandlePlayer(player);
        try {
            return fieldClass(handle.getClass(), handle, "playerConnection");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getHandlePlayer(Player player) {
        try {
            return obcClass("entity.CraftPlayer").getMethod("getHandle").invoke(player);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object toNMSWorld(World world) {
        try {
            return obcClass("CraftWorld").getMethod("getHandle").invoke(world);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
