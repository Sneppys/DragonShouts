package net.mcshockwave.DragonShouts;

import java.util.HashMap;

import net.mcshockwave.DragonShouts.Utils.ItemMetaUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DefaultListener implements Listener {

	HashMap<Player, Block>	select	= new HashMap<>();

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		Action a = event.getAction();
		ItemStack it = p.getItemInHand();
		Block b = event.getClickedBlock();

		if (a == Action.RIGHT_CLICK_BLOCK && DragonShouts.enable_ww && DragonShouts.word_walls.containsKey(b)) {
			Shout s = DragonShouts.word_walls.get(b);
			if (!s.hasLearnedShout(p, 3)) {
				if (DragonShouts.perms_enabled && p.hasPermission("dragonshouts.shout." + s.name())
						|| !DragonShouts.perms_enabled) {
					if (!DragonShouts.ins.hasUsedWW(p, b)) {
						s.setLearnedWithEffect(p, b);
					} else {
						p.sendMessage(DragonShouts.prefix + "You have already used this Word Wall!");
					}
				} else {
					p.sendMessage(DragonShouts.prefix + "No permission to learn §a" + s.name);
				}
			} else {
				p.sendMessage(DragonShouts.prefix + "You have already learned all 3 words for §a" + s.name);
			}
		}

		if (a == Action.RIGHT_CLICK_BLOCK && p.isOp() && DragonShouts.enable_ww && it.getType() == Material.BOOK
				&& b.getType() == Material.BOOKSHELF) {
			int le = Shout.values().length;
			Inventory i = Bukkit.createInventory(null, (le + (9 - (le % 9))), "Shouts");
			for (Shout s : Shout.values()) {
				i.addItem(ItemMetaUtils.setItemName(new ItemStack(Material.WOOL), ChatColor.GREEN + s.name));
			}
			p.openInventory(i);
			select.put(p, b);
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		HumanEntity he = event.getWhoClicked();
		Inventory i = event.getInventory();
		ItemStack cu = event.getCurrentItem();

		if (he instanceof Player) {
			Player p = (Player) he;

			if (i.getName().equalsIgnoreCase("Shouts")) {
				event.setCancelled(true);

				if (cu.getType() == Material.WOOL && ItemMetaUtils.hasCustomName(cu) && select.containsKey(p)) {
					Block b = select.get(p);
					select.remove(p);
					String name = ItemMetaUtils.getItemName(cu);

					Shout s = Shout.valueOf(ChatColor.stripColor(name.replace(' ', '_')));
					if (s != null) {
						p.sendMessage(DragonShouts.prefix + "Word wall added for §a" + s.name);
						DragonShouts.ins.addWordWall(b, s);
						p.closeInventory();
					}
				}
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Block b = event.getBlock();
		Player p = event.getPlayer();

		if (DragonShouts.enable_ww && DragonShouts.word_walls.containsKey(b)) {
			if (p.isOp()) {
				DragonShouts.word_walls.remove(b);
				p.sendMessage(DragonShouts.prefix + "Word Wall destroyed!");
				DragonShouts.ins.saveLearnedData();
			} else
				event.setCancelled(true);
		}
	}

}
