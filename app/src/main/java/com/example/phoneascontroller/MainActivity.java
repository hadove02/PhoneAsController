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
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private SensorDataSender sender;
    private ExecutorService executorService;
    private YPR ypr;
    /*
    private TextView[] accelerometerValues;
    private TextView[] gyroscopeValues;
    */
    private TextView[] rotationValues;

    final public int YAW = 0, PITCH = 1, ROLL = 2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME);

        ypr = new YPR();
        /*
        accelerometerValues = new TextView[3];
        accelerometerValues[X] = findViewById(R.id.PosiXvalue);
        accelerometerValues[Y] = findViewById(R.id.PosiYvalue);
        accelerometerValues[Z] = findViewById(R.id.PosiZvalue);

        gyroscopeValues = new TextView[3];
        gyroscopeValues[X] = findViewById(R.id.OriXvalue);
        gyroscopeValues[Y] = findViewById(R.id.OriYvalue);
        gyroscopeValues[Z] = findViewById(R.id.OriZvalue);
        */

        rotationValues = new TextView[3];
        rotationValues[YAW] = findViewById(R.id.yawValue);
        rotationValues[PITCH] = findViewById(R.id.pitchValue);
        rotationValues[ROLL] = findViewById(R.id.rollValue);

        executorService = Executors.newSingleThreadExecutor();
        try {
            sender = new SensorDataSender("172.30.75.78");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            ypr.inputAcc(event.values[YPR.AX], event.values[YPR.AY], event.values[YPR.AZ]);
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            ypr.inputGyro(event.values[YPR.P], event.values[YPR.Q], event.values[YPR.R]);
        }
        /*
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            ypr.inputElec(event.values[YPR.MX],event.values[YPR.MY],event.values[YPR.MZ]);
        }*/

        inputTexts(rotationValues, ypr.update());

        executorService.execute(() -> {
            try {
                sender.sendSensorData(ypr.getRotationValues());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        /*
        float[] sensorValue = event.values.clone();
        float[] formattedSensorValue = new float[3];

        for (int i = 0; i < sensorValue.length; i++) {
            formattedSensorValue[i] = Float.parseFloat(String.format("%.4f", sensorValue[i]));
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            inputTexts(positionValues, formattedSensorValue);
            Log.v("result", String.valueOf(formattedSensorValue[X]));
            executorService.execute(() -> {
                try {
                    sender.sendSensorData(formattedSensorValue, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            inputTexts(orientationValues, formattedSensorValue);
            executorService.execute(() -> {
                try {
                    sender.sendSensorData(null, formattedSensorValue);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float[] rotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            float[] orientationValues = new float[3];
            SensorManager.getOrientation(rotationMatrix, orientationValues);
            Log.v("result", String.valueOf(orientationValues[YAW]));
            inputTexts(this.orientationValues, orientationValues);

            executorService.execute(() -> {
                try {
                    sender.sendSensorData(orientationValues);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }*/
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
