package me.maplef.mapcdk.commands;

import me.maplef.mapcdk.CDK;
import me.maplef.mapcdk.GUI.GUIHub;
import me.maplef.mapcdk.utils.CDKLib;
import me.maplef.mapcdk.utils.CU;
import me.maplef.mapcdk.utils.ConfigManager;
import me.maplef.mapcdk.utils.Database;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.rmi.NoSuchObjectException;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Mapcdk implements CommandExecutor, TabExecutor {
    FileConfiguration config = new ConfigManager().getConfig();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) {
            sender.sendMessage(getHelpMessage(sender));
            return true;
        }

        switch (args[0]) {
            case "newcdk" -> {
                if(!sender.hasPermission("mapcdk.newcdk")){
                    sender.sendMessage("No Permission!");  //
                    return true;
                } else if(! (sender instanceof Player)){
                    sender.sendMessage("This command is only for players!");
                    return true;
                }
                else {
                    CDKLib.cdkMap.put(sender.getName(), new CDK(sender.getName(), args[1]));
                    GUIHub.newCDK((Player) sender);
                }
            }

            case "continue" -> {
                if(!sender.hasPermission("mapcdk.newcdk")){
                    sender.sendMessage("No Permission!");  //
                    return true;
                } else if(! (sender instanceof Player)){
                    sender.sendMessage("This command is only for players!");
                    return true;
                }
                else {
                    if(CDKLib.cdkMap.get(sender.getName()) == null){
                        sender.sendMessage(CU.t(config.getString("message-prefix")) + Component.text("你没有正在编辑中的CDK，请使用 /mapcdk newcdk 创建新的CDK").color(NamedTextColor.RED));
                        return true;
                    }
                    GUIHub.newCDK((Player) sender);
                }
            }

            case "deletecdk" -> {

            }

            case "cdkinfo" -> {

            }

            case "list" -> {
                List<CDK> CDKs = new ArrayList<>();
                try (Statement stmt = new Database().getC().createStatement();
                     ResultSet res = stmt.executeQuery("SELECT * FROM cdk_info")) {
                    while(res.next()) {
                        CDKs.add(new CDK(new Database().getC(), res.getString("cdk_string")));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (NoSuchObjectException ignored) {}


            }

            // receive CDK rewards
            default -> {
                if(! (sender instanceof Player)){
                    sender.sendMessage("Only players can receive CDKs!");
                    return true;
                }
                receiveCDK((Player) sender, args[0]);
                return true;
            }
        }

        return false;
    }

    private void receiveCDK(Player player, String cdkString) {
        CDK cdk;
        try {
            cdk = new CDK(new Database().getC(), cdkString);
        } catch (NoSuchObjectException e) {
            player.sendMessage(Component.text(CU.t(config.getString("message-prefix"))).append(Component.text("未找到此 CDK，请确保输入了正确的 CDK").color(NamedTextColor.RED)));
            return;
        } catch (SQLException e) {
            player.sendMessage(Component.text(CU.t(config.getString("message-prefix"))).append(Component.text("发生了数据库错误").color(NamedTextColor.RED)));
            e.printStackTrace();
            return;
        }

        if(LocalDateTime.now().isAfter(cdk.getExpireTime())) {
            player.sendMessage(Component.text(CU.t(config.getString("message-prefix"))).append(Component.text("此CDK已过期").color(NamedTextColor.RED)));
            return;
        }

        try (Statement stmt = new Database().getC().createStatement();
             ResultSet res = stmt.executeQuery(String.format("SELECT * FROM cdk_info WHERE cdk_string = '%s'", cdk.getCdkString()))) {
            res.next();
            if(res.getInt("amount_left") <= 0) {
                player.sendMessage(Component.text(CU.t(config.getString("message-prefix"))).append(Component.text("此CDK已被领取完").color(NamedTextColor.RED)));
                return;
            }
        } catch (SQLException e) {
            player.sendMessage(Component.text(CU.t(config.getString("message-prefix"))).append(Component.text("发生了数据库错误").color(NamedTextColor.RED)));
            e.printStackTrace();
            return;
        }

        try (Statement stmt = new Database().getC().createStatement();
             ResultSet res = stmt.executeQuery(String.format("SELECT * FROM cdk_receive WHERE cdk_string = '%s' AND receiver = '%s'", cdk.getCdkString(), player.getName()))){
            if(res.next()) {
                LocalDateTime receiveTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(res.getLong("receive_time")), ZoneId.systemDefault());
                player.sendMessage(Component.text(CU.t(config.getString("message-prefix")))
                        .append(Component.text(String.format("你已经于 %s 领取过了该CDK，不能再次领取", receiveTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))).color(NamedTextColor.RED)));
                return;
            }
        } catch (SQLException e) {
            player.sendMessage(Component.text(CU.t(config.getString("message-prefix"))).append(Component.text("发生了数据库错误").color(NamedTextColor.RED)));
            e.printStackTrace();
            return;
        }

        int rewardAmount = cdk.getRewardItems().size();
        int emptySlotCnt = 0;
        for(ItemStack item : player.getInventory().getStorageContents()) {
            if(item == null) emptySlotCnt++;
        }
        if(emptySlotCnt < rewardAmount) {
            player.sendMessage(Component.text(CU.t(config.getString("message-prefix"))).append(Component.text("你的背包空间不足，请清理背包").color(NamedTextColor.RED)));
            return;
        }

        try {
            PreparedStatement ps = new Database().getC().prepareStatement("INSERT INTO cdk_receive (cdk_string, receiver, receive_time) VALUES (?, ?, ?)");
            ps.setString(1, cdk.getCdkString());
            ps.setString(2, player.getName());
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.execute();

            ps = new Database().getC().prepareStatement("UPDATE cdk_info SET amount_left = ? WHERE cdk_string = ?");
            ps.setInt(1, cdk.getAmountLeft() - 1);
            ps.setString(2, cdk.getCdkString());
            ps.execute();

            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(ItemStack item : cdk.getRewardItems()) {
            player.getInventory().addItem(item);
        }
        for(String command : cdk.getRewardCmds()) {
            if(command.startsWith("/")) command = command.substring(1);
            command = command.replaceAll("%PLAYER%", player.getName());
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
        }

        player.sendMessage(Component.text(CU.t(config.getString("message-prefix"))).append(Component.text("CDK 领取成功！", NamedTextColor.GREEN, TextDecoration.BOLD)));
    }

    private @NotNull String getHelpMessage(CommandSender sender) {
        StringBuilder helpMsg = new StringBuilder("&a[MapCDK 帮助菜单]\n");

        Map<String, String> commands = new HashMap<>();
        commands.put("help", "&e/mapcdk help &f- 显示此菜单");
        commands.put("cdk", "&e/mapcdk <cdk> &f- 兑换礼包码");
        commands.put("newcdk", "&e/mapcdk newcdk &f- 创建新的礼包码");
        commands.put("continue", "&e/mapcdk continue &f- 修改正在创建的礼包码");
        commands.put("deletecdk", "&e/mapcdk deletecdk &f- 删除一个礼包码");
        commands.put("cdkinfo", "&e/mapcdk cdkinfo &f- 查看礼包码信息");

        for(String command : commands.keySet()){
            if(sender.hasPermission("mapcdk." + command))
                helpMsg.append(commands.get(command)).append('\n');
        }

        return CU.t(helpMsg.toString());
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(args.length == 1){
            String[] allCommands = {"help", "newcdk", "continue", "cdkinfo", "deletecdk"};

            List<String> commandList = new ArrayList<>();
            for(String commandName : allCommands)
                if(sender.hasPermission("mapcdk." + commandName))
                    commandList.add(commandName);

            commandList.add("<CDK>");
            return commandList;
        } else if(args.length == 2) {
            if(args[0].equals("newcdk")) {
                List<String> commandList = new ArrayList<>();
                commandList.add("<备注>");
                return commandList;
            }
        }
        return null;
    }
}
