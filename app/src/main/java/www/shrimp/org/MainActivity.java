package www.shrimp.org;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dev.sacot41.scviewpager.DotsView;
import com.dev.sacot41.scviewpager.SCPositionAnimation;
import com.dev.sacot41.scviewpager.SCViewAnimation;
import com.dev.sacot41.scviewpager.SCViewAnimationUtil;
import com.dev.sacot41.scviewpager.SCViewPager;
import com.dev.sacot41.scviewpager.SCViewPagerAdapter;

import in.shadowfax.proswipebutton.ProSwipeButton;
import kr.go.seoul.airquality.AirQualityButtonTypeB;
import kr.go.seoul.airquality.AirQualityTypeMini;
import kr.go.seoul.culturalevents.CulturalEventButtonTypeA;
import kr.go.seoul.culturalevents.CulturalEventTypeMini;

public class MainActivity extends AppCompatActivity {


    private static final int NUM_PAGES = 5;
    private Toolbar myToolbar;
    private AirQualityTypeMini typeMini;
    private AirQualityButtonTypeB typeB;
    private CulturalEventTypeMini ctypeMini;
    private CulturalEventButtonTypeA ctypeA;
    private SCViewPager mViewPager;
    private SCViewPagerAdapter mPageAdapter;
    private DotsView mDotsView;
    private String openApiKey = "4645705950646e6a3131324143655879";

    private ImageButton event;

