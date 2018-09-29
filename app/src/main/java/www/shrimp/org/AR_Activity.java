package www.shrimp.org;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AR_Activity extends AppCompatActivity implements SensorEventListener, LocationListener, OrientationListener {

    public static final int REQUEST_LOCATION_PERMISSIONS_CODE = 0;
    final static String TAG = "MainActivity";
    private final static int REQUEST_CAMERA_PERMISSIONS_CODE = 11;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 0;//1000 * 60 * 1; // 1 minute
    //OrientationManager 에서 Context 호출
    private static Context CONTEXT;
    public Location location;
    boolean isGPSEnabled;
    boolean isNetworkEnabled;
    boolean locationServiceAvailable;
    //날씨 정보
    TextView t1_temp, t2_city, t3_description, t4_date;
    ImageView weatherView, mainImgView, seoulImgView;
    CardView weatherCardview;
    private String iconString;
    //AR뷰 기능
    private SurfaceView surfaceView;
    private FrameLayout cameraContainerLayout;
    private AROverlayView arOverlayView;
    private Camera camera;
    private ARCamera arCamera;
    private TextView tvCurrentLocation;
    private SensorManager sensorManager;
    private LocationManager locationManager;

    private LinearLayout bottomNG;


    public static Context getContext() {
        return CONTEXT;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        cameraContainerLayout = (FrameLayout) findViewById(R.id.camera_container_layout);
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        tvCurrentLocation = (TextView) findViewById(R.id.tv_current_location);
        arOverlayView = new AROverlayView(this);


        t1_temp = (TextView) findViewById(R.id.t1_temp);
        t4_date = (TextView) findViewById(R.id.t4_date);
        weatherView = (ImageView) findViewById(R.id.imageView1);
        weatherCardview = (CardView) findViewById(R.id.wcardview);


        mainImgView = (ImageView) findViewById(R.id.main_desp_img);
        seoulImgView = (ImageView) findViewById(R.id.seoul_img);

        bottomNG = (LinearLayout) findViewById(R.id.bottom_ng);


        CONTEXT = this;

        find_weather();

        Toolbar toolbar = (Toolbar) findViewById(R.id.ar_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.btn_back);
        getSupportActionBar().setTitle("AR 날씨보기");
        toolbar.setTitleTextColor(Color.WHITE);


    }
    private long time= 0;
//    @Override
//    public void onBackPressed() {
//        if(System.currentTimeMillis()-time>=2000){
//            time=System.currentTimeMillis();
//            Toast.makeText(getApplicationContext(),"뒤로 버튼을 한번 더 누르면 AR(증강현실)을 종료합니다",Toast.LENGTH_SHORT).show();
//        }else if(System.currentTimeMillis()-time<2000){
//
//            finish();
//        }
//    }
        @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                if(System.currentTimeMillis()-time>=2000){
                    time=System.currentTimeMillis();
                    Toast.makeText(getApplicationContext(),"뒤로 버튼을 한번 더 누르면 AR(증강현실)을 종료합니다.",Toast.LENGTH_SHORT).show();
                }else if(System.currentTimeMillis()-time<2000){

                    finish();
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        requestLocationPermission();
        requestCameraPermission();
        registerSensors();
        initAROverlayView();

        if (OrientationManager.isSupported()) {
            OrientationManager.startListening(this);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (OrientationManager.isListening()) {
            OrientationManager.stopListening();
        }
    }

    @Override
    public void onOrientationChanged(float azimuth, float pitch, float roll) {

    }


    //자이로센서 위로 X방향
    @Override
    public void onTopUp() {
        Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
//        t1_temp.setVisibility(View.VISIBLE);
//        t2_city.setVisibility(View.VISIBLE);
//        t3_description.setVisibility(View.VISIBLE);
//        t4_date.setVisibility(View.VISIBLE);
//        weatherView.setVisibility(View.VISIBLE);


//        bottomNG.setVisibility(View.INVISIBLE);
//        weatherCardview.setVisibility(View.VISIBLE);


        weatherCardview.setVisibility(View.VISIBLE);

        find_weather();

        bottomNG.setVisibility(View.INVISIBLE);
    }


    //자이로센서 아래로 X방향
    @Override
    public void onBottomUp() {
        //Toast.makeText(this, "날씨정보를 확인하시려면 카메라를 위로 향해주세요", Toast.LENGTH_SHORT).show();
        bottomNG.setVisibility(View.VISIBLE);
        weatherCardview.setVisibility(View.INVISIBLE);


        //bottomNG.setVisibility(View.VISIBLE);
//        t1_temp.setVisibility(View.INVISIBLE);
//        t2_city.setVisibility(View.INVISIBLE);
//        t3_description.setVisibility(View.INVISIBLE);
//        t4_date.setVisibility(View.INVISIBLE);
//        weatherView.setVisibility(View.INVISIBLE);
//        weatherCardview.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onRightUp() {

    }

    @Override
    public void onLeftUp() {

    }

    @Override
    public void onPause() {
        releaseCamera();
        super.onPause();
    }

    //AR코딩 카메라 퍼미션
    public void requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSIONS_CODE);
        } else {
            initARCameraView();
        }
    }

    ////AR코딩 버전 체크
    public void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSIONS_CODE);
        } else {
            initLocationService();
        }
    }


    //AR코딩 서페이스뷰에 뿌려주기
    public void initAROverlayView() {
        if (arOverlayView.getParent() != null) {
            ((ViewGroup) arOverlayView.getParent()).removeView(arOverlayView);
        }
        cameraContainerLayout.addView(arOverlayView);
    }

    //AR코딩 카메라 생성
    public void initARCameraView() {
        reloadSurfaceView();

        if (arCamera == null) {
            arCamera = new ARCamera(this, surfaceView);
        }
        if (arCamera.getParent() != null) {
            ((ViewGroup) arCamera.getParent()).removeView(arCamera);
        }
        cameraContainerLayout.addView(arCamera);
        arCamera.setKeepScreenOn(true);
        initCamera();
    }

    //AR코딩
    private void initCamera() {
        int numCams = Camera.getNumberOfCameras();
        if (numCams > 0) {
            try {
                camera = Camera.open();
                camera.startPreview();
                arCamera.setCamera(camera);
            } catch (RuntimeException ex) {
                Toast.makeText(this, "Camera not found", Toast.LENGTH_LONG).show();
            }
        }
    }

    //AR코딩 서페이스뷰
    private void reloadSurfaceView() {
        if (surfaceView.getParent() != null) {
            ((ViewGroup) surfaceView.getParent()).removeView(surfaceView);
        }

        cameraContainerLayout.addView(surfaceView);
    }

    ////AR코딩 카메라 오류처리
    private void releaseCamera() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            arCamera.setCamera(null);
            camera.release();
            camera = null;
        }
    }


    //AR코딩 카메라 센서부분
    private void registerSensors() {
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                SensorManager.SENSOR_DELAY_FASTEST);
    }


    //AR코딩 카메라 뷰 부분
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            float[] rotationMatrixFromVector = new float[16];
            float[] projectionMatrix = new float[16];
            float[] rotatedProjectionMatrix = new float[16];

            SensorManager.getRotationMatrixFromVector(rotationMatrixFromVector, sensorEvent.values);

            if (arCamera != null) {
                projectionMatrix = arCamera.getProjectionMatrix();
            }

            Matrix.multiplyMM(rotatedProjectionMatrix, 0, projectionMatrix, 0, rotationMatrixFromVector, 0);
            this.arOverlayView.updateRotatedProjectionMatrix(rotatedProjectionMatrix);
        }
    }

    //AR코딩
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //do nothing
    }

    //AR코딩
    private void initLocationService() {

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            this.locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);

            // Get GPS and network status
            this.isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            this.isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isNetworkEnabled && !isGPSEnabled) {
                // cannot get location
                this.locationServiceAvailable = false;
            }

            this.locationServiceAvailable = true;

            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    updateLatestLocation();
                }
            }

            if (isGPSEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    updateLatestLocation();
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());

        }
    }

    //AR코딩
    private void updateLatestLocation() {
        if (arOverlayView != null && location != null) {
            arOverlayView.updateCurrentLocation(location);
            tvCurrentLocation.setText(String.format("lat: %s \nlon: %s \naltitude: %s \n",
                    location.getLatitude(), location.getLongitude(), location.getAltitude()));
        }
    }

    //AR코딩
    @Override
    public void onLocationChanged(Location location) {
        updateLatestLocation();
    }

    //AR코딩
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    //AR코딩
    @Override
    public void onProviderEnabled(String s) {

    }

    //AR코딩
    @Override
    public void onProviderDisabled(String s) {

    }

    //날씨 파싱!!! 부분 Fire base 서버 연동 부분
    public void find_weather() {

        String url = "http://api.openweathermap.org/data/2.5/weather?q=Seoul&appid=568ef5feb4363415ef41552bfc3280d4&units=imperial";
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject main_object = response.getJSONObject("main");

                    JSONArray array = response.getJSONArray("weather");
                    JSONObject object = array.getJSONObject(0);
                    String temp = String.valueOf(main_object.getDouble("temp"));
                    String description = object.getString("main");
                    String city = response.getString("name");
                    iconString = object.getString("icon");

//                    final String iconURL = "https://firebasestorage.googleapis.com/v0/b/fir-login-39b91.appspot.com/o/"
//                            + iconString + ".png?alt=media&token="+ weatherIcon;


                    //FireBase DB부러오기
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    StorageReference pathReference = storageRef.child("img/" + iconString + ".png");


                    Glide.with(AR_Activity.getContext())
                            .using(new FirebaseImageLoader())
                            .load(pathReference)
                            .fitCenter()
                            .into(weatherView);

                    StorageReference storageRef_main = storage.getReference();
                    StorageReference pathReference_main = storageRef_main.child("main/" + description + ".png");
                    StorageReference seoulRef = storageRef_main.child("main/" + city + ".png");

                    Glide.with(AR_Activity.getContext())
                            .using(new FirebaseImageLoader())
                            .load(pathReference_main)
                            .fitCenter()
                            .into(mainImgView);


                    Glide.with(AR_Activity.getContext())
                            .using(new FirebaseImageLoader())
                            .load(seoulRef)
                            .fitCenter()
                            .into(seoulImgView);


//                    final String iconURL = "http://openweathermap.org/img/w/"
//                            + iconString + ".png";

                    //openWeather icon 불러오기
                    // 안드로이드 네트워크 관력 작업시 메인스레드가아닌 별도 스레드에서 작업
//                    Thread t = new Thread() {
//                        @Override
//                        public void run() {
//                            try {
//                                URL url = new URL(iconURL); //URL 객체 생성
//
//                                //웹에서 이미지를 가져와서 Bitmap 에 저장
//                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                                conn.setDoInput(true);
//                                conn.connect();
//
//                                InputStream is = conn.getInputStream();
//                                bitmap = BitmapFactory.decodeStream(is);
//
//                            } catch (IOException ex) {
//
//                            }
//                        }
//                    };
//                    t.start(); //웹에서 가져온 이미지 가져오는 작업 스레드 시작
//                    try {
//                        t.join(); //이미지를 불러와 이미지뷰에 지정
//                        weatherView.setImageBitmap(bitmap);
//                        weatherView.getLayoutParams().width = 500;
//                        weatherView.getLayoutParams().height = 500;
//                    } catch (InterruptedException e) {
//
//                    }


//icon 불러오기 실패코드
//                    Thread t = new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                final ImageView weatherView = (ImageView)findViewById(R.id.imageView1);
//                                URL url = new URL(iconURL);
//                                InputStream is = url.openStream();
//                                final Bitmap bm = BitmapFactory.decodeStream(is);
//                                handler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        weatherView.setImageBitmap(bm);
//                                    }
//                                });
//                                weatherView.setImageBitmap(bm);
//                            } catch (IOException e) {
//
//                            }
//
//                        }
//
//                    });


                    //t1_temp.setText(temp);//

                    //위치

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 EEEE");
                    String formatted_date = sdf.format(calendar.getTime());

                    t4_date.setText(formatted_date);

                    double temp_int = Double.parseDouble(temp);
                    double centi = (temp_int - 32) / 1.8000;
                    centi = Math.round(centi);
                    int i = (int) centi;
                    t1_temp.setText(String.valueOf(i) + "°C");


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);
    }
}
