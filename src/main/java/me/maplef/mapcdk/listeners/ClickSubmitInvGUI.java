package me.maplef.mapcdk.listeners;

import me.maplef.mapcdk.GUI.GUIHub;
import me.maplef.mapcdk.GUI.ItemHub;
import me.maplef.mapcdk.utils.CDKLib;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.List;

public class ClickSubmitInvGUI implements Listener {
    @EventHandler
    public void onClickSubmitInvGUI(InventoryClickEvent e){
        if(!e.getView().title().equals(Component.text("MapCDK - 上传物品").color(NamedTextColor.BLACK))){
            return;
        }

        String clickerName = e.getWhoClicked().getName();

        if(e.getClickedInventory() == null || e.getClickedInventory().getType().equals(InventoryType.PLAYER)){
            return;
        }

        if(e.getCurrentItem() != null && e.getCurrentItem().getItemMeta().hasDisplayName()) {
            // submit
            if(e.getCurrentItem().equals(ItemHub.SUBMITINV_CONFIRM)){
                e.setCancelled(true);

                List<ItemStack> rewards = new ArrayList<>();
                for(ItemStack item : e.getClickedInventory().getContents()) {
                    if(item == null) continue;
                    if(item.equals(ItemHub.SUBMITINV_CONFIRM)) continue;

                    // detect command book
                    if(item.getType().equals(Material.WRITTEN_BOOK)) {
                        PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();

                        BookMeta bookMeta = (BookMeta) item.getItemMeta();
                        List<Component> pages = bookMeta.pages();

                        String title = serializer.serialize(bookMeta.title());
                        if(!title.equals("commands")) continue;

                        for(Component page : pages) {
                            String pageStr = serializer.serialize(page);
                            for(String str : pageStr.split("\n")) {
                                if(str.startsWith("/"))
                                    CDKLib.cdkMap.get(clickerName).addRewardCmd(str);
                            }
                        }

                        continue;
                    }
                    rewards.add(item);
                }
                CDKLib.cdkMap.get(clickerName).setRewardItems(rewards);

                GUIHub.newCDK((Player) e.getWhoClicked());
            }
        }
    }
}
