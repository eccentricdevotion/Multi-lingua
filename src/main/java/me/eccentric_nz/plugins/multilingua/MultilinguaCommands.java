package me.eccentric_nz.plugins.multilingua;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MultilinguaCommands implements CommandExecutor {

    private Multilingua plugin;
    private List<String> firstArgs = new ArrayList<String>();

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
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("multilingua")) {
            if (!sender.hasPermission("multilingua.admin")) {
                sender.sendMessage("[Multi-lingua] You do not have permission to use this command!");
                return true;
            }
            if (!firstArgs.contains(args[0])) {
                sender.sendMessage("[Multi-lingua] That is not a valid config item!");
                return false;
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
                String str = "'" + args[1] + "'";
                plugin.getConfig().set("plain_text_key", str);
            }
            if (args[0].equalsIgnoreCase("yell_key")) {
                String str = "'" + args[1] + "'";
                plugin.getConfig().set("yell_key", str);
            }
            plugin.saveConfig();
            sender.sendMessage("[Multi-lingua] The config was updated!");
            return true;
        }
        return false;
    }
}
