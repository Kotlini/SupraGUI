package fr.kotlini.supragui.bases;

import fr.kotlini.supragui.InvHandler;
import fr.kotlini.supragui.classes.SlotPosition;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public abstract class GUI {

    protected final Map<Integer, Consumer<InventoryClickEvent>> itemHandlers;

    protected final List<Consumer<InventoryOpenEvent>> openHandlers;

    protected final List<Consumer<InventoryCloseEvent>> closeHandlers;

    protected final List<Consumer<InventoryClickEvent>> clickHandlers;

    protected Predicate<Player> closeFilter;

    protected Inventory inventory;

    protected final Player player;

    protected final int size;

    protected String title;

    protected LinkedHashMap<Integer, ItemStack> items;

    protected int index;

    protected int maxPage;

    public GUI(Player player, String title, int size, Predicate<Player> closeFilter, int index, int maxPage) {
        this.player = player;
        this.itemHandlers = new HashMap<>();
        this.openHandlers = new ArrayList<>();
        this.closeHandlers = new ArrayList<>();
        this.clickHandlers = new ArrayList<>();
        this.title = title;
        this.size = size;
        this.closeFilter = closeFilter;
        this.maxPage = maxPage;
        this.index = index;
        this.items = new LinkedHashMap<>();
    }

    public void handleOpen(InventoryOpenEvent e) {
        onOpen(e);

        this.openHandlers.forEach(c -> c.accept(e));
    }

    public boolean handleClose(InventoryCloseEvent e) {
        onClose(e);

        this.closeHandlers.forEach(c -> c.accept(e));
        return this.closeFilter != null && this.closeFilter.test((Player) e.getPlayer());
    }

    public void handleClick(InventoryClickEvent e) {
        onClick(e);

        this.clickHandlers.forEach(c -> c.accept(e));

        if (this.itemHandlers.get((size * index - size) + e.getRawSlot()) != null) {
            this.itemHandlers.get((size * index - size) + e.getRawSlot()).accept(e);
        }
    }

    protected void onOpen(InventoryOpenEvent event) {
    }

    protected void onClick(InventoryClickEvent event) {
    }

    protected void onClose(InventoryCloseEvent event) {
    }

    protected void register() {
        InvHandler.put(player.getUniqueId(), this);
    }

    public void unRegister() {
        InvHandler.remove(player.getUniqueId());
    }

    public void close() {
        player.closeInventory();
    }

    public void open(boolean update) {
        if (update) {
            update();
        } else {
            refresh();
        }
        register();
        if (inventory != null) {
            player.openInventory(this.inventory);
        }
    }

    public void reOpen(boolean update) {
        close();
        open(update);
    }

    public void removeItem(int slot) {
        this.inventory.clear(slot);
        this.items.remove(slot);
        this.itemHandlers.remove(slot);
    }

    public void removeItems(int... slots) {
        for (int slot : slots) {
            removeItem(slot);
        }
    }

    public int[] getBorders() {
        return IntStream.range(0, size).filter(i -> size < 27 || i < 9
                || i % 9 == 0 || (i - 8) % 9 == 0 || i > size - 9).toArray();
    }

    public int[] getCorners() {
        return IntStream.range(0, size).filter(i -> i < 2 || (i > 6 && i < 10)
                || i == 17 || i == size - 18
                || (i > size - 11 && i < size - 7) || i > size - 3).toArray();
    }

    public void clear() {
        for (int slot = 0; slot < size; slot++) {
            removeItem(slot);
        }
    }

    public abstract void update();

    public abstract void refresh();

    public Map<Integer, Consumer<InventoryClickEvent>> getItemHandlers() {
        return itemHandlers;
    }

    public List<Consumer<InventoryOpenEvent>> getOpenHandlers() {
        return openHandlers;
    }

    public List<Consumer<InventoryCloseEvent>> getCloseHandlers() {
        return closeHandlers;
    }

    public List<Consumer<InventoryClickEvent>> getClickHandlers() {
        return clickHandlers;
    }

    public Predicate<Player> getCloseFilter() {
        return closeFilter;
    }

    public void setCloseFilter(Predicate<Player> closeFilter) {
        this.closeFilter = closeFilter;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Player getPlayer() {
        return player;
    }

    public int getSize() {
        return size;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LinkedHashMap<Integer, ItemStack> getItems() {
        return items;
    }

    public void setItems(LinkedHashMap<Integer, ItemStack> items) {
        this.items = items;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getMaxPage() {
        return maxPage;
    }

    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }
}
