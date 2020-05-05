package com.example.covid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.covid.database.DetailData;
import com.example.covid.database.DetailDataList;
import com.example.covid.database.State;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StateDetailActivity extends AppCompatActivity {

    private static final String TAG = "StateDetailActivity";

    private String date;
    LocalDate startDate = LocalDate.of(2020, 1, 22);
    private ArrayList<String> dateList = new ArrayList<>();

    // Database Reference and the Firebase data change listener
    private DatabaseReference dbRef;
    private ValueEventListener mDBListener;

    private List<DetailData> detailDataList = new ArrayList<>();
    private DetailDataList detailDataListObject;
    private List<State> stateList;

    private String countryName;
    private String stateName;

    private Toolbar toolbar;
    private TextView state_confirmed_num_text;
    private TextView state_deaths_num_text;
    private TextView state_recovered_num_text;
    private TextView state_current_num_text;

    //    MPChart
    private LineChart mpChart;
    ArrayList<Entry> mpConfirmedValues = new ArrayList<>();
    ArrayList<Entry> mpDeathsValues = new ArrayList<>();
    ArrayList<Entry> mpRecoveredValues = new ArrayList<>();
    ArrayList<Entry> mpCurrentValues = new ArrayList<>();

    LineDataSet confirmedSet;
    LineDataSet deathsSet;
    LineDataSet recoveredSet;
    LineDataSet currentSet;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state_detail);
        state_confirmed_num_text = findViewById(R.id.state_confirmed_num_text);
        Log.d(TAG, "onCreate: " + (state_confirmed_num_text == null));
        state_deaths_num_text = findViewById(R.id.state_death_num_text);
        state_recovered_num_text = findViewById(R.id.state_recovered_num_text);
        state_current_num_text = findViewById(R.id.state_current_num_text);

        LocalDate curDate = LocalDate.now();
        LocalDate yesterday = curDate.minusDays(1);
        date = yesterday.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        Log.d(TAG, "onCreate: " + date);

        for (LocalDate i = startDate; !i.equals(curDate); i = i.plusDays(1)) {
            dateList.add(i.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
        }

        Intent intent = this.getIntent();
        countryName = intent.getStringExtra("country");
        stateName = intent.getStringExtra("state");
        Log.d(TAG, "onCreate: " + countryName);
        Log.d(TAG, "onCreate: " + stateName);

        mpChart = findViewById(R.id.state_chart);
        mpChart.setTouchEnabled(true);

        toolbar = findViewById(R.id.state_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24px);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setTitle(stateName);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Listen firebase data change event
        new Thread() {
            @Override
            public void run() {
                super.run();
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
                        detailDataListObject = new DetailDataList(detailDataList);
                        stateList = detailDataListObject.getStateList(countryName, stateName);
                        Log.d(TAG, "onDataChange: " + stateList.get(51).getConfirmed());
                        loadData();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    }
                };
                dbRef.addValueEventListener(dataListener);
                mDBListener = dataListener;
            }
        }.start();
    }

    private void loadData() {
        new Thread() {
            int confirmed = 0;
            int deaths = 0;
            int recovered = 0;
            int current = 0;

            @Override
            public void run() {
                super.run();
                for (com.example.covid.database.State state: stateList) {
                    // Set Textview
                    if (state.getDate().equals(date)) {
                        confirmed = state.getConfirmed();
                        deaths = state.getDeaths();
                        recovered = state.getRecovered();
                        current = state.getCurrent();
                    }
                }

                Collections.sort(stateList, (o1, o2) -> LocalDate.parse(o1.getDate(), formatter).compareTo(LocalDate.parse(o2.getDate(), formatter)));

                //set chart data
                int i = 1;
                for (com.example.covid.database.State state: stateList) {
                    mpConfirmedValues.add(new Entry(i, state.getConfirmed()));
                    mpDeathsValues.add(new Entry(i, state.getDeaths()));
                    mpRecoveredValues.add(new Entry(i, state.getRecovered()));
                    mpCurrentValues.add(new Entry(i, state.getCurrent()));
                    i += 1;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        state_confirmed_num_text.setText(String.valueOf(confirmed));
                        state_deaths_num_text.setText(String.valueOf(deaths));
                        state_recovered_num_text.setText(String.valueOf(recovered));
                        state_current_num_text.setText(String.valueOf(current));

                        if (mpChart.getData() != null && mpChart.getData().getDataSetCount() > 0) {
                            confirmedSet = (LineDataSet)mpChart.getData().getDataSetByIndex(0);
                            confirmedSet.setValues(mpConfirmedValues);
                            deathsSet = (LineDataSet)mpChart.getData().getDataSetByIndex(0);
                            deathsSet.setValues(mpDeathsValues);
                            recoveredSet = (LineDataSet)mpChart.getData().getDataSetByIndex(0);
                            recoveredSet.setValues(mpRecoveredValues);
                            currentSet = (LineDataSet)mpChart.getData().getDataSetByIndex(0);
                            currentSet.setValues(mpCurrentValues);
                            mpChart.getData().notifyDataChanged();
                            mpChart.notifyDataSetChanged();
                        } else {
                            confirmedSet = new LineDataSet(mpConfirmedValues, "Confirmed");
                            confirmedSet.setDrawIcons(false);
                            confirmedSet.setColor(Color.parseColor("#f44336"));
                            confirmedSet.setCircleColor(Color.parseColor("#f44336"));
                            confirmedSet.setLineWidth(1f);
                            confirmedSet.setCircleRadius(3f);
                            confirmedSet.setDrawCircleHole(false);
                            confirmedSet.setValueTextSize(9f);
                            confirmedSet.setFormLineWidth(1f);

                            deathsSet = new LineDataSet(mpDeathsValues, "Deaths");
                            deathsSet.setDrawIcons(false);
                            deathsSet.setColor(Color.parseColor("#757575"));
                            deathsSet.setCircleColor(Color.parseColor("#757575"));
                            deathsSet.setLineWidth(1f);
                            deathsSet.setCircleRadius(3f);
                            deathsSet.setDrawCircleHole(false);
                            deathsSet.setValueTextSize(9f);
                            deathsSet.setFormLineWidth(1f);

                            recoveredSet = new LineDataSet(mpRecoveredValues, "Recovered");
                            recoveredSet.setDrawIcons(false);
                            recoveredSet.setColor(Color.parseColor("#2E7D32"));
                            recoveredSet.setCircleColor(Color.parseColor("#2E7D32"));
                            recoveredSet.setLineWidth(1f);
                            recoveredSet.setCircleRadius(3f);
                            recoveredSet.setDrawCircleHole(false);
                            recoveredSet.setValueTextSize(9f);
                            recoveredSet.setFormLineWidth(1f);

                            currentSet = new LineDataSet(mpCurrentValues, "Current");
                            currentSet.setDrawIcons(false);
                            currentSet.setColor(Color.parseColor("#ff94c2"));
                            currentSet.setCircleColor(Color.parseColor("#ff94c2"));
                            currentSet.setLineWidth(1f);
                            currentSet.setCircleRadius(3f);
                            currentSet.setDrawCircleHole(false);
                            currentSet.setValueTextSize(9f);
                            currentSet.setFormLineWidth(1f);

                            ArrayList<ILineDataSet> linesDataSets = new ArrayList<>();
                            linesDataSets.add(confirmedSet);
                            linesDataSets.add(deathsSet);
                            linesDataSets.add(recoveredSet);
                            linesDataSets.add(currentSet);
                            LineData data = new LineData(linesDataSets);
                            mpChart.setData(data);
                        }
                    }
                });
            }
        }.start();
    }
}
