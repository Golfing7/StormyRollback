package me.danjono.inventoryrollback.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.danjono.inventoryrollback.InventoryRollback;
import me.danjono.inventoryrollback.config.ConfigFile;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerData {
	
	private UUID uuid;
	private File folderLocation;

	private FileConfiguration deathConfig;
	private FileConfiguration forceConfig;
	private FileConfiguration joinConfig;
	private FileConfiguration quitConfig;
	private FileConfiguration worldConfig;

	private File deathFile;
	private File forceFile;
	private File joinFile;
	private File quitFile;
	private File worldFile;
	
	public PlayerData(Player player) {
		this.uuid = player.getUniqueId();		
		this.folderLocation = new File(ConfigFile.folderLocation, "saves/");

		findPlayerFile();
	}
	
	public PlayerData(OfflinePlayer player) {
		this.uuid = player.getUniqueId();	
		this.folderLocation = new File(ConfigFile.folderLocation, "saves/");
		
		findPlayerFile();
	}
	
	public PlayerData(UUID uuid) {
		this.uuid = uuid;	
		this.folderLocation = new File(ConfigFile.folderLocation, "saves/");
		
		findPlayerFile();
	}
		
	private synchronized void findPlayerFile() {
		try{
			deathConfig = YamlConfiguration.loadConfiguration(deathFile = new File(folderLocation, "deaths/" + uuid + ".yml"));
		}catch(Throwable exc){
			System.out.println("There was an error loading " + uuid + "'s death logs! Resetting data.");

			File file = new File(folderLocation, "deaths/" + uuid + ".yml");

			file.delete();

			try{
				file.createNewFile();

				worldConfig = YamlConfiguration.loadConfiguration(deathFile = file);
			}catch(Exception ignored){

			}
		}
		try{
			forceConfig = YamlConfiguration.loadConfiguration(forceFile = new File(folderLocation, "force/" + uuid + ".yml"));
		}catch(Throwable exc){
			System.out.println("There was an error loading " + uuid + "'s force logs! Resetting data.");

			File file = new File(folderLocation, "force/" + uuid + ".yml");

			file.delete();

			try{
				file.createNewFile();

				worldConfig = YamlConfiguration.loadConfiguration(forceFile = file);
			}catch(Exception ignored){

			}
		}
		try{
			joinConfig = YamlConfiguration.loadConfiguration(joinFile = new File(folderLocation, "joins/" + uuid + ".yml"));
		}catch(Throwable exc){
			System.out.println("There was an error loading " + uuid + "'s join logs! Resetting data.");

			File file = new File(folderLocation, "joins/" + uuid + ".yml");

			file.delete();

			try{
				file.createNewFile();

				worldConfig = YamlConfiguration.loadConfiguration(joinFile = file);
			}catch(Exception ignored){

			}
		}
		try{
			quitConfig = YamlConfiguration.loadConfiguration(quitFile = new File(folderLocation, "quits/" + uuid + ".yml"));
		}catch(Throwable exc){
			System.out.println("There was an error loading " + uuid + "'s quit logs! Resetting data.");

			File file = new File(folderLocation, "quits/" + uuid + ".yml");

			file.delete();

			try{
				file.createNewFile();

				worldConfig = YamlConfiguration.loadConfiguration(quitFile = file);
			}catch(Exception ignored){

			}
		}
		try{
			worldConfig = YamlConfiguration.loadConfiguration(worldFile = new File(folderLocation, "worldChanges/" + uuid + ".yml"));
		}catch(Throwable exc){
			System.out.println("There was an error loading " + uuid + "'s world logs! Resetting data.");

			File file = new File(folderLocation, "worldChanges/" + uuid + ".yml");

			file.delete();

			try{
				file.createNewFile();

				worldConfig = YamlConfiguration.loadConfiguration(worldFile = file);
			}catch(Exception ignored){

			}
		}
	}
	
	public synchronized FileConfiguration getData(LogType logType) {
		switch(logType){
			case JOIN:
				return joinConfig;
			case QUIT:
				return quitConfig;
			case DEATH:
				return deathConfig;
			case FORCE:
				return forceConfig;
			case WORLD_CHANGE:
				return worldConfig;
		}
		return null;
	}

	public synchronized File getDataFile(LogType logType) {
		switch(logType){
			case JOIN:
				return joinFile;
			case QUIT:
				return quitFile;
			case DEATH:
				return deathFile;
			case FORCE:
				return forceFile;
			case WORLD_CHANGE:
				return worldFile;
		}
		return null;
	}
	
	public void saveData(LogType logType) {
		new BukkitRunnable(){
			@Override
			public void run(){
				try {
					synchronized (PlayerData.this){
						switch(logType){
							case JOIN:
								joinConfig.save(joinFile);
							case QUIT:
								quitConfig.save(quitFile);
							case DEATH:
								deathConfig.save(deathFile);
							case FORCE:
								forceConfig.save(forceFile);
							case WORLD_CHANGE:
								worldConfig.save(worldFile);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(InventoryRollback.getInstance());
	}
	
	public int getMaxSaves(LogType logType) {
		switch(logType){
			case JOIN:
				return ConfigFile.maxSavesJoin;
			case QUIT:
				return ConfigFile.maxSavesQuit;
			case DEATH:
				return ConfigFile.maxSavesDeath;
			case FORCE:
				return ConfigFile.maxSavesForce;
			case WORLD_CHANGE:
				return ConfigFile.maxSavesWorldChange;
			default:
				return 0;
		}
	}

}
