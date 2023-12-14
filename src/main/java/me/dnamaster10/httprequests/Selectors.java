package me.dnamaster10.httprequests;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.regex.Pattern;

public class Selectors {
    //Contains methods for handling target selectors (@a, @p e.t.c)
    static Plugin p = HttpRequests.plugin;
    private static String replaceSelectors(String value, String selector, String finalString) {
        //Takes in a selector and a string, replaces unescaped selectors with final string.
        return Pattern.compile("(?<!\\\\)" + Pattern.quote(selector))
                .matcher(value)
                .replaceAll(finalString);
    }
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
                String playersString = players.substring(0, players.length() - 1);
                value = replaceSelectors(value, "@ad", playersString);
            }
            if (value.contains("@an")) {
                //Replace actual names
                StringBuilder players = new StringBuilder();
                for (Player player : p.getServer().getOnlinePlayers()) {
                    players.append(player.getName()).append(",");
                }
                //Build string and remove last comma
                String playersString = players.substring(0, players.length() - 1);
                value = replaceSelectors(value, "@an", playersString);
            }
            if (value.contains("@au")) {
                //Replace UUIDs
                StringBuilder players = new StringBuilder();
                for (Player player : p.getServer().getOnlinePlayers()) {
                    players.append(player.getUniqueId()).append(",");
                }
                //Build string and remove last comma
                String playersString = players.substring(0, players.length() - 1);
                value = replaceSelectors(value, "@au", playersString);
            }
        }
        return value;
    }
}