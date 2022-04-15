package me.dnamaster10.httprequests;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class HttpRequests extends JavaPlugin {
    private static HttpRequests plugin;
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
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("httpsend")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (getConfig().getBoolean("AllowRequest")) {
                    if (p.hasPermission("httprequest.sendhttp")) {
                        if (args.length < 2) {
                            p.sendMessage(ChatColor.RED + "Syntax: /httpsend <GET/POST> <destination> <name1=value1&name2=value2>");
                        }
                        else if (args[0].equals("POST") && !getConfig().getBoolean("AllowPost")) {
                            p.sendMessage(ChatColor.RED + "Post requests are disabled on this server");
                        }
                        else if (args[0].equals("GET") && !getConfig().getBoolean("AllowGet")) {
                            p.sendMessage(ChatColor.RED + "Get requests are disabled on this server");
                        }
                        else {
                            if (getConfig().getBoolean("UseGlobalCooldown")) {
                                if ((int) (System.currentTimeMillis()) - general_last_request_ms < getConfig().getInt("GlobalCooldownMs")) {
                                    p.sendMessage(ChatColor.RED + "Too many requests are being sent at this time");
                                    if (getConfig().getBoolean("PrintRequestsToConsole")) {
                                        getLogger().info("Too many requests are being attempted");
                                    }
                                }
                                else {
                                    command_args = args;
                                    general_last_request_ms = (int) (System.currentTimeMillis());
                                    new sendData(plugin, args);
                                }
                            }
                            else if (getConfig().getBoolean("UseUrlSpecificCooldown")) {
                                Boolean isContainedInArray = false;
                                for (int i = 0; i < url_last_request_ms.size(); i++) {
                                    String current_url_string = url_last_request_ms.get(i);
                                    if (current_url_string.contains(args[1])) {
                                        isContainedInArray = true;
                                        String[] current_url = url_last_request_ms.get(i).split(",");
                                        Long temp = Long.valueOf(current_url[1]);
                                        Long current_time_int = System.currentTimeMillis();
                                        if (current_time_int - temp < getConfig().getInt("UrlSpecificCooldown")) {
                                            p.sendMessage(ChatColor.RED + "Too many requests are being send to this URL!");
                                            if (getConfig().getBoolean("PrintRequestsToConsole")) {
                                                getLogger().warning("Too many requests are being sent to " + args[1]);
                                            }
                                            break;
                                        }
                                        else {
                                            url_last_request_ms.set(i, args[1] + "," + (System.currentTimeMillis()));
                                            command_args = args;
                                            new sendData(plugin, args);
                                            break;
                                        }
                                    }
                                }
                                if (!isContainedInArray) {
                                    url_last_request_ms.add(args[1] + "," + (System.currentTimeMillis()));
                                    command_args = args;
                                    new sendData(plugin, args);
                                }
                            }
                            else {
                                command_args = args;
                                new sendData(plugin, args);
                            }
                        }
                    } else {
                        p.sendMessage(ChatColor.RED + "You need the permission httprequest.sendhttp to perform that command");
                    }
                }
                else {
                    p.sendMessage(ChatColor.RED + "HTTP requests are disabled in the config");
                }
            } else if (sender instanceof ConsoleCommandSender) {
                if (getConfig().getBoolean("AllowRequest")) {
                    if (args.length < 2) {
                        getLogger().warning("Syntax: httpsend <request type> <destination> <name1:value1,name2:value2>");
                    }
                    else if (args[0].equals("POST") && !getConfig().getBoolean("AllowPost")) {
                        getLogger().warning("POST requests are disabled in the config");
                    }
                    else if (args[0].equals("GET") && !getConfig().getBoolean("AllowGet")) {
                        getLogger().warning("GET requests are disabled in the config.");
                    }
                    else {
                        if (getConfig().getBoolean("UseGlobalCooldown")) {
                            if ((int) (System.currentTimeMillis()) - general_last_request_ms < getConfig().getInt("GlobalCooldownMs")) {
                                getLogger().warning("Too many requests are being attempted");

                            }
                            else {
                                command_args = args;
                                general_last_request_ms = (int) (System.currentTimeMillis());
                                new sendData(plugin, args);
                            }
                        }
                        else if (getConfig().getBoolean("UseUrlSpecificCooldown")) {
                            Boolean isContainedInArray = false;
                            for (int i = 0; i < url_last_request_ms.size(); i++) {
                                String current_url_string = url_last_request_ms.get(i);
                                if (current_url_string.contains(args[1])) {
                                    isContainedInArray = true;
                                    String[] current_url = url_last_request_ms.get(i).split(",");
                                    Long temp = Long.valueOf(current_url[1]);
                                    Long current_time_int = System.currentTimeMillis();
                                    if (current_time_int - temp < getConfig().getInt("UrlSpecificCooldown")) {
                                        getLogger().warning("Too many requests are being sent to " + args[1]);
                                        break;
                                    }
                                    else {
                                        url_last_request_ms.set(i, args[1] + "," + (System.currentTimeMillis()));
                                        command_args = args;
                                        new sendData(plugin, args);
                                        break;
                                    }
                                }
                            }
                            if (!isContainedInArray) {
                                url_last_request_ms.add(args[1] + "," + (System.currentTimeMillis()));
                                command_args = args;
                                new sendData(plugin, args);
                            }
                        }
                        else {
                            command_args = args;
                            new sendData(plugin, args);
                        }
                    }
                }
                else {
                    getLogger().warning("HTTP requests are disabled in the config");
                }
            }
            else {
                if (getConfig().getBoolean("AllowRequest")) {
                    if (args.length < 2) {
                        if (getConfig().getBoolean("PrintRequestsToConsole")) {
                            getLogger().warning("Syntax: httpsend <request type> <destination> <name1:value1,name2:value2>");
                        }
                    }
                    else if (args[0].equals("POST") && !getConfig().getBoolean("AllowPost")) {
                        if (getConfig().getBoolean("PrintRequestsToConsole")) {
                            getLogger().warning("A command block tried to run a POST request, but they are disabled on this server.");
                        }
                    }
                    else if (args[0].equals("GET") && !getConfig().getBoolean("AllowGet")) {
                        if (getConfig().getBoolean("PrintRequestsToConsole")) {
                            getLogger().warning("A command block tried to run a GET request, but they are disabled on this server.");
                        }
                    }
                    else {
                        if (getConfig().getBoolean("UseGlobalCooldown")) {
                            if ((int) (System.currentTimeMillis()) - general_last_request_ms < getConfig().getInt("GlobalCooldownMs")) {
                                if (getConfig().getBoolean("PrintRequestsToConsole")) {
                                    getLogger().warning("Too many requests are being attempted");
                                }
                            }
                            else {
                                command_args = args;
                                general_last_request_ms = (int) (System.currentTimeMillis());
                                new sendData(plugin, args);
                            }
                        }
                        else if (getConfig().getBoolean("UseUrlSpecificCooldown")) {
                            Boolean isContainedInArray = false;
                            for (int i = 0; i < url_last_request_ms.size(); i++) {
                                String current_url_string = url_last_request_ms.get(i);
                                if (current_url_string.contains(args[1])) {
                                    isContainedInArray = true;
                                    String[] current_url = url_last_request_ms.get(i).split(",");
                                    Long temp = Long.valueOf(current_url[1]);
                                    Long current_time_int = System.currentTimeMillis();
                                    if (current_time_int - temp < getConfig().getInt("UrlSpecificCooldown")) {
                                        if (getConfig().getBoolean("PrintRequestsToConsole")) {
                                            getLogger().warning("Too many requests are being sent to " + args[1]);
                                        }
                                        break;
                                    }
                                    else {
                                        url_last_request_ms.set(i, args[1] + "," + (System.currentTimeMillis()));
                                        command_args = args;
                                        new sendData(plugin, args);
                                        break;
                                    }
                                }
                            }
                            if (!isContainedInArray) {
                                url_last_request_ms.add(args[1] + "," + (System.currentTimeMillis()));
                                command_args = args;
                                new sendData(plugin, args);
                            }
                        }
                        else {
                            command_args = args;
                            new sendData(plugin, args);
                        }
                    }
                }
                else {
                    if (getConfig().getBoolean("PrintRequestsToConole")) {
                        getLogger().warning("A command block tried to send an HTTP request, but HTTP requests are disabled in the config");
                    }
                }
            }
        }
        return true;
    }
    public class sendData extends BukkitRunnable {

        public sendData(JavaPlugin plugin, String[] args) {
            runTaskAsynchronously(plugin);
        }
        public void run() {
            boolean shouldSend = true;
            if (getConfig().getBoolean("UseWhitelist")) {
                String[] whitelist = getConfig().getString("Whitelist").split(",");
                boolean inWhitelist = false;
                for (int i = 0; i < whitelist.length; i++) {
                    if (command_args[1].contains(whitelist[i])) {
                        inWhitelist = true;
                        break;
                    }
                }
                if (!inWhitelist) {
                    shouldSend = false;
                    if (getConfig().getBoolean("PrintRequestsToConsole")) {
                        getLogger().warning("A request was attempted, but the URL was not found in the whitelist. Aborting");
                    }
                }
            }
            if (getConfig().getBoolean("UseBlacklist")) {
                String[] blacklist = getConfig().getString("Blacklist").split(",");
                boolean inBlacklist = false;
                for (int i = 0; i < blacklist.length; i++) {
                    if (command_args[1].contains(blacklist[i])) {
                        inBlacklist = true;
                        break;
                    }
                }
                if (inBlacklist) {
                    shouldSend = false;
                    if (getConfig().getBoolean("PrintRequestsToConsole")) {
                        getLogger().warning("A request was attempted, but the URL was found in the blacklist. Aborting");
                    }
                }
            }
            boolean hasValues;
            String httpMethod = command_args[0];
            hasValues = command_args.length > 2;
            if (hasValues && shouldSend) {
                try {
                    if (getConfig().getBoolean("PrintRequestsToConsole")) {
                        getLogger().info("An HTTP " + command_args[0] + " request with values is being sent to " + command_args[1]);
                    }
                    URL url;
                    String get_url;
                    String urlParameters = "";
                    if (Objects.equals(httpMethod, "GET")) {
                        get_url = command_args[1] + "?" + command_args[2];
                        url = new URL(get_url);
                    }
                    else {
                        url = new URL(command_args[1]);
                        urlParameters = command_args[2];
                    }
                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);
                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                    if (!Objects.equals(httpMethod, "GET")) {
                        writer.write(urlParameters);
                    }
                    writer.flush();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    if (getConfig().getBoolean("PrintRequestsToConsole")) {
                            getLogger().info("The request was sent successfully");
                    }
                    } catch (Exception e) {
                        if (getConfig().getBoolean("PrintRequestsToConsole")) {
                            getLogger().warning("The request failed to send");
                        }
                    }
            }
            else if (shouldSend){
                try {
                    if (getConfig().getBoolean("PrintRequestsToConsole")) {
                        getLogger().info("An HTTP " + command_args[0] + " request with no values is being sent to " + command_args[1]);
                    }
                    URL url = new URL(command_args[1]);
                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);
                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.flush();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    if (getConfig().getBoolean("PrintRequestsToConsole")) {
                        getLogger().info("The request was sent successfully");
                    }
                } catch (Exception e) {
                    if (getConfig().getBoolean("printRequestsToConsole")) {
                        getLogger().warning("The request failed to send");
                    }
                }
            }
        }
    }
    static String[] command_args;
    static List<String> url_last_request_ms = new ArrayList<String>();
    static int general_last_request_ms = 1;
}