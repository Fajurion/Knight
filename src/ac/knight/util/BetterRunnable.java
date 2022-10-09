package ac.knight.util;

import ac.knight.Knight;

public abstract class BetterRunnable implements Runnable {

    public void uninject() {
        Knight.getInstance().getTaskManager().uninject(this);
    }

}
