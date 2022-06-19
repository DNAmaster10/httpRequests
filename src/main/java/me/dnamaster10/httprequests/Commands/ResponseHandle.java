package me.dnamaster10.httprequests.Commands;

import me.dnamaster10.httprequests.HttpRequests;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

import static me.dnamaster10.httprequests.HttpRequests.command_args;

public class ResponseHandle extends JavaPlugin {
    String[] responseBody;
    public static Boolean HandleResponse(HttpRequests plugin, String response, String[] command_args) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            boolean shouldProcess = true;
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
                    shouldProcess = false;
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
                    shouldProcess = false;
                }
            }
            if (shouldProcess) {
                if (plugin.getConfig().isSet("RequestProcessing.Functions")) {
                    String[] functions = Objects.requireNonNull(plugin.getConfig().getString("RequestProcessing.Functions"), "Expression 'getString(\"RequestProcessing.Functions\")' must not be null").split(",");
                    for (String function : functions) {
                        if (plugin.getConfig().isSet("RequestProcessing." + function)) {
                            if (!plugin.getConfig().isSet("RequestProcessing." + function + ".Condition")) {
                                if (plugin.getConfig().isSet("RequestProcessing." + function + ".Action")) {
                                    String actionsString = plugin.getConfig().getString("RequestProcessing." + function + ".Action");

                                }
                            }
                        }
                        else {
                            plugin.getLogger().warning("The function " + function + " is declared, but not defined.");
                        }
                    }
                }
                else {
                    plugin.getLogger().warning("Request Processing is enabled, but no functions are set. Either set a function, or disable Response Processing");
                }
            }
        });
        return true;
    }
}
