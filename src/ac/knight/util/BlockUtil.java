package ac.knight.util;

import java.util.function.Predicate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;

public class BlockUtil {

    public static boolean checkCube(Location location, double radius, Predicate<Material> predicate) {

        for(double dX = -3; dX <= 3; dX += 0.5) {
            for(double dY = -3; dY <= 3; dY += 0.5) {
                for(double dZ = -3; dZ <= 3; dZ += 0.5) {

                    Location loc = new Location(location.getWorld(), location.getX()+dX, location.getY()+dY, location.getZ()+dZ);
                    if(!loc.getChunk().isLoaded()) break;

                    if(predicate.test(loc.getBlock().getType())) {
                        return true;
                    }

                }
            }
        }

        return false;

    }

    public static boolean checkForSolidBlock(World world, double x, double y, double z, double reach, double yOffset) {
        for(double dX = -reach; dX <= reach; dX += reach) {
            for(double dZ = -reach; dZ <= reach; dZ += reach) {

                Location loc = new Location(world, x+dX, y+yOffset, z+dZ);
                if(!loc.getChunk().isLoaded()) break;

                if(isSolid(loc.getBlock()))
                    return true;

            }
        }
        return false;
    }

    public static boolean checkForBlockOfType(World world, double x, double y, double z, double reach, double yOffset, Material... materials) {
        for(double dX = -reach; dX <= reach; dX += reach) {
            for(double dZ = -reach; dZ <= reach; dZ += reach) {

                Location loc = new Location(world, x+dX, y+yOffset, z+dZ);
                if(!loc.getChunk().isLoaded()) break;

                for(Material material : materials) {
                    if(loc.getBlock().getType().equals(material))
                        return true;
                }

            }
        }
        return false;
    }

    public static boolean checkForBlockOfType(World world, double x, double y, double z, double reach, double yOffset, String... materials) {
        for(double dX = -reach; dX <= reach; dX += reach) {
            for(double dZ = -reach; dZ <= reach; dZ += reach) {

                Location loc = new Location(world, x+dX, y+yOffset, z+dZ);
                if(!loc.getChunk().isLoaded()) break;

                for(String material : materials) {
                    if(loc.getBlock().getType().toString().contains(material))
                        return true;
                }

            }
        }
        return false;
    }

    public static boolean isSolid(Block block) {
        return isSolid(block.getType());
    }

    public static boolean isSolid(Material material) {
        return material.isSolid() || material.toString().contains("FENCE")
                || material.equals(Material.FLOWER_POT)
                || material.equals(Material.TRAP_DOOR)
                || material.equals(Material.IRON_TRAPDOOR)
                || material.toString().contains("FENCE_GATE")
                || material.toString().contains("SNOW")
                || material.toString().contains("STEP")
                || material.toString().contains("SLAB");
    }

    public static boolean isVehicleNearby(Player player) {
        for(Entity entity : player.getNearbyEntities(2, 2, 2))
            if(entity instanceof Boat || entity instanceof Minecart)
                return true;
        return false;
    }

}
