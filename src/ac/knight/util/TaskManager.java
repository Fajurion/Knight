package ac.knight.util;

import ac.knight.Knight;
import java.util.ArrayList;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskManager {

    private ArrayList<Runnable> runnables = new ArrayList<>();
    private ArrayList<Runnable> toRemove = new ArrayList<>();

    public void init() {

        new BukkitRunnable() {
            @Override
            public void run() {

                for(Runnable runnable : toRemove) {
                    runnables.remove(runnable);
                }

                for(Runnable runnable : runnables) {
                    runnable.run();
                }

            }
        }.runTaskTimer(Knight.getInstance(), 0, 1);

    }

    public void inject(Runnable runnable) {
        runnables.add(runnable);
    }

    public void uninject(Runnable runnable) {
        toRemove.add(runnable);
    }

}
