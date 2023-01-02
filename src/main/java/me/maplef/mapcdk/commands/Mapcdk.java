package me.maplef.mapcdk.commands;

import me.maplef.mapcdk.CDK;
import me.maplef.mapcdk.GUI.GUIHub;
import me.maplef.mapcdk.utils.CDKLib;
import me.maplef.mapcdk.utils.CU;
import me.maplef.mapcdk.utils.Database;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.rmi.NoSuchObjectException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mapcdk implements CommandExecutor, TabExecutor {
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
                    CDKLib.cdkMap.put(sender.getName(), new CDK(sender.getName()));
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
                        sender.sendMessage(Component.text("你没有正在编辑中的CDK，请使用 /mapcdk newcdk 创建新的CDK").color(NamedTextColor.RED));
                        return true;
                    }
                    GUIHub.newCDK((Player) sender);
                }
            }

            case "deletecdk" -> {

            }

            case "cdkinfo" -> {

            }

            // receive CDK rewards
            default -> {
                if(! (sender instanceof Player)){
                    sender.sendMessage("Only players can receive CDKs!");
                    return true;
                }
                receiveCDK((Player) sender, args[0]);
            }
        }

        return false;
    }

    private void receiveCDK(Player player, String cdkString) {
        CDK cdk;
        try {
            cdk = new CDK(new Database().getC(), cdkString);
        } catch (NoSuchObjectException e) {
            player.sendMessage(Component.text("未找到此 CDK，请确保输入了正确的 CDK").color(NamedTextColor.RED));
        } catch (SQLException e) {
            player.sendMessage(Component.text("发生了数据库错误").color(NamedTextColor.RED));
            e.printStackTrace();
        }
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
        }
        return null;
    }
}
