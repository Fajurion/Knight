package ac.knight.user.processor;

import ac.knight.user.UserData;
import net.minecraft.server.v1_8_R3.Packet;

public abstract class Processor {

    private final boolean active;
    private final int version;

    public UserData data;

    public Processor(UserData data, int version) {
        this(data, version, VersionType.MIN);
    }

    public Processor(UserData data, int version, VersionType type) {

        switch (type) {
            case MIN:
                this.active = data.user.getProtocolVersion() >= version;
                break;

            case ONLY:
                this.active = data.user.getProtocolVersion() == version;
                break;

            case MAX:
                this.active = data.user.getProtocolVersion() <= version;
                break;

            default:
                this.active = false;
        }
        this.version = version;
    }

    public abstract void handleIncomingPacket(Packet<?> packet);
    public abstract void handleOutgoingPacket(Packet<?> packet);

    public int getVersion() {
        return version;
    }

    public boolean isActive() {
        return active;
    }

}
