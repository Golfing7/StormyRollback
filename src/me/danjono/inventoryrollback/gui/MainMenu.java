package me.danjono.inventoryrollback.gui;

import java.io.File;
import java.util.UUID;

import me.danjono.inventoryrollback.InventoryRollback;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.danjono.inventoryrollback.config.ConfigFile;
import me.danjono.inventoryrollback.config.MessageData;
import me.danjono.inventoryrollback.data.LogType;
import me.danjono.inventoryrollback.data.PlayerData;

public class MainMenu {
	
	private Player staff;
	private OfflinePlayer player;
	
	public MainMenu(Player staff,  OfflinePlayer player) {
		this.staff = staff;
		this.player = player;
	}

	public Inventory getMenu() {				
		Inventory mainMenu = Bukkit.createInventory(staff, 9, InventoryName.MAIN_MENU.getName());
		Buttons buttons = new Buttons();

		UUID uuid = player.getUniqueId();

		PlayerData playerData = InventoryRollback.getInstance().getPlayerData(player);

		FileConfiguration joinsFile = playerData.getData(LogType.JOIN);
		FileConfiguration quitsFile = playerData.getData(LogType.QUIT);
		FileConfiguration deathsFile = playerData.getData(LogType.DEATH);
		FileConfiguration worldChangeFile = playerData.getData(LogType.WORLD_CHANGE);
		FileConfiguration forceSaveFile = playerData.getData(LogType.FORCE);
		
		if (!joinsFile.contains("data")
		        && !quitsFile.contains("data")
		        && !deathsFile.contains("data")
		        && !worldChangeFile.contains("data")
		        && !forceSaveFile.contains("data")) {
		    staff.sendMessage(MessageData.pluginName + new MessageData().noBackup(player.getName()));
		    return null;
		}

		mainMenu.setItem(0, buttons.playerHead(player, null));

		int position = 2;

		if (deathsFile.contains("data")) {
			mainMenu.setItem(position, buttons.createLogTypeButton(new ItemStack(ConfigFile.deathIcon), uuid, MessageData.deathIconName, LogType.DEATH, null));
			position++;
		}

		if (joinsFile.contains("data")) {
			mainMenu.setItem(position, buttons.createLogTypeButton(new ItemStack(ConfigFile.joinIcon), uuid, MessageData.joinIconName, LogType.JOIN, null));
			position++;
		}

		if (quitsFile.contains("data")) {
			mainMenu.setItem(position, buttons.createLogTypeButton(new ItemStack(ConfigFile.quitIcon), uuid, MessageData.quitIconName, LogType.QUIT, null));
			position++;
		}

		if (worldChangeFile.contains("data")) {
			mainMenu.setItem(position, buttons.createLogTypeButton(new ItemStack(ConfigFile.worldChangeIcon), uuid, MessageData.worldChangeIconName, LogType.WORLD_CHANGE, null));
			position++;
		}
		
		if (forceSaveFile.contains("data")) {
			mainMenu.setItem(position, buttons.createLogTypeButton(new ItemStack(ConfigFile.forceSaveIcon), uuid, MessageData.forceSaveIconName, LogType.FORCE, null));
		}

		return mainMenu;
	}

}
