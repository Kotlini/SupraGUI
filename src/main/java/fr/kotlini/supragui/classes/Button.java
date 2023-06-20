package fr.kotlini.supragui.classes;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class Button {

    private final ItemStack itemStack;

    private final Consumer<InventoryClickEvent> handler;

    public Button(ItemStack itemStack, Consumer<InventoryClickEvent> handler) {
        this.itemStack = itemStack;
        this.handler = handler;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public Consumer<InventoryClickEvent> getHandler() {
        return handler;
    }
}
