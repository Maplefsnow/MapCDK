package me.maplef.mapcdk.utils;

import me.maplef.mapcdk.Mapcdk;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database {
    ConfigManager configManager = new ConfigManager();
    FileConfiguration config = configManager.getConfig();

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    final String MYSQL_HOST = config.getString("mysql-host");
    final String PORT = config.getString("mysql-port");
    final String DB_NAME = config.getString("mysql-database");
    final String DB_URL = "jdbc:mysql://" + MYSQL_HOST + ":" + PORT + "/" + DB_NAME;

    final String USERNAME = config.getString("mysql-username");
    final String PASSWORD = config.getString("mysql-password");

    private Connection c = connect();

    public void init() throws SQLException {
        if(config.getBoolean("use-mysql")){
            PreparedStatement ps = c.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS cdk_info (" +
                            "    cdk_string   TEXT     PRIMARY KEY," +
                            "    numbers_left INTEGER  NOT NULL," +
                            "    create_time  DATETIME," +
                            "    expire_time  DATETIME," +
                            "    creator      TEXT" +
                            ");");
            ps.execute();

            ps.close();
        } else {
            PreparedStatement ps = c.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS cdk_info (" +
                            "    cdk_string   TEXT     PRIMARY KEY," +
                            "    numbers_left INTEGER  NOT NULL," +
                            "    create_time  DATETIME," +
                            "    expire_time  DATETIME," +
                            "    creator      TEXT" +
                            ");");
            ps.execute();

            ps = c.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS cdk_reward (" +
                            "    cdk_string   TEXT     NOT NULL," +
                            "    material     TEXT     NOT NULL," +
                            "    number       INTEGER  NOT NULL" +
                            ");");
            ps.execute();

            ps = c.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS cdk_command (" +
                            "    cdk_string   TEXT     NOT NULL," +
                            "    command      INTEGER  NOT NULL" +
                            ");");
            ps.execute();

            ps.close();
        }
    }

    private Connection connect() {
        FileConfiguration config = configManager.getConfig();

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

    public void reconnect() {
        c = connect();
    }

    public Connection getC() {
        return c;
    }
}
