package fr.kotlini.supragui.classes;

import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class Filler {

    private final SlotPosition startPos;

    private final SlotPosition endPos;

    private final LinkedList<Integer> slots;

    private final int size;

    public Filler(SlotPosition startPos, SlotPosition endPos, int size, boolean sorter) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.slots = new LinkedList<>();
        this.size = size;
        for (int column = startPos.getColumn(); column <= endPos.getColumn(); column++) {
            for (int row = startPos.getRow(); row <= endPos.getRow(); row++) {
                slots.add(new SlotPosition(column, row).toSlot());
            }
        }
        if (sorter) Collections.sort(slots);
    }

    public SlotPosition getStartPos() {
        return startPos;
    }

    public SlotPosition getEndPos() {
        return endPos;
    }

    public LinkedList<Integer> getSlots() {
        return slots;
    }

    public int getSize() {
        return size;
    }

    public int findEmptySlot(LinkedHashMap<Integer, ItemStack> items, int page) {
        for (int slot : slots) {
            if (items.get((size * page - size) + slot) == null) return (size * page - size) + slot;
        }
        return -1;
    }

    public int getValidItems(LinkedHashMap<Integer, ItemStack> items, int maxPage) {
        int it = 0;
        for (int page = 1; page <= maxPage; page++) {
            for (int slot : slots) {
                if (items.get((size * page - size) + slot) != null) it++;
            }
        }

        return it;
    }

    public int countOfSlot() {
        return slots.size();
    }

    public boolean isInFilling(int slot, int page) {
        return slots.contains(slot - (size * page - size));
    }
}
