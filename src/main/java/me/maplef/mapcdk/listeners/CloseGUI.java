package me.maplef.mapcdk.listeners;

import me.maplef.mapcdk.utils.CU;
import me.maplef.mapcdk.utils.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class CloseGUI implements Listener {
    ConfigManager configManager = new ConfigManager();
    FileConfiguration config = configManager.getConfig();

    @EventHandler
    public void onCloseGUI(InventoryCloseEvent e) {
        PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
        String invTitle = serializer.serialize(e.getView().title());

        // inventory title is MapCDK title and is closed by player
        if(invTitle.contains("MapCDK") && e.getReason().equals(InventoryCloseEvent.Reason.PLAYER)) {
            e.getPlayer().sendMessage(Component.text(CU.t(config.getString("message-prefix", "&e[MapCDK] ")))
                    .append(Component.text("所有更改均已自动保存，使用 /mapcdk continue 以继续").color(NamedTextColor.GREEN)));
        }
    }
}
