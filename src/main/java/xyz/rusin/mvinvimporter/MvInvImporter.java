package xyz.rusin.mvinvimporter;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class MvInvImporter extends JavaPlugin {

    static Logger LOGGER;

    @Override
    public void onEnable() {
        LOGGER = getLogger();
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        Commands commands = new Commands(dataFolder);
        getCommand(Commands.COMMAND).setExecutor(commands);
        getCommand(Commands.COMMAND).setTabCompleter(commands);
    }

}
