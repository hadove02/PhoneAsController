package com.example.phoneascontroller;

public class SensorDataSet {
    private float[] accelerometer;
    private float[] gyroscope;
    private float[] magneticFiled;

    public SensorDataSet() {
        accelerometer = new float[3];
        gyroscope = new float[3];
        magneticFiled = new float[3];
    }

    public boolean isCalYaw() {
        return accelerometer != null && magneticFiled != null;
    }

    public void setAccelerometer(float[] accelerometer) {
        System.arraycopy(accelerometer, 0, this.accelerometer, 0, accelerometer.length);
    }

    private float lowPassFilter(float pastValue, float currentValue){
        final float ALPHA = 0.8f;
        return ALPHA * pastValue + (1-ALPHA) * currentValue;
    }
    public void setGyroscope(float[] gyroscope) {
        System.arraycopy(gyroscope, 0, this.gyroscope, 0, gyroscope.length);
    }

    public void setMagneticFiled(float[] magneticFiled) {
        System.arraycopy(magneticFiled, 0, this.magneticFiled, 0, magneticFiled.length);
    }

    public float[] getAccelerometer() {
        return accelerometer;
    }

    public float[] getGyroscope() {
        return gyroscope;
    }

    public float[] getMagneticFiled() {
        return magneticFiled;
    }
}
