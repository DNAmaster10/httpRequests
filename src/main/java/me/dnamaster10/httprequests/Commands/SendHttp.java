package me.dnamaster10.httprequests.Commands;

import me.dnamaster10.httprequests.HttpRequests;
import me.dnamaster10.httprequests.Request;
import me.dnamaster10.httprequests.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.ConnectException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class SendHttp extends JavaPlugin {

    private static final HttpClient client = HttpClient.newHttpClient();

    private static final HttpRequests plugin = HttpRequests.plugin;
    public static void send(Request request) {
        if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
            if (!request.hasEncoding) {
                plugin.getLogger().info("A " + request.type + " request is being sent, destined for " + request.destination);
            }
            else {
                plugin.getLogger().info("A POST request with json encoding is being sent, destined for " + request.destination);
            }
        }
        if (request.hasValues) {
            if (plugin.getConfig().isSet("SpaceCharacter")) {
                request.values = request.values.replace(plugin.getConfig().getString("SpaceCharacter"), " ");
            }
            else {
                plugin.getLogger().warning("No space character is set in the config. Sending a request without spaces.");
            }
        }
        final long timeoutDuration = plugin.getConfig().getLong("RequestTimeoutDuration");
        final boolean printRequests = plugin.getConfig().getBoolean("PrintRequestsToConsole");
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            //URI
            URI uri;
            try {
                String uriString;
                if (request.type == Request.RequestType.GET && request.hasValues) {
                    uriString = request.destination + "?" + request.values;
                }
                else {
                    uriString = request.destination;
                }
                uri = URI.create(uriString);
                //Try to get the address authority
                //Will throw error if invalid running catch statement
                URL url = new URL(request.destination);
            }
            catch (Exception e) {
                Utilities.returnWarning(request.sender, "Failed to send HTTP request: Malformed URL. Make sure to include a protocol!", "Failed to send an HTTP request with a malformed URL: " + request.destination);
                return;
            }
            //Response
            HttpResponse<String> responseBody = null;
            HttpRequest hRequest;

            if (request.type == Request.RequestType.GET || !request.hasValues) {
                //Send a GET request
                //GET request without values
                hRequest = HttpRequest.newBuilder()
                        .uri(uri)
                        .timeout(Duration.of(timeoutDuration, ChronoUnit.MILLIS))
                        .build();

            }
            else {
                if (request.hasEncoding) {
                    //Send a request with JSON
                    hRequest = HttpRequest.newBuilder()
                            .uri(uri)
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(request.values))
                            .timeout(Duration.of(timeoutDuration, ChronoUnit.MILLIS))
                            .build();
                }
                else {
                    //Send a request in form encoding
                    hRequest = HttpRequest.newBuilder()
                            .uri(uri)
                            .header("Content-Type", "application/x-www-form-urlencoded")
                            .POST(HttpRequest.BodyPublishers.ofString(request.values))
                            .timeout(Duration.of(timeoutDuration, ChronoUnit.MILLIS))
                            .build();
                }
            }
            try {
                responseBody = client.send(hRequest, HttpResponse.BodyHandlers.ofString());
            }
            catch (HttpConnectTimeoutException e) {
                Utilities.returnWarning(request.sender, "The request timed out", "A request timed out destined for " + request.destination);
                return;
            }
            catch (ConnectException e) {
                Utilities.returnWarning(request.sender, "Failed to establish a connection to the remote server", "Failed to establish a connection to " + request.destination);
                return;
            }
            catch (Exception e) {
                Utilities.returnWarning(request.sender, "The request failed to send", "An error occurred sending a request to " + request.destination + ", " + e);
                return;
            }
            //Finally, handle the request and response in a sync thread
            ResponseHandle.handle(request, responseBody);
        });
    }
}