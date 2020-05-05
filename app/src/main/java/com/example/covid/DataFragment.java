package com.example.covid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.covid.database.Country;
import com.example.covid.database.DetailData;
import com.example.covid.database.DetailDataList;
import com.google.android.material.button.MaterialButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class DataFragment extends Fragment {
    private static final String TAG = "DataFragment";
    private LocalReceiver mReceiver;
    private DetailDataList detailDataListObject;
    private List<Country> countryObjectList;

    LocalBroadcastManager broadcastManager;
    private TableLayout tableLayout;

    private String date;
    LocalDate startDate = LocalDate.of(2020, 1, 22);
    private ArrayList<String> dateList = new ArrayList<>();

    public DataFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Get the date string of yesterday
        LocalDate curDate = LocalDate.now();
        LocalDate yesterday = curDate.minusDays(1);
        date = yesterday.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));

        for (LocalDate i = startDate; !i.equals(curDate); i = i.plusDays(1)) {
            dateList.add(i.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_data, container, false);
        tableLayout = (TableLayout) view.findViewById(R.id.table_layout);
        Log.d(TAG, "onCreateView: " + (tableLayout == null));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            // Receive the broadcast
            IntentFilter filter = new IntentFilter();
            filter.addAction("to_fragment");
            mReceiver = new DataFragment.LocalReceiver();
            broadcastManager = LocalBroadcastManager.getInstance(getActivity());
            broadcastManager.registerReceiver(mReceiver, filter);
            Log.d(TAG, "onCreateView: Broadcast");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        broadcastManager.unregisterReceiver(mReceiver);
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<DetailData> detailData = intent.getParcelableArrayListExtra("Data");
            Log.d(TAG, "onReceive: Received the broadcast");
            detailDataListObject = new DetailDataList(detailData);
            // Get yesterday's all country's data
            countryObjectList = detailDataListObject.getCountryList(date);
            // Load table data
            loadData();
        }
    }

    private void loadData() {
        createColumns();
        fillData(countryObjectList);
    }

    //Create table columns
    private void createColumns() {
        TableRow tableRow = new TableRow(getView().getContext());
        tableRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        TextView textViewCountry = new TextView(getView().getContext());
        textViewCountry.setText("Country/Region");
        textViewCountry.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewCountry.setPadding(5, 5, 5, 0);
        tableRow.addView(textViewCountry);

        // Confirmed Column
        TextView textViewConfirmed = new TextView(getView().getContext());
        textViewConfirmed.setText("Confirmed");
        textViewConfirmed.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewConfirmed.setPadding(5, 5, 5, 0);
        tableRow.addView(textViewConfirmed);

        // Death Column
        TextView textViewDeath = new TextView(getView().getContext());
        textViewDeath.setText("Death");
        textViewDeath.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewDeath.setPadding(5, 5, 5, 0);
        tableRow.addView(textViewDeath);

        // Recovered Column
        TextView textViewRecovered = new TextView(getView().getContext());
        textViewRecovered.setText("Recovered");
        textViewRecovered.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewRecovered.setPadding(5, 5, 5, 0);
        tableRow.addView(textViewRecovered);

        // Current Column
        TextView textViewCurrent = new TextView(getView().getContext());
        textViewCurrent.setText("Current");
        textViewCurrent.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewCurrent.setPadding(5, 5, 5, 0);
        tableRow.addView(textViewCurrent);

        tableLayout.addView(tableRow, new TableLayout.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        // Add Divider
        tableRow = new TableRow(getView().getContext());
        tableRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        // Country Column
        textViewCountry = new TextView(getView().getContext());
        textViewCountry.setText("-------------------");
        textViewCountry.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewCountry.setPadding(5, 5, 5, 0);
        tableRow.addView(textViewCountry);

        // Confirmed Column
        textViewConfirmed = new TextView(getView().getContext());
        textViewConfirmed.setText("-------------");
        textViewConfirmed.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewConfirmed.setPadding(5, 5, 5, 0);
        tableRow.addView(textViewConfirmed);

        // Death Column
        textViewDeath = new TextView(getView().getContext());
        textViewDeath.setText("-------");
        textViewDeath.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewDeath.setPadding(5, 5, 5, 0);
        tableRow.addView(textViewDeath);

        // Recovered Column
        textViewRecovered = new TextView(getView().getContext());
        textViewRecovered.setText("------------");
        textViewRecovered.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewRecovered.setPadding(5, 5, 5, 0);
        tableRow.addView(textViewRecovered);

        // Current Column
        textViewCurrent = new TextView(getView().getContext());
        textViewCurrent.setText("---------");
        textViewCurrent.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewCurrent.setPadding(5, 5, 5, 0);
        tableRow.addView(textViewCurrent);

        tableLayout.addView(tableRow, new TableLayout.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
    }

    //Filling table data
    private void fillData(List<Country> countryObjectList) {
        for (final Country countryObject: countryObjectList) {
            TableRow tableRow = new TableRow(getView().getContext());
            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            final MaterialButton buttonCountry = new MaterialButton(getContext(), null, R.attr.borderlessButtonStyle);
            buttonCountry.setText(countryObject.getCountry());
            buttonCountry.setWidth(30);;
            tableRow.addView(buttonCountry);
            buttonCountry.setOnClickListener(new View.OnClickListener() {
                @Override
                // Click to country detail page
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), CountryDetailActivity.class);
                    intent.putExtra("countryData", countryObject);
                    intent.putExtra("country", buttonCountry.getText());
                    getActivity().startActivity(intent);
                }
            });

            // Confirmed Column
            TextView textViewConfirmed = new TextView(getView().getContext());
            textViewConfirmed.setText(String.valueOf(countryObject.getConfirmed()));
            textViewConfirmed.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            textViewConfirmed.setPadding(5, 5, 5, 0);
            tableRow.addView(textViewConfirmed);

            // Death Column
            TextView textViewDeath = new TextView(getView().getContext());
            textViewDeath.setText(String.valueOf(countryObject.getDeaths()));
            textViewDeath.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            textViewDeath.setPadding(5, 5, 5, 0);
            tableRow.addView(textViewDeath);

            // Recovered Column
            TextView textViewRecovered = new TextView(getView().getContext());
            textViewRecovered.setText(String.valueOf(countryObject.getRecovered()));
            textViewRecovered.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            textViewRecovered.setPadding(5, 5, 5, 0);
            tableRow.addView(textViewRecovered);

            // Current Column
            TextView textViewCurrent= new TextView(getView().getContext());
            textViewCurrent.setText(String.valueOf(countryObject.getCurrent()));
            textViewCurrent.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            textViewCurrent.setPadding(5, 5, 5, 0);
            tableRow.addView(textViewCurrent);

            tableLayout.addView(tableRow, new TableLayout.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
        }
    }

}
