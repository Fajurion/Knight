package ac.knight.check.combat.block;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventIncoming;
import ac.knight.user.UserData;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;

public class BlockA extends Check {

    public BlockA(UserData userData) {
        super("Block", "A", "Checks for missing packets while attacking and blocking.", 5, userData);
    }

    @Override
    public void init(UserData data) {

    }

    private boolean interacted = false, attacked = false;

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventIncoming) {

            EventIncoming e = (EventIncoming) event;
            if(e.getPacket() instanceof PacketPlayInFlying /* && userData.attackTicks == 1 TODO: FIX */) {
                interacted = attacked = false;
            } else if(e.getPacket() instanceof PacketPlayInUseEntity) {
                PacketPlayInUseEntity.EnumEntityUseAction action = ((PacketPlayInUseEntity) e.getPacket()).a();
                if(action.equals(PacketPlayInUseEntity.EnumEntityUseAction.ATTACK)) {
                    attacked = true;
                } else interacted = true;
            } else if(e.getPacket() instanceof PacketPlayInBlockPlace) {
                if(!interacted && attacked) {
                    if(this.increaseBuffer() > 3) {
                        this.fail("interaction", interacted + "", "attacked", attacked + "");
                    }
                } else this.decreaseBuffer(0.05);
            }

        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new BlockA(data);
    }
}
