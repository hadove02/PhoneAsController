package com.example.phoneascontroller;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class SensorDataSender {
    private DatagramSocket socket;
    private InetAddress serverAddress;
    static final public int PORT = 9009, YAW = 0, PITCH = 1, ROLL = 2;

    public SensorDataSender(String serverIp) throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        serverAddress = InetAddress.getByName(serverIp);
    }

    public void sendSensorData(float[] orientationData) throws IOException {
        String message = xyzFormat(orientationData);
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, PORT);
        socket.send(packet);
        Log.d("SensorDataSendor","Data sent: "+message);
    }

    public static String xyzFormat(float[] array) {
        return array[YAW] + "," + array[PITCH] + "," + array[ROLL];
    }
}
