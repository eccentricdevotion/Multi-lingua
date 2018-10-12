package me.eccentric_nz.multilingua;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Multilingua extends JavaPlugin {

    PluginManager pm = Bukkit.getServer().getPluginManager();
    private String key;
    private String yell;
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
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new MultilinguaChatListener(this), this);
        getServer().getPluginManager().registerEvents(new MultilinguaCommandListener(this), this);
        getCommand("multilingua").setExecutor(new MultilinguaCommands(this));

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

    public String getKey() {
        return key;
    }

    public String getYell() {
        return yell;
    }
}
