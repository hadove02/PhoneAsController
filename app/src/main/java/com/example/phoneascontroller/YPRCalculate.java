package com.example.phoneascontroller;

import android.hardware.SensorManager;

public class YPRCalculate {
    public static final int AX = 0, AY = 1, AZ = 2, P = 0, Q = 1, R = 2, YAW = 0, PITCH = 1, ROLL = 2;
    private float[] rotationValues;
    private double previousTime = System.currentTimeMillis() / 1000.0;

    public YPRCalculate() {
        rotationValues = new float[3];
    }

    public float[] calculate(SensorDataSet sensorDataSet) {
        final double ALPHA = 0.98;

        double currentTime = System.currentTimeMillis() / 1000.0;
        double dt = currentTime - previousTime;

        double pitchAcc = Math.atan2(sensorDataSet.getAccelerometer()[AX],
                Math.sqrt(sensorDataSet.getAccelerometer()[AY] * sensorDataSet.getAccelerometer()[AY] + sensorDataSet.getAccelerometer()[AZ] * sensorDataSet.getAccelerometer()[AZ]))
                * 180.0 / Math.PI;
        double rollAcc = Math.atan2(sensorDataSet.getAccelerometer()[AY],
                Math.sqrt(sensorDataSet.getAccelerometer()[AX] * sensorDataSet.getAccelerometer()[AX] + sensorDataSet.getAccelerometer()[AZ] * sensorDataSet.getAccelerometer()[AZ]))
                * 180.0 / Math.PI;

        if (sensorDataSet.isCalYaw()) {
            rotationValues[PITCH] = (float) (ALPHA * (rotationValues[PITCH] + sensorDataSet.getGyroscope()[Q] * dt) + (1.0 - ALPHA) * pitchAcc);
            rotationValues[ROLL] = (float) (ALPHA * (rotationValues[ROLL] - sensorDataSet.getGyroscope()[Q] * dt) + (1.0 - ALPHA) * rollAcc);
            calculateYaw(sensorDataSet);
        }
        previousTime = currentTime;

        return rotationValues;
    }

    private void calculateYaw(SensorDataSet sensorDataSet) {
        float[] R = new float[9];
        float[] I = new float[9];

        if (SensorManager.getRotationMatrix(R, I, sensorDataSet.getAccelerometer(), sensorDataSet.getMagneticFiled())) {
            float[] orientation = new float[3];
            SensorManager.getOrientation(R, orientation);
            rotationValues[YAW] = (float) Math.toDegrees(orientation[0]);
        }
    }

    public float[] getRotationValues() {
        return rotationValues;
    }
}
