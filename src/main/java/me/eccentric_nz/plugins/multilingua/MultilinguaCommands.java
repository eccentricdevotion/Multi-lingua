package me.eccentric_nz.plugins.multilingua;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Role;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MultilinguaCommands implements CommandExecutor {

    private Multilingua plugin;
    private List<String> firstArgs = new ArrayList<String>();
    MultilinguaDatabase service = MultilinguaDatabase.getInstance();

    public MultilinguaCommands(Multilingua plugin) {
        this.plugin = plugin;
        firstArgs.add("plain_text_key");
        firstArgs.add("yell_key");
        firstArgs.add("use_chat_radius");
        firstArgs.add("chat_degrade_after");
        firstArgs.add("chat_deaf_after");
        firstArgs.add("dizzy_after_yell");
        firstArgs.add("dizzy_ticks");
        firstArgs.add("dizzy_hunger");
        firstArgs.add("config");
        firstArgs.add("add");
        firstArgs.add("remove");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("multilingua")) {
            Player player = null;
            if (sender instanceof Player) {
                player = (Player) sender;
            }
            if (args[0].equalsIgnoreCase("add")) {
                if (player == null) {
                    sender.sendMessage("[Multi-lingua] Command must be run by a player!");
                    return true;
                }
                FPlayer fp = FPlayers.i.get(player);
                if (!fp.hasFaction()) {
                    sender.sendMessage("[Multi-lingua] You must have joined a faction before running this command!");
                    return true;
                }
                if (!fp.getRole().equals(Role.ADMIN)) {
                    sender.sendMessage("[Multi-lingua] You must be a faction ADMIN to run this command!");
                    return true;
                }
                String id = fp.getFactionId();
                String queryAdd = "INSERT INTO multilingua (faction_id) VALUES ('" + id + "')";
                Statement statement = null;
                try {
                    Connection connection = service.getConnection();
                    statement = connection.createStatement();
                    statement.executeUpdate(queryAdd);
                } catch (SQLException e) {
                    sender.sendMessage("[Multi-lingua] There was a problem creating the faction language!");
                    return true;
                } finally {
                    try {
                        statement.close();
                    } catch (Exception e) {
                    }
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("remove")) {
                if (player == null) {
                    sender.sendMessage("[Multi-lingua] Command must be run by a player!");
                    return true;
                }
                FPlayer fp = FPlayers.i.get(player);
                if (!fp.hasFaction()) {
                    sender.sendMessage("[Multi-lingua] You must have joined a faction before running this command!");
                    return true;
                }
                if (!fp.getRole().equals(Role.ADMIN)) {
                    sender.sendMessage("[Multi-lingua] You must be a faction ADMIN to run this command!");
                    return true;
                }
                String id = fp.getFactionId();
                String queryRemove = "DELETE FROM multilingua WHERE faction_id = '" + id + "'";
                Statement statement = null;
                try {
                    Connection connection = service.getConnection();
                    statement = connection.createStatement();
                    statement.executeUpdate(queryRemove);
                } catch (SQLException e) {
                    sender.sendMessage("[Multi-lingua] There was a problem removing the faction language!");
                    return true;
                } finally {
                    try {
                        statement.close();
                    } catch (Exception e) {
                    }
                }
                return true;
            }
            if (!sender.hasPermission("multilingua.admin")) {
                sender.sendMessage("[Multi-lingua] You do not have permission to use this command!");
                return true;
            }
            if (!firstArgs.contains(args[0])) {
                sender.sendMessage("[Multi-lingua] That is not a valid config item!");
                return false;
            }
            if (args[0].equalsIgnoreCase("config")) {
                Set<String> configNames = plugin.getConfig().getKeys(false);
                sender.sendMessage("[Multi-lingua]" + ChatColor.RED + " Here are the current plugin config options!");
                for (String cname : configNames) {
                    String value = plugin.getConfig().getString(cname);
                    sender.sendMessage(ChatColor.AQUA + cname + ": " + ChatColor.RESET + value);
                }
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage("[Multi-lingua] Not enough command arguments!");
                return false;
            }
            if (args[0].equalsIgnoreCase("use_chat_radius")) {
                String tf = args[1].toLowerCase();
                if (!tf.equals("true") && !tf.equals("false")) {
                    sender.sendMessage("[Multi-lingua] The last argument must be true or false!");
                    return true;
                }
                plugin.getConfig().set("use_chat_radius", Boolean.valueOf(tf));
            }
            if (args[0].equalsIgnoreCase("dizzy_after_yell")) {
                String tf = args[1].toLowerCase();
                if (!tf.equals("true") && !tf.equals("false")) {
                    sender.sendMessage("[Multi-lingua] The last argument must be true or false!");
                    return true;
                }
                plugin.getConfig().set("dizzy_after_yell", Boolean.valueOf(tf));
            }
            if (args[0].equalsIgnoreCase("chat_degrade_after")) {
                String a = args[1];
                int val;
                try {
                    val = Integer.parseInt(a);
                } catch (NumberFormatException nfe) {
                    // not a number
                    sender.sendMessage("[Multi-lingua] The last argument must be a number!");
                    return false;
                }
                plugin.getConfig().set("chat_degrade_after", val);
            }
            if (args[0].equalsIgnoreCase("chat_deaf_after")) {
                String a = args[1];
                int val;
                try {
                    val = Integer.parseInt(a);
                } catch (NumberFormatException nfe) {
                    // not a number
                    sender.sendMessage("[Multi-lingua] The last argument must be a number!");
                    return false;
                }
                plugin.getConfig().set("chat_deaf_after", val);
            }
            if (args[0].equalsIgnoreCase("dizzy_ticks")) {
                String a = args[1];
                int val;
                try {
                    val = Integer.parseInt(a);
                } catch (NumberFormatException nfe) {
                    // not a number
                    sender.sendMessage("[Multi-lingua] The last argument must be a number!");
                    return false;
                }
                plugin.getConfig().set("dizzy_ticks", val);
            }
            if (args[0].equalsIgnoreCase("dizzy_hunger")) {
                String a = args[1];
                int val;
                try {
                    val = Integer.parseInt(a);
                } catch (NumberFormatException nfe) {
                    // not a number
                    sender.sendMessage("[Multi-lingua] The last argument must be a number!");
                    return false;
                }
                plugin.getConfig().set("dizzy_hunger", val);
            }
            if (args[0].equalsIgnoreCase("plain_text_key")) {
                // sanitise the string
                plugin.getConfig().set("plain_text_key", args[1]);
            }
            if (args[0].equalsIgnoreCase("yell_key")) {
                plugin.getConfig().set("yell_key", args[1]);
            }
            plugin.saveConfig();
            sender.sendMessage("[Multi-lingua] The config was updated!");
            return true;
        }
        return false;
    }
}