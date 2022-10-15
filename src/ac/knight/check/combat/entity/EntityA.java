package ac.knight.check.combat.entity;

import ac.knight.bot.Bot;
import ac.knight.check.Check;
import ac.knight.event.impl.EventIncoming;
import ac.knight.event.Event;
import ac.knight.event.impl.EventRotation;
import ac.knight.user.UserData;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class EntityA extends Check {

    public EntityA(UserData userData) {
        super("Entity", "A", "Spawns a bot behind a player.", 1, userData);
    }

    private Bot bot = null;
    private int botTicks = 0, swings = 0, hits = 0;

    @Override
    public void onEvent(Event event) {

        /* TODO: Migrate (maybe later on)
        if(event instanceof EventRotation) {

            if(userData.deltaYaw > 50 && bot == null) {
                bot = new Bot(userData.user.getPlayer(), 20.0, 3.0);
                bot.summon();
                botTicks = 0;
            }

        }

        if(event instanceof EventIncoming) {

            EventIncoming e = (EventIncoming) event;
            if(e.getPacket() instanceof PacketPlayInFlying && bot != null) {
                bot.handleTick();
                if(botTicks++ > 1000) {
                    bot.destroy();
                    bot = null;
                }
            } else if(e.getPacket() instanceof PacketPlayInUseEntity) {
                PacketPlayInUseEntity packet = (PacketPlayInUseEntity) e.getPacket();
                if(packet.a().equals(PacketPlayInUseEntity.EnumEntityUseAction.ATTACK)) {

                    if(packet.a(((CraftWorld) userData.user.getPlayer().getWorld()).getHandle()) == null) {
                        hits++;
                    }

                }

            } else if(e.getPacket() instanceof PacketPlayInArmAnimation) {
                swings++;
                if(swings >= 6) {
                    if(hits >= 4) {
                        if(this.increaseBuffer() > 0) {
                            this.fail();
                        }
                    } else this.decreaseBuffer(0.001);
                    swings = 0;
                    hits = 0;
                }
            }

        }

         */
    }

    @Override
    public Check newInstance(UserData data) {
        return new EntityA(data);
    }

    @Override
    public void init(UserData data) {

    }
}
