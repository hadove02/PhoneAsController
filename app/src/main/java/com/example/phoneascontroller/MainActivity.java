package com.example.phoneascontroller;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    public final int SENSOR = 0, TOUCH = 1;
    public final int ACCSENSOR = 0, GYROSENSOR = 1, MAGNETSENSOR = 2;
    private SensorManager sensorManager;
    private Sensor[] sensors;

    private SensorDataSender sender;
    private SensorDataSet sensorDataSet;
    private SendPacket sendPacketData;

    private ExecutorService executorService;
    private YPRCalculate yprCalculate;
    private TextView[] rotationValues;
    final public int YAW = 0, PITCH = 1, ROLL = 2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        sensors = new Sensor[3];
        sensors[ACCSENSOR] = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensors[GYROSENSOR] = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensors[MAGNETSENSOR] = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sensorManager.registerListener(this, sensors[ACCSENSOR], SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, sensors[GYROSENSOR], SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, sensors[MAGNETSENSOR], SensorManager.SENSOR_DELAY_GAME);

        try {
            sender = new SensorDataSender("172.30.76.247");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        sensorDataSet = new SensorDataSet();
        sendPacketData = new SendPacket();

        executorService = Executors.newSingleThreadExecutor();
        yprCalculate = new YPRCalculate();
        rotationValues = new TextView[3];
        rotationValues[YAW] = findViewById(R.id.yawValue);
        rotationValues[PITCH] = findViewById(R.id.pitchValue);
        rotationValues[ROLL] = findViewById(R.id.rollValue);


    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            sensorDataSet.setAccelerometer(event.values);
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            sensorDataSet.setGyroscope(event.values);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            sensorDataSet.setMagneticFiled(event.values);
        }

        sendPacketData.setData(sensorDataSet.getAccelerometer(), yprCalculate.calculate(sensorDataSet));
        inputTexts(rotationValues, yprCalculate.getRotationValues());

        executorService.execute(() -> {
            try {
                sender.sendSensorData(sendPacketData.sendBuffer(SENSOR));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void inputTexts(TextView[] views, float[] values) {
        views[YAW].setText(String.valueOf(values[YAW]));
        views[PITCH].setText(String.valueOf(values[PITCH]));
        views[ROLL].setText(String.valueOf(values[ROLL]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensors[ACCSENSOR], SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, sensors[GYROSENSOR], SensorManager.SENSOR_DELAY_GAME);
    }

    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
