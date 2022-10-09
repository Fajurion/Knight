package ac.knight.check.combat.aim;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventIncoming;
import ac.knight.util.MathUtil;
import ac.knight.event.impl.EventRotation;
import ac.knight.user.UserData;

import java.util.ArrayList;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class AimC extends Check {

    public AimC(UserData userData) {
        super("Aim", "C", "Checks for looking at one angle.", 12, userData);
    }

    private Entity lastTarget;
    private final ArrayList<Number> diffSamples = new ArrayList<>();
    private double lastAvg;

    @Override
    public void onEvent(Event event) {

        if(event instanceof EventRotation) {

            if(lastTarget != null && userData.attackTicks < 2) {

                final Location origin = userData.user.getPlayer().getLocation().clone();
                final Location bukkitLocation = lastTarget.getBukkitEntity().getLocation();
                final Vector end = bukkitLocation.clone().toVector();

                final double optimalYaw = origin.setDirection(end.subtract(origin.toVector())).getYaw() % 360F;
                final double optimalPitch = origin.setDirection(end.subtract(origin.toVector())).getPitch();
                final double fixedYaw = (userData.yaw % 360F + 360F) % 360F;

                diffSamples.add(Math.abs(optimalYaw - fixedYaw) + Math.abs(userData.pitch - optimalPitch));

                if(diffSamples.size() > 20) {

                    final double avg = MathUtil.getAverage(diffSamples).doubleValue();

                    if(MathUtil.round(lastAvg).equals(MathUtil.round(avg))) {
                        if(this.increaseBuffer() > 3) {
                            this.fail();
                        }
                    } else this.decreaseBuffer(0.05);

                    lastAvg = avg;
                    diffSamples.remove(0);
                }

            }

        } else if(event instanceof EventIncoming) {

            EventIncoming e = (EventIncoming) event;
            if(e.getPacket() instanceof PacketPlayInUseEntity) {

                final PacketPlayInUseEntity packet = (PacketPlayInUseEntity) e.getPacket();
                lastTarget = packet.a(userData.user.getCraftPlayer().getHandle().world);

            }

        }
    }
    @Override
    public Check newInstance(UserData data) {
        return new AimC(data);
    }
}
