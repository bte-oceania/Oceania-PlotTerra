package github.BTECompanion.utils;

import github.BTECompanion.BTECompanion;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class Utils {

    private static boolean driverIsRegistered;

    static {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            Bukkit.getLogger().log(Level.INFO, "MariaDB JDBC Driver Registered!");
            driverIsRegistered = true;
        } catch (ClassNotFoundException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not register MariaDB JDBC Driver!", ex);
        }
    }

    public static Connection getConnection() {
        try {
            if(driverIsRegistered) {
                FileConfiguration config = BTECompanion.getPlugin().getConfig();

                return DriverManager.getConnection(
                        "jdbc:mariadb://"+
                                config.getString("PlotSystem.database.address")
                                + ":"+ config.getString("PlotSystem.database.port")
                                + "/" + config.getString("PlotSystem.database.name"),
                        config.getString("PlotSystem.database.username"),
                        config.getString("PlotSystem.database.password"));
            }
        } catch (SQLException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Connection Failed!", ex);
        }
        return null;
    }

    public static String getFTPURI(int plotid, int plotcityid)
    {
        String address = "address";
        String username = "username";
        String password = "password";
        int port = 21;
        boolean secureFTP = false;
        String defaultPath = "";
        try {
            ResultSet rs = getConnection()
                    .createStatement()
                    .executeQuery("SELECT * FROM servers " +
                            "WHERE servername = 'hub'");
            if(rs.next()) {
                address = rs.getString("address");
                username = rs.getString("username");
                password = rs.getString("password");
                port = Integer.parseInt(rs.getString("port"));
                secureFTP = rs.getInt("secureftp") == 1;
                defaultPath = rs.getString("defaultpath");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return String.format(
                "%sftp://%s:%s@%s:%d/%s;type=i",
                secureFTP ? "s" : "",
                username,
                password,
                address,
                port,
                defaultPath
                        + "plugins/Oceania-PlotSystem/outlines/"
                        + plotcityid
                        + "/"
                        + plotid + ".schematic"
        );
    }

    private static final int BUFFER_SIZE = 4096;

    public static void sendFileFTP(String ftpURL, File schematic)
    {
        try {
            URL url = new URL(ftpURL);
            URLConnection conn = url.openConnection();
            OutputStream outputStream = conn.getOutputStream();
            FileInputStream inputStream = new FileInputStream(schematic);

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            System.out.println("File uploaded");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Player Messages
    private static final String messagePrefix = "§9[§bBTE Oceania§9] §r";

    public static String getInfoMessageFormat(String info) {
        return messagePrefix + "§a" + info;
    }

    public static String getErrorMessageFormat(String error) {
        return messagePrefix + "§c" + error;
    }

}
