package me.maplef.mapcdk.listeners;

import com.alibaba.fastjson2.util.Fnv;
import me.maplef.mapcdk.CDK;
import me.maplef.mapcdk.GUI.GUIHub;
import me.maplef.mapcdk.GUI.ItemHub;
import me.maplef.mapcdk.utils.Database;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import java.rmi.NoSuchObjectException;
import java.sql.SQLException;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClickListCDKGUI implements Listener {
    @EventHandler
    public void onClickListCDKGUI(InventoryClickEvent e) {
        PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
        if(!serializer.serialize(e.getView().title()).contains("CDK列表")) return;

        if(e.getSlotType().equals(InventoryType.SlotType.OUTSIDE) || e.getCurrentItem() == null) return;

        Player player = (Player) e.getWhoClicked();

        int page; String pageStr;
        String invTitle = serializer.serialize(e.getView().title());
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(invTitle);
        if(matcher.find()) {
            pageStr = matcher.group();
            page = Integer.parseInt(pageStr);
        } else {
            page = 0;
        }

        if(e.getCurrentItem().equals(ItemHub.getLISTCDK_NEXT())) {
            e.setCancelled(true);
            try {
                List<CDK> nextPageCDKs = Database.getCDKPageFromDatabase(page + 1);
                List<CDK> next2PageCDKs = Database.getCDKPageFromDatabase(page + 2);
                GUIHub.listCDK((Player) e.getWhoClicked(), nextPageCDKs, page + 1, !next2PageCDKs.isEmpty());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return;
        }

        if(e.getCurrentItem().equals(ItemHub.getLISTCDK_PREV())) {
            e.setCancelled(true);
            try {
                List<CDK> prevPageCDKs = Database.getCDKPageFromDatabase(page - 1);
                GUIHub.listCDK((Player) e.getWhoClicked(), prevPageCDKs, page - 1, true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        String cdkString = serializer.serialize(e.getCurrentItem().lore().get(0)).substring(5);

        if(e.getCurrentItem().getType().equals(Material.BOOK) || e.getCurrentItem().getType().equals(Material.GUNPOWDER)) {
            e.setCancelled(true);
            CDK cdk;
            try {
                cdk = new CDK(new Database().getC(), cdkString);
                player.sendMessage(cdk.toComponent());
            } catch (NoSuchObjectException | SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
