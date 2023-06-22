package fr.kotlini.supragui.commands;

import fr.kotlini.supragui.guis.RaisonGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;

        new RaisonGUI(((Player) sender).getUniqueId()).open(true);
        return true;
    }
}
