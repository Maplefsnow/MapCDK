package me.maplef.mapcdk;

import com.alibaba.fastjson.JSONArray;
import me.maplef.mapcdk.utils.CDKGenerator;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;

public class CDK {
    private String cdkString;
    private String creator;
    private LocalDateTime createTime;
    private LocalDateTime expireTime;
    private int numbersLeft;

    private List<ItemStack> rewardItems;
    private List<String> rewardCmds;

    public CDK(String creator){
        this.cdkString = CDKGenerator.generateCDK(10);
        this.creator = creator;
        this.createTime = LocalDateTime.now();
        this.expireTime = this.createTime.plusMonths(1);
        this.numbersLeft = 10;

        this.rewardItems = new ArrayList<>();
        this.rewardCmds = new ArrayList<>();
    }

    public CDK(String creator, LocalDateTime createTime, LocalDateTime expireTime, int numbersLeft, List<ItemStack> rewardItems, List<String> rewardCmds){
        this.cdkString = CDKGenerator.generateCDK(10);
        this.creator =creator;
        this.createTime = createTime;
        this.expireTime = expireTime;
        this.numbersLeft = numbersLeft;

        this.rewardItems = rewardItems;
        this.rewardCmds = rewardCmds;
    }

    public void addExpireTime(long amoutToAdd, TemporalUnit unit) {
        this.expireTime = this.expireTime.plus(amoutToAdd, unit);
    }

    public void minusExpireTime(long amoutToMinus, TemporalUnit unit) throws IllegalArgumentException {
        if(this.expireTime.minus(amoutToMinus, unit).isBefore(this.createTime)) {
            throw new IllegalArgumentException();
        } else {
            this.expireTime = this.expireTime.minus(amoutToMinus, unit);
        }
    }

    public void setNumbersLeft(int numberToSet) {
        this.numbersLeft = numberToSet;
    }

    public void addNumbersLeft(int numberToAdd) throws IllegalArgumentException {
        if(this.numbersLeft + numberToAdd <= 0) {
            throw new IllegalArgumentException();
        } else {
            this.numbersLeft += numberToAdd;
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

    public int getNumbersLeft() {
        return this.numbersLeft;
    }

    public void exportToJSON(File path) throws IOException{
        JSONObject cdkJson = new JSONObject();

        JSONObject cdkInfo = new JSONObject();
        cdkInfo.put("cdk_string", this.cdkString);
        cdkInfo.put("create_time", this.createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        cdkInfo.put("expire_time", this.expireTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        cdkInfo.put("creator", this.creator);
        cdkInfo.put("numbers_left", this.numbersLeft);

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
        bw.write(cdkJson.toJSONString());
        bw.close();
    }

    public void exportToDataBase(Connection c) throws SQLException {
        PreparedStatement ps = c.prepareStatement("INSERT INTO cdk_info (cdk_string, numbers_left, create_time, expire_time, creator)" +
                " VALUES (?, ?, ?, ?, ?)");

        ps.setString(1, this.cdkString);
        ps.setInt(2, this.numbersLeft);
        ps.setTimestamp(3, Timestamp.valueOf(this.expireTime));
        ps.setTimestamp(4, Timestamp.valueOf(this.expireTime));
        ps.setString(5, this.creator);

        ps.execute();

        ps = c.prepareStatement("INSERT INTO cdk_reward (cdk_string, material, number)" +
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
}
