package net.mcshockwave.DragonShouts.Utils;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class LocUtils {

	private static Random	rand	= new Random();

	public static boolean isSame(Location l1, Location l2) {
		return l1.getBlockX() == l2.getBlockX() && l1.getBlockY() == l2.getBlockY() && l1.getBlockZ() == l2.getBlockZ();
	}

	public static Location addRand(Location l, int radX, int radY, int radZ) {
		Location l2 = l.clone().add(rand.nextInt(radX * 2) - radX, radY > 0 ? (rand.nextInt(radY * 2) - radY) : 0,
				rand.nextInt(radZ * 2) - radZ);
		return l2;
	}

	public static Vector getVelocity(Location c, Location f) {
		return new Vector(f.getX() - c.getX(), 0.6, f.getZ() - c.getZ()).multiply(5 / f.distance(c));
	}

}
