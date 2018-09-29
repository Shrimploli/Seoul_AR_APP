package www.shrimp.org;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.opengl.Matrix;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import www.shrimp.org.helper.LocationHelper;
import www.shrimp.org.model.ARPoint;

/**
 * Created by ntdat on 1/13/17.
 */

public class AROverlayView extends View {

    Context context;
    private float[] rotatedProjectionMatrix = new float[16];
    private Location currentLocation;
    private List<ARPoint> arPoints;


    public AROverlayView(Context context) {
        super(context);

        this.context = context;
        //Demo points

        // 파일 베이스 서버 구축 부분
        arPoints = new ArrayList<ARPoint>() {{

        }};
    }


    public void updateRotatedProjectionMatrix(float[] rotatedProjectionMatrix) {
        this.rotatedProjectionMatrix = rotatedProjectionMatrix;
        this.invalidate();
    }

    public void updateCurrentLocation(Location currentLocation){
        this.currentLocation = currentLocation;
        this.invalidate();
    }
    //카메라 뷰에 그려주기
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //
        if (currentLocation == null) {

            return;
        }

        final int radius = 30;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(60);

        for (int i = 0; i < arPoints.size(); i ++) {
            float[] currentLocationInECEF = LocationHelper.WSG84toECEF(currentLocation);
            float[] pointInECEF = LocationHelper.WSG84toECEF(arPoints.get(i).getLocation());
            float[] pointInENU = LocationHelper.ECEFtoENU(currentLocation, currentLocationInECEF, pointInECEF);
            for(int j =0; j< pointInECEF.length;j++){
                Log.d("Test pointInENU "+j,""+pointInENU[j] );
            }
            float[] cameraCoordinateVector = new float[4];
            Matrix.multiplyMV(cameraCoordinateVector, 0, rotatedProjectionMatrix, 0, pointInENU, 0);
            Log.d("Test cameraCoo 0 "+ i,""+cameraCoordinateVector[0]);
            Log.d("Test cameraCoo 1 l"+ i,""+cameraCoordinateVector[1]);
            Log.d("Test cameraCoo 2 l"+ i,""+cameraCoordinateVector[2]);
            Log.d("Test cameraCoo 3 l"+ i,""+cameraCoordinateVector[3]);

            Log.d("Test rotatedPro",""+this.rotatedProjectionMatrix[0] );
            Log.d("Test rotatedPro",""+this.rotatedProjectionMatrix[2] );
            Log.d("Test rotatedPro",""+this.rotatedProjectionMatrix[3] );
            Log.d("Test rotatedPro",""+this.rotatedProjectionMatrix[4] );
            Log.d("Test rotatedPro",""+this.rotatedProjectionMatrix[10] );
            Log.d("Test rotatedPro",""+this.rotatedProjectionMatrix[15] );
            // Log.d("Test cameraCoo 4 l"+ i,""+cameraCoordinateVector[4]);
            // cameraCoordinateVector[2] is z, that always less than 0 to display on right position
            // if z > 0, the point will display on the opposite
            if (cameraCoordinateVector[2] < 0) {
                float x  = (0.5f + cameraCoordinateVector[0]/cameraCoordinateVector[3]) * canvas.getWidth();
                float y = (0.5f - cameraCoordinateVector[1]/cameraCoordinateVector[3]) * canvas.getHeight();
//                Toast.makeText(getContext(), arPoints.get(i).getName(),
//                        Toast.LENGTH_LONG).show();
                canvas.drawCircle(x, y, radius, paint);
                canvas.drawText(arPoints.get(i).getName(), x - (30 * arPoints.get(i).getName().length() / 2), y - 80, paint);
            }
            else {
                //Toast.makeText(getContext(),cameraCoordinateVector[2]+"" ,
                   //     Toast.LENGTH_LONG).show();
            }
        }
    }
}