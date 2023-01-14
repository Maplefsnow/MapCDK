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
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;


public class ClickManageCmdGUI implements Listener {
    @EventHandler
    public void onClickManageCmdGUI(InventoryClickEvent e) {
        if(!e.getView().title().equals(Component.text("MapCDK - 管理CDK命令").color(NamedTextColor.BLACK))){
            return;
        }
        if(e.getCurrentItem() == null){
            if (!e.getCursor().equals(ItemHub.MANAGECMD_BOOK) && !e.getCursor().getType().equals(Material.PAPER)) {
                e.setCancelled(true);
            }
            return;
        }
        if(!e.getCurrentItem().equals(ItemHub.MANAGECMD_BOOK) && !e.getCurrentItem().getType().equals(Material.PAPER) &&
            !e.getCursor().equals(ItemHub.MANAGECMD_BOOK) && !e.getCursor().getType().equals(Material.PAPER) &&
            !e.getCurrentItem().equals(ItemHub.MANAGECMD_DELETE) && !e.getCurrentItem().equals(ItemHub.MANAGECMD_CONFIRM)){
            e.setCancelled(true);
            return;
        }

        if(e.getSlot() == 35) {
            e.setCancelled(true);

            PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
            List<String> cmds = new ArrayList<>();
            for(ItemStack cmdPaper : e.getClickedInventory().getContents()) {
                if(cmdPaper == null || cmdPaper.equals(ItemHub.MANAGECMD_CONFIRM) || cmdPaper.equals(ItemHub.MANAGECMD_DELETE)) continue;
                if(cmdPaper.getItemMeta().hasDisplayName())
                    cmds.add(serializer.serialize(cmdPaper.getItemMeta().displayName()));
            }
            CDKLib.cdkMap.get(e.getWhoClicked().getName()).setRewardCmds(cmds);

            GUIHub.newCDK((Player) e.getWhoClicked());
        } else if(e.getSlot() == 27) {
            e.setCancelled(true);
        }
    }
}
