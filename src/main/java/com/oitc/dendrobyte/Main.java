package com.oitc.dendrobyte;

import com.oitc.dendrobyte.creation.ArenaCreationListeners;
import com.oitc.dendrobyte.creation.CreationStates;
import com.oitc.dendrobyte.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

/**
 * Created by mobki, aka Dendrobyte, on 8/23/2019
 * Written for project OneInTheChamber
 * Please do not use or edit this code unless permission has been given (or if it's on GitHub...)
 * Contact me on Twitter, @Mobkinz78, with any questions
 * §
 */
public class Main extends JavaPlugin {

    private static Main instance;
    private String prefix = "§8[§5OITC§8]§7 ";
    File configFile;
    FileConfiguration configFileConfig;

    ArenaManager am;

    @Override
    public void onEnable(){
        Bukkit.getLogger().log(Level.INFO, "OITC now enabling...");

        // Initialize instances
        instance = this;
        am = ArenaManager.getInstance();

        // Load arenas
        ArenaManager.getInstance().loadArenas();

        // Make configuration
        makeConfig();;

        // Register events
        Bukkit.getServer().getPluginManager().registerEvents(new SignClickListeners(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ArenaCreationListeners(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerHitListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerTeleportChatListeners(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new BlockPlaceBreakListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerLeaveListener(), this);

        // Register command
        getCommand("oitc").setExecutor(new BaseCommand());

        Bukkit.getLogger().log(Level.INFO, "OITC has successfully been enabled!");
    }

    @Override
    public void onDisable(){
        for(ArenaObject arena : am.getCurrentArenas()){
            if(arena.getState() == ArenaGameState.RUNNING){
                am.endGame(arena);
            }
            else if (arena.getState() == ArenaGameState.WAITING){
                for(Player player : arena.getPlayers())
                am.removePlayerFromGame(player, arena);
            }
        }
    }

    public static Main getInstance(){
        return instance;
    }

    public String getPrefix(){
        return prefix;
    }

    public void makeConfig(){
        if(!getDataFolder().exists()){
            getDataFolder().mkdirs();
        }
        File file = new File(getDataFolder(), "config.yml");
        if(!file.exists()){
            getLogger().log(Level.INFO, "Creating OITC config.yml");
            saveDefaultConfig();
        } else {
            getLogger().log(Level.INFO,"Loaded configuration for OITC v" + getDescription().getVersion() + "!");
        }
    }

}
