package com.example.phoneascontroller;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private Sensor geomagnetic;
    private SensorDataSender sender;
    private ExecutorService executorService;
    private YPR ypr;
    private TextView[] rotationValues;

    final public int YAW = 0, PITCH = 1, ROLL = 2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        geomagnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, geomagnetic, SensorManager.SENSOR_DELAY_GAME);

        ypr = new YPR();

        rotationValues = new TextView[3];
        rotationValues[YAW] = findViewById(R.id.yawValue);
        rotationValues[PITCH] = findViewById(R.id.pitchValue);
        rotationValues[ROLL] = findViewById(R.id.rollValue);

        executorService = Executors.newSingleThreadExecutor();
        try {
            sender = new SensorDataSender("172.30.76.247");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    float[] accValues, geoValues;

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accValues = event.values;
            ypr.inputAcc(event.values[YPR.AX], event.values[YPR.AY], event.values[YPR.AZ]);
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            ypr.inputGyro(event.values[YPR.P], event.values[YPR.Q], event.values[YPR.R]);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geoValues = event.values;
        }

        if (accelerometer != null && geoValues != null) {
            float[] R = new float[9];
            float[] I = new float[9];

            if (SensorManager.getRotationMatrix(R, I, accValues, geoValues)) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(R, orientation);
                ypr.setYAW(Math.toDegrees(orientation[0]));
            }
        }

        inputTexts(rotationValues, ypr.update());

        executorService.execute(() -> {
            try {
                sender.sendSensorData(ypr.getRotationValues());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void inputTexts(TextView[] views, double[] values) {
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
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME);
    }

    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
