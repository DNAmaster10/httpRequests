package me.dnamaster10.httprequests;

import org.bukkit.command.CommandSender;

public class Request {
    private static final HttpRequests plugin = HttpRequests.plugin;
    public RequestType type;
    public String destination;
    //Note: Currently only json encoding supported, so only boolean is needed
    public boolean hasEncoding;
    public boolean hasValues;
    public String values;
    public CommandSender sender;

    public enum RequestType {
        GET,
        POST
    }
    public Request(CommandSender sender, String[] args) {
        if (args[0].equals("GET")) {
            this.type = RequestType.GET;
        }
        else if (args[0].equals("POST")) {
            this.type = RequestType.POST;
        }
        this.destination = args[1];
        if (args.length > 2) {
            this.hasValues = true;
            this.values = args[2].replace(plugin.getConfig().getString("SpaceCharacter"), " ");
        }
        if (args.length > 3) {
            this.hasEncoding = true;
        }
        this.sender = sender;
    }
}
