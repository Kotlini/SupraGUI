package fr.kotlini.supragui.bases;

import fr.kotlini.supragui.classes.Filler;
import fr.kotlini.supragui.classes.SlotPosition;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
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

    public void lineItem(int row, ItemStack itemStack, Consumer<InventoryClickEvent> handler) {
        final SlotPosition slotPosition = new SlotPosition(0, row);
        for (int column = 0; column < 9; column++) {
            slotPosition.addColumn(1);

            items.put(slotPosition.toSlot(), itemStack);

            if (handler != null) {
                this.itemHandlers.put(slotPosition.toSlot(), handler);
            } else {
                this.itemHandlers.remove(slotPosition.toSlot());
            }
        }
    }

    public void lineItem(int row, ItemStack itemStack) {
        lineItem(row, itemStack, null);
    }

    public void fillItems(SlotPosition startPos, SlotPosition endPos, List<ItemStack> itemStackList, Consumer<InventoryClickEvent> handler) {
        final Filler filler = new Filler(startPos, endPos, size, true);

        for (ItemStack itemStack : itemStackList) {
            final int slot = filler.findEmptySlot(items, 1);
            if (slot != -1) {
                items.put(slot, itemStack);
            }

            if (handler != null) {
                this.itemHandlers.put(slot, handler);
            } else {
                this.itemHandlers.remove(slot);
            }
        }
    }

    public void fillItems(SlotPosition startPos, SlotPosition endPos, List<ItemStack> itemStackList) {
        fillItems(startPos, endPos, itemStackList, null);
    }

    public abstract void putItems();
}