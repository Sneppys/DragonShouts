package net.mcshockwave.DragonShouts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import net.mcshockwave.DragonShouts.Utils.LocUtils;
import net.mcshockwave.DragonShouts.Utils.PacketUtils;
import net.mcshockwave.DragonShouts.Utils.PacketUtils.ParticleEffect;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public enum Shout {

	Unrelenting_Force(
		"Fus",
		"Ro",
		"Dah",
		15,
		20,
		45),
	Fire_Breath(
		"Yol",
		"Toor",
		"Shul",
		30,
		50,
		100),
	Frost_Breath(
		"Fo",
		"Krah",
		"Diin",
		30,
		50,
		100),
	Ice_Form(
		"Liss",
		"Slen",
		"Nus",
		60,
		90,
		120),
	Storm_Call(
		"Strun",
		"Bah",
		"Qo",
		300,
		480,
		600),
	Whirlwind_Sprint(
		"Wuld",
		"Nah",
		"Kest",
		20,
		25,
		35),
	Become_Ethereal(
		"Feim",
		"Zii",
		"Gron",
		30,
		40,
		50),
	Dragon_Aspect(
		"Mul",
		"Qah",
		"Diiv",
		600,
		600,
		600),
	Aura_Whisper(
		"Laas",
		"Yah",
		"Nir",
		30,
		40,
		50),
	Marked_For_Death(
		"Krii",
		"Lun",
		"Aus",
		20,
		30,
		40),
	Drain_Vitality(
		"Gaan",
		"Lah",
		"Haas",
		30,
		60,
		90),
	Battle_Fury(
		"Mid",
		"Vur",
		"Shaan",
		20,
		30,
		40),
	Elemental_Fury(
		"Su",
		"Grah",
		"Dun",
		30,
		40,
		50),
	Soul_Tear(
		"Rii",
		"Vaaz",
		"Zol",
		5,
		5,
		90),
	Clear_Skies(
		"Lok",
		"Vah",
		"Koor",
		5,
		10,
		15),
	Cyclone(
		"Ven",
		"Gar",
		"Nos",
		30,
		45,
		60),
	Disarm(
		"Zun",
		"Haal",
		"Viik",
		30,
		35,
		50),
	Throw_Voice(
		"Zul",
		"Mey",
		"Gut",
		30,
		15,
		5),
	Dragonrend(
		"Joor",
		"Zah",
		"Frul",
		10,
		12,
		15);

	public String	name, w1, w2, w3;
	public int		c1, c2, c3;

	Shout(String w1, String w2, String w3, int cool1, int cool2, int cool3) {
		this.name = name().replace('_', ' ');

		this.w1 = w1;
		this.w2 = w2;
		this.w3 = w3;
		this.c1 = cool1;
		this.c2 = cool2;
		this.c3 = cool3;
	}

	public int getCooldown(int num) {
		if (num == 1) {
			return c1;
		}
		if (num == 2) {
			return c2;
		}
		if (num >= 3) {
			return c3;
		}
		return 0;
	}

	public Random						rand		= new Random();

	public static HashMap<Player, Long>	cooldown	= new HashMap<>();

	@SuppressWarnings("deprecation")
	public void shout(final Player p, final int num) {
		if (!hasLearnedShout(p, num)) {
			p.sendMessage(DragonShouts.prefix + "You haven't learned that shout yet!");
			return;
		}

		if (DragonShouts.enable_cooldown) {
			if (cooldown.containsKey(p)
					&& cooldown.get(p) > TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())) {
				p.sendMessage(DragonShouts.prefix + "You can't shout for another "
						+ (cooldown.get(p) - TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())) + " seconds!");
				return;
			} else {
				cooldown.remove(p);
				cooldown.put(p, TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + getCooldown(num));
				Bukkit.getScheduler().runTaskLaterAsynchronously(DragonShouts.ins, new Runnable() {
					public void run() {
						p.sendMessage(DragonShouts.prefix + "You can now shout!");
					}
				}, getCooldown(num) * 20);
			}
		}

		if (DragonShouts.broadcast_enabled) {
			for (Player p2 : Bukkit.getOnlinePlayers()) {
				if (p.getWorld() == p2.getWorld()
						&& p.getLocation().distance(p2.getLocation()) < DragonShouts.broadcast_range
						|| DragonShouts.broadcast_range <= 0) {
					String send = ChatColor.translateAlternateColorCodes(
							'&',
							DragonShouts.broadcast_format
									.replaceAll("%p", p.getDisplayName())
									.replaceAll(
											"%w",
											w1.toUpperCase()
													+ (num > 1 ? " " + w2.toUpperCase()
															+ (num > 2 ? " " + w3.toUpperCase() + "" : "") : ""))
									.replaceAll("%n", name));
					p2.sendMessage(send);
				}
			}
		}

		if (this == Unrelenting_Force) {
			Block[] bs = p.getLineOfSight(null, num * 2).toArray(new Block[0]);
			List<Entity> near = p.getNearbyEntities(15, 15, 15);
			p.getWorld().playSound(p.getLocation(), Sound.EXPLODE, 1, 1);
			for (Block b : bs) {
				PacketUtils.playParticleEffect(ParticleEffect.CLOUD, b.getLocation().add(0.5, 0.5, 0.5), 0, 0.3f,
						num * 15);

				for (Entity e : near) {
					if (e.getLocation().distance(b.getLocation()) < 4) {
						e.setVelocity(LocUtils.getVelocity(p.getLocation(), e.getLocation()).multiply(num / 2 + 1)
								.add(new Vector(0, 0.1, 0)));
					}
				}
			}
		}

		if (this == Fire_Breath) {
			Block[] bs = p.getLineOfSight(null, (num * 2) + 2).toArray(new Block[0]);
			List<Entity> near = p.getNearbyEntities(15, 15, 15);
			p.getWorld().playSound(p.getLocation(), Sound.GHAST_FIREBALL, 1, 0);
			for (Block b : bs) {
				PacketUtils.playParticleEffect(ParticleEffect.FLAME, b.getLocation().add(0.5, 0.5, 0.5), 0, 0.3f,
						num * 15);

				for (Entity e : near) {
					if (e.getLocation().distance(b.getLocation()) < 4) {
						e.setFireTicks(100 * num);
					}
				}
			}
		}

		if (this == Ice_Form) {
			Block[] bs = p.getLineOfSight(null, (num * 2) + 2).toArray(new Block[0]);
			List<Entity> near = p.getNearbyEntities(15, 15, 15);
			p.getWorld().playSound(p.getLocation(), Sound.GLASS, 1, 0);
			for (Block b : bs) {
				PacketUtils.playParticleEffect(ParticleEffect.INSTANT_SPELL, b.getLocation().add(0.5, 0.5, 0.5), 1,
						0.3f, num * 35);

				for (Entity e : near) {
					if (!(e instanceof LivingEntity)) {
						continue;
					}
					LivingEntity le = (LivingEntity) e;
					if (e.getLocation().distance(b.getLocation()) < 4) {
						le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, num * 20, 25));
					}
				}
			}
		}

		if (this == Frost_Breath) {
			Block[] bs = p.getLineOfSight(null, (num * 2) + 2).toArray(new Block[0]);
			List<Entity> near = p.getNearbyEntities(15, 15, 15);
			p.getWorld().playSound(p.getLocation(), Sound.GLASS, 1, 0);
			for (Block b : bs) {
				PacketUtils.playParticleEffect(ParticleEffect.INSTANT_SPELL, b.getLocation().add(0.5, 0.5, 0.5), 1,
						0.3f, num * 35);

				for (Entity e : near) {
					if (!(e instanceof LivingEntity)) {
						continue;
					}
					LivingEntity le = (LivingEntity) e;
					if (e.getLocation().distance(b.getLocation()) < 4) {
						le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, num * 50, num - 1));
						le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, num * 20, num - 1));
					}
				}
			}
		}

		if (this == Whirlwind_Sprint) {
			p.setVelocity(p.getLocation().getDirection().multiply((num * 2) + 3).setY(0.4f));
			p.getWorld().playSound(p.getLocation(), Sound.BAT_TAKEOFF, 1, 0);
		}

		if (this == Become_Ethereal) {
			p.getWorld().playSound(p.getLocation(), Sound.FIZZ, 1, 0);

			p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, num * 150, 50));
			p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, num * 150, 50));
		}

		if (this == Dragon_Aspect) {
			p.getWorld().playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 1, 1.3f);

			int dur = num * 300;

			p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, dur, 3));
			p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, dur, 2));
			p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, dur, 1));

			for (int i = 0; i < dur / 5; i++) {
				Bukkit.getScheduler().runTaskLater(DragonShouts.ins, new Runnable() {
					public void run() {
						if (p.isOnline()) {
							PacketUtils.playParticleEffect(ParticleEffect.LAVA, p.getLocation(), 0, 0.1f, 10);
						}
					}
				}, i * 5);
			}
		}

		if (this == Storm_Call) {
			Location l = p.getLocation();
			boolean ce = DragonShouts.enable_cooldown;

			for (int i = 0; i < rand.nextInt(5 + num) + (num * 2) + (ce ? 10 : 0); i++) {
				final Location l2 = LocUtils.addRand(l.clone(), 50, 25, 50);

				Bukkit.getScheduler().runTaskLater(DragonShouts.ins, new Runnable() {
					public void run() {
						Location m = l2;
						for (Player p2 : Bukkit.getOnlinePlayers()) {
							if (p2 != p && p2.getLocation().distance(m) < 25) {
								m = p2.getLocation();
								break;
							}
						}

						m.getWorld().strikeLightningEffect(m);

						for (Player p2 : Bukkit.getOnlinePlayers()) {
							if (p2.getWorld() == p.getWorld() && p2 != p && p2.getLocation().distance(m) < 10) {
								p2.setFireTicks(num * 100);
							}
						}
					}
				}, rand.nextInt(ce ? 200 : 100) + i * 10);
			}
		}

		if (this == Dragonrend) {
			Block[] bs = p.getLineOfSight(null, (num * 6) + 2).toArray(new Block[0]);
			List<Entity> near = p.getNearbyEntities(15, 15, 15);
			p.getWorld().playSound(p.getLocation(), Sound.WITHER_SHOOT, 0.5f, 0);
			for (Block b : bs) {
				PacketUtils.playParticleEffect(ParticleEffect.MAGIC_CRIT, b.getLocation().add(0.5, 0.5, 0.5), 1, 0.3f,
						num * 15);

				for (Entity e : near) {
					if (!(e instanceof Player)) {
						continue;
					}
					Player p2 = (Player) e;
					if (p2.getAllowFlight() && e.getLocation().distance(b.getLocation()) < 7) {
						PacketUtils.playParticleEffect(ParticleEffect.CLOUD, p2.getLocation(), 2, 0.2f, 50);
						p2.getWorld().playSound(p2.getLocation(), Sound.EXPLODE, 3, 2);
						p2.setAllowFlight(false);
					}
				}
			}
		}

		if (this == Aura_Whisper) {
			p.getWorld().playSound(p.getLocation(), Sound.ENDERDRAGON_HIT, 1, 0);
			p.sendMessage("§a[Aura Whisper] §7All nearby players:");
			for (Player p2 : Bukkit.getOnlinePlayers()) {
				if (p2.getWorld() == p.getWorld() && p2 != p && p2.getLocation().distance(p.getLocation()) < num * 50) {
					p.sendMessage(getAuraString(p2, p));
				}
			}
		}

		if (this == Marked_For_Death) {
			Block[] bs = p.getLineOfSight(null, (num * 6) + 2).toArray(new Block[0]);
			List<Entity> near = p.getNearbyEntities(15, 15, 15);
			p.getWorld().playSound(p.getLocation(), Sound.WITHER_HURT, 2f, 0);
			for (Block b : bs) {
				PacketUtils.playParticleEffect(ParticleEffect.LARGE_SMOKE, b.getLocation().add(0.5, 0.5, 0.5), 1, 0.3f,
						num * 15);

				for (Entity e : near) {
					if (!(e instanceof Player)) {
						continue;
					}
					Player p2 = (Player) e;
					if (e.getLocation().distance(b.getLocation()) < 7) {
						p2.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, num * 120, -num * 2));
						p2.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, num * 20, num - 1));
					}
				}
			}
		}

		if (this == Drain_Vitality) {
			Block[] bs = p.getLineOfSight(null, (num * 6) + 2).toArray(new Block[0]);
			List<Entity> near = p.getNearbyEntities(15, 15, 15);
			p.getWorld().playSound(p.getLocation(), Sound.WITHER_IDLE, 0.5f, 0.7f);
			for (Block b : bs) {
				PacketUtils.playParticleEffect(ParticleEffect.PORTAL, b.getLocation().add(0.5, 0.5, 0.5), 1, 0.3f,
						num * 15);

				for (Entity e : near) {
					if (!(e instanceof Player)) {
						continue;
					}
					Player p2 = (Player) e;
					if (e.getLocation().distance(b.getLocation()) < 7) {
						p2.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, num * 20, num * 4));
						if (num > 1) {
							p2.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, num * 20, num * 3));
						}
					}
				}
			}
		}

		if (this == Battle_Fury) {
			p.getWorld().playSound(p.getLocation(), Sound.WITHER_SPAWN, 1, 2);
			for (Player p2 : Bukkit.getOnlinePlayers()) {
				if (p2.getWorld() == p.getWorld() && p2 != p && p2.getLocation().distance(p.getLocation()) < num * 8) {
					p2.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, num * 100, num - 1));
					p2.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, num * 100, num - 1));
				}
			}
		}

		if (this == Elemental_Fury) {
			p.getWorld().playSound(p.getLocation(), Sound.WITHER_SPAWN, 1, 2);
			p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, num * 100, num - 1));
			p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, num * 100, num - 1));
		}

		if (this == Soul_Tear) {
			Block[] bs = p.getLineOfSight(null, (num * 6) + 2).toArray(new Block[0]);
			List<Entity> near = p.getNearbyEntities(15, 15, 15);
			p.getWorld().playSound(p.getLocation(), Sound.IRONGOLEM_HIT, 2, 0);
			if (num > 2) {
				for (Block b : bs) {
					PacketUtils.playParticleEffect(ParticleEffect.MOB_SPELL, b.getLocation().add(0.5, 0.5, 0.5), 1,
							0.3f, num * 15);

					for (Entity e : near) {
						if (!(e instanceof Player)) {
							continue;
						}
						Player p2 = (Player) e;
						if (e.getLocation().distance(b.getLocation()) < 7) {
							p2.damage(10);
						}
					}
				}
			}
		}

		if (this == Clear_Skies) {
			p.getWorld().playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 5, 1);
			p.getWorld().setWeatherDuration(0);
		}

		if (this == Cyclone) {
			Block[] bs = p.getLineOfSight(null, num * 2).toArray(new Block[0]);
			List<Entity> near = p.getNearbyEntities(15, 15, 15);
			p.getWorld().playSound(p.getLocation(), Sound.FIREWORK_LARGE_BLAST2, 3, 0);
			for (Block b : bs) {
				PacketUtils.playParticleEffect(ParticleEffect.EXPLODE, b.getLocation().add(0.5, 0.5, 0.5), 0, 0.3f,
						num * 10);

				for (Entity e : near) {
					if (e.getLocation().distance(b.getLocation()) < 4) {
						if (e instanceof LivingEntity) {
							((LivingEntity) e).damage(num * 3);
						}
						e.setVelocity(e.getVelocity().add(new Vector(0, 0.3, 0)));
					}
				}
			}
		}

		if (this == Disarm) {
			Block[] bs = p.getLineOfSight(null, (num * 6) + 2).toArray(new Block[0]);
			List<Entity> near = p.getNearbyEntities(15, 15, 15);
			p.getWorld().playSound(p.getLocation(), Sound.ANVIL_LAND, 1, 0);
			if (num > 2) {
				for (Block b : bs) {
					PacketUtils.playParticleEffect(ParticleEffect.CRIT, b.getLocation().add(0.5, 0.5, 0.5), 1, 0.3f,
							num * 15);

					for (Entity e : near) {
						if (!(e instanceof Player)) {
							continue;
						}
						Player p2 = (Player) e;
						if (e.getLocation().distance(b.getLocation()) < 7) {
							ItemStack it = p2.getItemInHand();
							if (it == null || it.getType() == Material.AIR)
								continue;
							Item i = p2.getWorld().dropItem(p2.getEyeLocation(), it);
							i.setVelocity(LocUtils.getVelocity(p.getLocation(), p2.getLocation()));
							p2.setItemInHand(null);
						}
					}
				}
			}
		}

		if (this == Throw_Voice) {
			Sound s = Sound.values()[rand.nextInt(Sound.values().length)];
			int pit = rand.nextInt(3);
			for (Player p2 : Bukkit.getOnlinePlayers()) {
				if (p2.getWorld() == p.getWorld() && p2.getLocation().distance(p.getLocation()) < num * 30) {
					p2.playSound(LocUtils.addRand(p2.getLocation().clone(), 10, 10, 10), s, 5, pit);
				}
			}
		}
	}

	public String getAuraString(Player p, Player n) {
		Location loc = p.getLocation();
		String ret = "§b" + p.getName() + "§7 - §a" + ((int) p.getLocation().distance(n.getLocation()))
				+ " blocks away at " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ();

		return ret;
	}

	public boolean hasLearnedShout(Player p, int num) {
		if (DragonShouts.require_learn) {

			if (DragonShouts.ins.getLearnedData() != null) {
				FileConfiguration ld = DragonShouts.ins.getLearnedData();

				if (ld.getList("learned_data." + p.getName()) != null) {
					List<String> sl = ld.getStringList("learned_data." + p.getName());

					for (String s : sl) {
						String[] ss = s.split(";");
						String sn = ss[0];
						int nn = Integer.parseInt(ss[1]);
						if (sn.equalsIgnoreCase(name()) && nn >= num) {
							return true;
						}
					}
				}
			}

			return false;
		} else
			return true;
	}

	public int getLearnLevel(Player p) {
		if (!hasLearnedShout(p, 1)) {
			return 0;
		}
		if (!hasLearnedShout(p, 2)) {
			return 1;
		}
		if (!hasLearnedShout(p, 3)) {
			return 2;
		}
		return 3;
	}

	public void setLearned(Player p) {
		if (DragonShouts.ins.getLearnedData() != null) {
			FileConfiguration ld = DragonShouts.ins.getLearnedData();

			List<String> sl = new ArrayList<>();
			if (ld.getList("learned_data." + p.getName()) != null) {
				sl = ld.getStringList("learned_data." + p.getName());
			}

			int ll = getLearnLevel(p);
			sl.remove(name() + ";" + ll);
			sl.add(name() + ";" + (ll + 1));

			ld.set("learned_data." + p.getName(), sl);

			DragonShouts.ins.saveLearnedData();
		}
	}

	public void setLearnedWithEffect(Player p, Block b) {
		setLearned(p);

		int num = getLearnLevel(p);
		p.sendMessage(DragonShouts.prefix + "Word Learned: §b" + (num >= 3 ? w3 : num == 2 ? w2 : w1) + "§7 - §a"
				+ name);
		for (int i = 0; i < 5; i++) {
			p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 4f, 1.3f);
		}
		if (b != null) {
			DragonShouts.ins.setUsedWW(p, b);
		}
	}

	public static Shout get(String string) {
		for (Shout s : values()) {
			if (s.name().equalsIgnoreCase(string)) {
				return s;
			}
		}
		return null;
	}

}
