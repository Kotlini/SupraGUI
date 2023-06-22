package fr.kotlini.supragui.guis;

import fr.kotlini.supragui.bases.MultiGUI;
import fr.kotlini.supragui.classes.Filler;
import fr.kotlini.supragui.classes.SlotPosition;
import fr.kotlini.supragui.classes.builders.ItemBuilder;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.UUID;

public class RaisonGUI extends MultiGUI {

    public RaisonGUI(UUID uuid) {
        super(uuid, "§cReport §7>> §6§lList <INDEX> / <MAX>", 6 * 9, 6,
                new Filler(new SlotPosition(1, 1), new SlotPosition(8, 4), 6 * 9, true),
                true);
        loadReport();
    }

    public ArrayList<String> reports = new ArrayList<>();

    public void loadReport() {
        for (int r = 1; r <= 150; r++) {
            reports.add("Report_" + r);
        }
    }

    @Override
    public void putItems() {
        setPatternItems(getCorners(), new ItemBuilder(Material.STAINED_GLASS_PANE).name("").data(11).build(), (event -> {}));
        setPatternItem(new SlotPosition(4, 5).toSlot(), new ItemBuilder(Material.BANNER).name("§cClose").build(),
                (event -> getPlayer().closeInventory()));

        for (String report : reports) {
            addFillerItem(new ItemBuilder(Material.BOOKSHELF).name("§9§l" + report).build(), event -> {
                reSize(9);
                getPlayer().sendMessage("§c§lresize set !");
            });
        }
    }
}
