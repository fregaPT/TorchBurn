package com.gmail.rcarretta.torchburn;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.PlayerInventory;


class TorchBurnPlayerListener extends PlayerListener {
	private final TorchBurn plugin;
	
	protected TorchBurnPlayerListener(final TorchBurn plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onPlayerMove (PlayerMoveEvent event) {
			Player player = event.getPlayer();

			if ( plugin.isLit(player) ) {
				if (!plugin.getAllowUnderwater())
					if ( player.getRemainingAir() < player.getMaximumAir() )
						plugin.extinguish(player);
			}
			
			if ( plugin.updatePlayerLoc(player) ) {
				plugin.lightArea(player, plugin.getIntensity(), plugin.getFalloff()); // values for a torch
			}
	}
		
	@Override
	public void onItemHeldChange (PlayerItemHeldEvent event) {
		if ( plugin.isLit(event.getPlayer())) {
			plugin.extinguish(event.getPlayer(), event.getPreviousSlot());
		}
	}

	@Override
	public void onPlayerDropItem (PlayerDropItemEvent event) {
		if ( plugin.isLit(event.getPlayer())) {
			if ( event.getItemDrop().getItemStack().getType() == Material.TORCH ) {
				plugin.extinguishNoRemove(event.getPlayer());
				event.getItemDrop().getItemStack().setDurability((short)0);
				if ( event.getItemDrop().getItemStack().getAmount() <= 1 ) {
					event.getItemDrop().remove();
				}
				else {
					event.getItemDrop().getItemStack().setAmount(event.getItemDrop().getItemStack().getAmount()-1);
				}
			}
		}
	}
		
	@Override
	public void onPlayerItem (PlayerItemEvent event) {
		// check if a torch to light
		if ( plugin.isLit(event.getPlayer()) ) {
			// player already has lit torch
			return;
		}
		PlayerInventory inv = event.getPlayer().getInventory();
		if ( inv.getItemInHand().getType() == Material.TORCH ) {
			if (plugin.getRequireSneaking()) {
				if (event.getPlayer().isSneaking()) {
					plugin.lightTorch(event.getPlayer());
				}
			}
			else {
				plugin.lightTorch(event.getPlayer());
			}
		}
	}
		
	@Override
	public void onPlayerQuit (PlayerEvent event) {
		if ( plugin.isLit(event.getPlayer()) ) {
			plugin.extinguish(event.getPlayer());
		}
	}
}