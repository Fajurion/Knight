package ac.knight.check.combat.aim;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventRotation;
import ac.knight.user.UserData;
import ac.knight.util.MathUtil;

import java.util.ArrayList;

public class AimE1 extends Check {

    public AimE1(UserData userData) {
        super("Aim", "E1", "Checks for smooth rotations. (yaw)", 12, userData);
    }

    private final ArrayList<Number> yawSamples = new ArrayList<>();
    private int streak = 0;
    private final ArrayList<Number> sumSamples = new ArrayList<>();
    private double lastSumAvg = 0;

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventRotation) {

            yawSamples.add(userData.deltaYaw);
            if(yawSamples.size() >= 20) {
                yawSamples.remove(0);

                final ArrayList<Number> differences = MathUtil.getDiffrencesOther(yawSamples);
                final double deviation = MathUtil.getStandardDeviation(differences).doubleValue();
                final double average = MathUtil.getAverage(differences).doubleValue();
                final double sum = average + deviation;

                sumSamples.add(sum);
                if(sumSamples.size() >= 10) {

                    sumSamples.remove(0);
                    final double sumAvg = MathUtil.getAverage(sumSamples).doubleValue();

                    if((Math.abs(sumAvg - lastSumAvg) > 4 || Math.abs(sumAvg - lastSumAvg) < 0.0001) && userData.deltaYaw < 10 && userData.deltaPitch < 10) {
                        if(streak++ > 5) {
                            if(this.increaseBuffer() > 3) {
                                this.fail();
                            }
                        }
                    } else {
                        streak = 0;
                        this.decreaseBuffer(0.01);
                    }

                    lastSumAvg = sumAvg;

                }
                /*
                if(sum < 3 && sum > 0.5) {
                    if(streak++ > 5) {
                        if(this.increaseBuffer() > 3) {
                            this.fail("streak", streak + "");
                        }
                    }
                } else {
                    streak = 0;
                    this.decreaseBuffer(0.05);
                }

                 */

            }

        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new AimE1(data);
    }
}
