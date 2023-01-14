package me.maplef.mapcdk;

import me.maplef.mapcdk.listeners.*;
import me.maplef.mapcdk.utils.ConfigManager;
import me.maplef.mapcdk.utils.Database;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Objects;

public final class Mapcdk extends JavaPlugin {

    private static Mapcdk instance;

    private ConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;

        if(!getDataFolder().exists()){
            Bukkit.getServer().getLogger().warning(String.format("[%s] 未检测到配置文件，正在生成新的配置文件...", getDescription().getName()));
            this.saveDefaultConfig();
            configManager = new ConfigManager();
        }

        try {
            new Database().init();
        } catch (SQLException e){
            e.printStackTrace();
        }

        this.getServer().getPluginManager().registerEvents(new ClickNewCDKGUI(), this);
        this.getServer().getPluginManager().registerEvents(new ClickSubmitInvGUI(), this);
        this.getServer().getPluginManager().registerEvents(new ClickManageCmdGUI(), this);
        this.getServer().getPluginManager().registerEvents(new ClickSetNumberGUI(), this);
        this.getServer().getPluginManager().registerEvents(new ClickSetExpTimeGUI(), this);
        this.getServer().getPluginManager().registerEvents(new CloseGUI(), this);
        this.getServer().getPluginManager().registerEvents(new ClickListCDKGUI(), this);

        Objects.requireNonNull(getCommand("mapcdk")).setExecutor(new me.maplef.mapcdk.commands.Mapcdk());
        Objects.requireNonNull(getCommand("mapcdk")).setTabCompleter(new me.maplef.mapcdk.commands.Mapcdk());

        this.getServer().getLogger().info(String.format("[%s] %s",
                this.getDescription().getName(),
                "MapCDK is started."));
    }

    @Override
    public void onDisable() {
        this.getServer().getLogger().info(String.format("[%s] %s",
                this.getDescription().getName(),
                "MapCDK is stopped, thanks for using!"));
    }

    public ConfigManager getConfigManager() {return configManager;}

    public static Mapcdk getInstance(){return instance;}
}
