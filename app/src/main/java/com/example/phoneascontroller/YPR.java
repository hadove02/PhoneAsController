package com.example.phoneascontroller;

public class YPR {
    private static final int YAW = 0, PITCH = 1, ROLL = 2;
    private static final double ALPHA = 0.98;
    private double previousTime = System.currentTimeMillis() / 1000.0;

    public double[] update(double[] ypr, double ax, double ay, double az, double p, double q, double r) {
        double[] newYPR = new double[3];

        double currentTime = System.currentTimeMillis() / 1000.0;
        double dt = currentTime - previousTime;

        double pitchAcc = Math.atan2(ax, Math.sqrt(ay * ay + az * az)) * 180.0 / Math.PI;
        double rollAcc = Math.atan2(ay, Math.sqrt(ax * ax + az * az)) * 180.0 / Math.PI;

        newYPR[YAW] = ypr[YAW] + r * dt;
        newYPR[PITCH] = ALPHA * (ypr[PITCH] + q * dt) + (1.0 - ALPHA) * pitchAcc;
        newYPR[ROLL] = ALPHA * (ypr[ROLL] - q * dt) + (1.0 - ALPHA) * rollAcc;

        previousTime = currentTime;

        return newYPR;
    }
}
