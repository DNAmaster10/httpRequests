package me.dnamaster10.httprequests.Tasks;

import me.dnamaster10.httprequests.HttpRequests;
import me.dnamaster10.httprequests.Queue;
import org.bukkit.scheduler.BukkitRunnable;

public class EveryTick extends BukkitRunnable {
    HttpRequests plugin;

    public EveryTick(HttpRequests plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Queue.tickQueue();
    }
}
