package fr.kotlini.supragui.bases;

import fr.kotlini.supragui.classes.Filler;
import fr.kotlini.supragui.classes.PatternPage;
import fr.kotlini.supragui.classes.builders.ItemBuilder;
import fr.kotlini.supragui.enums.NavigationPosition;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class MultiGUI extends GUI {

    private final PatternPage pattern;

    private final List<Integer> dynamicHandlers;

    private final Filler filler;

    private final boolean indexTitle;

    private int maxPageCountItem;

    public MultiGUI(Player player, String title, int size, Predicate<Player> closeFilter, int index, int maxPage, PatternPage pattern, Filler filler, boolean indexTitle) {
        super(player, title, size, closeFilter, index, maxPage);
        this.pattern = pattern;
        this.dynamicHandlers = new ArrayList<>();
        this.filler = filler;
        this.indexTitle = indexTitle;
        this.maxPageCountItem = 0;
    }

    public MultiGUI(Player player, String title, int size, Predicate<Player> closeFilter, int maxPage, NavigationPosition navigationPosition, ItemStack previousPage, ItemStack nextPage, Filler filler, boolean indexTitle) {
        this(player, title, size, closeFilter, 1, maxPage, new PatternPage(navigationPosition, previousPage, nextPage, size), filler, indexTitle);
    }

    public MultiGUI(Player player, String title, int size, Predicate<Player> closeFilter, int maxPage, Filler filler, boolean indexTitle) {
        this(player, title, size, closeFilter, maxPage, NavigationPosition.BOTTOM, new ItemBuilder(Material.ARROW).name("§cPrécédent").build(),
                new ItemBuilder(Material.ARROW).name("§aSuivant").build(), filler, indexTitle);
    }

    public MultiGUI(Player user, String title, int size, int maxPage, Filler filler, boolean indexTitle) {
        this(user, title, size, null, maxPage, filler, indexTitle);
    }

    @Override
    public void update() {
        clear();
        putItems();
        maxPageCountItem = getMaxPageCountItem();
        refresh();
    }

    @Override
    public void refresh() {
        final Inventory inv;

        if (indexTitle) {
            inv = Bukkit.createInventory(null, size, title + " §f" + index + "§8/§f" + maxPageCountItem);
        } else {
            if (inventory == null) {
                inv = Bukkit.createInventory(null, size, title);
            } else {
                inv = inventory;
                clear();
            }
        }

        putPattern();

        for (int x = size * index - size; x < size * index; x++) {
            if (items.get(x) != null) {
                inv.setItem(x - (size * index - size), items.get(x));
            }
        }

        this.inventory = inv;
    }

    private void navigateNext() {
        index++;
        reOpen(false);
    }

    private void navigatePrevious() {
        index--;
        reOpen(false);
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
        items.put((size * page - size) + slot, itemStack);

        if (handler != null) {
            itemHandlers.put((size * page - size) + slot, handler);
        }
    }

    public void setItem(int page, int slot, ItemStack itemStack) {
        setItem(page, slot, itemStack, null);
    }

    public void setPatternItem(int slot, ItemStack itemStack, Consumer<InventoryClickEvent> handler) {
        pattern.setItem(slot, itemStack);
        if (handler != null) {
            pattern.getItemHandlers().put(slot, handler);
        }
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

    public void putPattern() {
        clearCachedDynamicHandlers();

        for (int x = 0; x < size; x++) {
            final int slot = (size * index - size) + x;

            if (items.get(slot) == null) {
                items.put(slot, pattern.getItems().get(x));
                if (pattern.getItemHandlers().get(x) != null && itemHandlers.get(slot) == null) {
                    itemHandlers.put(slot, pattern.getItemHandlers().get(x));
                }
            }
        }

        if (maxPageCountItem != 1 && index != maxPage && index != maxPageCountItem) {
            dynamicHandlers.add(pattern.getSlotNextPage());
            setItem(index, pattern.getSlotNextPage(), pattern.getNextPage(), (event) -> {
                navigateNext();
            });
        }

        if (index != 1) {
            dynamicHandlers.add(pattern.getSlotPreviousPage());
            setItem(index, pattern.getSlotPreviousPage(), pattern.getPreviousPage(), (event) -> {
                navigatePrevious();
            });
        }
    }

    @Override
    public void close() {
        clearCachedDynamicHandlers();
        super.close();
    }

    public void clearCachedDynamicHandlers() {
        for (int slot : dynamicHandlers) {
            itemHandlers.remove(slot);
        }
        dynamicHandlers.clear();
    }

    public int getMaxPageCountItem() {
        final double page = (double) filler.getValidItems(this.items, maxPage) / (double) filler.countOfSlot();
        return (int) Math.ceil(page);
    }

    public abstract void putItems();
}
