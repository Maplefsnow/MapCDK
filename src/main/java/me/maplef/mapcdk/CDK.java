package me.maplef.mapcdk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import me.maplef.mapcdk.exceptions.InsufficientInventorySpaceException;
import me.maplef.mapcdk.utils.CDKGenerator;
import me.maplef.mapcdk.utils.ConfigManager;
import me.maplef.mapcdk.utils.Database;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.naming.InsufficientResourcesException;
import javax.naming.TimeLimitExceededException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.NoSuchObjectException;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class CDK {
    private final String cdkString;
    private final String creator;
    private final LocalDateTime createTime;
    private LocalDateTime expireTime;
    private int amountLeft;
    private final String note;

    private List<ItemStack> rewardItems;
    private List<String> rewardCmds;

    private final FileConfiguration config = new ConfigManager().getConfig();

    public CDK(String creator, String note){
        this.cdkString = CDKGenerator.generateCDKbyFormat(config.getString("CDK-format"));
        this.creator = creator;
        this.createTime = LocalDateTime.now();
        this.expireTime = LocalDateTime.now().plusMonths(1);
        this.amountLeft = 10;
        this.note = note;

        this.rewardItems = new ArrayList<>();
        this.rewardCmds = new ArrayList<>();
    }
    public CDK(String creator, LocalDateTime createTime, LocalDateTime expireTime, int numbersLeft, String note, List<ItemStack> rewardItems, List<String> rewardCmds){
        this.cdkString = CDKGenerator.generateCDKbyFormat(config.getString("CDK-format"));
        this.creator =creator;
        this.createTime = createTime;
        this.expireTime = expireTime;
        this.amountLeft = numbersLeft;
        this.note = note;

        this.rewardItems = rewardItems;
        this.rewardCmds = rewardCmds;
    }
    // construct a CDK from database by cdk_string
    public CDK(Connection c, String cdkString) throws NoSuchObjectException, SQLException {
        Statement stmt = c.createStatement();

        ResultSet res = stmt.executeQuery(String.format("SELECT * FROM cdk_info WHERE cdk_string = '%s';", cdkString));
        if (res.next()) {
            this.cdkString = cdkString;
            this.amountLeft = res.getInt("amount_left");
            this.createTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(res.getLong("create_time")), ZoneId.systemDefault());
            this.expireTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(res.getLong("expire_time")), ZoneId.systemDefault());
            this.creator = res.getString("creator");
            this.note = res.getString("note");
        } else throw new NoSuchObjectException("CDK not found");

        res = stmt.executeQuery(String.format("SELECT * FROM cdk_reward WHERE cdk_string = '%s';", cdkString));
        List<ItemStack> items = new ArrayList<>();
        while(res.next()) {
            String materialStr = res.getString("material");
            int amount = res.getInt("amount");
            ItemStack item = new ItemStack(Objects.requireNonNull(Material.getMaterial(materialStr)), amount);
            items.add(item);
        }
        this.rewardItems = items;

        res = stmt.executeQuery(String.format("SELECT * FROM cdk_command WHERE cdk_string = '%s';", cdkString));
        List<String> commands = new ArrayList<>();
        while(res.next()) {
            commands.add(res.getString("command"));
        }
        this.rewardCmds = commands;
    }
    // construct a CDK from file "cdk_string.json"
    public CDK(JSONObject json) throws IllegalArgumentException {
        if(!json.containsKey("cdk_info")) throw new IllegalArgumentException("missing key: cdk_info");
        if(!json.containsKey("cdk_rewards")) throw new IllegalArgumentException("missing key: cdk_rewards");
        if(!json.containsKey("cdk_commands")) throw new IllegalArgumentException("missing key: cdk_commands");
        if(!json.containsKey("note")) throw new IllegalArgumentException("missing key: note");

        JSONObject cdkInfo = json.getJSONObject("cdk_info");
        JSONArray cdkRewards = json.getJSONArray("cdk_rewards");
        JSONArray cdkCmds = json.getJSONArray("cdk_commands");

        HashSet<String> keySet = new HashSet<>();
        keySet.add("cdk_string"); keySet.add("creator");
        keySet.add("create_time"); keySet.add("expire_time");
        keySet.add("numbers_left");
        for(String key : keySet) {
            if (cdkInfo.getString(key) == null) throw new IllegalArgumentException("missing key: cdk_string." + key);
        }

        this.cdkString = cdkInfo.getString("cdk_string");
        this.creator = cdkInfo.getString("creator");
        this.createTime = LocalDateTime.parse(cdkInfo.getString("create_time"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.expireTime = LocalDateTime.parse(cdkInfo.getString("expire_time"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.amountLeft = cdkInfo.getInteger("numbers_left");
        this.note = cdkInfo.getString("note");

        // get reward items
        List<ItemStack> rewardItems = new ArrayList<>();
        for(int i = 0; i < cdkRewards.size(); i++) {
            String materialName = cdkRewards.getJSONObject(i).getString("material");
            String amountStr = cdkRewards.getJSONObject(i).getString("amount");

            if(materialName == null) throw new IllegalArgumentException(String.format("missing key: cdk_rewards[%d].material", i));
            if(amountStr == null) throw new IllegalArgumentException(String.format("missing key: cdk_rewards[%d].amount", i));

            int amount;
            try {
                amount = Integer.parseInt(amountStr);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException(String.format("invalid number format: cdk_rewards[%d].amount", i));
            }

            Material material = Material.getMaterial(materialName);
            if(material == null) throw new IllegalArgumentException(String.format("invalid material name: cdk_rewards[%d].material", i));
            ItemStack item = new ItemStack(material, amount);
            rewardItems.add(item);
        }
        this.rewardItems = rewardItems;

        List<String> rewardCommands = new ArrayList<>();
        for(int i = 0 ; i < cdkCmds.size(); i++) {
            rewardCommands.add(cdkCmds.getString(i));
        }
        this.rewardCmds = rewardCommands;
    }

    public void addExpireTime(long amountToAdd, TemporalUnit unit) {
        this.expireTime = this.expireTime.plus(amountToAdd, unit);
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

    public void minusExpireTime(long amountToMinus, TemporalUnit unit) throws IllegalArgumentException {
        if(this.expireTime.minus(amountToMinus, unit).isBefore(this.createTime)) {
            throw new IllegalArgumentException();
        } else {
            this.expireTime = this.expireTime.minus(amountToMinus, unit);
        }
    }

    public void setAmountLeft(int numberToSet) {
        this.amountLeft = numberToSet;
    }

    public void addNumbersLeft(int numberToAdd) throws IllegalArgumentException {
        if(this.amountLeft + numberToAdd <= 0) {
            throw new IllegalArgumentException();
        } else {
            this.amountLeft += numberToAdd;
        }
    }

    public void addRewardItems(ItemStack item) {
        this.rewardItems.add(item);
    }

    public void setRewardItems(List<ItemStack> items) {
        this.rewardItems = items;
    }

    public List<ItemStack> getRewardItems() {
        return rewardItems;
    }

    public void addRewardCmd(String cmd) {
        this.rewardCmds.add(cmd);
    }

    public void removeRewardCmd(int index) throws IllegalArgumentException {
        if(index >= this.rewardCmds.size() || index < 0) {
            throw new IllegalArgumentException();
        }

        this.rewardCmds.remove(index);
    }

    public boolean removeRewardCmd(String cmd) {
        return this.rewardCmds.remove(cmd);
    }

    public void setRewardCmds(List<String> cmds) {
        this.rewardCmds = cmds;
    }

    public List<String> getRewardCmds() {
        return rewardCmds;
    }

    public String getCdkString() {
        return this.cdkString;
    }

    public String getCreator() {
        return this.creator;
    }

    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    public LocalDateTime getExpireTime() {
        return this.expireTime;
    }

    public int getAmountLeft() {
        return this.amountLeft;
    }

    public String getNote() {
        return note;
    }

    public List<CDKReceiver> getReceivers() {
        List<CDKReceiver> receivers = new ArrayList<>();

        try (Statement stmt = new Database().getC().createStatement();
             ResultSet res = stmt.executeQuery(String.format("SELECT * FROM cdk_receive WHERE cdk_string = '%s'", this.cdkString))){
            CDKReceiver receiver = new CDKReceiver();
            while (res.next()) {
                receiver.setReceiverName(res.getString("receiver"));
                receiver.setReceiveTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(res.getLong("receive_time")), ZoneId.systemDefault()));
                receivers.add(receiver);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return receivers;
    }

    public void exportToJSON(File path) throws IOException{
        JSONObject cdkJson = new JSONObject();

        JSONObject cdkInfo = new JSONObject();
        cdkInfo.put("cdk_string", this.cdkString);
        cdkInfo.put("create_time", this.createTime);
        cdkInfo.put("expire_time", this.expireTime);
        cdkInfo.put("creator", this.creator);
        cdkInfo.put("numbers_left", this.amountLeft);
        cdkInfo.put("note", this.note);

        JSONArray cdkRewards = new JSONArray();
        for(ItemStack item : this.rewardItems) {
            JSONObject obj = new JSONObject();
            obj.put("material", item.getType().name());
            obj.put("amount", item.getAmount());
            cdkRewards.add(obj);
        }

        JSONArray cdkCommands = new JSONArray();
        cdkCommands.addAll(this.rewardCmds);

        cdkJson.put("cdk_info", cdkInfo);
        cdkJson.put("cdk_rewards", cdkRewards);
        cdkJson.put("cdk_commands", cdkCommands);

        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path, this.cdkString + ".json")));
        bw.write(JSON.toJSONString(cdkJson, SerializerFeature.PrettyFormat,
                                            SerializerFeature.WriteDateUseDateFormat));
        bw.close();
    }

    public void exportToDataBase(Connection c) throws SQLException {
        boolean is_exist;

        Statement stmt = c.createStatement();
        ResultSet res = stmt.executeQuery(String.format("SELECT * FROM cdk_info WHERE cdk_string = '%s'", this.cdkString));
        is_exist = res.next();

        PreparedStatement ps;

        if(is_exist) {
            ps = c.prepareStatement("DELETE FROM cdk_info WHERE cdk_string = ?");
            ps.setString(1, this.cdkString);
            ps.execute();

            ps = c.prepareStatement("DELETE FROM cdk_reward WHERE cdk_string = ?");
            ps.setString(1, cdkString);
            ps.execute();

            ps = c.prepareStatement("DELETE FROM cdk_command WHERE cdk_string = ?");
            ps.setString(1, cdkString);
            ps.execute();
        }

        ps = c.prepareStatement("INSERT INTO cdk_info (cdk_string, amount_left, create_time, expire_time, creator, note)" +
                    " VALUES (?, ?, ?, ?, ?, ?)");

        ps.setString(1, this.cdkString);
        ps.setInt(2, this.amountLeft);
        ps.setTimestamp(3, Timestamp.valueOf(this.createTime));
        ps.setTimestamp(4, Timestamp.valueOf(this.expireTime));
        ps.setString(5, this.creator);
        ps.setString(6, this.note);

        ps.execute();

        ps = c.prepareStatement("INSERT INTO cdk_reward (cdk_string, material, amount)" +
                " VALUES (?, ?, ?)");

        ps.setString(1, this.cdkString);
        for(ItemStack item : this.rewardItems) {
            ps.setString(2, item.getType().name());
            ps.setInt(3, item.getAmount());
            ps.execute();
        }

        ps = c.prepareStatement("INSERT INTO cdk_command (cdk_string, command)" +
                " VALUES (?, ?)");

        ps.setString(1, this.cdkString);
        for(String cmd : this.rewardCmds) {
            ps.setString(2, cmd);
            ps.execute();
        }

        ps.close();
    }

    public void sendToPlayer(Player player) throws TimeLimitExceededException, InsufficientResourcesException, InsufficientInventorySpaceException, SQLException {
        if(LocalDateTime.now().isAfter(this.expireTime)) throw new TimeLimitExceededException();

        if(this.getAmountLeft() <= 0) throw new InsufficientResourcesException();

        int rewardAmount = this.rewardItems.size();
        int emptySlotCnt = 0;
        for(ItemStack item : player.getInventory().getStorageContents()) {
            if(item == null) emptySlotCnt++;
        }
        if(emptySlotCnt < rewardAmount) throw new InsufficientInventorySpaceException(rewardAmount - emptySlotCnt);

        this.amountLeft--;

        PreparedStatement ps = new Database().getC().prepareStatement("INSERT INTO cdk_receive (cdk_string, receiver, receive_time) VALUES (?, ?, ?)");
        ps.setString(1, this.cdkString);
        ps.setString(2, player.getName());
        ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
        ps.execute();

        ps = new Database().getC()
                .prepareStatement("UPDATE cdk_info SET amount_left = ? WHERE cdk_string = ?");
        ps.setInt(1, this.amountLeft);
        ps.setString(2, this.cdkString);
        ps.execute();


        for(ItemStack item : this.rewardItems) {
            player.getInventory().addItem(item);
        }
        for(String command : this.rewardCmds) {
            if(command.startsWith("/")) command = command.substring(1);
            command = command.replaceAll("%PLAYER%", player.getName());
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }

    public Component toComponent() {
        ComponentBuilder<TextComponent, TextComponent.Builder> componentBuilder = Component.text();

        TextComponent noteComponent;
        if(this.note == null) {
            noteComponent = Component.text("=== " + this.cdkString, NamedTextColor.YELLOW)
                    .append(Component.text("的基本信息 ===", NamedTextColor.YELLOW));
        } else {
            noteComponent = Component.text("=== " + this.note, NamedTextColor.YELLOW)
                    .append(Component.text("的基本信息 ===", NamedTextColor.YELLOW));
        }

        TextComponent cdkComponent = Component.text("CDK: ", NamedTextColor.WHITE)
                .append(Component.text(this.cdkString, NamedTextColor.AQUA)
                .clickEvent(ClickEvent.copyToClipboard(this.cdkString)))
                .hoverEvent(HoverEvent.showText(Component.text("点击复制到剪贴板")));

        TextComponent leftComponent = Component.text("剩余数量: ", NamedTextColor.WHITE)
                .append(Component.text(this.amountLeft, NamedTextColor.YELLOW));

        TextComponent createTimeComponent = Component.text("创建时间: ", NamedTextColor.WHITE)
                .append(Component.text(this.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), NamedTextColor.GREEN));

        TextComponent expireTimeComponent = Component.text("过期时间: ", NamedTextColor.WHITE)
                .append(Component.text(this.expireTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), NamedTextColor.RED));

        TextComponent creatorComponent = Component.text("创建人: ", NamedTextColor.WHITE)
                .append(Component.text(this.creator, NamedTextColor.BLUE));

        componentBuilder.append(noteComponent, Component.newline());
        componentBuilder.append(cdkComponent, Component.newline());
        componentBuilder.append(leftComponent, Component.newline());
        componentBuilder.append(createTimeComponent, Component.newline());
        componentBuilder.append(expireTimeComponent, Component.newline());
        componentBuilder.append(creatorComponent);

        return componentBuilder.build();
    }
}
