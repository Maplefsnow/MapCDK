package me.maplef.mapcdk.GUI;

import me.maplef.mapcdk.CDK;
import me.maplef.mapcdk.utils.CDKLib;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.swing.text.StyledEditorKit;
import java.time.LocalDateTime;
import java.util.List;

public class GUIHub {
    public static void newCDK(Player player) {
        Inventory GUI = Bukkit.createInventory(player, 18, Component.text("MapCDK - 新建CDK").color(NamedTextColor.BLACK));

        GUI.setItem(0, ItemHub.NEWCDK_SUBMITINV);
        GUI.setItem(1, ItemHub.NEWCDK_SUBMITCMD);
        GUI.setItem(2, ItemHub.NEWCDK_SETNUMBER);
        GUI.setItem(3, ItemHub.NEWCDK_SETEXPTIME);

        GUI.setItem(9, ItemHub.NEWCDK_QUIT);
        GUI.setItem(17, ItemHub.NEWCDK_SUBMIT);

        player.openInventory(GUI);
    }

    public static void submitInv(Player player) {
        Inventory GUI = Bukkit.createInventory(player, 36, Component.text("MapCDK - 上传物品").color(NamedTextColor.BLACK));

        int pos = 0;
        for(ItemStack item : CDKLib.cdkMap.get(player.getName()).getRewardItems()){
            GUI.setItem(pos, item);
            pos++;
        }

        GUI.setItem(35, ItemHub.SUBMITINV_CONFIRM);

        player.openInventory(GUI);
    }

    public static void manageCmd(Player player) {
        Inventory GUI = Bukkit.createInventory(player, 36, Component.text("MapCDK - 管理CDK命令").color(NamedTextColor.BLACK));

        if(CDKLib.cdkMap.get(player.getName()).getRewardCmds().isEmpty()) {
            GUI.setItem(0, ItemHub.MANAGECMD_BOOK);
        } else {
            GUI.setItem(0, ItemHub.MANAGECMD_BOOK);
            int pos = 1;
            for(String cmd : CDKLib.cdkMap.get(player.getName()).getRewardCmds()) {
                GUI.setItem(pos, ItemHub.getMANEGECMD_CMDPAPER(cmd));
                pos++;
            }
        }

        GUI.setItem(27, ItemHub.MANAGECMD_DELETE);
        GUI.setItem(35, ItemHub.MANAGECMD_CONFIRM);

        player.openInventory(GUI);
    }

    public static void setExpTime(Player player) {
        Inventory GUI = Bukkit.createInventory(player, 27, Component.text("MapCDK - 设置过期时间").color(NamedTextColor.BLACK));

        GUI.setItem(3, ItemHub.EXPTIME_DEC_HOR);
        GUI.setItem(12, ItemHub.EXPTIME_DEC_MIN);
        GUI.setItem(21, ItemHub.EXPTIME_DEC_SEC);
        GUI.setItem(2, ItemHub.EXPTIME_DEC_YER);
        GUI.setItem(11, ItemHub.EXPTIME_DEC_MON);
        GUI.setItem(20, ItemHub.EXPTIME_DEC_DAY);

        GUI.setItem(5, ItemHub.EXPTIME_ADD_HOR);
        GUI.setItem(14, ItemHub.EXPTIME_ADD_MIN);
        GUI.setItem(23, ItemHub.EXPTIME_ADD_SEC);
        GUI.setItem(6, ItemHub.EXPTIME_ADD_YER);
        GUI.setItem(15, ItemHub.EXPTIME_ADD_MON);
        GUI.setItem(24, ItemHub.EXPTIME_ADD_DAY);

        GUI.setItem(16, ItemHub.EXPTIME_SET_PERM);
        GUI.setItem(10, ItemHub.EXPTIME_RESET);

        GUI.setItem(13, ItemHub.getEXPTIME_DISPLAY(CDKLib.cdkMap.get(player.getName()).getExpireTime()));

        GUI.setItem(26, ItemHub.EXPTIME_CONFIRM);

        player.openInventory(GUI);
    }

    public static void setNumber(Player player) {
        Inventory GUI = Bukkit.createInventory(player, 27, Component.text("MapCDK - 设置CDK数量").color(NamedTextColor.BLACK));

        GUI.setItem(3, ItemHub.SETNUMBER_DEC1);
        GUI.setItem(12, ItemHub.SETNUMBER_DEC10);
        GUI.setItem(21, ItemHub.SETNUMBER_DEC100);

        GUI.setItem(5, ItemHub.SETNUMBER_ADD1);
        GUI.setItem(14, ItemHub.SETNUMBER_ADD10);
        GUI.setItem(23, ItemHub.SETNUMBER_ADD100);

        GUI.setItem(13, ItemHub.getSETNUMBER_DISPLAY(CDKLib.cdkMap.get(player.getName()).getAmountLeft()));

        GUI.setItem(26, ItemHub.SETNUMBER_CONFIRM);

        player.openInventory(GUI);
    }

    public static void listCDK(Player player, List<CDK> cdks, int page, boolean hasNextPage) {
        Inventory GUI = Bukkit.createInventory(player, 36, Component.text(String.format("MapCDK - CDK列表 | 第 %d 页", page)).color(NamedTextColor.BLACK));

        int i = 0;
        for(CDK cdk : cdks) {
            if(cdk.getExpireTime().isAfter(LocalDateTime.now())){
                GUI.setItem(i++, ItemHub.getLISTCDK_CDK(cdk));
            } else {
                GUI.setItem(i++, ItemHub.getLISTCDK_EXPIRECDK(cdk));
            }
            if(i > 26) break;
        }

        if(page > 1) GUI.setItem(27, ItemHub.LISTCDK_PREV);
        if(hasNextPage) GUI.setItem(35, ItemHub.LISTCDK_NEXT);

        player.openInventory(GUI);
    }
}
