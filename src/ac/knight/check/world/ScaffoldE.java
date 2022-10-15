package ac.knight.check.world;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventIncoming;
import ac.knight.user.UserData;
import ac.knight.user.processor.impl.RotationProcessor;
import ac.knight.util.EnumFacing;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class ScaffoldE extends Check {

    public ScaffoldE(UserData userData) {
        super("Scaffold", "E", "Checks for an invalid blockface.", 20, userData);
        setKicking(false);
    }

    private int streak = 0;

    @Override
    public void init(UserData data) {}

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventIncoming) {

            EventIncoming e = (EventIncoming) event;
            if (e.getPacket() instanceof PacketPlayInBlockPlace) {
                PacketPlayInBlockPlace packet = (PacketPlayInBlockPlace) e.getPacket();
                if(packet.getItemStack() == null) return;

                if(packet.a().getY() != -1 && Material.getMaterial(Item.getId(packet.getItemStack().getItem())).isBlock()) {

                    final double x = packet.a().getX();
                    final double y = packet.a().getY();
                    final double z = packet.a().getZ();
                    final Location block = new Location(userData.user.getPlayer().getWorld(), x, y, z);
                    EnumFacing facing = EnumFacing.DOWN;
                    for(EnumFacing face : EnumFacing.values()) {
                        if(face.getIndex() == packet.getFace()) {
                            facing = face;
                        }
                    }

                    final BlockFace bukkitFace = BlockFace.valueOf(facing.getName2().toUpperCase());
                    final Vector direction = userData.user.getPlayer().getEyeLocation().getDirection().normalize();
                    final Location placedBlock = block.getBlock().getRelative(bukkitFace).getLocation();
                    boolean against = false, invalid = false, placed = false;

                    for(float f = 0; f <= 4f; f += 0.1f) {

                        final double rayX = direction.getX() * f;
                        final double rayY = direction.getY() * f;
                        final double rayZ = direction.getZ() * f;
                        final Location loc = userData.user.getPlayer().getEyeLocation().clone().add(rayX, rayY, rayZ);

                        if(loc.getBlock().equals(block.getBlock())) {
                            if(!against) invalid = true;
                            placed = true;
                        } else if(loc.getBlock().equals(placedBlock.getBlock())) {
                            against = true;
                        }

                    }

                    final boolean fail = !(placed && against) || invalid;
                    if(fail) {
                        if(streak++ > 3) {
                            if(this.increaseBuffer() > 6) {
                                this.fail("type", "invalid-block-face");
                            }
                        }
                    } else {
                        this.decreaseBuffer(0.5);
                        streak = 0;
                    }

                }


            }

        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new ScaffoldE(data);
    }
}
