package me.dnamaster10.httprequests.Commands;

import me.dnamaster10.httprequests.HttpRequests;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import static me.dnamaster10.httprequests.HttpRequests.general_last_request_ms;
import static me.dnamaster10.httprequests.HttpRequests.url_last_request_ms;

public class SendHttpChecks extends JavaPlugin {
    public static void sendHTTPCommand(HttpRequests plugin, CommandSender sender, String[] args) {
        if (sender instanceof Player p) {
            if (plugin.getConfig().getBoolean("AllowRequest")) {
                if (plugin.getConfig().getBoolean("AllowChatSender")) {
                    if (p.hasPermission("httprequest.sendhttp")) {
                        if (args.length < 2) {
                            p.sendMessage(ChatColor.RED + "Syntax: /httpsend <GET/POST> <destination> <name1=value1&name2=value2>");
                        } else if (args[0].equals("POST") && !plugin.getConfig().getBoolean("AllowPost")) {
                            p.sendMessage(ChatColor.RED + "Post requests are disabled on this server");
                        } else if (args[0].equals("GET") && !plugin.getConfig().getBoolean("AllowGet")) {
                            p.sendMessage(ChatColor.RED + "Get requests are disabled on this server");
                        } else {
                            if (plugin.getConfig().getBoolean("UseGlobalCooldown")) {
                                if ((int) (System.currentTimeMillis()) - general_last_request_ms < plugin.getConfig().getInt("GlobalCooldownMs")) {
                                    p.sendMessage(ChatColor.RED + "Too many requests are being sent at this time");
                                    if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                                        plugin.getLogger().info("Too many requests are being attempted");
                                    }
                                } else {
                                    if (args.length > 3 && !args[3].equals("application/json")) {
                                        p.sendMessage(ChatColor.RED + "Encoding type not recognised");
                                    } else {
                                        me.dnamaster10.httprequests.HttpRequests.command_args = args;
                                        general_last_request_ms = (int) (System.currentTimeMillis());
                                        SendHttp.SendData(plugin, sender);
                                    }
                                }
                            } else if (plugin.getConfig().getBoolean("UseUrlSpecificCooldown")) {
                                boolean isContainedInArray = false;
                                for (int i = 0; i < url_last_request_ms.size(); i++) {
                                    String current_url_string = url_last_request_ms.get(i);
                                    if (current_url_string.contains(args[1])) {
                                        isContainedInArray = true;
                                        String[] current_url = url_last_request_ms.get(i).split(",");
                                        Long temp = Long.valueOf(current_url[1]);
                                        Long current_time_int = System.currentTimeMillis();
                                        if (current_time_int - temp < plugin.getConfig().getInt("UrlSpecificCooldown")) {
                                            p.sendMessage(ChatColor.RED + "Too many requests are being send to this URL!");
                                            if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                                                plugin.getLogger().warning("Too many requests are being sent to " + args[1]);
                                            }
                                        } else {
                                            if (args.length > 3 && !args[3].equals("application/json")) {
                                                p.sendMessage(ChatColor.RED + "Encoding type not recognised");
                                            } else {
                                                url_last_request_ms.set(i, args[1] + "," + (System.currentTimeMillis()));
                                                me.dnamaster10.httprequests.HttpRequests.command_args = args;
                                                SendHttp.SendData(plugin, sender);
                                            }
                                        }
                                        break;
                                    }
                                }
                                if (!isContainedInArray) {
                                    if (args.length > 3 && !args[3].equals("application/json")) {
                                        p.sendMessage(ChatColor.RED + "Encoding type not recognised");
                                    } else {
                                        url_last_request_ms.add(args[1] + "," + (System.currentTimeMillis()));
                                        me.dnamaster10.httprequests.HttpRequests.command_args = args;
                                        SendHttp.SendData(plugin, sender);
                                    }
                                }
                            } else {
                                if (args.length > 3 && !args[3].equals("application/json")) {
                                    p.sendMessage(ChatColor.RED + "Encoding type not recognised");
                                } else {
                                    me.dnamaster10.httprequests.HttpRequests.command_args = args;
                                    SendHttp.SendData(plugin, sender);
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
            if (plugin.getConfig().getBoolean("AllowRequest")) {
                if (plugin.getConfig().getBoolean("AllowConsoleSender")) {
                    if (args.length < 2) {
                        if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                            plugin.getLogger().warning("Syntax: httpsend <request type> <destination> <name1:value1,name2:value2>");
                        }
                    } else if (args[0].equals("POST") && !plugin.getConfig().getBoolean("AllowPost")) {
                        if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                            plugin.getLogger().warning("POST requests are disabled in the config");
                        }
                    } else if (args[0].equals("GET") && !plugin.getConfig().getBoolean("AllowGet")) {
                        if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                            plugin.getLogger().warning("GET requests are disabled in the config.");
                        }
                    } else {
                        if (plugin.getConfig().getBoolean("UseGlobalCooldown")) {
                            if ((int) (System.currentTimeMillis()) - general_last_request_ms < plugin.getConfig().getInt("GlobalCooldownMs")) {
                                if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                                    plugin.getLogger().warning("Too many requests are being attempted");
                                }
                            } else {
                                if (args.length > 3 && !args[3].equals("application/json")) {
                                    if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                                        plugin.getLogger().warning("Encoding type not recognised");
                                    }
                                } else {
                                    general_last_request_ms = (int) (System.currentTimeMillis());
                                    me.dnamaster10.httprequests.HttpRequests.command_args = args;
                                    SendHttp.SendData(plugin, sender);
                                }
                            }
                        } else if (plugin.getConfig().getBoolean("UseUrlSpecificCooldown")) {
                            boolean isContainedInArray = false;
                            for (int i = 0; i < url_last_request_ms.size(); i++) {
                                String current_url_string = url_last_request_ms.get(i);
                                if (current_url_string.contains(args[1])) {
                                    isContainedInArray = true;
                                    String[] current_url = url_last_request_ms.get(i).split(",");
                                    Long temp = Long.valueOf(current_url[1]);
                                    Long current_time_int = System.currentTimeMillis();
                                    if (current_time_int - temp < plugin.getConfig().getInt("UrlSpecificCooldown")) {
                                        if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                                            plugin.getLogger().warning("Too many requests are being sent to " + args[1]);
                                        }
                                    } else {
                                        if (args.length > 3 && !args[3].equals("application/json")) {
                                            if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                                                plugin.getLogger().warning("Encoding type not recognised");
                                            }
                                        } else {
                                            url_last_request_ms.set(i, args[1] + "," + (System.currentTimeMillis()));
                                            me.dnamaster10.httprequests.HttpRequests.command_args = args;
                                            SendHttp.SendData(plugin, sender);
                                        }
                                    }
                                    break;
                                }
                            }
                            if (!isContainedInArray) {
                                if (args.length > 3 && !args[3].equals("application/json")) {
                                    if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                                        plugin.getLogger().warning("Encoding type not recognised");
                                    }
                                } else {
                                    url_last_request_ms.add(args[1] + "," + (System.currentTimeMillis()));
                                    me.dnamaster10.httprequests.HttpRequests.command_args = args;
                                    SendHttp.SendData(plugin, sender);
                                }
                            }
                        } else {
                            if (args.length > 3 && !args[3].equals("application/json")) {
                                if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                                    plugin.getLogger().warning("Encoding type not recognised");
                                }
                            }
                            me.dnamaster10.httprequests.HttpRequests.command_args = args;
                            SendHttp.SendData(plugin, sender);
                        }
                    }
                } else {
                    if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                        plugin.getLogger().warning("Sending HTTP requests from the console is disabled in the config");
                    }
                }
            } else {
                if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                    plugin.getLogger().warning("HTTP requests are disabled in the config");
                }
            }
        } else {
            if (plugin.getConfig().getBoolean("AllowRequest")) {
                if (plugin.getConfig().getBoolean("AllowCommandBlockSender")) {
                    if (args.length < 2) {
                        if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                            plugin.getLogger().warning("Syntax: httpsend <request type> <destination> <name1:value1,name2:value2>");
                        }
                    } else if (args[0].equals("POST") && !plugin.getConfig().getBoolean("AllowPost")) {
                        if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                            plugin.getLogger().warning("A command block tried to run a POST request, but they are disabled on this server.");
                        }
                    } else if (args[0].equals("GET") && !plugin.getConfig().getBoolean("AllowGet")) {
                        if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                            plugin.getLogger().warning("A command block tried to run a GET request, but they are disabled on this server.");
                        }
                    } else {
                        if (plugin.getConfig().getBoolean("UseGlobalCooldown")) {
                            if ((int) (System.currentTimeMillis()) - general_last_request_ms < plugin.getConfig().getInt("GlobalCooldownMs")) {
                                if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                                    plugin.getLogger().warning("Too many requests are being attempted");
                                }
                            } else {
                                if (args.length > 3 && !args[3].equals("application/json")) {
                                    if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                                        plugin.getLogger().warning("A command block tried to send a request, but the encoding type was not recognised");
                                    }
                                } else {
                                    me.dnamaster10.httprequests.HttpRequests.command_args = args;
                                    general_last_request_ms = (int) (System.currentTimeMillis());
                                    SendHttp.SendData(plugin, sender);
                                }
                            }
                        } else if (plugin.getConfig().getBoolean("UseUrlSpecificCooldown")) {
                            boolean isContainedInArray = false;
                            for (int i = 0; i < url_last_request_ms.size(); i++) {
                                String current_url_string = url_last_request_ms.get(i);
                                if (current_url_string.contains(args[1])) {
                                    isContainedInArray = true;
                                    String[] current_url = url_last_request_ms.get(i).split(",");
                                    Long temp = Long.valueOf(current_url[1]);
                                    Long current_time_int = System.currentTimeMillis();
                                    if (current_time_int - temp < plugin.getConfig().getInt("UrlSpecificCooldown")) {
                                        if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                                            plugin.getLogger().warning("Too many requests are being sent to " + args[1]);
                                        }
                                    } else {
                                        if (args.length > 3 && !args[3].equals("application/json")) {
                                            if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                                                plugin.getLogger().warning("A command block tried to send a request, but the encoding type was not recognised");
                                            }
                                        } else {
                                            url_last_request_ms.set(i, args[1] + "," + (System.currentTimeMillis()));
                                            me.dnamaster10.httprequests.HttpRequests.command_args = args;
                                            SendHttp.SendData(plugin, sender);
                                        }
                                    }
                                    break;
                                }
                            }
                            if (!isContainedInArray) {
                                if (args.length > 3 && !args[3].equals("application/json")) {
                                    if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                                        plugin.getLogger().warning("A command block tried to send a request, but the encoding type was not recognised");
                                    }
                                } else {
                                    url_last_request_ms.add(args[1] + "," + (System.currentTimeMillis()));
                                    me.dnamaster10.httprequests.HttpRequests.command_args = args;
                                    SendHttp.SendData(plugin, sender);
                                }
                            }
                        } else {
                            if (args.length > 3 && !args[3].equals("application/json")) {
                                if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                                    plugin.getLogger().warning("A command block tried to send a request, but the encoding type was not recognised");
                                }
                            } else {
                                me.dnamaster10.httprequests.HttpRequests.command_args = args;
                                SendHttp.SendData(plugin, sender);
                            }
                        }
                    }
                } else {
                    if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                        plugin.getLogger().warning("A command block tried to send an HTTP request, but sending HTTP requests from command blocks is disabled in the config");
                    }
                }
            } else {
                if (plugin.getConfig().getBoolean("PrintRequestsToConsole")) {
                    plugin.getLogger().warning("A command block tried to send an HTTP request, but HTTP requests are disabled in the config");
                }
            }
        }
    }
}
