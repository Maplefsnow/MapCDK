package me.maplef.mapcdk.utils;

import com.google.common.base.Charsets;
import me.maplef.mapcdk.Mapcdk;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConfigManager {
    FileConfiguration config, messageConfig, autoReplyConfig;

    public ConfigManager(){
        config = YamlConfiguration.loadConfiguration(new File(Mapcdk.getInstance().getDataFolder(), "config.yml"));
        messageConfig = YamlConfiguration.loadConfiguration(new File(Mapcdk.getInstance().getDataFolder(), "messages.yml"));
        autoReplyConfig = YamlConfiguration.loadConfiguration(new File(Mapcdk.getInstance().getDataFolder(), "auto_reply.yml"));
    }

    public void reloadConfig(String configFileName){
        switch (configFileName) {
            case "config.yml" : {
                config = YamlConfiguration.loadConfiguration(new File(Mapcdk.getInstance().getDataFolder(), configFileName));

                InputStream defConfigStream = Mapcdk.getInstance().getResource(configFileName);
                if (defConfigStream == null) {
                    return;
                }

                config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
                break;
            }
            case "messages.yml" : {
                messageConfig = YamlConfiguration.loadConfiguration(new File(Mapcdk.getInstance().getDataFolder(), configFileName));

                InputStream defConfigStream = Mapcdk.getInstance().getResource(configFileName);
                if (defConfigStream == null) {
                    return;
                }

                messageConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
                break;
            }
            case "auto_reply.yml" : {
                autoReplyConfig = YamlConfiguration.loadConfiguration(new File(Mapcdk.getInstance().getDataFolder(), configFileName));

                InputStream defConfigStream = Mapcdk.getInstance().getResource(configFileName);
                if (defConfigStream == null) {
                    return;
                }

                autoReplyConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
                break;
            }
        }
    }

    public FileConfiguration getConfig(){
        return config;
    }

    public FileConfiguration getMessageConfig(){
        return messageConfig;
    }

    public FileConfiguration getAutoReplyConfig(){
        return autoReplyConfig;
    }
}
