package me.eccentric_nz.plugins.multilingua;

import java.io.File;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Multilingua extends JavaPlugin {

    PluginManager pm = Bukkit.getServer().getPluginManager();
    public MultilinguaChatListener chatListener;
    public MultilinguaCommandListener commandListener;
    private MultilinguaCommands adminCommand;
    public String key;
    public String yell;
    MultilinguaDatabase service = MultilinguaDatabase.getInstance();

    @Override
    public void onDisable() {
        saveConfig();
    }

    @Override
    public void onEnable() {
        if (!setupFactions()) {
            pm.disablePlugin(this);
            return;
        }
        if (!getDataFolder().exists()) {
            if (!getDataFolder().mkdir()) {
                System.err.println(MultilinguaConstants.MY_PLUGIN_NAME + "Could not create directory!");
                System.out.println(MultilinguaConstants.MY_PLUGIN_NAME + "Requires you to manually make the Multilingua/ directory!");
            }
            getDataFolder().setWritable(true);
            getDataFolder().setExecutable(true);
        }
        this.saveDefaultConfig();
        chatListener = new MultilinguaChatListener(this);
        commandListener = new MultilinguaCommandListener(this);
        getServer().getPluginManager().registerEvents(chatListener, this);
        getServer().getPluginManager().registerEvents(commandListener, this);
        adminCommand = new MultilinguaCommands(this);
        getCommand("multilingua").setExecutor(adminCommand);

        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException e) {
            // Failed to submit the stats :-(
        }

        try {
            String path = getDataFolder() + File.separator + "multilingua.db";
            service.setConnection(path);
            service.createTables();
        } catch (Exception e) {
            debug("Connection and Tables Error: " + e);
        }

        key = "[" + getConfig().getString("plain_text_key") + "]";
        yell = "[" + getConfig().getString("yell_key") + "]";
    }

    private boolean setupFactions() {
        Plugin x = pm.getPlugin("Factions");
        if (x != null) {
            //factions = (Factions) x;
            return true;
        } else {
            System.err.println("[Multi-lingua] Factions is required, but wasn't found!");
            System.err.println("[Multi-lingua] Download it from http://dev.bukkit.org/server-mods/factions/");
            System.err.println("[Multi-lingua] Disabling plugin.");
            return false;
        }
    }

    public void debug(Object o) {
        if (getConfig().getBoolean("debug") == true) {
            System.out.println("[Multi-lingua Debug] " + o);
        }
    }
}