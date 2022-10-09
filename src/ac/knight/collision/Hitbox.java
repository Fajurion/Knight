package ac.knight.collision;

import ac.knight.util.BlockUtil;

import java.util.HashMap;
import java.util.function.Predicate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class Hitbox {

    public double x, y, z, x2, y2, z2;
    public World world;
    public boolean touchesGround = false, safeGround = false;
    public boolean containsSolidBlocks = false;
    public boolean isCollidedVertically = false;
    public boolean slimeBlocks = false;
    public boolean stuck = false, climbable = false;
    public final HashMap<Material, Boolean> cache = new HashMap<>();

    public Hitbox() {}

    public void recalculate(double x, double y, double z, double x2, double y2, double z2, World world) {
        this.cache.clear();
        this.x = x;
        this.y = y;
        this.z = z;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.world = world;
        this.touchesGround = false;
        this.safeGround = false;
        this.stuck = false;
        this.slimeBlocks = false;
        this.isCollidedVertically = false;
        this.containsSolidBlocks = false;
        double diffX = x2 - x;
        double diffY = y2 - y;
        double diffZ = z2 - z;
        for (double dX = 0; dX <= diffX; dX += diffX / 4) {
            for (double dZ = 0; dZ <= diffZ; dZ += diffZ / 4) {

                Location locDown = new Location(world, x + dX, y-0.01, z + dZ);
                Location locUp = new Location(world, x + dX, y+diffY+0.01, z + dZ);

                if(!locDown.getChunk().isLoaded()) break;

                if(BlockUtil.isSolid(locDown.getBlock())) touchesGround = true;
                if(BlockUtil.isSolid(locUp.getBlock())) isCollidedVertically = true;
                if(locDown.getBlock().getType().equals(Material.SLIME_BLOCK)) slimeBlocks = true;
                if(locDown.getBlock().getType().equals(Material.LADDER) || locDown.getBlock().getType().equals(Material.VINE)) climbable = true;

                for (double dY = -diffY / 4; dY <= diffY; dY += diffY / 4) {

                    Location loc = new Location(world, x + dX, y + dY, z + dZ);

                    if(dY < 0) {
                        if(BlockUtil.isSolid(loc.getBlock().getType())) {
                            safeGround = true;
                        }

                        continue;
                    }

                    if(BlockUtil.isSolid(loc.getBlock().getType())) {
                        containsSolidBlocks = true;
                    }
                    if(loc.getBlock().getType().equals(Material.WEB)) stuck = true;

                }

            }
        }


    }

    public boolean isInside(Location loc) {
        return ((loc.getY() > y && loc.getY() < y2) || (loc.getY() < y && loc.getY() > y2))
                && ((loc.getX() > x && loc.getX() < x2) || (loc.getX() < x && loc.getX() > x2))
                && ((loc.getZ() > z && loc.getZ() < z2) || (loc.getZ() < z && loc.getZ() > z2));
    }

    public boolean checkFor(Predicate<Material> predicate) {

        double diffX = x - x2;
        double diffY = y - y2;
        double diffZ = z - z2;
        for (double dX = 0; dX <= diffX; dX += diffX / 4) {
            for (double dY = 0; dY <= diffY; dY += diffY / 4) {
                for (double dZ = 0; dZ <= diffZ; dZ += diffZ / 4) {

                    Location loc = new Location(world, x + dX, y + dY, z + dZ);
                    if (predicate.test(loc.getBlock().getType())) {

                        return true;
                    }

                }
            }
        }
        return false;
    }

    public boolean checkForMaterial(Material material) {

        if(cache.containsKey(material)) {
            return cache.get(material);
        }

        boolean check = checkFor(material::equals);
        cache.put(material, check);
        return check;
    }

}
