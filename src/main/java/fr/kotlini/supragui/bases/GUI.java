package fr.kotlini.supragui.bases;

import fr.kotlini.supragui.classes.Filler;
import fr.kotlini.supragui.classes.SlotPosition;
import fr.kotlini.supragui.utils.SupraReflection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public abstract class GUI implements InventoryHolder {

    protected final Map<Integer, Consumer<InventoryClickEvent>> itemHandlers = new HashMap<>();

    protected final LinkedHashMap<Integer, ItemStack> items = new LinkedHashMap<>();

    protected final List<Consumer<InventoryOpenEvent>> openHandlers = new ArrayList<>();

    protected final List<Consumer<InventoryCloseEvent>> closeHandlers = new ArrayList<>();

    protected final List<Consumer<InventoryClickEvent>> clickHandlers = new ArrayList<>();

    protected final Inventory inventory;

    protected final UUID uuid;

    protected Predicate<Player> closeFilter;

    protected int index = 1;

    protected int maxPage = 1;

    public GUI(UUID uuid, Function<InventoryHolder, Inventory> inventoryFunction) {
        Objects.requireNonNull(inventoryFunction, "inventoryFunction");
        Inventory inv = inventoryFunction.apply(this);

        if (inv.getHolder() != this) {
            throw new IllegalStateException("Inventory holder is not GUI, found: " + inv.getHolder());
        }

        this.inventory = inv;
        this.uuid = uuid;
    }

    public GUI(UUID uuid, InventoryType type) {
        this(uuid, owner -> Bukkit.createInventory(owner, type));
    }

    public GUI(UUID uuid, InventoryType type, String title) {
        this(uuid, owner -> Bukkit.createInventory(owner, type, title));
    }

    public GUI(UUID uuid, String title, int size) {
        this(uuid, owner -> Bukkit.createInventory(owner, size, title));
    }

    public GUI(UUID uuid, int size) {
        this(uuid, owner -> Bukkit.createInventory(owner, size));
    }

    public GUI(UUID uuid, String title, int size, int index, int maxPage) {
        this(uuid, owner -> Bukkit.createInventory(owner, size, title));
        this.index = index;
        this.maxPage = maxPage;
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
        if (this.itemHandlers.get((getSize() * index - getSize()) + e.getRawSlot()) != null) {
            this.itemHandlers.get((getSize() * index - getSize()) + e.getRawSlot()).accept(e);
        }

        onClick(e);
        this.clickHandlers.forEach(c -> c.accept(e));
    }

    protected void onOpen(InventoryOpenEvent event) {
    }

    protected void onClick(InventoryClickEvent event) {
    }

    protected void onClose(InventoryCloseEvent event) {
    }

    public void open(boolean update) {
        if (update) {
            update(true);
        } else {
            refresh(true);
        }

        getPlayer().openInventory(this.inventory);
    }

    public void removeItem(int slot, boolean cache) {
        this.inventory.clear(slot);
        if (cache) {
            this.items.remove(slot);
            this.itemHandlers.remove(slot);
        }
    }

    public void removeItems(boolean cache, int... slots) {
        for (int slot : slots) {
            removeItem(slot, cache);
        }
    }

    public int[] getBorders() {
        return IntStream.range(0, getSize()).filter(i -> getSize() < 27 || i < 9
                || i % 9 == 0 || (i - 8) % 9 == 0 || i > getSize() - 9).toArray();
    }

    public int[] getCorners() {
        return IntStream.range(0, getSize()).filter(i -> i < 2 || (i > 6 && i < 10)
                || i == 17 || i == getSize() - 18
                || (i > getSize() - 11 && i < getSize() - 7) || i > getSize() - 3).toArray();
    }

    public void clearFill(SlotPosition startPos, SlotPosition endPos, boolean cache) {
        new Filler(startPos, endPos, getSize(), false).getSlots().forEach(slot -> removeItem(slot, cache));
    }

    public void clear(boolean cache) {
        inventory.clear();
        if (cache) {
            clickHandlers.clear();
            items.clear();
            itemHandlers.clear();
            openHandlers.clear();
            closeHandlers.clear();
        }
    }

    public void rename(String title) {
        if (inventory.getName().equalsIgnoreCase("container.crafting")) return;
        final Player player = getPlayer();

        try {
            SupraReflection.sendPacket(player, SupraReflection.instanceClass(SupraReflection.nmsClass("PacketPlayOutOpenWindow"),
                    1, SupraReflection.fieldClass(SupraReflection.nmsClass("Container"),
                            SupraReflection.fieldClass(SupraReflection.nmsClass("EntityHuman"),
                                    SupraReflection.getHandlePlayer(player), "activeContainer"), "windowId"),
                    "minecraft:chest", SupraReflection.instanceClass(SupraReflection.nmsClass("ChatMessage"), 0,
                            title, new Object[0]), getSize()));
        } catch (IllegalArgumentException | IllegalAccessException | InstantiationException |
                 InvocationTargetException | ClassNotFoundException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        player.updateInventory();
    }

    public void reSize(int size) {
        if (inventory.getName().equalsIgnoreCase("container.crafting")) return;
        final Player player = getPlayer();

        try {
            SupraReflection.sendPacket(player, SupraReflection.instanceClass(SupraReflection.nmsClass("PacketPlayOutOpenWindow"),
                    1, SupraReflection.fieldClass(SupraReflection.nmsClass("Container"),
                            SupraReflection.fieldClass(SupraReflection.nmsClass("EntityHuman"),
                                    SupraReflection.getHandlePlayer(player), "activeContainer"), "windowId"),
                    "minecraft:chest", SupraReflection.instanceClass(SupraReflection.nmsClass("ChatMessage"), 0,
                            getTitle(), new Object[0]), size));
        } catch (IllegalArgumentException | IllegalAccessException | InstantiationException |
                 InvocationTargetException | ClassNotFoundException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        player.updateInventory();
    }

    public abstract void update(boolean open);

    public abstract void refresh(boolean open);

    public abstract void refresh(SlotPosition startPos, SlotPosition endPos, boolean cache);

    public Map<Integer, Consumer<InventoryClickEvent>> getItemHandlers() {
        return itemHandlers;
    }

    public LinkedHashMap<Integer, ItemStack> getItems() {
        return items;
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

    public String getTitle() {
        return getInventory().getTitle();
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

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public int getSize() {
        return getInventory().getSize();
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
}