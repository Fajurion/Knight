package ac.knight.user.processor.impl;

import ac.knight.user.processor.ProtocolVersion;
import ac.knight.user.UserData;
import ac.knight.user.processor.Processor;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;

public class RotationProcessor extends Processor {

    public RotationProcessor(UserData data) {
        super(data, ProtocolVersion.V1_8);
    }

    public float yaw, lastYaw, pitch, lastPitch, deltaYaw, lastDeltaYaw, lastLastDeltaYaw, deltaPitch, lastDeltaPitch, lastLastDeltaPitch;

    @Override
    public void handleIncomingPacket(Packet<?> packet) {
        if(packet instanceof PacketPlayInFlying) {
            PacketPlayInFlying flying = (PacketPlayInFlying) packet;
            if(!flying.h()) return;

            lastYaw = this.yaw;
            lastPitch = this.pitch;
            this.yaw = flying.d();
            this.pitch = flying.e();
            lastLastDeltaYaw = lastDeltaYaw;
            lastLastDeltaPitch = lastDeltaPitch;
            lastDeltaYaw = deltaYaw;
            lastDeltaPitch = deltaPitch;
            deltaYaw = this.yaw - lastYaw;
            deltaPitch = this.pitch - lastPitch;
        }
    }

    @Override
    public void handleOutgoingPacket(Packet<?> packet) {

    }
}
