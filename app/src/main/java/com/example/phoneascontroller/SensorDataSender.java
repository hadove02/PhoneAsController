package com.example.phoneascontroller;

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

    public void sendSensorData(double[] orientationData) throws IOException {
        String message = xyzFormat(orientationData);
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, PORT);
        socket.send(packet);
    }

    public static String xyzFormat(double[] array) {
        return array[YAW] + "," + array[PITCH] + "," + array[ROLL];
    }
}
