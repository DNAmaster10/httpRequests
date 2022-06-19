package me.dnamaster10.httprequests;

import me.dnamaster10.httprequests.Commands.ReloadConfig;
import me.dnamaster10.httprequests.Commands.SendHttpChecks;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class HttpRequests extends JavaPlugin {
    private static HttpRequests plugin;
    public static String[] command_args;
    public static final List<String> url_last_request_ms = new ArrayList<>();
    public static int general_last_request_ms = 1;
    public static String result;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        getLogger().info("HttpRequests has finished loading!");
        plugin = this;
    }

    @Override
    public void onDisable() {
        System.out.println("HttpRequests shutdown successfully");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("httpreload")) {
            ReloadConfig.Reload(plugin, sender);
        }
        else if (command.getName().equalsIgnoreCase("httpsend")) {
            SendHttpChecks.sendHTTPCommand(plugin, sender, command, label, args);
        }
        return true;
    }
}