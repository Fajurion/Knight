package ac.knight.check;

import ac.knight.Knight;
import ac.knight.event.Event;
import ac.knight.user.User;
import ac.knight.user.UserData;

public abstract class Check {

    public String category, description, name;
    public boolean enabled = true, kick = true;
    private double buffer = 0;
    private final double maxBuffer;
    public UserData userData;

    public Check(String category, String name, String description, double maxBuffer, UserData userData) {
        this.category = category;
        this.name = name;
        this.maxBuffer = maxBuffer;
        this.description = description;
        this.userData = userData;
    }

    public void onEvent(Event event) {}

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean doesKick() {
        return kick;
    }

    public void setKicking(boolean kick) {
        this.kick = kick;
    }

    public double increaseBuffer() {
        return increaseBuffer(1);
    }

    public void fail() {
        Knight.getInstance().handleFail(this, buffer, userData.user);
    }

    public void fail(String... keys) {
        Knight.getInstance().handleFail(this, buffer, userData.user, keys);
    }

    public double getMaxBuffer() {
        return maxBuffer;
    }

    public double increaseBuffer(double value) {
        return buffer += value;
    }

    public double decreaseBuffer(double value) {
        return buffer = Math.max(0, buffer - value);
    }

    public void setBuffer(double buffer) {
        this.buffer = buffer;
    }

    public abstract Check newInstance(UserData data);

    public int getTicks(User user, int ticks) {
        return (int) (ticks + Math.round((user.getCraftPlayer().getHandle().ping / 50.0)));
    }

}
