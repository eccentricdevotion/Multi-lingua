package me.eccentric_nz.plugins.multilingua;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
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
        boolean use_chat_radius = plugin.getConfig().getBoolean("use_chat_radius");
        Player[] playerList;
        List<Player> step1Players = null;
        List<Player> step2Players = null;
        List<Player> step3Players = null;
        String chat = event.getMessage();
        String[] first = chat.split(" ");
        if (use_chat_radius && !first[0].equals(plugin.yell)) {
            int degradeAfter = plugin.getConfig().getInt("chat_degrade_after");
            int deafAfter = plugin.getConfig().getInt("chat_deaf_after");
            List<Player> collList = getPlayersWithin(event.getPlayer(), deafAfter);
            playerList = collList.toArray(new Player[collList.size()]);
            int degradeRange = deafAfter - degradeAfter;
            int step = Math.round(degradeRange / 3);
            List<Player> nearPlayers = getPlayersWithin(event.getPlayer(), degradeAfter);
            step1Players = getPlayersWithin(event.getPlayer(), degradeAfter + step);
            step2Players = getPlayersWithin(event.getPlayer(), degradeAfter + (step * 2));
            step3Players = getPlayersWithin(event.getPlayer(), degradeAfter + (step * 3));
            step3Players.removeAll(step2Players);
            step2Players.removeAll(step1Players);
            step1Players.removeAll(nearPlayers);
        } else {
            playerList = plugin.getServer().getOnlinePlayers();
        }
        if (first[0].equals(plugin.key)) {
            int len = chat.length();
            chat = chat.substring(3, len);
        }
        char[] chars = chat.toCharArray();
        int count = chars.length;
        int amount = Math.round(count / 3);
        List<Integer> numbers = new ArrayList<Integer>();
        for (int i = 0; i < count; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            if (plugin.encoder.containsKey(c)) {
                sb.append(plugin.encoder.get(c));
            } else {
                sb.append(c);
            }
        }
        String cipher = sb.toString();
        FPlayer fp = FPlayers.i.get(event.getPlayer());
        String fID = fp.getFactionId();
        for (Player p : playerList) {
            FPlayer onlineP = FPlayers.i.get(p);
            ChatColor factionColour = fp.getColorTo(onlineP);
            ChatColor opColour = (event.getPlayer().isOp()) ? ChatColor.RED : ChatColor.RESET;
            String pre = "<" + factionColour + fp.getChatTag() + " " + opColour + p.getName() + ChatColor.RESET + "> ";
            if ((onlineP.hasFaction() && onlineP.getFactionId().equals(fID)) || first[0].equals(plugin.key)) {
                // send the plain message
                if (use_chat_radius && !first[first.length-1].equals(plugin.yell)) {
                    char[] letters = chars;
                    if (step1Players != null && step1Players.contains(p)) {
                        chat = degradeChat(letters, amount, numbers);
                    }
                    if (step2Players != null && step2Players.contains(p)) {
                        chat = degradeChat(letters, (amount * 2), numbers);
                    }
                    if (step3Players != null && step3Players.contains(p)) {
                        chat = degradeChat(letters, (amount * 3), numbers);
                    }
                }
                p.sendMessage(pre + chat);
            } else {
                // send the cipher message
                if (use_chat_radius && !first[first.length-1].equals(plugin.yell)) {
                    char[] letters = cipher.toCharArray();
                    if (step1Players != null && step1Players.contains(p)) {
                        cipher = degradeChat(letters, amount, numbers);
                    }
                    if (step2Players != null && step2Players.contains(p)) {
                        cipher = degradeChat(letters, (amount * 2), numbers);
                    }
                    if (step3Players != null && step3Players.contains(p)) {
                        cipher = degradeChat(letters, (amount * 3), numbers);
                    }
                }
                p.sendMessage(pre + cipher);
            }
        }
        event.setCancelled(true);
    }

    public List<Player> getPlayersWithin(Player player, int distance) {
        List<Player> tmp = new ArrayList<Player>();
        int d2 = distance * distance;
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            if (p.getWorld() == player.getWorld() && p.getLocation().distanceSquared(player.getLocation()) <= d2) {
                tmp.add(p);
            }
        }
        return tmp;
    }

    private String degradeChat(char[] str, int amount, List<Integer> n) {
        StringBuilder degradeSB = new StringBuilder(str.length);
        String chat;
        for (int j = 0; j <= amount; j++) {
            str[n.get(j)] = '.';
        }
        for (char c : str) {
            degradeSB.append(c);
        }
        chat = degradeSB.toString();
        return chat;
    }
}
