package me.maplef.mapcdk.listeners;

import me.maplef.mapcdk.utils.CDKLib;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class CloseGUI implements Listener {
    @EventHandler
    public void onCloseGUI(InventoryCloseEvent e) {
        PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
        String invTitle = serializer.serialize(e.getView().title());

        // inventory title is MapCDK title and is closed by player
        if(invTitle.contains("MapCDK") && e.getReason().equals(InventoryCloseEvent.Reason.PLAYER)) {
            CDKLib.cdkMap.remove(e.getPlayer().getName());
        }
    }
}
