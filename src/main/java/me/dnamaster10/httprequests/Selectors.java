package me.dnamaster10.httprequests;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Selectors {
    //Contains methods for handling target selectors (@a, @p e.t.c)
    static Plugin p = HttpRequests.plugin;
    //Used to select characters between "|" characters where the first one is not escaped with a backslash
    private static final Pattern betweenSelector = Pattern.compile("(?<!\\\\\\\\)\\\\|([^\\\\|]+)\\\\|");
    private static String replaceSelectors(String value, String selector, String finalString) {
        //Takes in a selector and a string, replaces unescaped selectors with final string.
        String pattern = "(?<!\\\\)\\|(" + selector + ")\\|";
        return (value.replaceAll(pattern, finalString));
    }
    private static ArrayList<String> getOnlinePlayersD() {
        //Returns an array list of online player display names
        ArrayList<String> players = new ArrayList<>();
        for (Player player : p.getServer().getOnlinePlayers()) {
            players.add(player.getDisplayName());
        }
        return players;
    }
    private static ArrayList<String> getOnlinePlayersN() {
        //Returns an array list of online player names
        ArrayList<String> players = new ArrayList<>();
        for (Player player : p.getServer().getOnlinePlayers()) {
            players.add(player.getName());
        }
        return players;
    }
    private static ArrayList<String> getOnlinePlayersU() {
        //Returns an array list of online player UUIDs
        ArrayList<String> players = new ArrayList<>();
        for (Player player : p.getServer().getOnlinePlayers()) {
            players.add(String.valueOf(player.getUniqueId()));
        }
        return players;
    }
    public static String replaceSelectors(String value) {
        //Contains all matches, used to check whether a match has already been processed
        ArrayList<String> matches = new ArrayList<>();

        Matcher matcher = betweenSelector.matcher(value);
        while (matcher.find()) {
            //For every selector, handle and replace
            String match = matcher.group(1);
            if (matches.contains(match)) {
                continue;
            }
            switch(match) {
                case "@ad" -> {
                    //Get list of player display names and replace in string
                    value = replaceSelectors(value, "@ad", String.join(",", getOnlinePlayersD()));
                }
                case "@an" -> {
                    //Get list of all actual player names and replace in string
                    value = replaceSelectors(value, "@an", String.join(",", getOnlinePlayersN()));
                }
                case "@au" -> {
                    //Get a list of all player UUIDs and replace in string
                    value = replaceSelectors(value, "@au", String.join(",", getOnlinePlayersU()));
                }
            }
            //Add this match to the matches array
            matches.add(matcher.group(1));
        }
        return value;
    }
}