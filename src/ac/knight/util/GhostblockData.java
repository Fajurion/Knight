package ac.knight.util;

import ac.knight.user.User;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockChange;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

public class GhostblockData {

    public boolean sent = false;
    public int tick = 0;
    public Location blockLocation;

    public GhostblockData(Location blockLocation) {
        this.blockLocation = blockLocation;
    }

    public void update() {tick++;}

    public void updateBlock(User user) {
        sent = true;
        ((CraftPlayer) user.getPlayer()).getHandle().playerConnection.sendPacket(
                new PacketPlayOutBlockChange(((CraftWorld) user.getPlayer().getWorld()).getHandle(),
                        new BlockPosition(blockLocation.getBlockX(), blockLocation.getBlockY(), blockLocation.getBlockZ())));
    }

}
