package me.maplef.mapcdk.GUI;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ItemHub {
    public static final ItemStack NEWCDK_SUBMITINV = getNEWCDK_SUBMITINV();
    public static final ItemStack NEWCDK_SUBMITCMD = getNEWCDK_SUBMITCMD();
    public static final ItemStack NEWCDK_SETNUMBER = getNEWCDK_SETNUMBER();
    public static final ItemStack NEWCDK_SETEXPTIME = getNEWCDK_SETEXPTIME();
    public static final ItemStack NEWCDK_SUBMIT = getNEWCDK_SUBMIT();
    public static final ItemStack NEWCDK_QUIT = getNEWCDK_QUIT();
    public static final ItemStack NEWCDK_CONFIRMQUIT = getNEWCDK_CONFIRMQUIT();

    public static final ItemStack SUBMITINV_CANCEL = getSUBMITINV_CANCEL();
    public static final ItemStack SUBMITINV_CONFIRM = getSUBMITINV_CONFIRM();
    public static final ItemStack SUBMITINV_CONFIRMCANCEL = getSUBMITINV_CONFIRMCANCEL();

    public static final ItemStack SETNUMBER_CONFIRM = getSUBMITINV_CONFIRM();
    public static final ItemStack SETNUMBER_CANCEL = getSUBMITINV_CANCEL();
    public static final ItemStack SETNUMBER_CONFIRMCANCEL = getSUBMITINV_CONFIRMCANCEL();
//    public static final ItemStack SETNUMBER_DISPLAY = getSETNUMBER_DISPLAY(10);
    public static final ItemStack SETNUMBER_ADD1 = getSETNUMBER_ADD(1);
    public static final ItemStack SETNUMBER_ADD10 = getSETNUMBER_ADD(10);
    public static final ItemStack SETNUMBER_ADD100 = getSETNUMBER_ADD(100);
    public static final ItemStack SETNUMBER_DEC1 = getSETNUMBER_DEC(1);
    public static final ItemStack SETNUMBER_DEC10 = getSETNUMBER_DEC(10);
    public static final ItemStack SETNUMBER_DEC100 = getSETNUMBER_DEC(100);

    public static final ItemStack MANAGECMD_CONFIRM = getSUBMITINV_CONFIRM();
    public static final ItemStack MANAGECMD_BOOK = getMANAGECMD_BOOK();
    public static final ItemStack MANAGECMD_DELETE = getMANAGECMD_DELETE();

    public static final ItemStack EXPTIME_CONFIRM = getSUBMITINV_CONFIRM();
    public static final ItemStack EXPTIME_ADD_SEC = getEXPTIME_ADD("秒");
    public static final ItemStack EXPTIME_ADD_MIN = getEXPTIME_ADD("分");
    public static final ItemStack EXPTIME_ADD_HOR = getEXPTIME_ADD("时");
    public static final ItemStack EXPTIME_ADD_DAY = getEXPTIME_ADD("日");
    public static final ItemStack EXPTIME_ADD_MON = getEXPTIME_ADD("月");
    public static final ItemStack EXPTIME_ADD_YER = getEXPTIME_ADD("年");
    public static final ItemStack EXPTIME_DEC_SEC = getEXPTIME_DEC("秒");
    public static final ItemStack EXPTIME_DEC_MIN = getEXPTIME_DEC("分");
    public static final ItemStack EXPTIME_DEC_HOR = getEXPTIME_DEC("时");
    public static final ItemStack EXPTIME_DEC_DAY = getEXPTIME_DEC("日");
    public static final ItemStack EXPTIME_DEC_MON = getEXPTIME_DEC("月");
    public static final ItemStack EXPTIME_DEC_YER = getEXPTIME_DEC("年");

    public static final ItemStack RECEIVECDK_RECEIVE_ALL = getRECEIVECDK_RECEIVE_ALL();
    public static final ItemStack RECEIVECDK_RECEIVE_FAIL = getRECEIVECDK_RECEIVE_FAIL();

    // ----------------------------------------

    private static @NotNull ItemStack getSUBMITINV_CANCEL(){
        ItemStack cancelItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta cancelItem_meta = cancelItem.getItemMeta();
        cancelItem_meta.displayName(Component.text("取消").color(NamedTextColor.RED));
        cancelItem.setItemMeta(cancelItem_meta);
        return cancelItem;
    }

    public static @NotNull ItemStack getSUBMITINV_CONFIRM() {
        ItemStack submitItem = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta submitItem_meta = submitItem.getItemMeta();
        submitItem_meta.displayName(Component.text("提交").color(NamedTextColor.GREEN));
        submitItem.setItemMeta(submitItem_meta);
        return submitItem;
    }

    public static @NotNull ItemStack getSUBMITINV_CONFIRMCANCEL() {
        ItemStack doubleCancelItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta doubleCancelItem_meta = doubleCancelItem.getItemMeta();
        doubleCancelItem_meta.displayName(Component.text("再次点击以返回").color(NamedTextColor.RED));
        doubleCancelItem.setItemMeta(doubleCancelItem_meta);
        return doubleCancelItem;
    }

    // ------------------------------------------

    public static @NotNull ItemStack getNEWCDK_SUBMITINV() {
        ItemStack submitInvItem = new ItemStack(Material.CHEST);
        ItemMeta submitInvItem_meta = submitInvItem.getItemMeta();
        submitInvItem_meta.displayName(Component.text("上传物品").color(NamedTextColor.GREEN));
        List<Component> submitInvItem_lore = new ArrayList<>();
        submitInvItem_lore.add(Component.text("上传CDK内包含的物品").color(NamedTextColor.GRAY));
        submitInvItem_meta.lore(submitInvItem_lore);
        submitInvItem.setItemMeta(submitInvItem_meta);
        return submitInvItem;
    }

    public static @NotNull ItemStack getNEWCDK_SUBMITCMD() {
        ItemStack submitCmdItem = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta submitCmdItem_meta = submitCmdItem.getItemMeta();
        submitCmdItem_meta.displayName(Component.text("管理指令").color(NamedTextColor.GREEN));
        List<Component> submitCmdItem_lore = new ArrayList<>();
        submitCmdItem_lore.add(Component.text("管理 CDK 内包含的指令").color(NamedTextColor.GRAY));
        submitCmdItem_meta.lore(submitCmdItem_lore);
        submitCmdItem.setItemMeta(submitCmdItem_meta);
        return submitCmdItem;
    }

    public static @NotNull ItemStack getNEWCDK_SETNUMBER() {
        ItemStack setNumberItem = new ItemStack(Material.BUCKET);
        ItemMeta setNumberItem_meta = setNumberItem.getItemMeta();
        setNumberItem_meta.displayName(Component.text("设置数量").color(NamedTextColor.GREEN));
        List<Component> setNumberItem_lore = new ArrayList<>();
        setNumberItem_lore.add(Component.text("设置CDK总份数").color(NamedTextColor.GRAY));
        setNumberItem_meta.lore(setNumberItem_lore);
        setNumberItem.setItemMeta(setNumberItem_meta);
        return setNumberItem;
    }

    public static @NotNull ItemStack getNEWCDK_SETEXPTIME() {
        ItemStack setExpireTimeItem = new ItemStack(Material.CLOCK);
        ItemMeta setExpireTimeItem_meta = setExpireTimeItem.getItemMeta();
        setExpireTimeItem_meta.displayName(Component.text("设置过期时间").color(NamedTextColor.GREEN));
        List<Component> setExpireTimItem_lore = new ArrayList<>();
        setExpireTimItem_lore.add(Component.text("设置CDK过期时间").color(NamedTextColor.GRAY));
        setExpireTimeItem_meta.lore(setExpireTimItem_lore);
        setExpireTimeItem.setItemMeta(setExpireTimeItem_meta);
        return setExpireTimeItem;
    }

    private static @NotNull ItemStack getNEWCDK_SUBMIT() {
        ItemStack cancelItem = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta cancelItem_meta = cancelItem.getItemMeta();
        cancelItem_meta.displayName(Component.text("提交").color(NamedTextColor.GREEN));
        cancelItem.setItemMeta(cancelItem_meta);
        return cancelItem;
    }

    public static @NotNull ItemStack getNEWCDK_QUIT() {
        ItemStack quitItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta quitItem_meta = quitItem.getItemMeta();
        quitItem_meta.displayName(Component.text("退出").color(NamedTextColor.RED));
        quitItem.setItemMeta(quitItem_meta);
        return quitItem;
    }

    public static @NotNull ItemStack getNEWCDK_CONFIRMQUIT() {
        ItemStack confirmQuitItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta confirmQuitItem_meta = confirmQuitItem.getItemMeta();
        confirmQuitItem_meta.displayName(Component.text("确定退出吗？").color(NamedTextColor.RED).decorate(TextDecoration.BOLD));
        List<Component> lores = new ArrayList<>();
        lores.add(Component.text("所做所有更改将保存在后台").color(NamedTextColor.LIGHT_PURPLE));
        lores.add(Component.text("使用 /mapcdk continue 重新进入编辑").color(NamedTextColor.LIGHT_PURPLE));
        lores.add(Component.text("确定退出请按住 shift 再次单击").color(NamedTextColor.LIGHT_PURPLE));
        confirmQuitItem_meta.lore(lores);
        confirmQuitItem.setItemMeta(confirmQuitItem_meta);
        return confirmQuitItem;
    }

    // -------------------------------------------

    public static @NotNull ItemStack getSETNUMBER_DISPLAY(int num) {
        ItemStack displayItem = new ItemStack(Material.OAK_SIGN);
        ItemMeta displayItem_meta = displayItem.getItemMeta();
        displayItem_meta.displayName(Component.text("数量：").color(NamedTextColor.BLUE));
        List<Component> lores = new ArrayList<>();
        lores.add(Component.text(num).color(NamedTextColor.YELLOW));
        displayItem_meta.lore(lores);
        displayItem.setItemMeta(displayItem_meta);
        return displayItem;
    }

    public static @NotNull ItemStack getSETNUMBER_ADD(int num) {
        ItemStack addItem = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta addItem_meta = addItem.getItemMeta();
        addItem_meta.displayName(Component.text("+" + num).color(NamedTextColor.GREEN));
        addItem.setItemMeta(addItem_meta);
        return addItem;
    }

    public static @NotNull ItemStack getSETNUMBER_DEC(int num) {
        ItemStack decItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta decItem_meta = decItem.getItemMeta();
        decItem_meta.displayName(Component.text("-" + num).color(NamedTextColor.RED));
        decItem.setItemMeta(decItem_meta);
        return decItem;
    }

    // -------------------------------------------

    public static @NotNull ItemStack getMANAGECMD_BOOK() {
        ItemStack cmdBookItem = new ItemStack(Material.WRITABLE_BOOK);
        BookMeta cmdBookItem_meta = (BookMeta) cmdBookItem.getItemMeta();

        cmdBookItem_meta.addPages(Component.text("在下一页写下所有要添加到CDK的命令\n" +
                "每个命令独占一行，以 '/' 开头，允许添加多页\n" +
                "完成后点击署名，将书名设为 \"commands\"，在上传物品页面和物品一同提交\n" +
                "可使用的变量占位符：%PLAYER%\n" +
                "尚未支持 PlaceHolder API，敬请期待\n" +
                "---------------------------\n"));
        List<Component> lores = new ArrayList<>();
        lores.add(Component.text("MapCDK 上传命令用书").color(NamedTextColor.BLUE));
        lores.add(Component.text("打开以获得帮助").color(NamedTextColor.BLUE));
        cmdBookItem_meta.lore(lores);

        cmdBookItem.setItemMeta(cmdBookItem_meta);

        return cmdBookItem;
    }

    public static @NotNull ItemStack getMANEGECMD_CMDPAPER(String cmd) {
        ItemStack cmdPaperItem = new ItemStack(Material.PAPER);
        ItemMeta cmdPaperItem_meta = cmdPaperItem.getItemMeta();
        cmdPaperItem_meta.displayName(Component.text(cmd).color(NamedTextColor.YELLOW));
        cmdPaperItem.setItemMeta(cmdPaperItem_meta);
        return cmdPaperItem;
    }

    public static @NotNull ItemStack getMANAGECMD_DELETE() {
        ItemStack deleteItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta deleteItem_meta = deleteItem.getItemMeta();
        deleteItem_meta.displayName(Component.text("直接移除命令纸条以删除命令").color(NamedTextColor.RED));
        deleteItem.setItemMeta(deleteItem_meta);
        return deleteItem;
    }

    // -------------------------------------------

    public static ItemStack getEXPTIME_DISPLAY(LocalDateTime time) {
        ItemStack displayItem = new ItemStack(Material.OAK_SIGN);
        ItemMeta displayItem_meta = displayItem.getItemMeta();
        displayItem_meta.displayName(Component.text("过期时间：").color(NamedTextColor.BLUE));
        List<Component> lores = new ArrayList<>();
        lores.add(Component.text(time.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss"))).color(NamedTextColor.YELLOW));
        displayItem_meta.lore(lores);
        displayItem.setItemMeta(displayItem_meta);
        return displayItem;
    }

    public static ItemStack getEXPTIME_ADD(String unit) {
        ItemStack addItem = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta addItem_meta = addItem.getItemMeta();
        addItem_meta.displayName(Component.text("+1" + unit).color(NamedTextColor.GREEN));
        addItem.setItemMeta(addItem_meta);
        return addItem;
    }

    public static ItemStack getEXPTIME_DEC(String unit) {
        ItemStack decItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta decItem_meta = decItem.getItemMeta();
        decItem_meta.displayName(Component.text("-1" + unit).color(NamedTextColor.RED));
        decItem.setItemMeta(decItem_meta);
        return decItem;
    }

    // ------------------------------------------------------------

    public static ItemStack getRECEIVECDK_RECEIVE_ALL() {
        ItemStack receiveAllItem = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta receiveAllItem_meta = receiveAllItem.getItemMeta();
        receiveAllItem_meta.displayName(Component.text("一键领取").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));

        List<Component> lores = new ArrayList<>();
        lores.add(Component.text("一键领取所有物品和指令").color(NamedTextColor.BLUE));

        receiveAllItem_meta.lore(lores);
        receiveAllItem.setItemMeta(receiveAllItem_meta);
        return receiveAllItem;
    }

    public static ItemStack getRECEIVECDK_RECEIVE_FAIL() {
        ItemStack receiveFailItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta receiveFailItem_meta = receiveFailItem.getItemMeta();
        receiveFailItem_meta.displayName(Component.text("领取失败").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));

        List<Component> lores = new ArrayList<>();
        lores.add(Component.text("背包内剩余空间不足，请清理背包").color(NamedTextColor.LIGHT_PURPLE));

        receiveFailItem_meta.lore(lores);
        receiveFailItem.setItemMeta(receiveFailItem_meta);
        return receiveFailItem;
    }
}
