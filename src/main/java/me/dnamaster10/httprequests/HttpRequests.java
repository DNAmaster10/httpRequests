package me.dnamaster10.httprequests;

import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
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
import java.util.Objects;

public final class HttpRequests extends JavaPlugin {
    private static HttpRequests plugin;
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        System.out.println("HttpRequests has finished loading!");
        plugin = this;
    }
    @Override
    public void onDisable() {
        System.out.println("HttpRequests shutdown successfully");
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //send
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
                            command_args = args;
                            new sendData(plugin, args);
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
                        System.out.println("Syntax: httpsend <request type> <destination> <name1:value1,name2:value2>");
                    }
                    else if (args[0].equals("POST") && !getConfig().getBoolean("AllowPost")) {
                        System.out.println("A command block tried to run a POST request, but they are disabled on this server.");
                    }
                    else if (args[0].equals("GET") && !getConfig().getBoolean("AllowGet")) {
                        System.out.println("Get requests are disabled on this server");
                    }
                    else {
                        command_args = args;
                        new sendData(plugin, args);
                    }
                }
                else {
                    System.out.println("HTTP requests are disabled in the config");
                }
            }
            else {
                if (getConfig().getBoolean("AllowRequest")) {
                    if (args.length < 2) {
                        if (getConfig().getBoolean("PrintRequestsToConsole")) {
                            System.out.println("Syntax: httpsend <request type> <destination> <name1:value1,name2:value2>");
                        }
                    }
                    else if (args[0].equals("POST") && !getConfig().getBoolean("AllowPost")) {
                        if (getConfig().getBoolean("PrintRequestsToConsole")) {
                            System.out.println("A command block tried to run a POST request, but they are disabled on this server.");
                        }
                    }
                    else if (args[0].equals("GET") && !getConfig().getBoolean("AllowGet")) {
                        if (getConfig().getBoolean("PrintRequestsToConsole")) {
                            System.out.println("A command block tried to run a GET request, but they are disabled on this server.");
                        }
                    }
                    else {
                        command_args = args;
                        new sendData(plugin, args);
                    }
                }
                else {
                    System.out.println("HTTP requests are disabled in the config");
                }
            }
        }
        return true;
    }
    static String[] command_args;
    public class sendData extends BukkitRunnable {

        public sendData(JavaPlugin plugin, String[] args) {
            runTaskAsynchronously(plugin);
        }
        public void run() {
            boolean hasValues;
            String httpMethod = command_args[0];
            hasValues = command_args.length > 2;
            if (hasValues) {
                if (Objects.equals(httpMethod, "POST")) {
                    try {
                        if (getConfig().getBoolean("PrintRequestsToConsole")) {
                            System.out.println("An HTTP " + command_args[0] + " request with values is being sent to " + command_args[1]);
                        }
                        String urlParameters = command_args[2];
                        URL url = new URL(command_args[1]);
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
                        Thread t = Thread.currentThread();
                        t.getUncaughtExceptionHandler().uncaughtException(t, e);
                        if (getConfig().getBoolean("PrintRequestsToConsole")) {
                            System.out.println("The request failed to send");
                        }
                    }
                }
                else if (Objects.equals(httpMethod, "GET")) {
                    try {
                        if (getConfig().getBoolean("PrintRequestsToConsole")) {
                            System.out.println("An HTTP " + command_args[0] + " request with values is being sent to " + command_args[1]);
                        }
                        String get_url = command_args[1] + "?" + command_args[2];
                        System.out.println(get_url);
                        URL url = new URL(get_url);
                        URLConnection conn = url.openConnection();
                        conn.setDoOutput(true);
                        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                        writer.flush();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        if (getConfig().getBoolean("PrintRequestsToConsole")) {
                            System.out.println("The request was sent successfully");
                        }
                    } catch (Exception e) {
                        Thread t = Thread.currentThread();
                        t.getUncaughtExceptionHandler().uncaughtException(t, e);
                        if (getConfig().getBoolean("PrintRequestsToConsole")) {
                            System.out.println("The request failed to send");
                        }
                    }
                }
            }
            else {
                try {
                    if (getConfig().getBoolean("PrintRequestsToConsole")) {
                        System.out.println("An HTTP " + command_args[0] + " request with no values is being sent to " + command_args[1]);
                    }
                    URL url = new URL(command_args[1]);
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