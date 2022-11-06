package ac.knight.user.processor.impl;

import ac.knight.user.processor.ProtocolVersion;
import ac.knight.user.UserData;
import ac.knight.user.processor.Processor;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;

public class InitializationProcessor extends Processor {

    public InitializationProcessor(UserData data) {
        super(data, ProtocolVersion.V1_8);
    }

    public int ticksExisted = 0;
    public boolean initialized = false;

    private double lastY = 0;
    @Override
    public void handleIncomingPacket(Packet<?> packet) {
        if(packet instanceof PacketPlayInFlying) {
            PacketPlayInFlying flying = (PacketPlayInFlying) packet;
            ticksExisted = Math.min(10000, ticksExisted + 1);

            if(flying.g() && flying.b() - lastY >= 0.0 && ticksExisted > 10) {
                initialized = true;
            }

            lastY = flying.b();
        }
    }

    @Override
    public void handleOutgoingPacket(Packet<?> packet) {

    }
}
