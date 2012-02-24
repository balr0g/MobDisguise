package me.desmin88.mobdisguise;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.desmin88.mobdisguise.commands.MDCommand;
import me.desmin88.mobdisguise.listeners.MDEntityListener;
import me.desmin88.mobdisguise.listeners.MDPlayerListener;
import me.desmin88.mobdisguise.utils.Disguise;
import me.desmin88.mobdisguise.utils.Disguise.MobType;
import me.desmin88.mobdisguise.utils.DisguiseTask;
import me.desmin88.mobdisguise.utils.PacketUtils;
import net.minecraft.server.DataWatcher;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MobDisguise extends JavaPlugin {
    public static Set<String> disList = new HashSet<String>();
    public static Set<String> apiList = new HashSet<String>();
    public static Map<String, Disguise> playerMobDis = new HashMap<String, Disguise>();
    // Player -> Datawatcher
    public static Map<String, DataWatcher> data = new HashMap<String, DataWatcher>();

    //public static Set<String> baby = new HashSet<String>();
    // Player disguising -> player disguised as
    public static Map<String, String> p2p = new HashMap<String, String>();
    public static Set<String> playerdislist = new HashSet<String>();
    // end
    public static Set<Integer> playerEntIds = new HashSet<Integer>();
    public static PacketUtils pu = new PacketUtils();
    public static Set<String> telelist = new HashSet<String>();
    // public final PacketListener packetlistener = new PacketListener(this);
    public final MDPlayerListener playerlistener = new MDPlayerListener(this);
    public final MDEntityListener entitylistener = new MDEntityListener(this);
    public static final String pref = "[MobDisguise] ";
    public static FileConfiguration cfg;
    public static boolean perm;
    public static PluginDescriptionFile pdf;

    @Override
    public void onDisable() {
        this.getServer().getScheduler().cancelTasks(this);
        System.out.println("[" + pdf.getName() + "]" + " by " + pdf.getAuthors().get(0) + " version " + pdf.getVersion() + " disabled.");

    }

    @Override
    public void onEnable() {
        pdf = this.getDescription();
        // Begin config code
        if (!new File(getDataFolder(), "config.yml").exists()) {
            try {
                getDataFolder().mkdir();
                new File(getDataFolder(), "config.yml").createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(pref + "Error making config.yml?!");
                getServer().getPluginManager().disablePlugin(this); // Cleanup
                return;
            }
        }
        cfg = this.getConfig(); // Get config

        if (cfg.getKeys(true).isEmpty()) { // Config hasn't been made
            System.out.println(pref + "config.yml not found, making with default values");
            cfg.set("RealDrops.enabled", false);
            cfg.set("Permissions.enabled", true);
            cfg.set("MobTarget.enabled", true);
            cfg.set("DisableItemPickup", true);
            for (String mobtype : MobType.types) {
            //    cfg.setHeader("#Setting a mobtype to false will not allow a player to disguise as that type");
                cfg.set("Blacklist." + mobtype, true); // Just making
            }
            this.saveConfig();
        }
        if (cfg.get("MobTarget.enabled") == null || cfg.get("DisableItemPickup.enabled") == null) {
            cfg.set("MobTarget.enabled", true);
            cfg.set("DisableItemPickup.enabled", true);
            this.saveConfig();
        }
        if (cfg.get("Blacklist.enderman") == null) {
            cfg.set("Blacklist.enderman", true);
            cfg.set("Blacklist.silverfish", true);
            cfg.set("Blacklist.cavespider", true);
        }

        this.saveConfig();
        perm = cfg.getBoolean("Permissions.enabled", true);

        PluginManager pm = getServer().getPluginManager();
        this.getCommand("md").setExecutor(new MDCommand(this));

        pm.registerEvents(playerlistener, this);
        pm.registerEvents(entitylistener, this);

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new DisguiseTask(this), 1200, 1200);

        System.out.println("[" + pdf.getName() + "]" + " by " + pdf.getAuthors().get(0) + " version " + pdf.getVersion() + " enabled.");
    }
}