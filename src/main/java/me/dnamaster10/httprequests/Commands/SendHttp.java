package me.dnamaster10.httprequests.Commands;

import me.dnamaster10.httprequests.HttpRequests;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static me.dnamaster10.httprequests.HttpRequests.command_args;

public class SendHttp extends JavaPlugin {
    public static void SendData (HttpRequests plugin, CommandSender sender) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String responseText = "nullval-#-";
            Integer responseCode = null;
            boolean shouldSend = true;
            if (!plugin.getConfig().isSet("SpaceCharacter")) {
                if (sender instanceof Player p) {
                    p.sendMessage("The request failed to send as no Space Character is set in the plugin's config");
                }
                plugin.getLogger().warning("No Space Character is set in the plugin's config");
                shouldSend = false;
            }
            if (plugin.getConfig().getBoolean("UseWhitelist")) {
                String[] whitelist = Objects.requireNonNull(plugin.getConfig().getString("Whitelist"), "Expression 'getString(\"Whitelist\")' must not be null").split(",");
                boolean inWhitelist = false;
                for (String s : whitelist) {
                    if (command_args[1].contains(s)) {
                        inWhitelist = true;
                        break;
                    }
                }
                if (!inWhitelist) {
                    shouldSend = false;
                    if (sender instanceof Player p) {
                        p.sendMessage(ChatColor.RED + "URL not in whitelist");
                    }
                    if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                        plugin.getLogger().warning("A request was attempted, but the URL was not found in the whitelist. Aborting");
                    }
                }
            }
            if (plugin.getConfig().getBoolean("UseBlacklist")) {
                String[] blacklist = Objects.requireNonNull(plugin.getConfig().getString("Blacklist"), "Expression 'getString(\"Blacklist\")' must not be null").split(",");
                boolean inBlacklist = false;
                for (String s : blacklist) {
                    if (command_args[1].contains(s)) {
                        inBlacklist = true;
                        break;
                    }
                }
                if (inBlacklist) {
                    shouldSend = false;
                    if (sender instanceof Player p) {
                        p.sendMessage(ChatColor.RED + "URL is contained in blacklist");
                    }
                    if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                        plugin.getLogger().warning("A request was attempted, but the URL was found in the blacklist. Aborting");
                    }
                }
            }
            boolean hasValues;
            hasValues = command_args.length > 2;
            if (hasValues && shouldSend && !(command_args.length > 3)) {
                if (Objects.equals(command_args[0], "POST")) {
                    if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                        plugin.getLogger().info("A POST request with values is being sent");
                    }
                    String postVals = command_args[2].replace(plugin.getConfig().getString("SpaceCharacter"), " ");
                    var request = HttpRequest.newBuilder()
                            .uri(URI.create(command_args[1]))
                            .header("Content-Type","application/x-www-form-urlencoded")
                            .POST(HttpRequest.BodyPublishers.ofString(postVals))
                            .timeout(Duration.of(plugin.getConfig().getInt("RequestTimeoutDuration"), ChronoUnit.MILLIS))
                            .build();
                    var client = HttpClient.newHttpClient();
                    try {
                        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
                        if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                            plugin.getLogger().info("HTTP status code returned: " + response.statusCode());
                        }
                        if (sender instanceof Player p) {
                            if(plugin.getConfig().getBoolean("SendResponseCodeToPlayer")) {
                                p.sendMessage(ChatColor.GREEN + "Request sent successfully and returned code: " + response.statusCode());
                            }
                            if(plugin.getConfig().getBoolean("SendResponseBodyToPlayer")) {
                                p.sendMessage(response.body());
                            }
                        }
                        if (plugin.getConfig().getBoolean("AllowResponseProcessing")) {
                            responseText = response.body();
                            responseCode = response.statusCode();
                        }

                    } catch (HttpConnectTimeoutException e) {
                        if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                            plugin.getLogger().warning("The request timed out.");
                        }
                        if (sender instanceof Player p) {
                            p.sendMessage(ChatColor.RED + "The request timed out");
                        }
                    } catch (IOException | InterruptedException e) {
                        if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                            plugin.getLogger().warning("An error occurred sending the request");
                        }
                        if (sender instanceof Player p) {
                            p.sendMessage(ChatColor.RED + "An error occurred sending the request");
                        }
                    }
                }
                else {
                    if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                        plugin.getLogger().info("A GET request with values is being sent");
                    }
                    String getVals = command_args[2].replace(plugin.getConfig().getString("SpaceCharacter"), " ");
                    String GETurl = command_args[1] + "?" + getVals;
                    var request = HttpRequest.newBuilder()
                            .uri(URI.create(GETurl))
                            .timeout(Duration.of(plugin.getConfig().getInt("RequestTimeoutDuration"), ChronoUnit.MILLIS))
                            .build();
                    var client = HttpClient.newHttpClient();
                    try {
                        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
                        if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                            plugin.getLogger().info("The GET request sent successfully with HTTP response: " + response.statusCode());
                        }
                        if (sender instanceof Player p) {
                            if(plugin.getConfig().getBoolean("SendResponseCodeToPlayer")) {
                                p.sendMessage(ChatColor.GREEN + "Request sent successfully and returned code: " + response.statusCode());
                            }
                            if(plugin.getConfig().getBoolean("SendResponseBodyToPlayer")) {
                                p.sendMessage(response.body());
                            }
                        }
                        if (plugin.getConfig().getBoolean("AllowResponseProcessing")) {
                            responseText = response.body();
                            responseCode = response.statusCode();
                        }
                    } catch (HttpConnectTimeoutException e) {
                        if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                            plugin.getLogger().warning("The request timed out.");
                        }
                        if (sender instanceof Player p) {
                            p.sendMessage(ChatColor.RED + "The request timed out");
                        }
                    } catch (IOException | InterruptedException e) {
                        if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                            plugin.getLogger().warning("An error occurred sending the request");
                        }
                        if (sender instanceof Player p) {
                            p.sendMessage(ChatColor.RED + "An error occurred sending the request");
                        }
                    }
                }
            }
            else if (shouldSend && !hasValues){
                if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                    plugin.getLogger().info("A GET request with no values is being sent");
                }
                var request = HttpRequest.newBuilder()
                        .uri(URI.create(command_args[1]))
                        .timeout(Duration.of(plugin.getConfig().getInt("RequestTimeoutDuration"), ChronoUnit.MILLIS))
                        .build();
                var client = HttpClient.newHttpClient();
                try {
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                        plugin.getLogger().info("The GET request sent successfully with HTTP response: " + response.statusCode());
                    }
                    if (sender instanceof Player p) {
                        if(plugin.getConfig().getBoolean("SendResponseCodeToPlayer")) {
                            p.sendMessage(ChatColor.GREEN + "Request sent successfully and returned code: " + response.statusCode());
                        }
                        if(plugin.getConfig().getBoolean("SendResponseBodyToPlayer")) {
                            p.sendMessage(response.body());
                        }
                    }
                    if (plugin.getConfig().getBoolean("AllowResponseProcessing")) {
                        responseText = response.body();
                        responseCode = response.statusCode();
                    }
                } catch (HttpConnectTimeoutException e) {
                    if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                        plugin.getLogger().warning("The request timed out.");
                    }
                    if (sender instanceof Player p) {
                        p.sendMessage(ChatColor.RED + "Request timed out");
                    }
                } catch (IOException | InterruptedException e) {
                    if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                        plugin.getLogger().warning("An error occurred sending the request");
                    }
                    if (sender instanceof Player p) {
                        p.sendMessage(ChatColor.GREEN + "An error occurred sending the request");
                    }
                }
            }
            else if (shouldSend) {
                if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                    plugin.getLogger().info("A JSON encoded request is being sent");
                }
                String postVals = command_args[2].replace(plugin.getConfig().getString("SpaceCharacter"), " ");
                var request = HttpRequest.newBuilder()
                        .uri(URI.create(command_args[1]))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(postVals))
                        .timeout(Duration.of(plugin.getConfig().getInt("RequestTimeoutDuration"), ChronoUnit.MILLIS))
                        .build();
                var client = HttpClient.newHttpClient();
                try {
                    var response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                        plugin.getLogger().warning("JSON request returned code: " + response.statusCode());
                    }
                    if (sender instanceof Player p) {
                        if(plugin.getConfig().getBoolean("SendResponseCodeToPlayer")) {
                            p.sendMessage(ChatColor.GREEN + "Request sent successfully and returned code: " + response.statusCode());
                        }
                        if(plugin.getConfig().getBoolean("SendResponseBodyToPlayer")) {
                            p.sendMessage(response.body());
                        }
                    }
                    if (plugin.getConfig().getBoolean("AllowResponseProcessing")) {
                        responseText = response.body();
                        responseCode = response.statusCode();
                    }
                } catch (HttpConnectTimeoutException e) {
                    if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                        plugin.getLogger().warning("The request timed out.");
                    }
                    if (sender instanceof Player p) {
                        p.sendMessage(ChatColor.RED + "The request timed out");
                    }
                } catch (IOException | InterruptedException e) {
                    if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                        plugin.getLogger().warning("An error occurred sending the request");
                    }
                    if (sender instanceof Player p) {
                        p.sendMessage(ChatColor.RED + "An error occurred sending the request");
                    }
                }
            }
            else {
                if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                    plugin.getLogger().warning("An unknown error was found with a request");
                }
                if (sender instanceof Player p) {
                    p.sendMessage(ChatColor.RED + "An unknown error was found with the request");
                }
                responseText = "nullval-#-";
            }
            if (plugin.getConfig().getBoolean("AllowResponseProcessing") && !(Objects.equals(responseText, "nullval-#-"))) {
                assert responseText != null;
                if (responseText.length() > 0) {
                    ResponseHandle.HandleResponse(plugin, responseText, responseCode, command_args, sender);
                }
            }
        });
    }
}