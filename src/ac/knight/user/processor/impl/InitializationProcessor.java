package ac.knight.user.processor.impl;

import ac.knight.user.ProtocolVersion;
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
    public void handlePacket(Packet<?> packet) {
        if(packet.getClass().equals(PacketPlayInFlying.class)) {
            PacketPlayInFlying flying = (PacketPlayInFlying) packet;
            ticksExisted = Math.min(10000, ticksExisted + 1);

            if(flying.g()) {
                if(!flying.g() && ticksExisted < 8) {
                    initialized = false;
                } else if(flying.b() - lastY >= 0.0) initialized = true;
            }

            lastY = flying.b();
        }
    }
}
