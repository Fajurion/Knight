package ac.knight.check.world;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventIncoming;
import ac.knight.user.UserData;
import ac.knight.util.EnumFacing;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class ScaffoldE extends Check {

    public ScaffoldE(UserData userData) {
        super("Scaffold", "E", "Checks for an invalid blockface/placement.", 12, userData);
    }
    @Override
    public void init(UserData data) {}

    private Location lastBlockLocation;

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
                    Location block = new Location(userData.user.getPlayer().getWorld(), x, y, z);
                    EnumFacing facing = EnumFacing.DOWN;
                    for(EnumFacing face : EnumFacing.values()) {
                        if(face.getIndex() == packet.getFace()) {
                            facing = face;
                        }
                    }

                    final BlockFace bukkitFace = BlockFace.valueOf(facing.getName2().toUpperCase());
                    final Vector direction = userData.user.getPlayer().getEyeLocation().getDirection().normalize();
                    final Location placedBlock = block.getBlock().getRelative(bukkitFace).getLocation().add(0.5, 0.5, 0.5);
                    block = block.add(0.5, 0.5, 0.5);
                    boolean against = false, placed = false, expand = false;

                    for(float f = 0; f <= 4f; f += 0.2f) {

                        final double rayX = direction.getX() * f;
                        final double rayY = direction.getY() * f;
                        final double rayZ = direction.getZ() * f;
                        final Location loc = userData.user.getPlayer().getEyeLocation().clone().add(rayX, rayY, rayZ);

                        if(loc.distance(block) <= 0.87) {
                            if(against) expand = true;
                            placed = true;
                        }

                        if(loc.distance(placedBlock) <= 0.87) {
                            against = true;
                        }

                    }

                    boolean bridging = false;
                    if(lastBlockLocation != null) {
                        bridging = lastBlockLocation.getY() == placedBlock.getY()
                                && bukkitFace.getModY() == 0;
                    }

                    lastBlockLocation = placedBlock;

                    boolean invalidPlacement = !(placed || against);
                    boolean invalidBlockFace = bridging && !(placed && against);
                    boolean expanded = bridging && expand && placedBlock.distance(userData.player().getLocation()) > 1.6;

                    if(invalidPlacement || invalidBlockFace || expanded) {
                        if(this.increaseBuffer() > 3) {
                            this.fail("type", (invalidPlacement ? "invalid-placement " : "")
                                    + (invalidBlockFace ? "invalid-block-face " : ""
                                    + (expanded ? "expanded " : "")));
                        }
                    } else {
                        this.decreaseBuffer(0.5);
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
