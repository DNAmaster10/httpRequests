package me.dnamaster10.httprequests;

import me.dnamaster10.httprequests.Commands.CommandChecks;
import me.dnamaster10.httprequests.Commands.ReloadConfig;
import me.dnamaster10.httprequests.Commands.ResponseHandle;
import me.dnamaster10.httprequests.Tasks.EveryTick;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public final class HttpRequests extends JavaPlugin {
    public static HttpRequests plugin;
    @Override
    public void onEnable() {
        plugin = this;
        //Generate the file path for the js file
        String jsFilePath = "." + File.separator + "plugins" + File.separator +"HttpRequests" + File.separator + "requestProcessing.js";
        File jsFile = new File(jsFilePath);
        try {
            //If the file does not exist, create it
            jsFile.getParentFile().mkdirs();
            if (jsFile.createNewFile()) {
                FileWriter writer = new FileWriter(jsFilePath);
                writer.write("function main (httpResponseCode, httpResponseBody, httpUrl, senderType, senderName) {");
                writer.write(System.lineSeparator());
                writer.write("  return 'nothing';");
                writer.write(System.lineSeparator());
                writer.write("}");
                writer.close();
                plugin.getLogger().warning("No js processing file was found, so one was created at " + jsFilePath);
            }
        } catch (IOException e) {
            //Else, throw an error to server log
            plugin.getLogger().warning("Error: " + e);
        }
        //Load the js file
        ResponseHandle.loadScript();
        //Generate the file path for the whitelist file
        String whitelistFilePath = "." + File.separator + "plugins" + File.separator + "HttpRequests" + File.separator + "whitelist.txt";
        File whitelistFile = new File(whitelistFilePath);
        try {
            //If the file does not exist, create it
            whitelistFile.getParentFile().mkdirs();
            if (whitelistFile.createNewFile()) {
                FileWriter writer = new FileWriter(whitelistFilePath);
                writer.write("http://www.example.com");
                plugin.getLogger().warning("No whitelist file was found, so one was created at " + whitelistFilePath);
            }
        } catch (IOException e) {
            //Else, throw an error to server log
            plugin.getLogger().warning("Error: " + e);
        }
        //Generate the file path for the blacklist file
        String blacklistFilePath = "." + File.separator + "plugins" + File.separator + "HttpRequests" + File.separator + "blacklist.txt";
        File blacklistFile = new File(blacklistFilePath);
        try {
            //If the file does not exist, create it
            blacklistFile.getParentFile().mkdirs();
            if (blacklistFile.createNewFile()) {
                FileWriter writer = new FileWriter(blacklistFile);
                writer.write("http://www.example.com");
                plugin.getLogger().warning("No blacklist file was found, so one was created at " + blacklistFilePath);
            }
        }
        catch (IOException e) {
            plugin.getLogger().warning("Error: " + e);
        }
        //Generate the file path for the response whitelist file
        String responseWhitelistFilePath = "." + File.separator + "plugins" + File.separator + "HttpRequests" + File.separator + "responseWhitelist.txt";
        File responseWhitelistFile = new File(responseWhitelistFilePath);
        try {
            responseWhitelistFile.getParentFile().mkdirs();
            if (responseWhitelistFile.createNewFile()) {
                FileWriter writer = new FileWriter(responseWhitelistFile);
                writer.write("http://example.com");
                plugin.getLogger().warning("No response whitelist file was found, so one was created at " + responseWhitelistFilePath);
            }
        }
        catch (Exception e) {
            plugin.getLogger().warning("Error: " + e);
        }
        //Generate the file path for the response blacklist file
        String responseBlacklistFilePath = "." + File.separator + "plugins" + File.separator + "HttpRequests" + File.separator + "responseBlacklist.txt";
        File responseBlacklistFile = new File(responseBlacklistFilePath);
        try {
            responseBlacklistFile.getParentFile().mkdirs();
            if (responseBlacklistFile.createNewFile()) {
                FileWriter writer = new FileWriter(responseBlacklistFile);
                writer.write("http://example.com");
                plugin.getLogger().warning("No response blacklist file was found, so one was created at " + responseBlacklistFilePath);
            }
        }
        catch (Exception e) {
            plugin.getLogger().warning("Error: " + e);
        }

        getConfig().options().copyDefaults();
        saveDefaultConfig();
        if (getConfig().getInt("MaxQueueSize") < 0 && getConfig().getInt("MaxQueueSize") != -1) {
            getLogger().warning("MaxQueueSize invalid in config. An infinite queue size will be used.");
        }
        if (getConfig().getInt("MaxQueueSize") == 0) {
            getLogger().warning("MaxQueueSize is set to 0. Requests will never be sent!");
        }

        //Load blacklist and whitelist
        if (plugin.getConfig().getBoolean("UseWhitelist")) {
            Whitelist.loadWhitelist();
        }
        if (plugin.getConfig().getBoolean("UseBlacklist")) {
            Blacklist.loadBlacklist();
        }

        //Configure looping task
        BukkitTask loopingTask = new EveryTick(this).runTaskTimer(this, 40L, 1L);

        getLogger().info("HttpRequests has finished loading!");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Successfully shutdown HTTP requests.");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        switch (command.getName().toLowerCase()) {
            case "httpsend" -> {
                //Check command syntax
                if (!CommandChecks.checkHttpSend(sender, args)) {
                    return true;
                }
                //If the syntax and permissions are fine, create a new request object
                //and add the request to the queue
                Request request = new Request(sender, args);

                //Now check that the url is / isn't in the black list or whitelist
                if (plugin.getConfig().getBoolean("UseWhitelist") && !Whitelist.checkWhitelist(request.destination)) {
                    Utilities.returnWarning(sender, "Destination address not present in whitelist", "A request destined for " + request.destination + " was aborted as it was not present in the whitelist.");
                    return true;
                }
                if (plugin.getConfig().getBoolean("UseBlacklist") && Blacklist.checkBlacklist(request.destination)) {
                    Utilities.returnWarning(sender, "Destination address was present in whitelist", "A request destined for " + request.destination + " was aborted as it was present in the blacklist.");
                    return true;
                }
                //If not, add the request to the queue
                Queue.addRequest(request);
            }
            case "httpreload" -> {
                //Check the syntax and permissions
                if (!CommandChecks.checkReload(sender)) {
                    return true;
                }
                Utilities.returnInfo(sender, "Reloading HTTPrequests config...");
                ReloadConfig.Reload(plugin, sender);
            }
            case "httpreloadwhitelist" -> {
                //Check the syntax and permissions
                Utilities.returnInfo(sender, "Reloading HTTPRequests whitelist...");
                Whitelist.reloadWhitelist(sender);
            }
            case "httpreloadblacklist" -> {
                //Check the syntax and permissions
                Utilities.returnInfo(sender, "Reloading HTTPRequests blacklist...");
                Blacklist.reloadBlacklist(sender);
            }
            case "httpreloadresponsewhitelist" -> {
                //Check the syntax and permissions
                Utilities.returnInfo(sender, "Reloading HTTPRequests response whitelist...");
                ResponseWhitelist.reloadWhitelist(sender);
            }
            case "httpreloadresponseblacklist" -> {
                //Check the syntax and permissions
                Utilities.returnInfo(sender, "Reloading HTTPRequests response blacklist...");
                ResponseBlacklist.reloadBlacklist(sender);
            }
            case "httpreloadscript" -> {
                //Check syntax and permissions
                Utilities.returnInfo(sender, "Reloading HTTPRequests response processing script...");
                ResponseHandle.reloadScript(sender);
            }
        }
        return true;
    }
}