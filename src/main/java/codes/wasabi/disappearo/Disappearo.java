package codes.wasabi.disappearo;

import codes.wasabi.disappearo.api.Keys;
import codes.wasabi.disappearo.service.Commands;
import codes.wasabi.disappearo.service.config.Config;
import codes.wasabi.disappearo.service.Events;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class Disappearo extends JavaPlugin {

    public static Disappearo instance;
    public static Logger logger;
    public static Events events;
    public static Commands commands;
    public static Config config;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        logger.log(Level.INFO, "Loading config");
        config = new Config(this);
        logger.log(Level.INFO, "Instantiating keys");
        Keys.initialize(this);
        logger.log(Level.INFO, "Registering events");
        events = new Events();
        Bukkit.getPluginManager().registerEvents(events, this);
        logger.log(Level.INFO, "Registering commands");
        PluginCommand pc = Bukkit.getPluginCommand("disappearo");
        if (pc != null) {
            commands = new Commands();
            pc.setExecutor(commands);
            pc.setTabCompleter(commands);
        } else {
            logger.log(Level.WARNING, "Failed to bind to command!");
        }
    }

    @Override
    public void onDisable() {
        logger.log(Level.INFO, "Saving config");
        config.save();
    }

}
