package ac.knight.user.processor.impl;

import ac.knight.user.processor.ProtocolVersion;
import ac.knight.user.UserData;
import ac.knight.user.processor.Processor;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;

public class ActionProcessor extends Processor {

    public ActionProcessor(UserData data) {
        super(data, ProtocolVersion.V1_8);
    }

    public int attackTicks = 0;

    @Override
    public void handleIncomingPacket(Packet<?> packet) {
        if(packet instanceof PacketPlayInFlying) {
            attackTicks = Math.min(10000, attackTicks + 1);
        } else if(packet instanceof PacketPlayInUseEntity) {
            PacketPlayInUseEntity useEntity = (PacketPlayInUseEntity) packet;

            if(useEntity.a().equals(PacketPlayInUseEntity.EnumEntityUseAction.ATTACK)) {
                attackTicks = 0;
            }
        }
    }

    @Override
    public void handleOutgoingPacket(Packet<?> packet) {

    }
}
