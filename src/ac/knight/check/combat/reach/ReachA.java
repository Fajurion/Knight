package ac.knight.check.combat.reach;

import ac.knight.check.Check;
import ac.knight.event.impl.EventIncoming;
import ac.knight.event.impl.EventMove;
import ac.knight.Knight;
import ac.knight.event.Event;
import ac.knight.user.UserData;
import ac.knight.user.processor.impl.ActionProcessor;
import ac.knight.user.processor.impl.MovementProcessor;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class ReachA extends Check {

    public ReachA(UserData userData) {
        super("Reach", "A", "Checks for reach on players. >3.1", 8, userData);
    }

    private ActionProcessor action;

    @Override
    public void init(UserData data) {
        action = (ActionProcessor) data.processor(ActionProcessor.class);
    }

    private UserData lastTarget;

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventMove) {

            if(action.attackTicks > 3 ||
            lastTarget == null)
                return;

            MovementProcessor targetProcessor = (MovementProcessor) lastTarget.processor(MovementProcessor.class);

            if(targetProcessor.lastLocations.size() < 15) {
                return;
            }

            if(lastTarget.user.getPlayer().getLocation().distance(userData.user.getPlayer().getLocation()) > 10) {
                lastTarget = null;
                return;
            }

            boolean fail = true;
            for(Location location : targetProcessor.lastLocations) {
                if(!fail) continue;
                if(userData.user.getPlayer().getLocation().toVector().isInSphere(location.toVector(), 3.8)) {
                    fail = false;
                }
            }

            if(fail) {
                if(this.increaseBuffer() > 3) {
                    this.fail();
                }
            } else this.decreaseBuffer(0.05);

        } else if(event instanceof EventIncoming) {

            EventIncoming e = (EventIncoming) event;

            if(e.getPacket() instanceof PacketPlayInUseEntity) {

                PacketPlayInUseEntity packet = (PacketPlayInUseEntity) e.getPacket();
                if(packet.a().equals(PacketPlayInUseEntity.EnumEntityUseAction.ATTACK)) {

                    Entity entity = packet.a(((CraftWorld) userData.user.getPlayer().getWorld()).getHandle());
                    if(entity instanceof EntityPlayer) lastTarget = Knight.getInstance().users.get(Bukkit.getPlayer(entity.getName())).data;

                }

            }

        }

    }

    @Override
    public Check newInstance(UserData data) {
        return new ReachA(data);
    }
}
