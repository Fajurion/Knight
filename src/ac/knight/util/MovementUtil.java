package ac.knight.util;

import ac.knight.user.UserData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MovementUtil {

    public static boolean isSafeOnGround(double x, double y, double z, World world) {
        return BlockUtil.checkCube(new Location(world, x, y, z), 3, BlockUtil::isSolid);
    }

    public static boolean isInWater(World world, double x, double y, double z) {
        return BlockUtil.checkForBlockOfType(world, x, y, z, 0.6, 0, "WATER", "LAVA");
    }

    public static boolean isOnGround(double x, double y, double z, World world) {
        return BlockUtil.checkForSolidBlock(world, x, y, z, 0.7, -0.3) ||
                BlockUtil.checkForBlockOfType(world, x, y, z, 0.3, 0, Material.WATER_LILY, Material.CARPET, Material.WEB);
    }

    public static boolean isOnIce(double x, double y, double z, World world) {
        return BlockUtil.checkForBlockOfType(world, x, y, z, 0.3, -0.3, Material.PACKED_ICE, Material.SLIME_BLOCK, Material.ICE);
    }

    public static boolean stairsNear(double x, double y, double z, World world) {
        return BlockUtil.checkForBlockOfType(world, x, y, z, 0.3, -0.3, "STEP", "STAIR");
    }

    public static boolean snowNear(double x, double y, double z, World world) {
        return BlockUtil.checkForBlockOfType(world, x, y, z, 0.3, -0.05, Material.SNOW, Material.SNOW_BLOCK);
    }

    public static boolean wallsNear(double x, double y, double z, World world) {
        return BlockUtil.checkForSolidBlock(world, x, y, z, 0.7, 0.5);
    }

    public static boolean isClimbable(double x, double y, double z, World world) {
        return BlockUtil.checkForBlockOfType(world, x, y, z, 0.3, -0.3, Material.LADDER, Material.VINE)
                || BlockUtil.checkForBlockOfType(world, x, y, z, 0.3, 0, Material.LADDER, Material.VINE);
    }

    public static boolean isOnSlime(double x, double y, double z, World world) {
        return BlockUtil.checkForBlockOfType(world, x, y, z, 0.3, -0.3, Material.SLIME_BLOCK);
    }

    public static boolean isNear(Location location, Location location2, double offset) {
        return MathUtil.isRoughly(location.getX(), location2.getX(), offset)
                && MathUtil.isRoughly(location.getY(), location2.getY(), offset)
                && MathUtil.isRoughly(location.getZ(), location2.getZ(), offset);
    }

    public static boolean isCollidedVertically(World world, double x, double y, double z) {
        return BlockUtil.checkForSolidBlock(world, x, y, z, 0.6, 2)
                || BlockUtil.checkForBlockOfType(world, x, y, z, 0.6, 2, Material.WATER_LILY, Material.CARPET);
    }

    public static int getPotionEffectLevel(Player player, PotionEffectType type) {
        for(PotionEffect effect : player.getActivePotionEffects()) {
            if(effect.getType().equals(type)) {
                return effect.getAmplifier();
            }
        }
        return 0;
    }

}
