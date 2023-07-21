package fr.kotlini.supragui.bases;

import fr.kotlini.supragui.classes.Button;
import fr.kotlini.supragui.classes.Filler;
import fr.kotlini.supragui.classes.SlotPosition;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class SingleGUI extends GUI {

    public SingleGUI(UUID uuid, String title, int size) {
        super(uuid, title, size, 1, 1);
    }

    @Override
    public void update(boolean open) {
        if (open) {
            clear(true);
        }
        putItems();
        refresh(true);
    }

    @Override
    public void refresh(boolean open) {
        if (!open) {
            clear(false);
        }

        for (int x = 0; x < getSize(); x++) {
            if (items.get(x) != null) {
                inventory.setItem(x, items.get(x));
            }
        }

        getPlayer().updateInventory();
    }

    @Override
    public void refresh(SlotPosition startPos, SlotPosition endPos, boolean cache) {
        new Filler(startPos, endPos, getSize(), true).getSlots().forEach(slot -> {
            removeItem(slot, cache);
            if (items.get(slot) != null) {
                inventory.setItem(slot, items.get(slot));
            }
        });
        getPlayer().updateInventory();
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
        if (slot >= 0 && slot < getSize()) {
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
            items.put(slotPosition.toSlot(), itemStack);

            if (handler != null) {
                this.itemHandlers.put(slotPosition.toSlot(), handler);
            } else {
                this.itemHandlers.remove(slotPosition.toSlot());
            }

            slotPosition.addColumn(1);
        }
    }

    public void lineItem(int row, ItemStack itemStack) {
        lineItem(row, itemStack, null);
    }

    public void fillItems(SlotPosition startPos, SlotPosition endPos, List<ItemStack> itemStackList, Consumer<InventoryClickEvent> handler) {
        final Filler filler = new Filler(startPos, endPos, getSize(), true);

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

    public void fillItems(SlotPosition startPos, SlotPosition endPos, Collection<Button> buttons) {
        final Filler filler = new Filler(startPos, endPos, getSize(), true);

        for (Button button : buttons) {
            final int slot = filler.findEmptySlot(items, 1);
            if (slot != -1) {
                items.put(slot, button.getItemStack());
            }

            if (button.getHandler() != null) {
                this.itemHandlers.put(slot, button.getHandler());
            } else {
                this.itemHandlers.remove(slot);
            }
        }
    }

    public abstract void putItems();
}