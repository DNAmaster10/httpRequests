package me.dnamaster10.httprequests;

import me.dnamaster10.httprequests.Commands.SendHttp;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.System.currentTimeMillis;

public class Queue {
    //This class is used to temporarily store requests which need to be sent
    private static final HttpRequests plugin = HttpRequests.plugin;

    //Used for global cooldown
    private static ArrayList<Request> queue = new ArrayList<>();
    private static long lastSend = 0L;

    //Used for URL specific cooldown
    private static HashMap<String, ArrayList<Request>> urlQueue = new HashMap<>();
    private static HashMap<String, Long> urlCooldowns = new HashMap<>();

    public static void addRequest(Request request) {
        //Handles request addition based on max queue size
        //Check queue size
        if (plugin.getConfig().getInt("MaxQueueSize") >= 0) {
            if (plugin.getConfig().getBoolean("UseGlobalCooldown") && queue.size() >= plugin.getConfig().getInt("MaxQueueSize")) {
                Utilities.returnWarning(request.sender, "Request aborted as the queue is full", "A request was aborted ans the queue was full");
                return;
            }
            else if (plugin.getConfig().getBoolean("UseUrlSpecificCooldown")) {
                if (plugin.getConfig().getInt("MaxQueueSize") == 0) {
                    Utilities.returnWarning(request.sender, "Request aborted as the queue is full", "A request was aborted as the queue was full");
                    return;
                }
                else if (urlQueue.containsKey(request.destination) && urlQueue.get(request.destination).size() > plugin.getConfig().getInt("MaxQueueSize")) {
                    Utilities.returnWarning(request.sender, "Request aborted as the queue is full", "A request was aborted as the queue was full");
                    return;
                }
            }
        }
        //If the queue isn't full, check which queue should be used and add the request
        if (plugin.getConfig().getBoolean("UseGlobalCooldown")) {
            queue.add(request);
            Utilities.returnInfo(request.sender, "Request added to queue", "A request, destined for " + request.destination + ", was added to the queue");
        }
        else if (plugin.getConfig().getBoolean("UseUrlSpecificCooldown")) {
            //First check if the URL exists in the hashmap
            if (!urlQueue.containsKey(request.destination)) {
                urlQueue.put(request.destination, new ArrayList<>());
            }
            if (!urlCooldowns.containsKey(request.destination)) {
                urlCooldowns.put(request.destination, 0L);
            }
            //Then add it
            urlQueue.get(request.destination).add(request);
            Utilities.returnInfo(request.sender, "Request added to queue", "A request, destined for " + request.destination + ", was added to the queue");
        }
        else {
            queue.add(request);
            Utilities.returnInfo(request.sender, "Request added to queue", "A request, destined for " + request.destination + ", was added to the queue");
        }
    }
    public static void tickQueue() {
        //Checks whether the queue should be handled
        //and sends the next request if it needs to be sent.
        if (plugin.getConfig().getBoolean("UseUrlSpecificCooldown")) {
            //For each address, check if packets should be sent
            if (urlQueue.isEmpty()) {
                return;
            }
            long cooldown = plugin.getConfig().getLong("UrlSpecificCooldown");
            for (String url : urlQueue.keySet()) {
                if (currentTimeMillis() - urlCooldowns.get(url) >= cooldown) {
                    //Send the request
                    SendHttp.send(urlQueue.get(url).get(0));

                    //Remove the request from the queue
                    urlQueue.get(url).remove(0);

                    //If there are no more requests for that URL, remove the URL
                    if (urlQueue.get(url).isEmpty()) {
                        urlQueue.remove(url);
                    }
                    //Finally, update the cooldown time
                    urlCooldowns.replace(url, currentTimeMillis());
                }
            }
        }
        else if (plugin.getConfig().getBoolean("UseGlobalCooldown")) {
            if (queue.isEmpty()) {
                return;
            }
            if (!((currentTimeMillis() - lastSend) >= plugin.getConfig().getLong("GlobalCooldownMs"))) {
                return;
            }
            SendHttp.send(queue.get(0));
            queue.remove(0);
            lastSend = currentTimeMillis();
        }
        else {
            if (queue.isEmpty()) {
                return;
            }
            //Send all packets
            for (Request request : queue) {
                SendHttp.send(request);
            }
            queue.clear();
        }
    }
}