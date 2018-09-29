package www.shrimp.org;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static www.shrimp.org.SeoulEventCm.EXTRA_CREATOR;
import static www.shrimp.org.SeoulEventCm.EXTRA_DESC;
import static www.shrimp.org.SeoulEventCm.EXTRA_TIME;
import static www.shrimp.org.SeoulEventCm.EXTRA_URL;

public class SeoulEventDetail extends AppCompatActivity {

    private Toolbar detail_ToorBar;
    boolean isImageFitToScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seoul_event_detail);

        Intent intent = getIntent();
        String imgUrl = intent.getStringExtra(EXTRA_URL);
        String creatorName = intent.getStringExtra(EXTRA_CREATOR);
        String creatordesc = intent.getStringExtra(EXTRA_DESC);
        String creatorTime = intent.getStringExtra(EXTRA_TIME);
        final ImageView imageView = findViewById(R.id.image_view_detail);
        TextView textViewCreator = findViewById(R.id.text_view_creator_detail);
        TextView textViewCreatordesc = findViewById(R.id.text_view_creator_desc);
        TextView textViewCreatorTime = findViewById(R.id.text_view_creator_time);
//
//
        textViewCreator.setText(creatorName);
         textViewCreatordesc.setText("소개: "+ creatordesc);
         textViewCreatorTime.setText("글쓴이: "+"\n" + creatorTime);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isImageFitToScreen) {
                    detail_ToorBar.setVisibility(View.VISIBLE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    isImageFitToScreen=false;
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }else{
                    isImageFitToScreen=true;

                    detail_ToorBar.setVisibility(View.INVISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                }

            }
        });



        Picasso.get()
                .load(imgUrl)
                .fit()
                .centerCrop()
                .into(imageView);


        detail_ToorBar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(detail_ToorBar);
        getSupportActionBar().setTitle(creatorName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.btn_back);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //toolbar의 back키 눌렀을 때 동작
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
