package me.eccentric_nz.plugins.multilingua;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MultilinguaChatListener implements Listener {

    private Multilingua plugin;
    public static final String DEFAULT_ENCODING = "UTF-8";
    private final String key = "[" + plugin.getConfig().getString("plain_text_key") + "]";

    public MultilinguaChatListener(Multilingua plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String chat = event.getMessage();
        String[] first = chat.split(" ");
        String[] chars = chat.split("");
        StringBuilder sb = new StringBuilder();
        for (String c : chars) {
            if (plugin.encoder.containsKey(c)) {
                sb.append(plugin.encoder.get(c));
            } else {
                sb.append(c);
            }
        }
        String cipher = sb.toString();
        FPlayer fp = FPlayers.i.get(event.getPlayer());
        String faction = fp.getFactionId();
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            FPlayer onlineP = FPlayers.i.get(p);
            if ((onlineP.hasFaction() && onlineP.getFactionId().equals(faction)) || first[0].equals(key)) {
                // send the plain message
                if (first[0].equals(key)) {
                    int len = chat.length();
                    chat = chat.substring(3, len);
                }
                p.sendMessage(chat);
            } else {
                // send the cipher message
                p.sendMessage(cipher);
            }
        }
        event.setCancelled(true);
    }
}