package com.santosh.facialrecognition;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.vision.L;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceReportFragment extends Fragment {

    private final String PREF_NAME = "FR_SP";
    private final String URL_DATA = "https://thecodont-tears.000webhostapp.com/fras/frs_student_count.php";
    private LineChart lineChart;
    String[] weekdays = new String[9];
    int [] vals = new int[9];

    private ProgressDialog progressDialog;

    private String table_name;

    HashMap<String,Integer> hashMapVals;

    TextView tv;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_attendance_report, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        doJob(view);
    }

    private void doJob(View view) {
        hashMapVals = new HashMap<>();
        tv = view.findViewById(R.id.temp_holder);

        lineChart = view.findViewById(R.id.att_report_chart1);
        lineChart.setDrawGridBackground(true);
        lineChart.setDrawBorders(true);
        lineChart.getDescription().setEnabled(false);
        lineChart.setPinchZoom(true);
        Legend legend = lineChart.getLegend();
        legend.setEnabled(false);

        YAxis la = lineChart.getAxisLeft();
        la.setAxisMaximum(10);
        la.setAxisMinimum(0);

        lineChart.getAxisRight().setDrawLabels(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.setScaleEnabled(false);
        lineChart.animateXY(1000, 1000);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading data . . .");
        progressDialog.show();

        getData(9);
        setData(9);

        Button button = view.findViewById(R.id.att_report_save_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission())
                    lineChart.saveToGallery(table_name);
                Toast.makeText(getContext(), "Saved !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 531) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
        }
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    531);
            return false;
        }
        return true;
    }

    private void setData(int i1) {
        ArrayList<Entry> yVals = new ArrayList<>();
        yVals.add(new Entry(0, 5));
        for (int i = 1; i < i1 - 1; i++) {
            yVals.add(new Entry(i, vals[i]));
        }
        yVals.add(new Entry(i1 - 1, 5));
        LineDataSet set1;

        set1 = new LineDataSet(yVals, "weekly");
        set1.setColor(Color.GRAY);
        set1.setDrawCircles(true);
        set1.setCircleColor(Color.RED);
        set1.setLineWidth(3f);

        LineData data = new LineData(set1);
        data.setDrawValues(true);

        lineChart.setData(data);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setLabelRotationAngle(-45f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(weekdays));
    }

    private void getData(int i1) {
        weekdays[0] = "";
        for (int i = 1; i < i1 - 1; i++) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -(7 - i));
            Date todate = cal.getTime();
            weekdays[i] = sdf.format(todate);
            table_name = "CSE_4_A_" + sdf.format(todate);
            vals[i] = uploadToDatabase(table_name);
        }
        weekdays[i1 - 1] = "";
    }

    private int uploadToDatabase(final String table_name) {
        // Hiding the progress dialog after all task complete.
        progressDialog.dismiss();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        // Showing response message coming from server.
                        try {
                            JSONArray jsonArray = new JSONArray(ServerResponse);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject o = jsonArray.getJSONObject(i);
                                //t.name = o.getString("table_name");
                                tv.setText(o.getString("attended"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }catch (NumberFormatException e){
                            // if sunday or holiday table will be null so number of students is zero
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        // Showing error message if something goes wrong.
//                        Toast.makeText(getActivity().getApplicationContext(), volleyError.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params;
                // Creating Map String Params.
                params = new HashMap<>();
                // Adding All values to Params.
                params.put("table_name", table_name);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
        requestQueue.getCache().clear();
        return 0;
    }

}