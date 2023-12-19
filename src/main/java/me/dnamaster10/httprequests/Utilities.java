package me.dnamaster10.httprequests;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class Utilities {
    //For commonly used methods across the plugin
    public static void returnError(CommandSender sender, String error, String consoleError) {
        //Returns an error to the player and / or console
        //Should be used when things break, e.g. NOT when a request fails to send
        if (sender instanceof Player p) {
            p.sendMessage(ChatColor.RED + error);
        }
        else if (sender instanceof ConsoleCommandSender) {
            HttpRequests.plugin.getLogger().severe(error);
        }
        HttpRequests.plugin.getLogger().severe("An error occurred: " + consoleError);
    }
    public static void returnError(CommandSender sender, String error) {
        returnError(sender, error, error);
    }
    public static void returnWarning(CommandSender sender, String warning, String consoleWarning) {
        //Returns a warning to the player and / or console
        if (sender instanceof Player p) {
            p.sendMessage(ChatColor.RED + warning);
        }
        else if (sender instanceof ConsoleCommandSender) {
            HttpRequests.plugin.getLogger().warning(warning);
        }
        if (HttpRequests.plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
            HttpRequests.plugin.getLogger().warning("Warning: " + consoleWarning);
        }
    }
    public static void returnWarning(CommandSender sender, String warning) {
        //Used to send same warning to console and player
        returnWarning(sender, warning, warning);
    }
    public static void returnWarningS(CommandSender sender, String warning) {
        //Used to send a warning to sender but not console
        if (sender instanceof Player p) {
            p.sendMessage(ChatColor.RED + warning);
        }
        else if (sender instanceof ConsoleCommandSender) {
            HttpRequests.plugin.getLogger().warning(warning);
        }
    }
    public static void returnInfo(CommandSender sender, String info, String consoleInfo) {
        //Returns info to the player and / or console
        if (sender instanceof Player p) {
            p.sendMessage(ChatColor.GREEN + info);
        }
        else if (sender instanceof ConsoleCommandSender) {
            HttpRequests.plugin.getLogger().info(info);
        }
        if (HttpRequests.plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
            HttpRequests.plugin.getLogger().info(consoleInfo);
        }
    }
    public static void returnInfo(CommandSender sender, String info) {
        returnInfo(sender, info, info);
    }
    public static void returnInfoS(CommandSender sender, String info) {
        //Returns info to the sender but not console
        if (sender instanceof Player p) {
            p.sendMessage(ChatColor.GREEN + info);
        }
        else if (sender instanceof ConsoleCommandSender) {
            HttpRequests.plugin.getLogger().info(info);
        }
    }
}
