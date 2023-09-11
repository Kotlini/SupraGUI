package fr.kotlini.supragui.utils;

import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TitleUpdater {

    private static Method getHandle, sendPacket;
    private static Field activeContainerField, windowIdField, playerConnectionField;
    private static Constructor<?> chatMessageConstructor, packetPlayOutOpenWindowConstructor;

    static {
        try {
            chatMessageConstructor = SupraReflection.nmsClass("ChatMessage").getConstructor(String.class, Object[].class);
            Class<?> nmsPlayer = SupraReflection.nmsClass("EntityPlayer");
            activeContainerField = nmsPlayer.getField("activeContainer");
            windowIdField = SupraReflection.nmsClass("Container").getField("windowId");
            playerConnectionField = nmsPlayer.getField("playerConnection");
            packetPlayOutOpenWindowConstructor = SupraReflection.nmsClass("PacketPlayOutOpenWindow").getConstructor(Integer.TYPE, String.class,
                    SupraReflection.nmsClass("IChatBaseComponent"), Integer.TYPE);
            sendPacket = SupraReflection.nmsClass("PlayerConnection").getMethod("sendPacket", SupraReflection.nmsClass("Packet"));
        } catch (NoSuchMethodException | SecurityException | ClassNotFoundException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static void update(final Player p, String title) {
        if (p.getOpenInventory().getTopInventory().getName().equalsIgnoreCase("container.crafting")) return;
        try {
            Object handle = SupraReflection.getHandlePlayer(p);
            Object message = chatMessageConstructor.newInstance(title, new Object[0]);
            Object container = activeContainerField.get(handle);
            Object windowId = windowIdField.get(container);
            Object packet = packetPlayOutOpenWindowConstructor.newInstance(windowId, "minecraft:chest", message, p.getOpenInventory().getTopInventory().getSize());
            SupraReflection.sendPacket(p, packet);
        } catch (IllegalArgumentException | IllegalAccessException | InstantiationException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }
        p.updateInventory();
    }
}
