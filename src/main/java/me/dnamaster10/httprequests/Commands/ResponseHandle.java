package me.dnamaster10.httprequests.Commands;

import com.sun.tools.jconsole.JConsoleContext;
import me.dnamaster10.httprequests.HttpRequests;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;


import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.List;

public class ResponseHandle extends JavaPlugin {
    String[] responseBody;
    public static Boolean HandleResponse(HttpRequests plugin, String response, String[] command_args) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            List<ScriptEngineFactory> engines = (new ScriptEngineManager()).getEngineFactories();
            plugin.getLogger().info("Engines: ");
            for (ScriptEngineFactory f: engines) {
                String output = (f.getLanguageName()+" "+f.getEngineName()+" "+f.getNames().toString());
                plugin.getLogger().info(output);
            }

        });
        return true;
    }
}
