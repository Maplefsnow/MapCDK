package me.maplef.mapcdk.commands;

import com.sun.source.tree.ReturnTree;
import me.maplef.mapcdk.CDK;
import me.maplef.mapcdk.CDKReceiver;
import me.maplef.mapcdk.GUI.GUIHub;
import me.maplef.mapcdk.exceptions.InsufficientInventorySpaceException;
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

import javax.naming.InsufficientResourcesException;
import javax.naming.TimeLimitExceededException;
import javax.sound.midi.Receiver;
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
                    if(args.length == 1) CDKLib.cdkMap.put(sender.getName(), new CDK(sender.getName(), null));
                    else CDKLib.cdkMap.put(sender.getName(), new CDK(sender.getName(), args[1]));
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
                        sender.sendMessage(Component.text(CU.t(config.getString("message-prefix"))).append(Component.text("你没有正在编辑中的CDK，请使用 /mapcdk newcdk 创建新的CDK").color(NamedTextColor.RED)));
                        return true;
                    }
                    GUIHub.newCDK((Player) sender);
                }
            }

            case "deletecdk" -> {

            }

            case "list" -> {
                List<CDK> allCDK, CDK_page1;
                try {
                    allCDK = Database.getAllCDKFromDatabase();
                    CDK_page1 = Database.getCDKPageFromDatabase(1);
                } catch (SQLException e) {
                    sender.sendMessage(Component.text(CU.t(config.getString("message-prefix"))).append(Component.text("发生了数据库错误").color(NamedTextColor.RED)));
                    e.printStackTrace();
                    return true;
                }

                if(allCDK.size() <= 27) {
                    GUIHub.listCDK((Player) sender, allCDK, 1, false);
                } else {
                    GUIHub.listCDK((Player) sender, CDK_page1, 1, true);
                }

                return true;
            }

            // receive CDK rewards
            default -> {
                if(! (sender instanceof Player)){
                    sender.sendMessage("Only players can receive CDKs!");
                    return true;
                }

                Player player = (Player) sender;
                CDK cdk;
                try {
                    cdk = new CDK(new Database().getC(), args[0]);
                } catch (NoSuchObjectException e) {
                    player.sendMessage(Component.text(CU.t(config.getString("message-prefix"))).append(Component.text("未找到此 CDK，请确保输入了正确的 CDK").color(NamedTextColor.RED)));
                    return true;
                } catch (SQLException e) {
                    player.sendMessage(Component.text(CU.t(config.getString("message-prefix"))).append(Component.text("发生了数据库错误").color(NamedTextColor.RED)));
                    e.printStackTrace();
                    return true;
                }

                List<CDKReceiver> cdkReceivers = cdk.getReceivers();
                for(CDKReceiver receiver : cdkReceivers) {
                    if(receiver.getReceiverName().equals(player.getName())) {
                        player.sendMessage(Component.text(CU.t(config.getString("message-prefix")))
                                .append(Component.text(String.format("你已经于 %s 领取了该CDK，不能再次领取",
                                        receiver.getReceiveTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))));
                        return true;
                    }
                }

                try {
                    cdk.sendToPlayer(player);
                } catch (TimeLimitExceededException e) {
                    player.sendMessage(Component.text(CU.t(config.getString("message-prefix")))
                            .append(Component.text("此 CDK 已过期，无法领取").color(NamedTextColor.RED)));
                } catch (InsufficientResourcesException e) {
                    player.sendMessage(Component.text(CU.t(config.getString("message-prefix")))
                            .append(Component.text("此 CDK 已被领取完，无法领取").color(NamedTextColor.RED)));
                } catch (InsufficientInventorySpaceException e) {
                    player.sendMessage(Component.text(CU.t(config.getString("message-prefix")))
                            .append(Component.text(e.getMessage()).color(NamedTextColor.RED)));
                } catch (SQLException e){
                    player.sendMessage(Component.text(CU.t(config.getString("message-prefix"))).append(Component.text("发生了数据库错误").color(NamedTextColor.RED)));
                    e.printStackTrace();
                    return true;
                }

                player.sendMessage(Component.text(CU.t(config.getString("message-prefix")))
                        .append(Component.text("CDK 领取成功！", NamedTextColor.GREEN, TextDecoration.BOLD)));

                return true;
            }
        }

        return false;
    }

    private @NotNull String getHelpMessage(CommandSender sender) {
        StringBuilder helpMsg = new StringBuilder("&a[MapCDK 帮助菜单]\n");

        Map<String, String> commands = new HashMap<>();
        commands.put("help", "&e/mapcdk help &f- 显示此菜单");
        commands.put("cdk", "&e/mapcdk <cdk> &f- 兑换礼包码");
        commands.put("newcdk", "&e/mapcdk newcdk &f- 创建新的礼包码");
        commands.put("continue", "&e/mapcdk continue &f- 修改正在创建的礼包码");
//        commands.put("deletecdk", "&e/mapcdk deletecdk &f- 删除一个礼包码");
        commands.put("list", "&e/mapcdk list &f- 查看礼包码信息");

        for(String command : commands.keySet()){
            if(sender.hasPermission("mapcdk." + command))
                helpMsg.append(commands.get(command)).append('\n');
        }

        return CU.t(helpMsg.toString());
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(args.length == 1){
            String[] allCommands = {"help", "newcdk", "continue", "list", "deletecdk"};

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
