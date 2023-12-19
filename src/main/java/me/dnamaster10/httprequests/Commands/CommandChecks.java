package me.dnamaster10.httprequests.Commands;

import jdk.jshell.execution.Util;
import me.dnamaster10.httprequests.HttpRequests;
import me.dnamaster10.httprequests.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;

import java.net.URI;

public class CommandChecks {
    private static final HttpRequests plugin = HttpRequests.plugin;
    //Takes a command and checks that the syntax, logic e.t.c is valid
    public static boolean checkReload(CommandSender sender) {
        //Check permissions
        if (sender instanceof Player p && !p.hasPermission("httprequest.reload")) {
            p.sendMessage(ChatColor.RED + "You do not have permission to perform that action. Please contact a server administrator if you believe this is an error");
            return false;
        }

        if (!plugin.getConfig().getBoolean("AllowReload")) {
            Utilities.returnWarningS(sender, "Reloading is disabled in the config");
            return false;
        }

        //If all checks are passed, return true
        return true;
    }
    public static boolean checkReloadWhitelist(CommandSender sender) {
        //Check permissions
        if (sender instanceof Player p) {
            if (!p.hasPermission("httprequest.reloadwhitelist")) {
                p.sendMessage(ChatColor.RED + "You do not have permission to perform that action. Please contact a server administrator if you believe this is an error");
                return false;
            }
        }
        if (!plugin.getConfig().getBoolean("AllowWhitelistReload")) {
            Utilities.returnWarningS(sender, "Reloading the whitelist is disabled in the config");
            return false;
        }
        //If all checks are passed, return true
        return true;
    }
    public static boolean checkReloadBlacklist(CommandSender sender) {
        //Check permissions
        if (sender instanceof Player p) {
            if (!p.hasPermission("httprequest.reloadblacklist")) {
                p.sendMessage(ChatColor.RED + "You do not have permission to perform that action. Please contact a server administrator if you believe this is an error");
                return false;
            }
        }
        if (!plugin.getConfig().getBoolean("AllowBlacklistReload")) {
            Utilities.returnWarningS(sender, "Reloading the blacklist is disabled in the config");
            return false;
        }
        //If all checks are passed, return true
        return true;
    }
    public static boolean checkReloadResponseWhitelist(CommandSender sender) {
        if (sender instanceof Player p) {
            if (!p.hasPermission("httprequest.reloadresponsewhitelist")) {
                p.sendMessage(ChatColor.RED + "You do not have permission to perform that action. Please contact an administrator if you believe this is an error");
                return false;
            }
        }
        if (!plugin.getConfig().getBoolean("AllowResponseWhitelistReload")) {
            Utilities.returnWarningS(sender, "Reloading the response whitelist is disabled in the config");
            return false;
        }
        //If all checks are passed, return true
        return true;
    }
    public static boolean checkReloadResponseBlacklist(CommandSender sender) {
        if (sender instanceof Player p) {
            if (!p.hasPermission("httprequest.reloadresponseblacklist")) {
                p.sendMessage(ChatColor.RED + "You do not have permission to perform that action. Please contact an administrator if you believe this is an error");
                return false;
            }
        }
        if (!plugin.getConfig().getBoolean("AllowResponseBlacklistReload")) {
            Utilities.returnWarningS(sender, "Reloading the response blacklist is disabled in the config");
            return false;
        }
        return true;
    }
    public static boolean checkScriptReload(CommandSender sender) {
        if (sender instanceof Player p) {
            if (!p.hasPermission("httprequest.reloadscript")) {
                p.sendMessage(ChatColor.RED + "You do not have permission to perform that action. Please contact an administrator if you believe this is an error");
                return false;
            }
        }
        if (!plugin.getConfig().getBoolean("AllowScriptReload")) {
            Utilities.returnWarningS(sender, "Reloading the response processing script is disabled in the config");
            return false;
        }
        return true;
    }
    public static boolean checkHttpSend(CommandSender sender, String[] args) {
        //Check an HTTP send request
        //Checks only syntax and permissions

        //Are requests enabled?
        if (!plugin.getConfig().getBoolean("AllowRequest")) {
            Utilities.returnWarningS(sender, "HTTP requests are disabled in the config");
            return false;
        }
        //Check sender and sender permissions
        if (sender instanceof BlockCommandSender) {
            if (!plugin.getConfig().getBoolean("AllowCommandBlockSender")) {
                return false;
            }
        }
        else if (sender instanceof Player p) {
            if (!plugin.getConfig().getBoolean("AllowChatSender")) {
                p.sendMessage(ChatColor.RED + "Sending HTTP requests from chat is disabled on this server");
                return false;
            }
        }
        else if (sender instanceof ConsoleCommandSender) {
            if (!plugin.getConfig().getBoolean("AllowConsoleSender")) {
                plugin.getLogger().warning("Sending HTTP requests from console is disabled on this server");
                return false;
            }
        }
        else if (sender instanceof CommandMinecart) {
            if (!plugin.getConfig().getBoolean("AllowCommandBlockMinecartSender")) {
                return false;
            }
        }
        else {
            if (!plugin.getConfig().getBoolean("AllowOtherSenders")) {
                return false;
            }
        }
        //Check permissions
        if (sender instanceof Player p) {
            if (!p.hasPermission("httprequest.sendhttp")) {
                p.sendMessage(ChatColor.RED + "You do not have permission to perform that action. Contact an administrator if you believe this is an error.");
                return false;
            }
        }

        //Command syntax checks
        if (args.length < 2) {
            Utilities.returnWarningS(sender, "Syntax: /httpsend [GET/POST] [destination] [name1=value&name2=value2");
            return false;
        }
        //Check permission for GET or POST
        if (args[0].equals("POST")) {
            if (!plugin.getConfig().getBoolean("AllowPost")) {
                Utilities.returnWarningS(sender, "Post requests are disabled in the config");
                return false;
            }
        }
        else if (args[0].equals("GET")) {
            if (!plugin.getConfig().getBoolean("AllowGet")) {
                Utilities.returnWarningS(sender, "Get requests are disabled in the config");
                return false;
            }
        }
        else {
            Utilities.returnWarningS(sender, "Request type '" + args[0] + "' is invalid, or not currently supported");
            return false;
        }
        //Check encoding type
        if (args.length > 3 && !args[3].equals("application/json")) {
            Utilities.returnWarningS(sender, "Encoding type not recognised or not yet supported");
            return false;
        }
        //If all checks are passed, return true
        return true;
    }
}
