package me.dnamaster10.httprequests.Commands;

import me.dnamaster10.httprequests.HttpRequests;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class ReloadConfig {
    public static void Reload(HttpRequests plugin, CommandSender sender) {
        plugin.reloadConfig();
        plugin.getLogger().info("Successfully reloaded config");
        if (sender instanceof Player p) {
            p.sendMessage(ChatColor.GREEN + "Successfully reloaded config");
        }
    }
}
