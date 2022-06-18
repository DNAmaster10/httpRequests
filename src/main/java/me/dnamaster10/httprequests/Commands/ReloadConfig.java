package me.dnamaster10.httprequests.Commands;

import me.dnamaster10.httprequests.HttpRequests;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;


public class ReloadConfig extends JavaPlugin {
    public static <sender> Boolean Reload(HttpRequests plugin, sender sender) {
        if (sender instanceof Player p) {
            if (p.hasPermission("httprequest.reload")) {
                if (plugin.getConfig().getBoolean("AllowReload")) {
                    p.sendMessage(ChatColor.GREEN + "Reloading config");
                }
                else {
                    p.sendMessage("Reloading the plugin is disabled in the config");
                    return false;
                }
            }
            else {
                p.sendMessage(ChatColor.RED + "You do not have the permissions to perform that action");
                return false;
            }
        }
        if (plugin.getConfig().getBoolean("AllowReload")) {
            plugin.getLogger().info("Reloading HttpRequests config");
        }
        else {
            plugin.getLogger().warning("Reloading the config is disabled in the config");
            return false;
        }
        plugin.reloadConfig();
        plugin.getLogger().info("Successfully reloaded config");
        if (sender instanceof Player p) {
            p.sendMessage(ChatColor.GREEN + "Successfully reloaded config");
        }
        return true;
    }
}
