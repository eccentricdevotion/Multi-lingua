package me.eccentric_nz.plugins.multilingua;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MultilinguaChatListener implements Listener {

    private Multilingua plugin;
    private HashMap<Character, Character> encoder;
    MultilinguaDatabase service = MultilinguaDatabase.getInstance();
    ConsoleCommandSender console;

    public MultilinguaChatListener(Multilingua plugin) {
        this.plugin = plugin;
        console = plugin.getServer().getConsoleSender();
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        boolean use_chat_radius = plugin.getConfig().getBoolean("use_chat_radius");
        Player[] playerList;
        List<Player> step1Players = null;
        List<Player> step2Players = null;
        List<Player> step3Players = null;
        String chat = event.getMessage();
        String cipher = "";
        String pre;
        String[] first = chat.split(" ");
        // remove the plain text prefix if present
        if (first[0].equals(plugin.key)) {
            int key = first[0].length();
            int len = chat.length();
            chat = chat.substring(key, len).trim();
        }
        // remove the yell suffix if present
        if (first[first.length - 1].equals(plugin.yell)) {
            int yell = first[0].length();
            int len = chat.length() - yell;
            chat = chat.substring(0, len).trim();
        }
        char[] chatchars = chat.toCharArray();
        int count = chatchars.length;
        int amount = Math.round(count / 4);
        if (use_chat_radius && !first[first.length - 1].equals(plugin.yell)) {
            int degradeAfter = plugin.getConfig().getInt("chat_degrade_after");
            int deafAfter = plugin.getConfig().getInt("chat_deaf_after");
            List<Player> collList = getPlayersWithin(event.getPlayer(), deafAfter);
            playerList = collList.toArray(new Player[collList.size()]);
            int degradeRange = deafAfter - degradeAfter;
            int step = Math.round(degradeRange / 3);
            List<Player> nearPlayers = getPlayersWithin(event.getPlayer(), degradeAfter);
            step1Players = getPlayersWithin(event.getPlayer(), degradeAfter + step);
            step2Players = getPlayersWithin(event.getPlayer(), degradeAfter + (step * 2));
            step3Players = getPlayersWithin(event.getPlayer(), deafAfter);
            step3Players.removeAll(step2Players);
            step2Players.removeAll(step1Players);
            step1Players.removeAll(nearPlayers);
        } else {
            playerList = plugin.getServer().getOnlinePlayers();
        }
        FPlayer fp = FPlayers.i.get(event.getPlayer());
        String fID = "";
        if (fp.hasFaction()) {
            fID = fp.getFactionId();
            String query = "SELECT faction_id FROM multilingua WHERE faction_id = '" + fID + "'";
            Statement statement = null;
            ResultSet rs = null;
            try {
                Connection connection = service.getConnection();
                statement = connection.createStatement();
                rs = statement.executeQuery(query);
                if (rs.isBeforeFirst()) {
                    encoder = new HashMap<Character, Character>();
                    char[] shuffled = MultilinguaConstants.shuffle(MultilinguaConstants.cipher).toCharArray();
                    int i = 0;
                    for (char c : MultilinguaConstants.chars) {
                        encoder.put(c, shuffled[i]);
                        i++;
                    }
                    StringBuilder sb = new StringBuilder();
                    for (char c : chatchars) {
                        if (encoder.containsKey(c)) {
                            sb.append(encoder.get(c));
                        } else {
                            sb.append(c);
                        }
                    }
                    cipher = sb.toString();
                }
            } catch (SQLException e) {
                player.sendMessage("[Multi-lingua] There was a problem finding the faction language!");
            } finally {
                try {
                    rs.close();
                    statement.close();
                } catch (Exception e) {
                }
            }
        }
        List<Integer> numbers = new ArrayList<Integer>();
        for (int j = 0; j < count; j++) {
            numbers.add(j);
        }
        Collections.shuffle(numbers);
        for (Player p : playerList) {
            // reset chat!
            String playerchat = chat;
            String playercipher = cipher;
            FPlayer onlineP = FPlayers.i.get(p);
            ChatColor factionColour = fp.getColorTo(onlineP);
            ChatColor opColour = (player.isOp()) ? ChatColor.RED : ChatColor.RESET;
            String space = (fp.hasFaction()) ? " " : "";
            pre = "<" + factionColour + fp.getChatTag() + space + opColour + player.getName() + ChatColor.RESET + "> ";
            // console always gets the plain message...
            console.sendMessage(pre + chat);
            if ((onlineP.hasFaction() && onlineP.getFactionId().equals(fID)) || first[0].equals(plugin.key) || !fp.hasFaction()) {
                // send the plain message
                if (use_chat_radius && !first[first.length - 1].equals(plugin.yell)) {
                    char[] letters = playerchat.toCharArray();
                    if (step1Players != null && step1Players.contains(p)) {
                        playerchat = degradeChat(letters, amount, numbers);
                    }
                    if (step2Players != null && step2Players.contains(p)) {
                        playerchat = degradeChat(letters, (amount * 2), numbers);
                    }
                    if (step3Players != null && step3Players.contains(p)) {
                        playerchat = degradeChat(letters, (amount * 3), numbers);
                    }
                }
                p.sendMessage(pre + playerchat);
            } else {
                // send the cipher message
                if (use_chat_radius && !first[first.length - 1].equals(plugin.yell)) {
                    char[] letters = playercipher.toCharArray();
                    if (step1Players != null && step1Players.contains(p)) {
                        playercipher = degradeChat(letters, amount, numbers);
                    }
                    if (step2Players != null && step2Players.contains(p)) {
                        playercipher = degradeChat(letters, (amount * 2), numbers);
                    }
                    if (step3Players != null && step3Players.contains(p)) {
                        playercipher = degradeChat(letters, (amount * 3), numbers);
                    }
                }
                p.sendMessage(pre + playercipher);
            }
        }
        event.setCancelled(true);

        if (first[first.length - 1].equals(plugin.yell) && plugin.getConfig().getBoolean("dizzy_after_yell")) {
            // remove hunger and make dizzy
            player.setFoodLevel(player.getFoodLevel() - plugin.getConfig().getInt("dizzy_hunger"));
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, plugin.getConfig().getInt("dizzy_ticks"), 10));
        }
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