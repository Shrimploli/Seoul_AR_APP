package www.shrimp.org;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.List;

public class OrientationManager {

    private static Sensor sensor;
    private static SensorManager sensorManager;


    private static OrientationListener listener;
    private static Boolean supported;
    private static boolean running = false;

    //폰의 사이드
    enum Side{
        TOP,
        BOTTOM,
        LEFT,
        RIGHT,
    }

    //센서 매너저가 방위(위치) 변경을 리스닝하고 있으면 true를 변환
    public static boolean isListening(){
        return running;
    }

    //리스너 등록 헤제
    public static void stopListening(){
        running = false;
        try{
            if (sensorManager != null && sensorEventListener != null) {
                sensorManager.unregisterListener(sensorEventListener);
            }
        }catch (Exception e){

        }
    }

    public static boolean isSupported() {
        if (supported == null){
            if (AR_Activity.getContext() != null) {
                sensorManager = (SensorManager) AR_Activity.getContext().getSystemService(Context.SENSOR_SERVICE);
                List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
                supported = new Boolean(sensors.size()>0);
            } else {
                supported = Boolean.FALSE;
            }
        }
        return supported;
    }

    public static void startListening (OrientationListener orientationListener) {
        sensorManager = (SensorManager) AR_Activity.getContext().getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
        if (sensors.size()>0){
            sensor = sensors.get(0);
            running = sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
            listener = orientationListener;
        }
    }
    private static SensorEventListener sensorEventListener =new SensorEventListener(){
        private  Side currentSide = null;
        private Side oldSide = null;
        private float azimuth;
        private float pitch;
        private float roll;

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            azimuth = sensorEvent.values[0];
            pitch = sensorEvent.values[1];
            roll = sensorEvent.values[2];

            if (pitch < -135 ) {
                //위를 보고있다.
                currentSide = Side.TOP;
            }else if (pitch >-45){
                //아래를 보고있다.
                currentSide = Side.BOTTOM;
            }else if (roll > 45) {
                //오른쪽이 올라갔다.
                currentSide = Side.RIGHT;
            }else if (roll<-45){
                //왼쪽이 올라갔다.
                currentSide = Side.LEFT;
            }
            if (currentSide !=null && !currentSide.equals(oldSide)) {
                switch (currentSide) {
                    case TOP:
                        listener.onTopUp();
                        break;
                    case BOTTOM:
                        listener.onBottomUp();
                        break;
                    case LEFT:
                        listener.onLeftUp();
                        break;
                    case RIGHT:
                        listener.onRightUp();
                        break;
                }
                oldSide = currentSide;
            }

            listener.onOrientationChanged(azimuth,pitch,roll);
        }
    };
}
