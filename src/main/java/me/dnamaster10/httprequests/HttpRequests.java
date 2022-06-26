package me.dnamaster10.httprequests;

import me.dnamaster10.httprequests.Commands.ReloadConfig;
import me.dnamaster10.httprequests.Commands.SendHttpChecks;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class HttpRequests extends JavaPlugin {
    private static HttpRequests plugin;
    public static String[] command_args;
    public static final List<String> url_last_request_ms = new ArrayList<>();
    public static int general_last_request_ms = 1;

    @Override
    public void onEnable() {
        plugin = this;
        String jsFilePath = "." + File.separator + "plugins" + File.separator +"HttpRequests" + File.separator + "requestProcessing.js";
        File jsFile = new File(jsFilePath);
        try {
            jsFile.getParentFile().mkdirs();
            if (jsFile.createNewFile()) {
                FileWriter writer = new FileWriter(jsFilePath);
                writer.write("function main (httpResponseCode, httpResponseBody, httpUrl, sender) {");
                writer.write(System.lineSeparator());
                writer.write("  return 'nothing';");
                writer.write(System.lineSeparator());
                writer.write("}");
                writer.close();
                plugin.getLogger().warning("No js processing file was found, so one was created at " + jsFilePath);
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Error: " + e);
        }
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        getLogger().info("HttpRequests has finished loading!");
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
            SendHttpChecks.sendHTTPCommand(plugin, sender, args);
        }
        return true;
    }
}