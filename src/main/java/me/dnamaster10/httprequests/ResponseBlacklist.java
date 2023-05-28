package me.dnamaster10.httprequests;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.HashSet;

public class ResponseBlacklist {
    private static final HttpRequests plugin = HttpRequests.plugin;
    private static HashSet<String> blacklist = new HashSet<>();

    public static boolean loadBlacklist() {
        //Get the blacklist file
        try {
            String blacklistFilePath = "." + File.separator + "plugins" + File.separator + "HttpRequests" + File.separator + "responseBlacklist.txt";
            //Set up buffered reader
            BufferedReader reader = new BufferedReader(new FileReader(blacklistFilePath));

            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    line = line.replace("\n", "").replace("\r", "");
                    URL url = new URL(line);
                    String domain = url.getAuthority();
                    blacklist.add(domain);
                }
                catch (Exception e) {
                    plugin.getLogger().warning("Malformed URL in response blacklist at line " + lineNumber);
                }
            }
        }
        catch (Exception e) {
            plugin.getLogger().severe("Failed to load response blacklist: " + e);
            return false;
        }
        return true;
    }
    public static void clearBlacklist() {
        blacklist.clear();
    }
    public static void reloadBlacklist(CommandSender sender) {
        clearBlacklist();
        if (loadBlacklist()) {
            if (sender instanceof Player p) {
                p.sendMessage(ChatColor.GREEN + "Successfully reloaded the response blacklist!");
            }
            plugin.getLogger().info("Successfully reloaded the response blacklist");
        }
        else {
            if (sender instanceof Player p) {
                p.sendMessage(ChatColor.RED + "Failed to reload the response blacklist! Check server logs for more details.");
            }
            plugin.getLogger().severe("Failed to reload the response blacklist! Check server logs for more details.");
        }
    }
    public static boolean checkBlacklist(String address) {
        try {
            URL url = new URL(address);
            if (!blacklist.contains(url.getAuthority())) {
                return false;
            }
        }
        catch (Exception e) {
            if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                plugin.getLogger().warning("Failed to do response blacklist check for a request. Possible malformed URL. URL: " + address);
            }
            return false;
        }
        return true;
    }
}