    //툴바 메뉴 옵션
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    // 메뉴에서 버튼이 눌리면
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                Toast.makeText(getApplicationContext(), "AR(증강현실)", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, AR_Activity.class);
                startActivity(intent);

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                Toast.makeText(getApplicationContext(), "나머지 버튼 클릭됨", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ProSwipeButton proSwipeBtn = findViewById(R.id.proswipebutton_main);
        // final ProSwipeButton proSwipeBtnError = findViewById(R.id.proswipebutton_main_error);
        proSwipeBtn.setSwipeDistance(0.5f);


        proSwipeBtn.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
            @Override
            public void onSwipeConfirm() {
                // user has swiped the btn. Perform your async operation now
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        proSwipeBtn.showResultIcon(true, true);
                        Toast.makeText(getApplicationContext(), "AR(증강현실)", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MainActivity.this, AR_Activity.class);
                        startActivity(intent);
                    }
                }, 2000);
            }
        });

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("서울 AR려줄게");

        event = (ImageButton) findViewById(R.id.seoul_event_button);
        event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SeoulEventCm.class);

                startActivity(intent);
            }
        });


        //모바일 플렛폼 대기정보 API Mini , TypeB button
        typeMini = (AirQualityTypeMini) findViewById(R.id.airquality_mini);
        typeMini.setOpenAPIKey(openApiKey);

        typeB = (AirQualityButtonTypeB) findViewById(R.id.weather_qility_btn);
        typeB.setOpenAPIKey(openApiKey);
        typeB.setButtonImage(R.drawable.weather_btn);
        typeB.setButtonText("상세 대기정보 Click");

        //모바일 플렛폼 문화 정보 API  Mini , TypeA button
        ctypeMini = (CulturalEventTypeMini) findViewById(R.id.cultural_mini);
        ctypeMini.setOpenAPIKey(openApiKey);

        ctypeA = (CulturalEventButtonTypeA) findViewById(R.id.cultural_quality_btn);
        ctypeA.setOpenAPIKey(openApiKey);
        ctypeA.setButtonImage(R.drawable.caltrue_btn);
        ctypeA.setButtonText("상세 문화정보 Click");



        //
        mViewPager = (SCViewPager) findViewById(R.id.viewpager_main_activity);
        mDotsView = (DotsView) findViewById(R.id.dotsview_main);
        mDotsView.setDotRessource(R.drawable.dot_selected, R.drawable.dot_unselected);
        mDotsView.setNumberOfPage(NUM_PAGES);

        mPageAdapter = new SCViewPagerAdapter(getSupportFragmentManager());
        mPageAdapter.setNumberOfPage(NUM_PAGES);
        mPageAdapter.setFragmentBackgroundColor(R.color.theme_100);
        mViewPager.setAdapter(mPageAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mDotsView.selectDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        final Point size = SCViewAnimationUtil.getDisplaySize(this);

        View nameTag = findViewById(R.id.airquality_mini);
        SCViewAnimation nameTagAnimation = new SCViewAnimation(nameTag);
        nameTagAnimation.addPageAnimation(new SCPositionAnimation(this, 0, 0, -size.y / 2));
        mViewPager.addAnimation(nameTagAnimation);

        View currentlyWork = findViewById(R.id.weather_qility_btn);
        SCViewAnimation currentlyWorkAnimation = new SCViewAnimation(currentlyWork);
        currentlyWorkAnimation.addPageAnimation(new SCPositionAnimation(this, 0, -size.x, 0));
        mViewPager.addAnimation(currentlyWorkAnimation);

        View atSkex = findViewById(R.id.clock);
        SCViewAnimationUtil.prepareViewToGetSize(atSkex);
        SCViewAnimation atSkexAnimation = new SCViewAnimation(atSkex);
        atSkexAnimation.addPageAnimation(new SCPositionAnimation(getApplicationContext(), 0, 0, -(size.y - atSkex.getHeight())));
        atSkexAnimation.addPageAnimation(new SCPositionAnimation(getApplicationContext(), 1, -size.x, 0));
        mViewPager.addAnimation(atSkexAnimation);

        View mobileView = findViewById(R.id.AR_image);
        SCViewAnimation mobileAnimation = new SCViewAnimation(mobileView);
        mobileAnimation.startToPosition((int) (size.x * 1.5), null);
        mobileAnimation.addPageAnimation(new SCPositionAnimation(this, 0, -(int) (size.x * 1.5), 0));
        mobileAnimation.addPageAnimation(new SCPositionAnimation(this, 1, -(int) (size.x * 1.5), 0));
        mViewPager.addAnimation(mobileAnimation);

        View djangoView = findViewById(R.id.ar_weather_bottom);
        SCViewAnimation djangoAnimation = new SCViewAnimation(djangoView);
        djangoAnimation.startToPosition(null, -size.y);
        djangoAnimation.addPageAnimation(new SCPositionAnimation(this, 0, 0, size.y));
        djangoAnimation.addPageAnimation(new SCPositionAnimation(this, 1, 0, size.y));
        mViewPager.addAnimation(djangoAnimation);

        View commonlyView = findViewById(R.id.ar_weather_title);
        SCViewAnimation commonlyAnimation = new SCViewAnimation(commonlyView);
        commonlyAnimation.startToPosition(size.x, null);
        commonlyAnimation.addPageAnimation(new SCPositionAnimation(this, 0, -size.x, 0));
        commonlyAnimation.addPageAnimation(new SCPositionAnimation(this, 1, -size.x, 0));
        mViewPager.addAnimation(commonlyAnimation);

        View butView = findViewById(R.id.cultural_mini);
        SCViewAnimation butAnimation = new SCViewAnimation(butView);
        butAnimation.startToPosition(size.x, null);
        butAnimation.addPageAnimation(new SCPositionAnimation(this, 1, -size.x, 0));
        butAnimation.addPageAnimation(new SCPositionAnimation(this, 2, -size.x, 0));
        mViewPager.addAnimation(butAnimation);

        View diplomeView = findViewById(R.id.cultural_quality_btn);
        SCViewAnimation diplomeAnimation = new SCViewAnimation(diplomeView);
        diplomeAnimation.startToPosition((size.x * 2), null);
        diplomeAnimation.addPageAnimation(new SCPositionAnimation(this, 1, -size.x * 2, 0));
        diplomeAnimation.addPageAnimation(new SCPositionAnimation(this, 2, -size.x * 2, 0));
        mViewPager.addAnimation(diplomeAnimation);

        View whyView = findViewById(R.id.calture_bottom);
        SCViewAnimation whyAnimation = new SCViewAnimation(whyView);
        whyAnimation.startToPosition(size.x, null);
        whyAnimation.addPageAnimation(new SCPositionAnimation(this, 1, -size.x, 0));
        whyAnimation.addPageAnimation(new SCPositionAnimation(this, 2, -size.x, 0));
        mViewPager.addAnimation(whyAnimation);

        View futureView = findViewById(R.id.community_title);
        SCViewAnimation futureAnimation = new SCViewAnimation(futureView);
        futureAnimation.startToPosition(null, -size.y);
        futureAnimation.addPageAnimation(new SCPositionAnimation(this, 2, 0, size.y));
        futureAnimation.addPageAnimation(new SCPositionAnimation(this, 3, -size.x, 0));
        mViewPager.addAnimation(futureAnimation);

        View arduinoView = findViewById(R.id.seoul_event_button);
        SCViewAnimation arduinoAnimation = new SCViewAnimation(arduinoView);
        arduinoAnimation.startToPosition(size.x * 2, null);
        arduinoAnimation.addPageAnimation(new SCPositionAnimation(this, 2, -size.x * 2, 0));
        arduinoAnimation.addPageAnimation(new SCPositionAnimation(this, 3, -size.x, 0));
        mViewPager.addAnimation(arduinoAnimation);

        View raspberryView = findViewById(R.id.seoul_comit_CardView);
        SCViewAnimation raspberryAnimation = new SCViewAnimation(raspberryView);
        raspberryAnimation.startToPosition(-size.x, null);
        raspberryAnimation.addPageAnimation(new SCPositionAnimation(this, 2, size.x, 0));
        raspberryAnimation.addPageAnimation(new SCPositionAnimation(this, 3, -size.x, 0));
        mViewPager.addAnimation(raspberryAnimation);

        View connectedDeviceView = findViewById(R.id.seoul_nanum_CardView);
        SCViewAnimation connectedDeviceAnimation = new SCViewAnimation(connectedDeviceView);
        connectedDeviceAnimation.startToPosition((int) (size.x * 1.5), null);
        connectedDeviceAnimation.addPageAnimation(new SCPositionAnimation(this, 2, -(int) (size.x * 1.5), 0));
        connectedDeviceAnimation.addPageAnimation(new SCPositionAnimation(this, 3, -size.x, 0));
        mViewPager.addAnimation(connectedDeviceAnimation);

        View checkOutView = findViewById(R.id.imageview_main_check_out);
        SCViewAnimation checkOutAnimation = new SCViewAnimation(checkOutView);
        checkOutAnimation.startToPosition(size.x, null);
        checkOutAnimation.addPageAnimation(new SCPositionAnimation(this, 3, -size.x, 0));
        mViewPager.addAnimation(checkOutAnimation);

        View linkedinView = findViewById(R.id.textview_main_linkedin_link);
        SCViewAnimation linkedinAnimation = new SCViewAnimation(linkedinView);
        linkedinAnimation.startToPosition(size.x, null);
        linkedinAnimation.addPageAnimation(new SCPositionAnimation(this, 3, -size.x, 0));
        mViewPager.addAnimation(linkedinAnimation);

        View githubView = findViewById(R.id.textview_main_github_link);
        SCViewAnimation githubAnimation = new SCViewAnimation(githubView);
        githubAnimation.startToPosition(size.x, null);
        githubAnimation.addPageAnimation(new SCPositionAnimation(this, 3, -size.x, 0));
        mViewPager.addAnimation(githubAnimation);
    }

}
