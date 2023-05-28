package me.dnamaster10.httprequests;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.HashSet;

public class ResponseWhitelist {
    private static final HttpRequests plugin = HttpRequests.plugin;
    private static HashSet<String> whitelist = new HashSet<>();

    public static boolean loadWhitelist() {
        //Get the whitelist file
        try {
            String whitelistFilePath = "." + File.separator + "plugins" + File.separator + "HttpRequests" + File.separator + "responseWhitelist.txt";
            //Set up buffered reader
            BufferedReader reader = new BufferedReader(new FileReader(whitelistFilePath));

            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    line = line.replace("\n", "").replace("\r", "");
                    URL url = new URL(line);
                    String domain = url.getAuthority();
                    whitelist.add(domain);
                }
                catch (Exception e) {
                    plugin.getLogger().warning("Malformed URL in response whitelist at line " + lineNumber);
                }
            }
        }
        catch (Exception e) {
            plugin.getLogger().severe("Failed to load response whitelist: " + e);
            return false;
        }
        return true;
    }
    public static void clearWhitelist() {
        whitelist.clear();
    }
    public static void reloadWhitelist(CommandSender sender) {
        clearWhitelist();
        if (loadWhitelist()) {
            if (sender instanceof Player p) {
                p.sendMessage(ChatColor.GREEN + "Successfully reloaded the response whitelist!");
            }
            plugin.getLogger().info("Successfully reloaded the response whitelist");
        }
        else {
            if (sender instanceof Player p) {
                p.sendMessage(ChatColor.RED + "Failed to reload the response whitelist! Check server logs for more details.");
            }
            plugin.getLogger().severe("Failed to reload the response whitelist! Check server logs for more details.");
        }
    }
    public static boolean checkWhitelist(String address) {
        try {
            URL url = new URL(address);
            if (!whitelist.contains(url.getAuthority())) {
                return false;
            }
        }
        catch (Exception e) {
            if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                plugin.getLogger().warning("Failed to do response whitelist check for a request. Possible malformed URL. URL: " + address);
            }
            return false;
        }
        return true;
    }
}
