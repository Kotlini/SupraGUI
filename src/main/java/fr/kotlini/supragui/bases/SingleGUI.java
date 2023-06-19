package fr.kotlini.supragui.bases;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class SingleGUI extends GUI {

    public SingleGUI(Player player, String title, int size, Predicate<Player> closeFilter) {
        super(player, title, size, closeFilter, 1, 1);
    }

    @Override
    public void update() {
        putItems();
        refresh();
    }

    @Override
    public void refresh() {
        final Inventory inv;

        if (inventory == null) {
            inv = Bukkit.createInventory(null, size, title);
        } else {
            inv = inventory;
            inv.clear();
        }

        for (int x = 0; x < size; x++) {
            if (items.get(x) != null) {
                inv.setItem(x, items.get(x));
            }
        }

        this.inventory = inv;
    }

    public void addItem(ItemStack itemStack) {
        setItem(inventory.firstEmpty(), itemStack);
    }

    public void addItems(ItemStack... items) {
        for (ItemStack itemStack : items) {
            addItem(itemStack);
        }
    }

    public void setItems(int[] slots, ItemStack item, Consumer<InventoryClickEvent> handler) {
        for (int slot : slots) {
            setItem(slot, item, handler);
        }
    }

    public void setItems(int[] slots, ItemStack item) {
        setItems(slots, item, null);
    }

    public void setItem(int slot, ItemStack item) {
        setItem(slot, item, null);
    }

    public void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
        if (slot >= 0 && slot < size) {
            items.put(slot, item);

            if (handler != null) {
                this.itemHandlers.put(slot, handler);
            } else {
                this.itemHandlers.remove(slot);
            }
        }
    }

    public abstract void putItems();
}