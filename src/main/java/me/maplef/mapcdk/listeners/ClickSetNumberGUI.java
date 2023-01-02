package me.maplef.mapcdk.listeners;

import me.maplef.mapcdk.GUI.GUIHub;
import me.maplef.mapcdk.GUI.ItemHub;
import me.maplef.mapcdk.utils.CDKLib;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class ClickSetNumberGUI implements Listener {
    @EventHandler
    public void onClickSetNumberGUI(InventoryClickEvent e) {
        if(!e.getView().title().equals(Component.text("MapCDK - 设置CDK数量").color(NamedTextColor.BLACK))){
            return;
        }

        e.setCancelled(true);

        String clickerName = e.getWhoClicked().getName();

        if(e.getClickedInventory() == null || e.getClickedInventory().getType().equals(InventoryType.PLAYER)){
            return;
        }

        if(e.getCurrentItem() != null && e.getClickedInventory() != null) {
            switch (e.getSlot()) {
                case 26 -> {
                    GUIHub.newCDK((Player) e.getWhoClicked());
                }

                case 5 -> {
                    CDKLib.cdkMap.get(clickerName).addNumbersLeft(1);
                    e.getClickedInventory().setItem(13,
                            ItemHub.getSETNUMBER_DISPLAY(CDKLib.cdkMap.get(clickerName).getAmountLeft()));
                }
                case 14 -> {
                    CDKLib.cdkMap.get(clickerName).addNumbersLeft(10);
                    e.getClickedInventory().setItem(13,
                            ItemHub.getSETNUMBER_DISPLAY(CDKLib.cdkMap.get(clickerName).getAmountLeft()));
                }
                case 23 -> {
                    CDKLib.cdkMap.get(clickerName).addNumbersLeft(100);
                    e.getClickedInventory().setItem(13,
                            ItemHub.getSETNUMBER_DISPLAY(CDKLib.cdkMap.get(clickerName).getAmountLeft()));
                }

                case 3 -> {
                    try {
                        CDKLib.cdkMap.get(clickerName).addNumbersLeft(-1);
                        e.getClickedInventory().setItem(13,
                                ItemHub.getSETNUMBER_DISPLAY(CDKLib.cdkMap.get(clickerName).getAmountLeft()));
                    } catch (IllegalArgumentException ignored){}
                }
                case 12 -> {
                    try {
                        CDKLib.cdkMap.get(clickerName).addNumbersLeft(-10);
                        e.getClickedInventory().setItem(13,
                                ItemHub.getSETNUMBER_DISPLAY(CDKLib.cdkMap.get(clickerName).getAmountLeft()));
                    } catch (IllegalArgumentException ignored){}
                }
                case 21 -> {
                    try {
                        CDKLib.cdkMap.get(clickerName).addNumbersLeft(-100);
                        e.getClickedInventory().setItem(13,
                                ItemHub.getSETNUMBER_DISPLAY(CDKLib.cdkMap.get(clickerName).getAmountLeft()));
                    } catch (IllegalArgumentException ignored){}
                }
            }
        }
    }
}
