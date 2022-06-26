package me.dnamaster10.httprequests.Commands;

import me.dnamaster10.httprequests.HttpRequests;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class ResponseHandle extends JavaPlugin {
    public static void HandleResponse(HttpRequests plugin, String response, Integer responseCode, String[] command_args, CommandSender sender) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            boolean shouldContinue = true;
            String js = null;
            String commandSender;

            if (plugin.getConfig().getBoolean("UseResponseWhitelist")) {
                String[] whitelist = Objects.requireNonNull(plugin.getConfig().getString("ResponseProcessingWhitelist"), "Expression 'getString(\"ResponseProcessingWhitelist\")' must not be null").split(",");
                boolean inWhitelist = false;
                for (String s : whitelist) {
                    if (command_args[1].contains(s)) {
                        inWhitelist = true;
                        break;
                    }
                }
                if (!inWhitelist) {
                    shouldContinue = false;
                }
            }
            if (plugin.getConfig().getBoolean("UseResponseBlacklist")) {
                String[] blacklist = Objects.requireNonNull(plugin.getConfig().getString("ResponseProcessingBlacklist"), "Expression 'getString(\"ResponseProcessingBlacklist\")' must not be null").split(",");
                boolean inBlacklist = false;
                for (String s : blacklist) {
                    if (command_args[1].contains(s)) {
                        inBlacklist = true;
                        break;
                    }
                }
                if (inBlacklist) {
                    shouldContinue = false;
                }
            }

            if (sender instanceof Player p) {
                commandSender = p.getName();
            }
            else if (sender instanceof ConsoleCommandSender) {
                commandSender = "console";
            }
            else {
                commandSender = "command_block";
            }

            ScriptEngineFactory sef = new NashornScriptEngineFactory();
            ScriptEngine engine = sef.getScriptEngine();
            try {
                js = Files.readString(Paths.get("." + File.separator + "plugins" + File.separator + "HttpRequests" + File.separator + "requestProcessing.js"));
            } catch (IOException e) {
                plugin.getLogger().severe("No processing js file found, but processing is enabled");
                shouldContinue = false;
            }
            if (js == null || js.equals("")) {
                plugin.getLogger().warning("Request processing js file is empty");
                shouldContinue = false;
            }
            if (shouldContinue) {
                try {
                    engine.eval(js);
                    Invocable invocable = (Invocable) engine;

                    Object result = invocable.invokeFunction("main", responseCode, response, command_args[1], commandSender);
                    String resultText = result.toString();
                    if (!resultText.equals("nothing")) {
                        if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                            plugin.getLogger().info("Command: " + resultText + " is being run, as specified by request processing");
                        }
                        ConsoleCommandSender console = plugin.getServer().getConsoleSender();
                        plugin.getServer().dispatchCommand(console, resultText);
                    }
                } catch (ScriptException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
