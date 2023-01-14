package me.maplef.mapcdk.utils;

import me.maplef.mapcdk.CDK;
import me.maplef.mapcdk.Mapcdk;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.rmi.NoSuchObjectException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    ConfigManager configManager = new ConfigManager();
FileConfiguration config = configManager.getConfig();

    private static Connection c = connect();

    public void init() throws SQLException {
        // if(config.getBoolean("use-mysql"))

        PreparedStatement ps = c.prepareStatement(
                "CREATE TABLE IF NOT EXISTS cdk_info (" +
                        "    cdk_string   TEXT     PRIMARY KEY," +
                        "    amount_left  INTEGER  NOT NULL," +
                        "    create_time  DATETIME," +
                        "    expire_time  DATETIME," +
                        "    creator      TEXT," +
                        "    note         TEXT" +
                        ");");
        ps.execute();

        ps = c.prepareStatement(
                "CREATE TABLE IF NOT EXISTS cdk_reward (" +
                        "    cdk_string   TEXT     NOT NULL," +
                        "    material     TEXT     NOT NULL," +
                        "    amount       INTEGER  NOT NULL" +
                        ");");
        ps.execute();

        ps = c.prepareStatement(
                "CREATE TABLE IF NOT EXISTS cdk_command (" +
                        "    cdk_string   TEXT     NOT NULL," +
                        "    command      INTEGER  NOT NULL" +
                        ");");
        ps.execute();

        ps = c.prepareStatement(
                "CREATE TABLE IF NOT EXISTS cdk_receive (" +
                        "    cdk_string   TEXT     NOT NULL," +
                        "    receiver     TEXT     NOT NULL," +
                        "    receive_time DATETIME NOT NULL" +
                        ");");
        ps.execute();

        ps.close();

    }

    private static Connection connect() {
        ConfigManager configManager = new ConfigManager();
        FileConfiguration config = configManager.getConfig();

        final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

        final String MYSQL_HOST = config.getString("mysql-host");
        final String PORT = config.getString("mysql-port");
        final String DB_NAME = config.getString("mysql-database");
        final String DB_URL = "jdbc:mysql://" + MYSQL_HOST + ":" + PORT + "/" + DB_NAME;

        final String USERNAME = config.getString("mysql-username");
        final String PASSWORD = config.getString("mysql-password");

        if(config.getBoolean("use-mysql")){
            Connection conn = null;
            try{
                Class.forName(JDBC_DRIVER);
                conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            } catch (Exception e){
                e.printStackTrace();
            }
            return conn;
        } else {
            String url = "jdbc:sqlite:" + new File(Mapcdk.getInstance().getDataFolder(), "database.db");
            Connection conn = null;
            try {
                conn = DriverManager.getConnection(url);
            } catch (SQLException e) {
                Bukkit.getLogger().warning(e.getClass().getName() + ": " + e.getMessage());
            }
            return conn;
        }
    }

    public static List<CDK> getAllCDKFromDatabase() throws SQLException {
        List<CDK> cdks = new ArrayList<>();

        Statement stmt = c.createStatement();
        ResultSet res = stmt.executeQuery("SELECT * FROM cdk_info");

        CDK cdk;
        while(res.next()) {
            try {
                cdk = new CDK(c, res.getString("cdk_string"));
                cdks.add(cdk);
            } catch (NoSuchObjectException ignored) {}
        }

        return cdks;
    }

    public static List<CDK> getCDKPageFromDatabase(int page) throws SQLException {
        List<CDK> cdks = new ArrayList<>();

        Statement stmt = c.createStatement();
        ResultSet res = stmt.executeQuery(String.format("SELECT * FROM cdk_info LIMIT %d, %d", (page-1) * 27, page * 27));

        CDK cdk;
        while(res.next()) {
            try {
                cdk = new CDK(c, res.getString("cdk_string"));
                cdks.add(cdk);
            } catch (NoSuchObjectException ignored) {}
        }

        return cdks;
    }

    public static void updateDatabase(Connection c, String tableName, String rowName, Object value, String condition) {
        try {
            PreparedStatement ps = c.prepareStatement("UPDATE ? SET ? = '?' WHERE ?");
            ps.setString(1, tableName);
            ps.setString(2, rowName);
            ps.setObject(3, value);
            ps.setString(4, condition);
            ps.execute(); ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void reconnect() {
        c = connect();
    }

    public Connection getC() {
        return c;
    }
}
