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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ClickSetExpTimeGUI implements Listener {
    @EventHandler
    public void onClickSetExpTimeGUI(InventoryClickEvent e) {
        if(!e.getView().title().equals(Component.text("MapCDK - 设置过期时间").color(NamedTextColor.BLACK))) {
            return;
        }

        e.setCancelled(true);

        String clickerName = e.getWhoClicked().getName();

        if(e.getClickedInventory() == null || e.getClickedInventory().getType().equals(InventoryType.PLAYER)){
            return;
        }

        if(e.getCurrentItem() != null && e.getClickedInventory() != null) {
            switch (e.getSlot()) {
                case 26 : {
                    GUIHub.newCDK((Player) e.getWhoClicked());
                    return;
                }

                case 3 : {
                    try {
                        CDKLib.cdkMap.get(clickerName).minusExpireTime(1, ChronoUnit.HOURS);
                    } catch (IllegalArgumentException ignored) {}
                    break;
                }
                case 12 : {
                    try {
                        CDKLib.cdkMap.get(clickerName).minusExpireTime(1, ChronoUnit.MINUTES);
                    } catch (IllegalArgumentException ignored) {}
                    break;
                }
                case 21 : {
                    try {
                        CDKLib.cdkMap.get(clickerName).minusExpireTime(1, ChronoUnit.SECONDS);
                    } catch (IllegalArgumentException ignored) {}
                    break;
                }
                case 2 : {
                    try {
                        CDKLib.cdkMap.get(clickerName).minusExpireTime(1, ChronoUnit.YEARS);
                    } catch (IllegalArgumentException ignored) {}
                    break;
                }
                case 11 : {
                    try {
                        CDKLib.cdkMap.get(clickerName).minusExpireTime(1, ChronoUnit.MONTHS);
                    } catch (IllegalArgumentException ignored) {}
                    break;
                }
                case 20 : {
                    try {
                        CDKLib.cdkMap.get(clickerName).minusExpireTime(1, ChronoUnit.DAYS);
                    } catch (IllegalArgumentException ignored) {}
                    break;
                }

                case 5 : {
                    CDKLib.cdkMap.get(clickerName).addExpireTime(1, ChronoUnit.HOURS);
                    break;
                }
                case 14 : {
                    CDKLib.cdkMap.get(clickerName).addExpireTime(1, ChronoUnit.MINUTES);
                    break;
                }
                case 23 : {
                    CDKLib.cdkMap.get(clickerName).addExpireTime(1, ChronoUnit.SECONDS);
                    break;
                }
                case 6 : {
                    CDKLib.cdkMap.get(clickerName).addExpireTime(1, ChronoUnit.YEARS);
                    break;
                }
                case 15 : {
                    CDKLib.cdkMap.get(clickerName).addExpireTime(1, ChronoUnit.MONTHS);
                    break;
                }
                case 24 : {
                    CDKLib.cdkMap.get(clickerName).addExpireTime(1, ChronoUnit.DAYS);
                    break;
                }

                case 10 : {
                    CDKLib.cdkMap.get(clickerName).setExpireTime(LocalDateTime.now());
                    break;
                }
                case 16 : {
                    CDKLib.cdkMap.get(clickerName).setExpireTime(LocalDateTime.of(2099, 12, 31, 23, 59));
                    break;
                }
            }

            e.getInventory().setItem(13,
                    ItemHub.getEXPTIME_DISPLAY(CDKLib.cdkMap.get(clickerName).getExpireTime()));
        }
    }
}
