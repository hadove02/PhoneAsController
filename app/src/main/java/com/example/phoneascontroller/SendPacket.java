package com.example.phoneascontroller;

public class SendPacket {
    private float[] accelerometer;
    private float[] rotation;
    private final int X = 0, Y = 1, Z = 2;

    public SendPacket() {
        accelerometer = new float[3];
        rotation = new float[3];
    }

    public void setData(float[] accelerometer, float[] rotation) {
        System.arraycopy(accelerometer, 0, this.accelerometer, 0, accelerometer.length);
        System.arraycopy(rotation, 0, this.rotation, 0, rotation.length);
    }

    public byte[] sendBuffer(int type) {
        String msg = type + "," + xyzFormat(accelerometer) + "," + xyzFormat(rotation);
        return msg.getBytes();
    }


    private String xyzFormat(float[] array) {
        return array[X] + "," + array[Y] + "," + array[Z];
    }
}
