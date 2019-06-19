package com.santosh.facialrecognition;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListActivity extends AppCompatActivity {


    private static final String URL_DATA = "https://thecodont-tears.000webhostapp.com/fras/frs_student_list.php";
    private static final String URL_POST_ATTENDANCE = "https://thecodont-tears.000webhostapp.com/fras/frs_attendance.php";

    private static RecyclerView recyclerView_attended,recyclerView_absent;
    private RecyclerView.Adapter adapter;
    public static List<ListItem> listItems;

    public List<ListItem> getListItems() {
        return listItems;
    }

    public List<ListItem> listItems_attended = new ArrayList<>();
    public static List<ListItem> listItems_attended2 ;
    public List<ListItem> listItems_absent = new ArrayList<>();
    
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        
        fab = findViewById(R.id.list_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this)
                        .setTitle("POST")
                        .setMessage("Are you sure to post the attendance?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                postAttendance();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                builder.create().show();
            }
        });

        recyclerView_attended = findViewById(R.id.list_attended_list);
        recyclerView_attended.setHasFixedSize(true);
        recyclerView_attended.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_absent = findViewById(R.id.list_absent_list);
        recyclerView_absent.setHasFixedSize(true);
        recyclerView_absent.setLayoutManager(new LinearLayoutManager(this));

        listItems = new ArrayList<>();
        listItems.clear();

        loadRecyclerViewData();

    }

    private void postAttendance() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading data . . .");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_ATTENDANCE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Hiding the progress dialog after all task complete.
                        progressDialog.dismiss();
                        // Showing response message coming from server.
                        Toast.makeText(getApplicationContext(), "Attendance Successfully Posted.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        // Hiding the progress dialog after all task complete.
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams(){
                // Creating Map String Params.
                Map<String, String> params = new HashMap<>();
                // Adding All values to Params.
                params.put("table_name", generateTableName());
                params.put("class_number", generateClassNumber());
                params.put("students_attended", generateStudentAttendance());
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.getCache().clear();//needed as might be called multiple times
        requestQueue.add(stringRequest);

    }

    private String generateClassNumber() {
        return getIntent().getStringExtra("period");
    }

    private String generateTableName() {
        String op = "CSE_4_A_";
        SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy");
        Date date = new Date();
        op += formatter.format(date);
        return op;
    }

    private String generateStudentAttendance() {
        String op = "";
        if (listItems_attended2.isEmpty())
            return op;
        for (ListItem item: listItems_attended2){
            op = op + item.getRoll()+"+1+";
        }

        return op.substring(0, op.length() - 1);
    }

    public void setUpLists(List<ListItem> listItem, Context context) {
        listItems_attended.clear();
        listItems_absent.clear();

        for(ListItem item: listItem){
            if(item.getStatus() == 0)
                listItems_absent.add(item);
            else
                listItems_attended.add(item);

        }
        adapter = new StudentAdapter(listItems_absent,context);
        recyclerView_absent.setAdapter(adapter);
        adapter = new StudentAdapter(listItems_attended,context);
        recyclerView_attended.setAdapter(adapter);

        listItems_attended2 = new ArrayList<>(listItems_attended);
    }

    private void loadRecyclerViewData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading data . . .");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            // JSONObject jsonObject = new JSONObject(response);
                            // JSONArray jsonArray = jsonObject.getJSONArray("datamy");
                            JSONArray jsonArray  = new JSONArray(response);
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject o = jsonArray.getJSONObject(i);
                                ListItem item = new ListItem(
                                        o.getString("student_id"),
                                        o.getString("student_name"),
                                        //todo:change status according to student attendance
                                        0
                                );
                                listItems.add(item);
                                setUpLists(listItems,getApplicationContext());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }
    
    
}
