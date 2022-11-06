package ac.knight.check.world;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventIncoming;
import ac.knight.user.UserData;
import ac.knight.user.processor.impl.MovementProcessor;
import ac.knight.util.EnumFacing;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

public class ScaffoldF extends Check {

    public ScaffoldF(UserData userData) {
        super("Scaffold", "F", "Checks for sprint scaffold.", 12, userData);
        setKicking(false);
    }

    private MovementProcessor movement;

    @Override
    public void init(UserData data) {
        movement = (MovementProcessor) data.processor(MovementProcessor.class);
    }
    Location lastBlockLocation;
    long lastBlock = 0;
    int disableCount = 0;

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

                    long delay = System.currentTimeMillis() - lastBlock;
                    final BlockFace bukkitFace = BlockFace.valueOf(facing.getName2().toUpperCase());
                    final Location placedBlock = block.getBlock().getRelative(bukkitFace).getLocation().add(0.5, 0.5, 0.5);

                    boolean bridging = false;
                    if(lastBlockLocation != null) {
                        bridging = lastBlockLocation.getY() == placedBlock.getY()
                        && bukkitFace.getModY() == 0;
                    }

                    lastBlockLocation = placedBlock;
                    lastBlock = System.currentTimeMillis();

                    if(delay > 500) {
                        disableCount = 4;
                    }

                    disableCount = Math.max(0, disableCount - 1);

                    if(delay < 210 && bridging && movement.deltaXZ > 0.22 && disableCount == 0) {
                        if(this.increaseBuffer() > 5) {
                            this.fail();
                        }
                    } else this.decreaseBuffer(0.05);
                }


            }

        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new ScaffoldF(data);
    }
}
