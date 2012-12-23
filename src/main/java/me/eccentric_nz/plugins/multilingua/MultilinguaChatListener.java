package me.eccentric_nz.plugins.multilingua;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MultilinguaChatListener implements Listener {

    private Multilingua plugin;

    public MultilinguaChatListener(Multilingua plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {

    }
}