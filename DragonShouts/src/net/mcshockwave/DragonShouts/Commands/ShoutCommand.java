package net.mcshockwave.DragonShouts.Commands;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.mcshockwave.DragonShouts.DragonShouts;
import net.mcshockwave.DragonShouts.Shout;
import net.mcshockwave.DragonShouts.Utils.ItemMetaUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShoutCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			if (args[0].equalsIgnoreCase("teachall") && sender.isOp()) {
				if (args.length > 1) {
					Player p2 = Bukkit.getPlayer(args[1]);
					if (p2 != null) {
						for (Shout s : Shout.values()) {
							for (int i = 0; i <= (4 - (s.getLearnLevel(p2) - 1)); i++) {
								s.setLearned(p2);
							}
						}
						p2.sendMessage(DragonShouts.prefix + "Learned all shouts from §a" + sender.getName());
						sender.sendMessage(DragonShouts.prefix + "Taught all shouts to §a" + p2.getName());
					}
				}
			}
			if (args[0].equalsIgnoreCase("teach") && sender.isOp()) {
				if (args.length > 2) {
					try {
						Player p2 = Bukkit.getPlayer(args[1]);
						Shout s = Shout.get(args[2]);
						if (p2 != null) {
							s.setLearnedWithEffect(p2, null);
							sender.sendMessage(DragonShouts.prefix + "Taught §a" + s.name + "§7 to §a" + p2.getName());
						}
					} catch (Exception e) {
						sender.sendMessage(DragonShouts.prefix + "Invalid args");
					}
				}
			}
		}
		if (sender instanceof Player && (sender.isOp() || !DragonShouts.op_only)) {

			Player p = (Player) sender;

			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("reload") && p.isOp()) {
					DragonShouts.ins.reloadConfig();
					DragonShouts.ins.reloadLearnedData();
					DragonShouts.ins.reloadAll();

					p.sendMessage(DragonShouts.prefix + "Configuration reloaded!");
					return true;
				}
				if (args[0].equalsIgnoreCase("teachall") && p.isOp()) {
					if (args.length > 1) {
						Player p2 = Bukkit.getPlayer(args[1]);
						if (p2 != null) {
							for (Shout s : Shout.values()) {
								for (int i = 0; i <= (4 - (s.getLearnLevel(p2) - 1)); i++) {
									s.setLearned(p2);
								}
							}
							p2.sendMessage(DragonShouts.prefix + "Learned all shouts from §a" + p.getName());
							p.sendMessage(DragonShouts.prefix + "Taught all shouts to §a" + p2.getName());
						}
					}
				}
				if (args[0].equalsIgnoreCase("teach") && p.isOp()) {
					if (args.length > 2) {
						try {
							Player p2 = Bukkit.getPlayer(args[1]);
							Shout s = Shout.get(args[2]);
							if (p2 != null) {
								s.setLearnedWithEffect(p2, null);
								p.sendMessage(DragonShouts.prefix + "Taught §a" + s.name + "§7 to §a" + p2.getName());
							}
						} catch (Exception e) {
							p.sendMessage(DragonShouts.prefix + "Invalid args");
						}
					}
				}
				if (args[0].equalsIgnoreCase("bind")) {
					if (args.length > 1) {
						String toBind = "";
						for (int i = 1; i < 4; i++) {
							if (args.length > i) {
								toBind += " " + args[i];
							}
						}
						ItemStack it = p.getItemInHand();
						if (it == null)
							return false;
						List<String> lore = ItemMetaUtils.getLore(it);
						if (lore == null) {
							lore = new ArrayList<>();
						}

						String pre = "§aBound>";
						for (String s : lore) {
							if (s.startsWith(pre)) {
								lore.remove(s);
								break;
							}
						}

						lore.add(pre + toBind);
						ItemMetaUtils.setLore(it, lore.toArray(new String[0]));
					}
				}
				if (args.length == 1) {
					for (Shout s : Shout.values()) {
						if (args[0].equalsIgnoreCase(s.w1)) {
							if (DragonShouts.perms_enabled && p.hasPermission("dragonshouts.shout." + s.name())
									|| !DragonShouts.perms_enabled) {
								if (DragonShouts.require_learn && s.hasLearnedShout(p, 1)
										|| !DragonShouts.require_learn) {
									s.shout(p, 1);
									break;
								} else {
									p.sendMessage(DragonShouts.prefix + "You have not learned this word!");
								}
							} else {
								p.sendMessage(DragonShouts.prefix + "No permission to use this shout");
							}
						}
					}
				}
				if (args.length == 2) {
					for (Shout s : Shout.values()) {
						if (args[0].equalsIgnoreCase(s.w1) && args[1].equalsIgnoreCase(s.w2)) {
							if (DragonShouts.perms_enabled && p.hasPermission("dragonshouts.shout." + s.name())
									|| !DragonShouts.perms_enabled) {
								if (DragonShouts.require_learn && s.hasLearnedShout(p, 2)
										|| !DragonShouts.require_learn) {
									s.shout(p, 2);
									break;
								} else {
									p.sendMessage(DragonShouts.prefix + "You have not learned this word!");
								}
							} else {
								p.sendMessage(DragonShouts.prefix + "No permission to use this shout");
							}
						}
					}
				}
				if (args.length >= 3) {
					for (Shout s : Shout.values()) {
						if (args[0].equalsIgnoreCase(s.w1) && args[1].equalsIgnoreCase(s.w2)
								&& args[2].equalsIgnoreCase(s.w3)) {
							if (DragonShouts.perms_enabled && p.hasPermission("dragonshouts.shout." + s.name())
									|| !DragonShouts.perms_enabled) {
								if (DragonShouts.require_learn && s.hasLearnedShout(p, 3)
										|| !DragonShouts.require_learn) {
									s.shout(p, 3);
									break;
								} else {
									p.sendMessage(DragonShouts.prefix + "You have not learned this word!");
								}
							} else {
								p.sendMessage(DragonShouts.prefix + "No permission to use this shout");
							}
						}
					}
				}
			} else {
				p.sendMessage(ChatColor.GRAY + "All shouts: ");
				ArrayList<String> snames = new ArrayList<>();
				for (Shout s : Shout.values()) {
					snames.add(s.name());
				}
				Collections.sort(snames, Collator.getInstance());
				for (String sn : snames) {
					Shout s = Shout.valueOf(sn);
					if (DragonShouts.perms_enabled && p.hasPermission("dragonshouts.shout." + s.name())
							|| !DragonShouts.perms_enabled) {
						if (DragonShouts.require_learn && s.hasLearnedShout(p, 1)) {
							p.sendMessage(ChatColor.AQUA + s.name + ChatColor.DARK_AQUA + " - " + s.w1 + " "
									+ (s.hasLearnedShout(p, 2) ? s.w2 : "") + " "
									+ (s.hasLearnedShout(p, 3) ? s.w3 : ""));
						} else if (!DragonShouts.require_learn) {
							p.sendMessage(ChatColor.AQUA + s.name + ChatColor.DARK_AQUA + " - " + s.w1 + " " + s.w2
									+ " " + s.w3);
						}
					}
				}
			}

			return true;
		}

		return false;
	}
}
