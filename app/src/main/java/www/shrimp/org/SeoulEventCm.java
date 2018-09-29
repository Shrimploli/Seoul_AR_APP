package www.shrimp.org;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SeoulEventCm extends AppCompatActivity implements SeoulEventImageAdapter.OnItemClickListener {

    private FloatingActionButton eventFloat_BT;

    public static final String EXTRA_URL="imageUrl";
    public static final String EXTRA_CREATOR = "creatorName";
    public static final String EXTRA_DESC = "creatorDesc";
    public static final String EXTRA_TIME = "creatorTime";

    private RecyclerView mRecyclerView;
    private SeoulEventImageAdapter mAdapter;
    private ProgressBar progressBar_Cycle;

    private Toolbar eventToolbar;


    private DatabaseReference mDatabaseRef;
    private List<Upload> mUploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seoul_event_cm);


        mRecyclerView = findViewById(R.id.eventRecycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar_Cycle = findViewById(R.id.progress_circle);

        mUploads = new ArrayList<>();

        eventFloat_BT = findViewById(R.id.event_upload);
        eventFloat_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeoulEventCm.this,SeoulEventUpload.class);
                startActivity(intent);
            }
        });

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("eventUploads");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    mUploads.add(upload);
                }

                mAdapter= new SeoulEventImageAdapter(SeoulEventCm.this, mUploads);

                mRecyclerView.setAdapter(mAdapter);
                mAdapter.setOnItemClickListener(SeoulEventCm.this);
                progressBar_Cycle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SeoulEventCm.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar_Cycle.setVisibility(View.INVISIBLE);
            }
        });

        eventToolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(eventToolbar);
        getSupportActionBar().setTitle("서울 이벤트 커뮤니티");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.btn_back);


    }

    private long time= 0;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //toolbar의 back키 눌렀을 때 동작
                    finish();
            }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {
        Intent detailIntent = new Intent(SeoulEventCm.this,SeoulEventDetail.class);
        Upload uploads = mUploads.get(position);
        detailIntent.putExtra(EXTRA_URL, uploads.getImageUri());
        detailIntent.putExtra(EXTRA_CREATOR,uploads.getName());
        detailIntent.putExtra(EXTRA_DESC,uploads.getmDesc());
        detailIntent.putExtra(EXTRA_TIME,uploads.getmTime());

        startActivity(detailIntent);
    }
}
