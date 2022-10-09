package ac.knight.user.processor.impl;

import ac.knight.user.ProtocolVersion;
import ac.knight.user.UserData;
import ac.knight.user.processor.Processor;
import net.minecraft.server.v1_8_R3.Packet;

public class RotationProcessor extends Processor {

    public RotationProcessor(UserData data) {
        super(data, ProtocolVersion.V1_8);
    }

    @Override
    public void handlePacket(Packet<?> packet) {

    }

}
