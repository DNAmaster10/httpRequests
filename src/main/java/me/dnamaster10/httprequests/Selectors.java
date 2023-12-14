package me.dnamaster10.httprequests;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Selectors {
    //Contains methods for handling target selectors (@a, @p e.t.c)
    static Plugin p = HttpRequests.plugin;
    public static String replaceSelectors(String value) {
        //If selectors are present
        if (value.contains("@a")) {
            //Check for subselectors
            if (value.contains("@ad")) {
                //Replace player names
                StringBuilder players = new StringBuilder();
                for (Player player : p.getServer().getOnlinePlayers()) {
                    players.append(player.getDisplayName()).append(",");
                }
                //Build string and remove last comma
                value = value.replace("@ad", players.substring(0, players.length() - 1));
            }
            if (value.contains("@an")) {
                //Replace actual names
                StringBuilder players = new StringBuilder();
                for (Player player : p.getServer().getOnlinePlayers()) {
                    players.append(player.getName()).append(",");
                }
                //Build string and remove last comma
                value = value.replace("@an", players.substring(0, players.length() - 1));
            }
            if (value.contains("@au")) {
                //Replace UUIDs
                StringBuilder players = new StringBuilder();
                for (Player player : p.getServer().getOnlinePlayers()) {
                    players.append(player.getUniqueId()).append(",");
                }
                //Build string and remove last comma
                value = value.replace("@au", players.substring(0, players.length() - 1));
            }
        }
        return value;
    }
}