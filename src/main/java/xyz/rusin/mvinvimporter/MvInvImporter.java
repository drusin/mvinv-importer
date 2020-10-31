package xyz.rusin.mvinvimporter;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

public class MvInvImporter extends JavaPlugin {

    @Override
    public void onEnable() {
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        Commands commands = new Commands(dataFolder);
        getCommand(Commands.COMMAND).setExecutor(commands);
        getCommand(Commands.COMMAND).setTabCompleter(commands);
    }

}
