package fr.kotlini.supragui;

import fr.kotlini.supragui.bases.GUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class InvHandler {

    private static final AtomicBoolean REGISTERED = new AtomicBoolean(false);

    public InvHandler() {
        throw new UnsupportedOperationException();
    }

    public static void register(Plugin plugin) {
        Objects.requireNonNull(plugin, "plugin");

        if (REGISTERED.getAndSet(true)) {
            throw new IllegalStateException("SupraGUI is already registered");
        }

        Bukkit.getPluginManager().registerEvents(new InventoryListener(plugin), plugin);
    }

    public static void closeAll() {
        Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.getOpenInventory().getTopInventory().getHolder() instanceof GUI)
                .forEach(Player::closeInventory);
    }

    public static class InventoryListener implements Listener {

        private final Plugin plugin;

        public InventoryListener(Plugin plugin) {
            this.plugin = plugin;
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            if (event.getCurrentItem() == null) return;

            if (event.getInventory().getHolder() instanceof GUI) {
                final GUI value = (GUI) event.getInventory().getHolder();

                if (value.getItemHandlers().get((value.getSize() * value.getIndex() - value.getSize()) + event.getRawSlot()) != null) {
                    final boolean wasCancelled = event.isCancelled();
                    event.setCancelled(true);

                    value.handleClick(event);

                    // This prevents un-canceling the event if another plugin canceled it before
                    if (!wasCancelled && !event.isCancelled()) {
                        event.setCancelled(false);
                    }
                }
            }
        }

        @EventHandler
        public void onInventoryOpen(InventoryOpenEvent event) {
            if (event.getInventory().getHolder() instanceof GUI) {
                ((GUI) event.getInventory().getHolder()).handleOpen(event);
            }
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent event) {
            if (event.getInventory().getHolder() instanceof GUI) {
                final GUI value = (GUI) event.getInventory().getHolder();
                if (value.handleClose(event)) {
                    Bukkit.getScheduler().runTask(plugin, () -> value.open(false));
                }
            }
        }

        @EventHandler
        public void onPluginDisable(PluginDisableEvent e) {
            if (e.getPlugin() == this.plugin) {
                closeAll();

                REGISTERED.set(false);
            }
        }
    }
}