package www.shrimp.org;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class IntroActivity extends AppCompatActivity {

    Handler handler;
    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        imageView = (ImageView) findViewById(R.id.StartImg);

        final Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_start);

        imageView.startAnimation(anim);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(intent);

                finish();


            }
        }, 3000);

    }
}
