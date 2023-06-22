package fr.kotlini.supragui;

import fr.kotlini.supragui.commands.TestCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {


    @Override
    public void onEnable() {
        InvHandler.register(this);

        getCommand("test").setExecutor(new TestCommand());
    }
}
