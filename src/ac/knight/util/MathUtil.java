package ac.knight.util;

import java.util.ArrayList;

public class MathUtil {

    public static final double EXPANDER = Math.pow(2, 24);

    public static Number getAverage(ArrayList<Number> list) {
        Number sum = list.stream().mapToDouble(Number::doubleValue).sum();
        return sum.doubleValue() / list.size();
    }

    public static Number getVariance(ArrayList<Number> list) {
        Number average = getAverage(list);
        Number sum = list.stream().mapToDouble(d -> expand(d.intValue()-average.intValue(), 2).doubleValue()).sum();
        return 1.0/list.size() * sum.doubleValue();
    }

    public static Number getStandardDeviation(ArrayList<Number> list) {
        return Math.sqrt(getVariance(list).doubleValue());
    }

    public static Number expand(Number value, int expander) {
        Number result = value.doubleValue();
        for(int i = 1; i < expander; i++) {
            result = result.doubleValue() * value.doubleValue();
        }
        return result;
    }

    public static ArrayList<Number> removeDuplicates(ArrayList<Number> list) {
        ArrayList<Number> toReturn = new ArrayList<>();
        for(Number num : list) {
            if(!toReturn.contains(num)) toReturn.add(num);
        }
        return toReturn;
    }

    public static int getDuplicates(ArrayList<Number> list) {
        return list.size() - removeDuplicates(list).size();
    }

    public static double getDistance(double x, double y, double z, double x2, double y2, double z2) {
        final double xDiff = x - x2;
        final double yDiff = y - y2;
        final double zDiff = z - z2;
        return Math.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);
    }

    public static boolean isRoughly(Number value, Number value2, Number offset) {
        return value.doubleValue()+offset.doubleValue() > value2.doubleValue() && value.doubleValue()-offset.doubleValue() < value2.doubleValue();
    }

    public static Number expand(Number... values) {

        double sum = 0;
        for(Number num : values) {
            sum += num.doubleValue() * num.doubleValue();
        }

        return Math.sqrt(sum);
    }

    public static Number getDiffrence(Number val1, Number val2) {
        return val1.doubleValue() > val2.doubleValue() ? val1.doubleValue() - val2.doubleValue() : val2.doubleValue() - val1.doubleValue();
    }

    public static long getGcd(final long current, final long previous) {
        return (previous <= 16384L) ? current : getGcd(previous, current % previous);
    }

    public static double getGcd(double a, double b) {
        if (a < b) {
            return getGcd(b, a);
        } else {
            return Math.abs(b) < 0.001D ? a : getGcd(b, a - Math.floor(a / b) * b);
        }
    }

    public static Number getMinimum(ArrayList<Number> numbers) {
        Number min = getMaximum(numbers);
        for(Number number : numbers) {
            if(number.doubleValue() < min.doubleValue()) {
                min = number;
            }
        }
        return min;
    }

    public static Number getMaximum(ArrayList<Number> numbers) {
        Number max = 0;
        for(Number number : numbers) {
            if(number.doubleValue() > max.doubleValue()) {
                max = number;
            }
        }
        return max;
    }

    public static ArrayList<Number> getDiffrencesOther(ArrayList<Number> list) {
        ArrayList<Number> toReturn = new ArrayList<>();
        int count = -1;
        for(Number d : list) {
            if(count != -1) {
                toReturn.add(d.doubleValue() - list.get(count).doubleValue());
            }
            count++;
        }
        return toReturn;
    }

    public static Number round(Number d) {
        return Math.round(d.doubleValue() * 100d) / 100d;
    }

    public static int getNegatives(ArrayList<Number> list) {
        int amount = 0;
        for(Number number : list) {
            if(number.doubleValue() < 0) {
                amount++;
            }
        }
        return amount;
    }

    public static int getPositives(ArrayList<Number> list) {
        int amount = 0;
        for(Number number : list) {
            if(number.doubleValue() >= 0) {
                amount++;
            }
        }
        return amount;
    }

}
