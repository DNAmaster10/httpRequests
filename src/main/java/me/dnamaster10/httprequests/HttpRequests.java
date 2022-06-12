package me.dnamaster10.httprequests;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpConnectTimeoutException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class HttpRequests extends JavaPlugin {
    private static HttpRequests plugin;
    private static String[] command_args;
    private static final List<String> url_last_request_ms = new ArrayList<>();
    private static int general_last_request_ms = 1;

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
        if (command.getName().equalsIgnoreCase("httpsend")) {
            if (sender instanceof Player p) {
                if (getConfig().getBoolean("AllowRequest")) {
                    if (getConfig().getBoolean("AllowChatSender")) {
                        if (p.hasPermission("httprequest.sendhttp")) {
                            if (args.length < 2) {
                                p.sendMessage(ChatColor.RED + "Syntax: /httpsend <GET/POST> <destination> <name1=value1&name2=value2>");
                            } else if (args[0].equals("POST") && !getConfig().getBoolean("AllowPost")) {
                                p.sendMessage(ChatColor.RED + "Post requests are disabled on this server");
                            } else if (args[0].equals("GET") && !getConfig().getBoolean("AllowGet")) {
                                p.sendMessage(ChatColor.RED + "Get requests are disabled on this server");
                            } else {
                                if (getConfig().getBoolean("UseGlobalCooldown")) {
                                    if ((int) (System.currentTimeMillis()) - general_last_request_ms < getConfig().getInt("GlobalCooldownMs")) {
                                        p.sendMessage(ChatColor.RED + "Too many requests are being sent at this time");
                                        if (getConfig().getBoolean("PrintRequestsToConsole")) {
                                            getLogger().info("Too many requests are being attempted");
                                        }
                                    } else {
                                        if (args.length > 3 && !args[3].equals("application/json")) {
                                            p.sendMessage(ChatColor.RED + "Encoding type not recognised");
                                        }
                                        else {
                                            command_args = args;
                                            general_last_request_ms = (int) (System.currentTimeMillis());
                                            new SendData(plugin);
                                        }
                                    }
                                } else if (getConfig().getBoolean("UseUrlSpecificCooldown")) {
                                    boolean isContainedInArray = false;
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
                                            } else {
                                                if (args.length > 3 && !args[3].equals("application/json")) {
                                                    p.sendMessage(ChatColor.RED + "Encoding type not recognised");
                                                }
                                                else {
                                                    url_last_request_ms.set(i, args[1] + "," + (System.currentTimeMillis()));
                                                    command_args = args;
                                                    new SendData(plugin);
                                                }
                                            }
                                            break;
                                        }
                                    }
                                    if (!isContainedInArray) {
                                        if (args.length > 3 && !args[3].equals("application/json")) {
                                            p.sendMessage(ChatColor.RED + "Encoding type not recognised");
                                        }
                                        else {
                                            url_last_request_ms.add(args[1] + "," + (System.currentTimeMillis()));
                                            command_args = args;
                                            new SendData(plugin);
                                        }
                                    }
                                } else {
                                    if (args.length > 3 && !args[3].equals("application/json")) {
                                        p.sendMessage(ChatColor.RED + "Encoding type not recognised");
                                    }
                                    else {
                                        command_args = args;
                                        new SendData(plugin);
                                    }
                                }
                            }
                        } else {
                            p.sendMessage(ChatColor.RED + "You need the permission httprequest.sendhttp to perform that command");
                        }
                    } else {
                        p.sendMessage(ChatColor.RED + "Sending requests from chat is disabled in the config");
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "HTTP requests are disabled in the config");
                }
            } else if (sender instanceof ConsoleCommandSender) {
                if (getConfig().getBoolean("AllowRequest")) {
                    if (getConfig().getBoolean("AllowConsoleSender")) {
                        if (args.length < 2) {
                            getLogger().warning("Syntax: httpsend <request type> <destination> <name1:value1,name2:value2>");
                        } else if (args[0].equals("POST") && !getConfig().getBoolean("AllowPost")) {
                            getLogger().warning("POST requests are disabled in the config");
                        } else if (args[0].equals("GET") && !getConfig().getBoolean("AllowGet")) {
                            getLogger().warning("GET requests are disabled in the config.");
                        } else {
                            if (getConfig().getBoolean("UseGlobalCooldown")) {
                                if ((int) (System.currentTimeMillis()) - general_last_request_ms < getConfig().getInt("GlobalCooldownMs")) {
                                    getLogger().warning("Too many requests are being attempted");
                                } else {
                                    if (args.length > 3 && !args[3].equals("application/json"))
                                    command_args = args;
                                    general_last_request_ms = (int) (System.currentTimeMillis());
                                    new SendData(plugin);
                                }
                            } else if (getConfig().getBoolean("UseUrlSpecificCooldown")) {
                                boolean isContainedInArray = false;
                                for (int i = 0; i < url_last_request_ms.size(); i++) {
                                    String current_url_string = url_last_request_ms.get(i);
                                    if (current_url_string.contains(args[1])) {
                                        isContainedInArray = true;
                                        String[] current_url = url_last_request_ms.get(i).split(",");
                                        Long temp = Long.valueOf(current_url[1]);
                                        Long current_time_int = System.currentTimeMillis();
                                        if (current_time_int - temp < getConfig().getInt("UrlSpecificCooldown")) {
                                            getLogger().warning("Too many requests are being sent to " + args[1]);
                                        } else {
                                            if (args.length > 3 && !args[3].equals("application/json")) {
                                                getLogger().warning("Encoding type not recognised");
                                            }
                                            else {
                                                url_last_request_ms.set(i, args[1] + "," + (System.currentTimeMillis()));
                                                command_args = args;
                                                new SendData(plugin);
                                            }
                                        }
                                        break;
                                    }
                                }
                                if (!isContainedInArray) {
                                    if (args.length > 3 && !args[3].equals("application/json")) {
                                        getLogger().warning("Encoding type not recognised");
                                    }
                                    else {
                                        url_last_request_ms.add(args[1] + "," + (System.currentTimeMillis()));
                                        command_args = args;
                                        new SendData(plugin);
                                    }
                                }
                            } else {
                                if (args.length > 3 && !args[3].equals("application/json")) {
                                    getLogger().warning("Encoding type not recognised");
                                }
                                command_args = args;
                                new SendData(plugin);
                            }
                        }
                    }
                    else {
                        getLogger().warning("Sending HTTP requests from the console is disabled in the config");
                    }
                }
                else {
                    getLogger().warning("HTTP requests are disabled in the config");
                }
            }
            else {
                if (getConfig().getBoolean("AllowRequest")) {
                    if (getConfig().getBoolean("AllowCommandBlockSender")) {
                        if (args.length < 2) {
                            if (getConfig().getBoolean("PrintRequestsToConsole")) {
                                getLogger().warning("Syntax: httpsend <request type> <destination> <name1:value1,name2:value2>");
                            }
                        } else if (args[0].equals("POST") && !getConfig().getBoolean("AllowPost")) {
                            if (getConfig().getBoolean("PrintRequestsToConsole")) {
                                getLogger().warning("A command block tried to run a POST request, but they are disabled on this server.");
                            }
                        } else if (args[0].equals("GET") && !getConfig().getBoolean("AllowGet")) {
                            if (getConfig().getBoolean("PrintRequestsToConsole")) {
                                getLogger().warning("A command block tried to run a GET request, but they are disabled on this server.");
                            }
                        } else {
                            if (getConfig().getBoolean("UseGlobalCooldown")) {
                                if ((int) (System.currentTimeMillis()) - general_last_request_ms < getConfig().getInt("GlobalCooldownMs")) {
                                    if (getConfig().getBoolean("PrintRequestsToConsole")) {
                                        getLogger().warning("Too many requests are being attempted");
                                    }
                                } else {
                                    if (args.length > 3 && !args[3].equals("application/json")) {
                                        if (getConfig().getBoolean("PrintRequestsToConsole")) {
                                            getLogger().warning("A command block tried to send a request, but the encoding type was not recognised");
                                        }
                                    }
                                    else {
                                        command_args = args;
                                        general_last_request_ms = (int) (System.currentTimeMillis());
                                        new SendData(plugin);
                                    }
                                }
                            } else if (getConfig().getBoolean("UseUrlSpecificCooldown")) {
                                boolean isContainedInArray = false;
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
                                        } else {
                                            if (args.length > 3 && !args[3].equals("application/json")) {
                                                if (getConfig().getBoolean("PrintRequestsToConsole")) {
                                                    getLogger().warning("A command block tried to send a request, but the encoding type was not recognised");
                                                }
                                            }
                                            else {
                                                url_last_request_ms.set(i, args[1] + "," + (System.currentTimeMillis()));
                                                command_args = args;
                                                new SendData(plugin);
                                            }
                                        }
                                        break;
                                    }
                                }
                                if (!isContainedInArray) {
                                    if (args.length > 3 && !args[3].equals("application/json")) {
                                        if (getConfig().getBoolean("PrintRequestsToConsole")) {
                                            getLogger().warning("A command block tried to send a request, but the encoding type was not recognised");
                                        }
                                    }
                                    else {
                                        url_last_request_ms.add(args[1] + "," + (System.currentTimeMillis()));
                                        command_args = args;
                                        new SendData(plugin);
                                    }
                                }
                            } else {
                                if (args.length > 3 && !args[3].equals("application/json")) {
                                    if (getConfig().getBoolean("PrintRequestsToConsole")) {
                                        getLogger().warning("A command block tried to send a request, but the encoding type was not recognised");
                                    }
                                }
                                else {
                                    command_args = args;
                                    new SendData(plugin);
                                }
                            }
                        }
                    }
                    else {
                        if (getConfig().getBoolean("PrintRequestsToConsole")) {
                            getLogger().warning("A command block tried to send an HTTP request, but sending HTTP requests from command blocks is disabled in the config");
                        }
                    }
                }
                else {
                    if (getConfig().getBoolean("PrintRequestsToConsole")) {
                        getLogger().warning("A command block tried to send an HTTP request, but HTTP requests are disabled in the config");
                    }
                }
            }
        }
        return true;
    }
    public class SendData extends BukkitRunnable {

        public SendData(JavaPlugin plugin) {
            runTaskAsynchronously(plugin);
        }
        public void run() {
            boolean shouldSend = true;
            if (getConfig().getBoolean("UseWhitelist")) {
                String[] whitelist = Objects.requireNonNull(getConfig().getString("Whitelist"), "Expression 'getString(\"Whitelist\")' must not be null").split(",");
                boolean inWhitelist = false;
                for (String s : whitelist) {
                    if (command_args[1].contains(s)) {
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
                String[] blacklist = Objects.requireNonNull(getConfig().getString("Blacklist"), "Expression 'getString(\"Blacklist\")' must not be null").split(",");
                boolean inBlacklist = false;
                for (String s : blacklist) {
                    if (command_args[1].contains(s)) {
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
            hasValues = command_args.length > 2;
            if (hasValues && shouldSend && !(command_args.length > 3)) {
                if (Objects.equals(command_args[0], "POST")) {
                    if (getConfig().getBoolean("PrintRequestsToConsole")) {
                        getLogger().info("A POST request with values is being sent");
                    }
                    var request = HttpRequest.newBuilder()
                            .uri(URI.create(command_args[1]))
                            .header("Content-Type","application/x-www-form-urlencoded")
                            .POST(HttpRequest.BodyPublishers.ofString(command_args[2]))
                            .timeout(Duration.of(getConfig().getInt("RequestTimeoutDuration"), ChronoUnit.MILLIS))
                            .build();
                    var client = HttpClient.newHttpClient();
                    try {
                        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
                        if (getConfig().getBoolean("PrintRequestsToConsole")) {
                            getLogger().info("HTTP status code returned: " + response.statusCode());
                        }

                    } catch (HttpConnectTimeoutException e) {
                        if (getConfig().getBoolean("PrintRequestsToConsole")) {
                            getLogger().warning("The request timed out.");
                        }
                    } catch (IOException | InterruptedException e) {
                        if (getConfig().getBoolean("PrintRequestsToConsole")) {
                            getLogger().warning("An error occured sending the request");
                        }
                    }
                }
                else {
                    if (getConfig().getBoolean("PrintRequestsToConsole")) {
                        getLogger().info("A GET request with values is being sent");
                    }
                    String GETurl = command_args[1] + "?" + command_args[2];
                    var request = HttpRequest.newBuilder()
                            .uri(URI.create(GETurl))
                            .timeout(Duration.of(getConfig().getInt("RequestTimeoutDuration"), ChronoUnit.MILLIS))
                            .build();
                    var client = HttpClient.newHttpClient();
                    try {
                        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
                        if (getConfig().getBoolean("PrintRequestsToConsole")) {
                            getLogger().info("The GET request sent successfully with HTTP response: " + response.statusCode());
                        }
                    } catch (HttpConnectTimeoutException e) {
                        if (getConfig().getBoolean("PrintRequestsToConsole")) {
                            getLogger().warning("The request timed out.");
                        }
                    } catch (IOException | InterruptedException e) {
                        if (getConfig().getBoolean("PrintRequestsToConsole")) {
                            getLogger().warning("An error occured sending the request");
                        }
                    }
                }
            }
            else if (shouldSend && !hasValues){
                if (getConfig().getBoolean("PrintRequestsToConsole")) {
                    getLogger().info("A GET request with no values is being sent");
                }
                var request = HttpRequest.newBuilder()
                        .uri(URI.create(command_args[1]))
                        .timeout(Duration.of(getConfig().getInt("RequestTimeoutDuration"), ChronoUnit.MILLIS))
                        .build();
                var client = HttpClient.newHttpClient();
                try {
                    var response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    if (getConfig().getBoolean("PrintRequestsToConsole")) {
                        getLogger().info("The GET request sent successfully with HTTP response: " + response.statusCode());
                    }
                } catch (HttpConnectTimeoutException e) {
                    if (getConfig().getBoolean("PrintRequestsToConsole")) {
                        getLogger().warning("The request timed out.");
                    }
                } catch (IOException | InterruptedException e) {
                    if (getConfig().getBoolean("PrintRequestsToConsole")) {
                        getLogger().warning("An error occured sending the request");
                    }
                }
            }
            else if (shouldSend) {
                if (getConfig().getBoolean("PrintRequestsToConsole")) {
                    getLogger().info("A JSON encoded request is being sent");
                }
                var request = HttpRequest.newBuilder()
                        .uri(URI.create(command_args[1]))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(command_args[2]))
                        .timeout(Duration.of(getConfig().getInt("RequestTimeoutDuration"), ChronoUnit.MILLIS))
                        .build();
                var client = HttpClient.newHttpClient();
                try {
                    var response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    if (getConfig().getBoolean("PrintRequestsToConsole")) {
                        getLogger().warning("JSON request returned code: " + response.statusCode());
                    }
                } catch (HttpConnectTimeoutException e) {
                    if (getConfig().getBoolean("PrintRequestsToConsole")) {
                        getLogger().warning("The request timed out.");
                    }
                } catch (IOException | InterruptedException e) {
                    if (getConfig().getBoolean("PrintRequestsToConsole")) {
                        getLogger().warning("An error occured sending the request");
                    }
                }
            }
            else {
                if (getConfig().getBoolean("PrintRequestsToConsole")) {
                    getLogger().warning("An unknown error was found with a request");
                }
            }
        }
    }
}