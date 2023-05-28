package me.dnamaster10.httprequests.Commands;

import me.dnamaster10.httprequests.HttpRequests;
import me.dnamaster10.httprequests.Request;
import me.dnamaster10.httprequests.ResponseBlacklist;
import me.dnamaster10.httprequests.ResponseWhitelist;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.plugin.java.JavaPlugin;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.*;
import java.io.File;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResponseHandle extends JavaPlugin {
    private static final HttpRequests plugin = HttpRequests.plugin;
    private static final ScriptEngineFactory sef = new NashornScriptEngineFactory();
    private static final ScriptEngine engine = sef.getScriptEngine();
    private static Invocable invocable;
    public static boolean loadScript() {
        //Get the whitelist file
        try {
            String jsFilePath = "." + File.separator + "plugins" + File.separator + "HttpRequests" + File.separator + "requestProcessing.js";
            String js = Files.readString(Paths.get(jsFilePath));
            engine.eval(js);
            invocable = (Invocable) engine;
            return true;
        }
        catch (Exception e) {
            plugin.getLogger().severe("Failed to load script");
            plugin.getLogger().severe("Error: " + e);
            return false;
        }
    }
    public static void reloadScript(CommandSender sender) {
        if (loadScript()) {
            if (sender instanceof Player p) {
                p.sendMessage(ChatColor.GREEN + "Successfully reloaded the script");
            }
            plugin.getLogger().info("Successfully reloaded the script");
        }
        else {
            if (sender instanceof Player p) {
                p.sendMessage(ChatColor.RED + "Failed to reload the script! Check server logs for more details.");
            }
            plugin.getLogger().severe("Failed to reload the script! Check server logs for more details.");
        }
    }
    public static void handle(Request request, HttpResponse<String> response) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                plugin.getLogger().info("A request which was sent to " + request.destination + " returned status code: " + response.statusCode());
            }
            if (request.sender instanceof Player p) {
                if (plugin.getConfig().getBoolean("SendResponseCodeToPlayer")) {
                    p.sendMessage(ChatColor.GREEN + "Response code: " + response.statusCode());
                }
                if (plugin.getConfig().getBoolean("SendResponseBodyToPlayer")) {
                    p.sendMessage(ChatColor.GREEN + "Response body: " + response.body());
                }
            }
            else if (request.sender instanceof ConsoleCommandSender) {
                plugin.getLogger().info("Response code: " + response.statusCode());
                plugin.getLogger().info("Response body: " + response.body());
            }

            //Check whether the response should be processed
            if (!plugin.getConfig().getBoolean("AllowResponseProcessing")) {
                return;
            }

            //Check the whitelist and the blacklist
            if (plugin.getConfig().getBoolean("UseResponseWhitelist")) {
                if (!ResponseWhitelist.checkWhitelist(request.destination)) {
                    return;
                }
            }
            else if (plugin.getConfig().getBoolean("UseResponseBlacklist")) {
                if (ResponseBlacklist.checkBlacklist(request.destination)) {
                    return;
                }
            }

            //Process the response
            String senderType;
            String commandSender;
            if (request.sender instanceof Player p) {
                senderType = "player";
                commandSender = p.getName();
            }
            else if (request.sender instanceof ConsoleCommandSender) {
                senderType = "console";
                commandSender = "console";
            }
            else if (request.sender instanceof CommandBlock) {
                senderType = "commandBlock";
                commandSender = "commandBlock";
            }
            else if (request.sender instanceof CommandMinecart) {
                senderType = "commandMinecart";
                commandSender = "commandMinecart";
            }
            else {
                senderType = "other";
                commandSender = "other";
            }

            //Execute the javascript file
            try {
                Object result = invocable.invokeFunction("main", response.previousResponse(), response.body(), request.destination, senderType, commandSender);
                String resultText = result.toString();
                if (resultText.equals("nothing")) {
                    return;
                }
                String[] resultTextSplit = resultText.split(":", 2);
                if (resultTextSplit.length < 2) {
                    plugin.getLogger().warning("Response processing aborted due to invalid return text");
                    return;
                }
                switch(resultTextSplit[0]) {
                    case "command" -> {
                        if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                            plugin.getLogger().info("Command: " + resultTextSplit[1] + " is being run, as specified by response processing");
                        }
                        ConsoleCommandSender console = plugin.getServer().getConsoleSender();
                        plugin.getServer().dispatchCommand(console, resultTextSplit[1]);
                    }
                }
            } catch (ScriptException e) {
                plugin.getLogger().severe("An error occurred in the javascript file: " + e);
            } catch (NoSuchMethodException e) {
                plugin.getLogger().severe("No function 'main' found in javascript file");
            }

        });
    }
}
