package com.example.phoneascontroller;

public class YPR {
    public static final int AX = 0, AY = 1, AZ = 2, P = 0, Q = 1, R = 2, YAW = 0, PITCH = 1, ROLL = 2;
    private static final double ALPHA = 0.98;
    private double[] accValues;
    private double[] gyroValues;
    private double[] rotationValues;
    private double previousTime = System.currentTimeMillis() / 1000.0;

    public YPR() {
        accValues = new double[]{0.0, 0.0, 0.0};
        gyroValues = new double[]{0.0, 0.0, 0.0};
        rotationValues = new double[]{0.0, 0.0, 0.0};
    }

    public void inputAcc(double ax, double ay, double az) {
        accValues[AX] = ax;
        accValues[AY] = ay;
        accValues[AZ] = az;
    }

    public void inputGyro(double p, double q, double r) {
        gyroValues[P] = p;
        gyroValues[Q] = q;
        gyroValues[R] = r;
    }

    public void update() {
        double currentTime = System.currentTimeMillis() / 1000.0;
        double dt = currentTime - previousTime;

        double pitchAcc = Math.atan2(accValues[AX], Math.sqrt(accValues[AY] * accValues[AY] + accValues[AZ] * accValues[AZ])) * 180.0 / Math.PI;
        double rollAcc = Math.atan2(accValues[AY], Math.sqrt(accValues[AX] * accValues[AX] + accValues[AZ] * accValues[AZ])) * 180.0 / Math.PI;

        rotationValues[YAW] = rotationValues[YAW] + gyroValues[R] * dt;
        rotationValues[PITCH] = ALPHA * (rotationValues[PITCH] + gyroValues[Q] * dt) + (1.0 - ALPHA) * pitchAcc;
        rotationValues[ROLL] = ALPHA * (rotationValues[ROLL] - gyroValues[Q] * dt) + (1.0 - ALPHA) * rollAcc;

        previousTime = currentTime;
    }

    public double[] getRotationValues() {
        return rotationValues;
    }
}
