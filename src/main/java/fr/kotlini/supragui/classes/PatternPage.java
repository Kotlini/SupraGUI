package fr.kotlini.supragui.classes;

import fr.kotlini.supragui.enums.NavigationPosition;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class PatternPage {

    private final LinkedHashMap<Integer, ItemStack> items;

    protected final Map<Integer, Consumer<InventoryClickEvent>> itemHandlers;

    private final NavigationPosition navigationPosition;

    private final ItemStack previousPage;

    private final ItemStack nextPage;

    private final int size;

    public PatternPage(NavigationPosition navigationPosition, ItemStack previousPage, ItemStack nextPage, int size) {
        this.items = new LinkedHashMap<>();
        this.itemHandlers = new HashMap<>();
        this.navigationPosition = navigationPosition;
        this.previousPage = previousPage;
        this.nextPage = nextPage;
        this.size = size;
    }

    public LinkedHashMap<Integer, ItemStack> getItems() {
        return items;
    }

    public NavigationPosition getNavigationPosition() {
        return navigationPosition;
    }

    public ItemStack getPreviousPage() {
        return previousPage;
    }

    public ItemStack getNextPage() {
        return nextPage;
    }

    public int getSize() {
        return size;
    }

    public void setItem(int slot, ItemStack itemStack) {
        items.put(slot, itemStack);
    }

    public int getSlotNextPage() {
        return (navigationPosition.equals(NavigationPosition.TOP) ? 8 : size - 1);
    }

    public Map<Integer, Consumer<InventoryClickEvent>> getItemHandlers() {
        return itemHandlers;
    }

    public int getSlotPreviousPage() {
        return (navigationPosition.equals(NavigationPosition.TOP) ? 0 : size - 9);
    }
}