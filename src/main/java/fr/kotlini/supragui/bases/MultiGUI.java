package fr.kotlini.supragui.bases;

import fr.kotlini.supragui.classes.Button;
import fr.kotlini.supragui.classes.Filler;
import fr.kotlini.supragui.classes.PatternPage;
import fr.kotlini.supragui.classes.SlotPosition;
import fr.kotlini.supragui.classes.builders.ItemBuilder;
import fr.kotlini.supragui.enums.NavigationPosition;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.function.Consumer;

public abstract class MultiGUI extends GUI {

    private final PatternPage pattern;

    private final Filler filler;

    private final boolean dynamicTitle;

    private int maxPageCountItem;

    private String title;

    public MultiGUI(UUID uuid, String title, int size, int index, int maxPage, PatternPage pattern, Filler filler, boolean dynamicTitle) {
        super(uuid, title, size, index, maxPage);
        this.title = title;
        this.pattern = pattern;
        this.filler = filler;
        this.dynamicTitle = dynamicTitle;
        this.maxPageCountItem = 0;
    }

    public MultiGUI(UUID uuid, String title, int size, int maxPage, NavigationPosition navigationPosition, ItemStack previousPage, ItemStack nextPage, Filler filler, boolean dynamicTitle) {
        this(uuid, title, size, 1, maxPage, new PatternPage(navigationPosition, previousPage, nextPage, size), filler, dynamicTitle);
    }

    public MultiGUI(UUID uuid, String title, int size, int maxPage, Filler filler, boolean dynamicTitle) {
        this(uuid, title, size, maxPage, NavigationPosition.BOTTOM, new ItemBuilder(Material.ARROW).name("§cPrécédent").build(),
                new ItemBuilder(Material.ARROW).name("§aSuivant").build(), filler, dynamicTitle);
    }

    public MultiGUI(UUID uuid, String title, int size, int maxPage, Filler filler) {
        this(uuid, title, size, maxPage, filler, false);
    }

    @Override
    public void update(boolean open) {
        if (!open) {
            clear(true);
            if (dynamicTitle) {
                rename(getTitle());
            }
        }
        putItems();
        maxPageCountItem = getMaxPageCountItem();
        pattern.merge(items, itemHandlers, maxPageCountItem);
        mergeButtonNavigation();
        refresh(true);
    }

    @Override
    public void refresh(boolean open) {
        if (!open) {
            clear(false);
        }

        for (int x = 0; x < getSize(); x++) {
            final int slot = (getSize() * index - getSize()) + x;

            if (items.get(slot) != null) {
                inventory.setItem(x, items.get(slot));
            }
        }

        getPlayer().updateInventory();
    }

    @Override
    public void refresh(SlotPosition startPos, SlotPosition endPos, boolean cache) {
        new Filler(startPos, endPos, getSize(), false).getSlots().forEach(slot -> {
            removeItem(slot, cache);
            final int newSlot = (getSize() * index - getSize()) + slot;
            if (items.get(newSlot) != null) {
                inventory.setItem(slot, items.get(newSlot));
            }
        });

        getPlayer().updateInventory();
    }

    private void navigateNext() {
        index++;
        rename(getTitle());
        refresh(false);
    }

    private void navigatePrevious() {
        index--;
        rename(getTitle());
        refresh(false);
    }

    public void addFillerItem(ItemStack itemStack) {
        addFillerItem(itemStack, null);
    }

    public void addFillerItem(ItemStack itemStack, Consumer<InventoryClickEvent> handler) {
        int page = 1;

        while (true) {
            if (page > maxPage) return;

            final int slot = filler.findEmptySlot(this.items, page);
            if (slot != -1) {
                setFillerItem(page, slot, itemStack, handler);
                return;
            }

            page++;
        }
    }

    public void setFillerItem(int page, int slot, ItemStack itemStack, Consumer<InventoryClickEvent> handler) {
        if (filler.isInFilling(slot, page)) {
            items.put(slot, itemStack);

            if (handler != null) {
                itemHandlers.put(slot, handler);
            }
        }
    }

    public void setItem(int page, int slot, ItemStack itemStack, Consumer<InventoryClickEvent> handler) {
        final int newSlot = (getSize() * page - getSize()) + slot;
        items.put(newSlot, itemStack);

        if (handler != null) {
            itemHandlers.put(newSlot, handler);
        } else {
            itemHandlers.remove(newSlot);
        }
    }

    public void setItem(int page, int slot, ItemStack itemStack) {
        setItem(page, slot, itemStack, null);
    }

    public void lineItem(int page, int row, ItemStack itemStack, Consumer<InventoryClickEvent> handler) {
        final SlotPosition slotPosition = new SlotPosition(0, row);
        for (int column = 0; column < 9; column++) {

            final int slot = slotPosition.toSlot(page, getSize());
            items.put(slot, itemStack);

            if (handler != null) {
                this.itemHandlers.put(slot, handler);
            } else {
                this.itemHandlers.remove(slot);
            }

            slotPosition.addColumn(1);
        }
    }

    public void lineItem(int page, int row, ItemStack itemStack) {
        lineItem(page, row, itemStack, null);
    }

    public void setPatternItem(int slot, ItemStack itemStack, Consumer<InventoryClickEvent> handler) {
        pattern.setItem(slot, new Button(itemStack, handler));
    }

    public void setPatternItem(int slot, ItemStack itemStack) {
        setPatternItem(slot, itemStack, null);
    }

    public void setPatternItems(int[] slots, ItemStack itemStack, Consumer<InventoryClickEvent> handler) {
        for (int slot : slots) {
            setPatternItem(slot, itemStack, handler);
        }
    }

    public void setPatternItems(int[] slots, ItemStack itemStack) {
        setPatternItems(slots, itemStack, null);
    }

    public void mergeButtonNavigation() {
        for (int page = 1; page <= maxPageCountItem; page++) {
            if (maxPageCountItem != 1 && page != maxPage && page != maxPageCountItem) {
                setItem(page, pattern.getSlotNextPage(), pattern.getNextPage(), (event) -> {
                    navigateNext();
                });
            }

            if (page != 1) {
                setItem(page, pattern.getSlotPreviousPage(), pattern.getPreviousPage(), (event) -> {
                    navigatePrevious();
                });
            }
        }
    }

    public int getMaxPageCountItem() {
        final double page = (double) filler.getValidItems(this.items, maxPage) / (double) filler.countOfSlot();
        return (int) Math.ceil(page);
    }

    @Override
    public void open(boolean update) {
        if (update) {
            update(true);
        } else {
            refresh(true);
        }

        getPlayer().openInventory(this.inventory);
        if (dynamicTitle) {
            rename(getTitle());
        }
    }

    @Override
    public String getTitle() {
        if (dynamicTitle) {
            return title.replaceAll("<INDEX>", String.valueOf(index)).replaceAll("<MAX>",
                    String.valueOf(maxPageCountItem));
        }

        return super.getTitle();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public abstract void putItems();
}
