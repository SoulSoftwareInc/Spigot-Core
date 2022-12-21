package org.soulsoftware.spigot.core.Utils;

@FunctionalInterface
interface Interpolator {
    double[] interpolate(double from, double to, int max);

    class Quadratic implements Interpolator {
        public double[] interpolate(double from, double to, int max) {
            final double[] results = new double[max];
            double a = (to - from) / (max * max);
            for (int i = 0; i < results.length; i++) {
                results[i] = a * i * i + from;
            }
            return results;
        }
    }

    class Linear implements Interpolator {
        public double[] interpolate(double from, double to, int max) {
            final double[] res = new double[max];
            for (int i = 0; i < max; i++) {
                res[i] = from + i * ((to - from) / (max - 1));
            }
            return res;
        }
    }
}
