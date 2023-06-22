package fr.kotlini.supragui.classes;

import fr.kotlini.supragui.enums.NavigationPosition;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class PatternPage {

    protected final LinkedHashMap<Integer, Button> buttons;

    private final NavigationPosition navigationPosition;

    private final ItemStack previousPage;

    private final ItemStack nextPage;

    private final int size;

    public PatternPage(NavigationPosition navigationPosition, ItemStack previousPage, ItemStack nextPage, int size) {
        this.buttons = new LinkedHashMap<>();
        this.navigationPosition = navigationPosition;
        this.previousPage = previousPage;
        this.nextPage = nextPage;
        this.size = size;
    }

    public LinkedHashMap<Integer, Button> getButton() {
        return buttons;
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

    public void setItem(int slot, Button button) {
        buttons.put(slot, button);
    }

    public void merge(LinkedHashMap<Integer, ItemStack> items, Map<Integer, Consumer<InventoryClickEvent>> handlers, int maxPageCountItem) {
        for (int page = 1; page <= maxPageCountItem; page++) {
            for (Integer x : buttons.keySet()) {
                final int slot = (size * page - size) + x;
                final Button button = buttons.get(x);

                if (items.get(slot) == null && button != null) {
                    items.put(slot, button.getItemStack());

                    if (button.getHandler() != null && handlers.get(slot) == null) {
                        handlers.put(slot, button.getHandler());
                    } else {
                        handlers.remove(slot);
                    }
                }
            }
        }
    }

    public int getSlotNextPage() {
        return (navigationPosition.equals(NavigationPosition.TOP) ? 8 : size - 1);
    }

    public int getSlotPreviousPage() {
        return (navigationPosition.equals(NavigationPosition.TOP) ? 0 : size - 9);
    }
}