package me.danjono.inventoryrollback.listeners;

import me.danjono.inventoryrollback.InventoryRollback;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.danjono.inventoryrollback.config.ConfigFile;
import me.danjono.inventoryrollback.data.LogType;
import me.danjono.inventoryrollback.inventory.SaveInventory;

public class EventLogs implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	private void playerJoin(PlayerJoinEvent e) {
		if (!ConfigFile.enabled) return;

		Player player = e.getPlayer();
		if (player.hasPermission("inventoryrollback.joinsave")) {			
			new SaveInventory(e.getPlayer(), LogType.JOIN, null, null, player.getInventory()).createSave();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void playerJoinSave(PlayerJoinEvent e) {
		InventoryRollback.getInstance().getPlayerData(e.getPlayer());
	}

	@EventHandler
	private void playerQuit(PlayerQuitEvent e) {
		if (!ConfigFile.enabled) return;

		Player player = e.getPlayer();

		if (player.hasPermission("inventoryrollback.leavesave")) {				
			new SaveInventory(e.getPlayer(), LogType.QUIT, null, null, player.getInventory()).createSave();
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void playerDeath(EntityDamageEvent e) {
		if(e.isCancelled())return;
		if (!ConfigFile.enabled) return;
		if (!(e.getEntity() instanceof Player)) return;

		Player player = (Player) e.getEntity();

		Player killer = player.getKiller();

		if (player.hasPermission("inventoryrollback.deathsave") && player.getHealth() - e.getFinalDamage() <= 0.0) {
			new SaveInventory(player, LogType.DEATH, e.getEntity().getLastDamageCause() != null ? e.getEntity().getLastDamageCause().getCause() : EntityDamageEvent.DamageCause.CUSTOM, killer != null && killer.getName() != null ? killer.getName() : "NONE", player.getInventory()).createSave();
		}
	}

	@EventHandler
	private void playerChangeWorld(PlayerChangedWorldEvent e) {
		if (!ConfigFile.enabled) return;

		Player player = e.getPlayer();

		if (player.hasPermission("inventoryrollback.worldchangesave")) {				
			new SaveInventory(e.getPlayer(), LogType.WORLD_CHANGE, null, null, player.getInventory()).createSave();
		}
	}

}
