package me.maplef.mapcdk.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class ClickListCDKGUI implements Listener {
    @EventHandler
    public void onClickListCDKGUI(InventoryClickEvent e) {
        if(!e.getView().title().equals(Component.text("MapCDK - CDK列表").color(NamedTextColor.BLACK))){
            return;
        }
        if(e.getSlotType().equals(InventoryType.SlotType.OUTSIDE)) return;


    }
}
