package com.example.covid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;

import com.example.covid.database.DetailData;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    // Database Reference and the Firebase data change listener
    private DatabaseReference dbRef;
    private ValueEventListener mDBListener;

    //A list to store DetailData object, DetailData is defined to store default database data, each entry in the default database is an DetailData object
    private List<DetailData> detailDataList = new ArrayList<>();

    //View pager to implement fragment
    ViewPager viewPager;
    MyPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        adapter = new MyPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Listen firebase data change event
        dbRef = FirebaseDatabase.getInstance().getReference();
        ValueEventListener dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    for (DataSnapshot d: ds.getChildren()) {
                        // Default database classloader
                        DetailData detailData = d.getValue(DetailData.class);
                        // Add to detailDataList
                        detailDataList.add(detailData);
                    }
                }
                //send detailDataList to fragment using broadcast
                Intent intent = new Intent();
                intent.setAction("to_fragment");
                intent.putParcelableArrayListExtra("Data", (ArrayList<? extends Parcelable>) detailDataList);
                LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        dbRef.addValueEventListener(dataListener);
        mDBListener = dataListener;
    }
}
