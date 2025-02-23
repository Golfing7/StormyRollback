package me.danjono.inventoryrollback.inventory;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import me.danjono.inventoryrollback.InventoryRollback;
import me.danjono.inventoryrollback.InventoryRollback.VersionName;
import me.danjono.inventoryrollback.data.LogType;
import me.danjono.inventoryrollback.data.PlayerData;

public class SaveInventory {
	
	private Player player;
	private LogType logType;
	private DamageCause deathCause;
	private String killer;
	
	private PlayerInventory mainInventory;
	
	public SaveInventory(Player player, LogType logType, DamageCause deathCause, String killer, PlayerInventory mainInventory) {
		this.player = player;
		this.logType = logType;
		this.deathCause = deathCause;
		this.killer = killer;
		
		this.mainInventory = mainInventory;
	}
		
	public void createSave() {
		PlayerData data = InventoryRollback.getInstance().getPlayerData(player);

		FileConfiguration inventoryData = data.getData(logType);
		
		ItemStack[] armour = null;

        if (InventoryRollback.getVersion().equals(VersionName.v1_8))
            armour = mainInventory.getArmorContents();
				
		int maxSaves = data.getMaxSaves(logType);
				
		float xp = getTotalExperience(player);

		long time = System.currentTimeMillis();

		int saves = inventoryData.getInt("saves");

		if (maxSaves > 0 && inventoryData.contains("data")) {
			if (saves >= maxSaves) {
				List<Double> timeSaved = new ArrayList<Double>();

				for (String times : inventoryData.getConfigurationSection("data").getKeys(false)) {
					timeSaved.add(Double.parseDouble(times));
				}
				
				int deleteAmount = saves - maxSaves + 1;
				
				for (int i = 0; i < deleteAmount; i++) {
					Double deleteData = Collections.min(timeSaved);
					DecimalFormat df = new DecimalFormat("#.##############");
					
					inventoryData.set("data." + df.format(deleteData), null);
					timeSaved.remove(deleteData);
					saves--;
				}
			}
		}

		inventoryData.set("data." + time + ".inventory", toBase64(mainInventory));	
		
		if (InventoryRollback.getVersion().equals(VersionName.v1_8) && armour != null)
            inventoryData.set("data." + time + ".armour", toBase64(armour));

		inventoryData.set("data." + time + ".xp", xp);
		inventoryData.set("data." + time + ".health", player.getHealth());
		inventoryData.set("data." + time + ".hunger", player.getFoodLevel());
		inventoryData.set("data." + time + ".saturation", player.getSaturation());
		inventoryData.set("data." + time + ".location.world", player.getWorld().getName());
		inventoryData.set("data." + time + ".location.x", Math.floor(player.getLocation().getX()) + 0.5);
		inventoryData.set("data." + time + ".location.y", Math.floor(player.getLocation().getY()));
		inventoryData.set("data." + time + ".location.z", Math.floor(player.getLocation().getZ()) + 0.5);
		inventoryData.set("data." + time + ".logType", logType.name());
		inventoryData.set("data." + time + ".version", InventoryRollback.getPackageVersion());

		if (deathCause != null)
			inventoryData.set("data." + time + ".deathReason", deathCause.name());

		if(killer != null)
			inventoryData.set("data." + time + ".killer", killer);
		
		inventoryData.set("saves", saves + 1);

		data.saveData(logType);
	}
		
	//Conversion to Base64 code courtesy of github.com/JustRayz	
	private String toBase64(Inventory inventory) {
		return toBase64(inventory.getContents());
	}

	private String toBase64(ItemStack[] contents) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

			dataOutput.writeInt(contents.length);

			for (ItemStack stack : contents) {
				dataOutput.writeObject(stack);
			}
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception e) {
			throw new IllegalStateException("Unable to save item stacks.", e);
		}
	}

	//Credits to Dev_Richard (https://www.spigotmc.org/members/dev_richard.38792/)
	//https://gist.github.com/RichardB122/8958201b54d90afbc6f0
	private float getTotalExperience(Player player) {
        int level = player.getLevel();
        float currentExp = player.getExp();
        int experience;
        int requiredExperience;
        
        if(level >= 0 && level <= 15) {
            experience = (int) Math.ceil(Math.pow(level, 2) + (6 * level));
            requiredExperience = 2 * level + 7;
        } else if(level > 15 && level <= 30) {
            experience = (int) Math.ceil((2.5 * Math.pow(level, 2) - (40.5 * level) + 360));
            requiredExperience = 5 * level - 38;
        } else {
            experience = (int) Math.ceil(((4.5 * Math.pow(level, 2) - (162.5 * level) + 2220)));
            requiredExperience = 9 * level - 158;
        }
        
        experience += Math.ceil(currentExp * requiredExperience);
        
        return experience;
    }

}
