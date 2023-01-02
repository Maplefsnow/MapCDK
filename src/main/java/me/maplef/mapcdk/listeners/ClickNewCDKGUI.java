package me.maplef.mapcdk.listeners;

import me.maplef.mapcdk.GUI.GUIHub;
import me.maplef.mapcdk.GUI.ItemHub;
import me.maplef.mapcdk.Mapcdk;
import me.maplef.mapcdk.utils.CDKLib;
import me.maplef.mapcdk.utils.Database;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class ClickNewCDKGUI implements Listener {
    @EventHandler
    public void onClicknewCDKGUI(InventoryClickEvent e) {
        if(!e.getView().title().equals(Component.text("MapCDK - 新建CDK").color(NamedTextColor.BLACK))) {
            return;
        }

        e.setCancelled(true);

        if(e.getCurrentItem() == null && e.getClickedInventory() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)){
            e.getClickedInventory().setItem(9, ItemHub.NEWCDK_QUIT);
        }

        if(e.getCurrentItem() != null && e.getClickedInventory() != null) {
            if(!e.getCurrentItem().equals(ItemHub.NEWCDK_CONFIRMQUIT)){
                e.getClickedInventory().setItem(9, ItemHub.NEWCDK_QUIT);
            }

            switch (e.getSlot()) {
                case 0 -> {
                    GUIHub.submitInv((Player) e.getWhoClicked());
                }
                case 1 -> {
                    GUIHub.manageCmd((Player) e.getWhoClicked());
                }
                case 2 -> {
                    GUIHub.setNumber((Player) e.getWhoClicked());
                }
                case 3 -> {
                    GUIHub.setExpTime((Player) e.getWhoClicked());
                }
                case 17 -> {
                    try {
                        CDKLib.cdkMap.get(e.getWhoClicked().getName()).exportToDataBase(new Database().getC());
                        CDKLib.cdkMap.get(e.getWhoClicked().getName()).exportToJSON(Mapcdk.getInstance().getDataFolder());
                        e.getClickedInventory().close();

                        Component successMsg = Component.text("[MapCDK] ").color(NamedTextColor.GREEN);

                        successMsg = successMsg.append(Component.text("CDK 创建成功，CDK：").color(NamedTextColor.YELLOW));

                        HoverEvent<Component> showTextHover = HoverEvent.showText(Component.text("点此复制到剪贴板"));
                        successMsg = successMsg.append(Component.text("[ " + CDKLib.cdkMap.get(e.getWhoClicked().getName()).getCdkString() + " ]")
                                .color(NamedTextColor.AQUA))
                                .clickEvent(ClickEvent.copyToClipboard(CDKLib.cdkMap.get(e.getWhoClicked().getName()).getCdkString()))
                                .hoverEvent(showTextHover);

                        e.getWhoClicked().sendMessage(successMsg);

                        CDKLib.cdkMap.remove(e.getWhoClicked().getName());
                    } catch (SQLException | IOException ex) {
                        ex.printStackTrace();
                    }
                }
                case 9 -> {
                    if(e.getCurrentItem().equals(ItemHub.NEWCDK_QUIT)) {
                        e.getClickedInventory().setItem(9, ItemHub.NEWCDK_CONFIRMQUIT);
                    } else if (e.getCurrentItem().equals(ItemHub.NEWCDK_CONFIRMQUIT)) {
                        if(e.getClick().equals(ClickType.SHIFT_LEFT)){
                            e.setCancelled(true);
                            e.getClickedInventory().close();
                        }
                    }
                }
            }
        }
    }
}
