package me.dnamaster10.httprequests;

import org.bukkit.Bukkit;
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

public final class HttpRequests extends JavaPlugin {
    private static HttpRequests plugin;
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        System.out.println("HTTPRequests has finished loading!");
        plugin = this;
    }
    @Override
    public void onDisable() {
        System.out.println("HTTPrequests shutdown successfully");
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //send
        if (command.getName().equalsIgnoreCase("httpsend")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (getConfig().getBoolean("AllowRequest")) {
                    if (p.hasPermission("HttpRequest")) {
                        if (args.length < 2) {
                            p.sendMessage(ChatColor.RED + "Syntax: /httpsend <GET/POST> <destination> <name1=value1&name2=value2>");
                        }
                        else if (args[1] != "POST" || command_args[1] != "GET") {
                            p.sendMessage(ChatColor.RED + "Only POST and GET requests are supported at this time");
                        }
                        else if (args[1] == "POST" && !getConfig().getBoolean("AllowPost")) {
                            p.sendMessage(ChatColor.RED + "Post requests are disabled on this server");
                        }
                        else if (args[1] == "GET" && !getConfig().getBoolean("AllowGet")) {
                            p.sendMessage(ChatColor.RED + "Get requests are disabled on this server");
                        }
                        else {
                            command_args = args;
                            new sendData(plugin, args);
                        }
                    } else {
                        p.sendMessage(ChatColor.RED + "You need the HttpRequest permission to perform that command");
                    }
                }
                else {
                    p.sendMessage(ChatColor.RED + "HTTP requests are disabled in the config");
                }
            } else if (sender instanceof ConsoleCommandSender) {
                if (getConfig().getBoolean("AllowRequest")) {
                    if (args.length == 0) {
                        System.out.println("Syntax: httpsend <request type> <destination> <name1:value1,name2:value2>");
                    }
                    else {
                        System.out.println("Data sent from server");
                    }
                }
                else {
                    System.out.println("HTTP requests are disabled in the config");
                }
            }
        }
        return true;
    }
    public static HttpRequests getPlugin() {
        return plugin;
    }
    static String[] command_args;
    public class sendData extends BukkitRunnable {

        public sendData(JavaPlugin plugin, String[] args) {
            //objects = plugin.objects_args;
            runTaskAsynchronously(plugin);
        }
        public void run() {
            String request_type = command_args[0];
            String destination_url = command_args[1];
            boolean has_values;
            if (command_args[2].length() > 0) {
                has_values = true;
            }
            else {
                has_values = false;
            }
            if (has_values) {
                try {
                    if (getConfig().getBoolean("PrintRequestsToConsole")) {
                        System.out.println("An HTTP " + command_args[0] + " request with values is being sent to " + command_args[1]);
                    }
                    String urlParameters = command_args[2];
                    URL url = new URL(request_type);
                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);
                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.write(urlParameters);
                    writer.flush();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    if (getConfig().getBoolean("PrintRequestsToConsole")) {
                        System.out.println("The request was sent successfully");
                    }
                } catch (Exception e) {
                    if (getConfig().getBoolean("PrintRequestsToConsole")) {
                        System.out.println("The request failed to send");
                    }
                }
            }
            else {
                try {
                    if (getConfig().getBoolean("PrintRequestsToConsole")) {
                        System.out.println("An HTTP " + command_args[0] + " request with no values is being sent to " + command_args[1]);
                    }
                    URL url = new URL(request_type);
                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);
                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.flush();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    if (getConfig().getBoolean("PrintRequestsToConsole")) {
                        System.out.println("The request was sent successfully");
                    }
                } catch (Exception e) {
                    if (getConfig().getBoolean("printRequestsToConsole")) {
                        System.out.println("The request failed to send");
                    }
                }
            }
        }
    }
}
