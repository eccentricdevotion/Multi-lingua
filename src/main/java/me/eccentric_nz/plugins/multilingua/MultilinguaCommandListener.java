package me.eccentric_nz.plugins.multilingua;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class MultilinguaCommandListener implements Listener {

    private Multilingua plugin;

    public MultilinguaCommandListener(Multilingua plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        final String command = event.getMessage();
        final Player player = event.getPlayer();
        boolean hasPermission = player.hasPermission("factions.create");
        boolean isCommand = (command.contains("f create") || command.contains("faction create"));
        if (hasPermission && isCommand) {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                public void run() {
                    player.sendMessage("If you want to use Multi-lingua with your new faction, type " + ChatColor.AQUA + "/ml add");
                }
            }, 30L);
        }
    }
}
