package com.example.covid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.covid.database.Country;
import com.example.covid.database.CountryTotalDaily;
import com.example.covid.database.CountryTotalDailyList;
import com.example.covid.database.State;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.button.MaterialButton;
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

import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class CountryDetailActivity extends AppCompatActivity{
    private static final String TAG = "CountryDetailActivity";
    private String date;
    LocalDate startDate = LocalDate.of(2020, 1, 22);
    private ArrayList<String> dateList = new ArrayList<>();

    final FirebaseDatabase countryDB = FirebaseDatabase.getInstance("https://covid-country.firebaseio.com/");
    private DatabaseReference countryDBRef;

    private CountryTotalDaily countryTotalDaily;
    private CountryTotalDailyList countryTotalDailyList;
    private List<CountryTotalDaily> countryTotalDailyArray = new ArrayList<>();

    private Country countryObject;
    private TextView country_confirmed_num_text;
    private TextView country_death_num_text;
    private TextView country_recovered_num_text;
    private TextView country_current_num_text;
    private Toolbar toolbar;

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

    private TableLayout tableLayout;

    Intent intent;

    String countryName;

    public CountryDetailActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_detail);

        country_confirmed_num_text = findViewById(R.id.country_confirmed_num_text);
        country_death_num_text = findViewById(R.id.country_death_num_text);
        country_recovered_num_text = findViewById(R.id.country_recovered_num_text);
        country_current_num_text = findViewById(R.id.country_current_num_text);

        tableLayout = findViewById(R.id.country_table_layout);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24px);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //mChart = findViewById(R.id.country_chart);

        mpChart = findViewById(R.id.country_mp_chart);
        mpChart.setTouchEnabled(true);

        LocalDate curDate = LocalDate.now();
        LocalDate yesterday = curDate.minusDays(1);
        date = yesterday.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        Log.d(TAG, "onCreate: " + date);

        for (LocalDate i = startDate; !i.equals(curDate); i = i.plusDays(1)) {
            dateList.add(i.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
        }

        intent = this.getIntent();
        countryName = intent.getStringExtra("country");
        Log.d(TAG, "onCreate: CountryName" + countryName);
        toolbar.setTitle(countryName);

        countryDBRef = countryDB.getReference("countries").child(countryName);
        ValueEventListener countryListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    countryTotalDaily = ds.getValue(CountryTotalDaily.class);
                    countryTotalDailyArray.add(countryTotalDaily);
                }
                countryTotalDailyList = new CountryTotalDailyList(countryTotalDailyArray);
                loadChartData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        countryDBRef.addValueEventListener(countryListener);

        countryObject = (Country) intent.getParcelableExtra("countryData");
        if (countryObject.getStateList().size() > 1) {
            loadTableData();
        }

        Log.d(TAG, "onCreate: CountryDetailActivity Created");
    }

    private void loadTableData() {
        createColumns();
        fillTableData(countryObject.getStateList());
    }

    private void createColumns() {
        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        TextView textViewState = new TextView(this);
        textViewState.setText("State/Province");
        textViewState.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewState.setWidth(20);
        textViewState.setPadding(5, 5, 5, 0);
        tableRow.addView(textViewState);

        // Confirmed Column
        TextView textViewConfirmed = new TextView(this);
        textViewConfirmed.setText("Confirmed");
        textViewConfirmed.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewConfirmed.setPadding(5, 5, 5, 0);
        tableRow.addView(textViewConfirmed);

        // Death Column
        TextView textViewDeath = new TextView(this);
        textViewDeath.setText("Death");
        textViewDeath.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewDeath.setPadding(5, 5, 5, 0);
        tableRow.addView(textViewDeath);

        // Recovered Column
        TextView textViewRecovered = new TextView(this);
        textViewRecovered.setText("Recovered");
        textViewRecovered.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewRecovered.setPadding(5, 5, 5, 0);
        tableRow.addView(textViewRecovered);

        // Current Column
        TextView textViewCurrent = new TextView(this);
        textViewCurrent.setText("Current");
        textViewCurrent.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewCurrent.setPadding(5, 5, 5, 0);
        tableRow.addView(textViewCurrent);

        tableLayout.addView(tableRow, new TableLayout.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        // Add Divider
        tableRow = new TableRow(this);
        tableRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));


        // State Column
        textViewState = new TextView(this);
        textViewState.setText("-------------------");
        textViewState.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewState.setPadding(5, 5, 5, 0);
        tableRow.addView(textViewState);

        // Confirmed Column
        textViewConfirmed = new TextView(this);
        textViewConfirmed.setText("-------------");
        textViewConfirmed.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewConfirmed.setPadding(5, 5, 5, 0);
        tableRow.addView(textViewConfirmed);

        // Death Column
        textViewDeath = new TextView(this);
        textViewDeath.setText("-------");
        textViewDeath.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewDeath.setPadding(5, 5, 5, 0);
        tableRow.addView(textViewDeath);

        // Recovered Column
        textViewRecovered = new TextView(this);
        textViewRecovered.setText("------------");
        textViewRecovered.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewRecovered.setPadding(5, 5, 5, 0);
        tableRow.addView(textViewRecovered);

        // Current Column
        textViewCurrent = new TextView(this);
        textViewCurrent.setText("---------");
        textViewCurrent.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewCurrent.setPadding(5, 5, 5, 0);
        tableRow.addView(textViewCurrent);

        tableLayout.addView(tableRow, new TableLayout.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
    }

    private void fillTableData(List<State> stateList) {
        Collections.sort(stateList);
        for (State stateObject: stateList) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            final MaterialButton buttonState = new MaterialButton(this, null, R.attr.borderlessButtonStyle);
            buttonState.setText(stateObject.getState());
            buttonState.setWidth(30);;
            tableRow.addView(buttonState);
            buttonState.setOnClickListener(new View.OnClickListener() {
                @Override
                // Click to country detail page
                public void onClick(View v) {
                    Intent intent = new Intent(CountryDetailActivity.this, StateDetailActivity.class);
                    intent.putExtra("country", countryName);
                    intent.putExtra("state", buttonState.getText());
                    startActivity(intent);
                }
            });

            // Confirmed Column
            TextView textViewConfirmed = new TextView(this);
            textViewConfirmed.setText(String.valueOf(stateObject.getConfirmed()));
            textViewConfirmed.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            textViewConfirmed.setPadding(5, 5, 5, 0);
            tableRow.addView(textViewConfirmed);

            // Death Column
            TextView textViewDeath = new TextView(this);
            textViewDeath.setText(String.valueOf(stateObject.getDeaths()));
            textViewDeath.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            textViewDeath.setPadding(5, 5, 5, 0);
            tableRow.addView(textViewDeath);

            // Recovered Column
            TextView textViewRecovered = new TextView(this);
            textViewRecovered.setText(String.valueOf(stateObject.getRecovered()));
            textViewRecovered.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            textViewRecovered.setPadding(5, 5, 5, 0);
            tableRow.addView(textViewRecovered);

            // Current Column
            TextView textViewCurrent= new TextView(this);
            textViewCurrent.setText(String.valueOf(stateObject.getCurrent()));
            textViewCurrent.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            textViewCurrent.setPadding(5, 5, 5, 0);
            tableRow.addView(textViewCurrent);

            tableLayout.addView(tableRow, new TableLayout.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
        }
    }

    private class ValueTouchListener implements LineChartOnValueSelectListener {
        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            Toast.makeText(getApplication(), "Selected Value: " + (int)(value.getY() * 1000), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() { }
    }

    private void loadChartData() {
        Log.d(TAG, "loadChartData: Start load chart");
        if (countryTotalDailyList != null) {
            final CountryTotalDaily yesterdayWorldTotal = countryTotalDailyList.getCountryTotalDailyOnDate(date);
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    // Set MPChart data
                    int j = 1;
                    for (String d: dateList) {
                        CountryTotalDaily countryTotalOnCertainDateObject = countryTotalDailyList.getCountryTotalDailyOnDate(d);
                        if (countryTotalOnCertainDateObject != null) {
                            mpConfirmedValues.add(new Entry(j, countryTotalOnCertainDateObject.getConfirmed()));
                            mpDeathsValues.add(new Entry(j, countryTotalOnCertainDateObject.getDeaths()));
                            mpRecoveredValues.add(new Entry(j, countryTotalOnCertainDateObject.getRecovered()));
                            mpCurrentValues.add(new Entry(j, countryTotalOnCertainDateObject.getCurrent()));
                            j += 1;
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run: run on UI thread");
                            Log.d(TAG, "run: " + yesterdayWorldTotal.getConfirmed());
                            country_confirmed_num_text.setText(String.valueOf(yesterdayWorldTotal.getConfirmed()));
                            country_death_num_text.setText(String.valueOf(yesterdayWorldTotal.getRecovered()));
                            country_recovered_num_text.setText(String.valueOf(yesterdayWorldTotal.getDeaths()));
                            country_current_num_text.setText(String.valueOf(yesterdayWorldTotal.getCurrent()));

                            // Set MPChart

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
        } else {
            Log.d(TAG, "onStart: onStart Obejct Null");
        }
    }
}
