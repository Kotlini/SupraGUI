package fr.kotlini.supragui.guis;


import fr.kotlini.supragui.bases.MultiGUI;
import fr.kotlini.supragui.classes.Filler;
import fr.kotlini.supragui.classes.SlotPosition;
import fr.kotlini.supragui.classes.builders.ItemBuilder;
import org.bukkit.Material;

import java.util.UUID;

public class RaisonGUI extends MultiGUI {


    public RaisonGUI(UUID uuid) {
        super(uuid, "test <INDEX> / <MAX>", 9 * 6, 5, new Filler(SlotPosition.of(1, 1),
                        SlotPosition.of(7, 4), 9 * 6, true), true);
    }

    @Override
    public void putItems() {
        for (int x = 0; x < 84; x++) {
            addFillerItem(new ItemBuilder(Material.ARROW).name("Item n°" + x).build());
        }
    }
}