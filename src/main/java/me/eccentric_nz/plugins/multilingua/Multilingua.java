package me.eccentric_nz.plugins.multilingua;

import com.massivecraft.factions.Factions;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Multilingua extends JavaPlugin {

    PluginManager pm = Bukkit.getServer().getPluginManager();
    private Factions factions;
    public MultilinguaChatListener chatListener;

    @Override
    public void onDisable() {
        saveConfig();
    }

    @Override
    public void onEnable() {
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
        getServer().getPluginManager().registerEvents(chatListener, this);

        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException e) {
            // Failed to submit the stats :-(
        }
    }

    private boolean setupVault() {
        Plugin x = pm.getPlugin("Vault");
        if (x != null && x instanceof Factions) {
            factions = (Factions) x;
            return true;
        } else {
            System.err.println("Vault is required for economy, but wasn't found!");
            System.err.println("Download it from http://dev.bukkit.org/server-mods/vault/");
            System.err.println("Disabling plugin.");
            return false;
        }
    }

    public void debug(Object o) {
        if (getConfig().getBoolean("debug") == true) {
            System.out.println("[Multi-lingua Debug] " + o);
        }
    }
}
