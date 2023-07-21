package fr.kotlini.supragui.guis;


import fr.kotlini.supragui.bases.SingleGUI;
import fr.kotlini.supragui.classes.SlotPosition;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class RaisonGUI extends SingleGUI {


    public RaisonGUI(UUID uuid) {
        super(uuid, "test", 9 * 6);
    }

    @Override
    public void putItems() {
        setItems(getBorders(), new ItemStack(Material.STAINED_CLAY, 1, (byte) 3), (event) -> {
            setItem(16, new ItemStack(Material.BEDROCK));
            getInventory().setItem(16, new ItemStack(Material.BEDROCK));
            refresh(new SlotPosition(1, 1), new SlotPosition(7, 4), false);
        });
    }
}