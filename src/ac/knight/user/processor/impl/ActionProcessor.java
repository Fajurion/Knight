package ac.knight.user.processor.impl;

import ac.knight.user.processor.ProtocolVersion;
import ac.knight.user.UserData;
import ac.knight.user.processor.Processor;
import net.minecraft.server.v1_8_R3.Packet;

public class ActionProcessor extends Processor {

    public ActionProcessor(UserData data) {
        super(data, ProtocolVersion.V1_8);
    }


    @Override
    public void handleIncomingPacket(Packet<?> packet) {

    }

    @Override
    public void handleOutgoingPacket(Packet<?> packet) {

    }
}
