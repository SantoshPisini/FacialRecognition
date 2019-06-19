package com.santosh.facialrecognition;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StudentProfileActivity extends AppCompatActivity {

    private static final String URL_DATA = "https://thecodont-tears.000webhostapp.com/fras/frs_student_profile.php";
    TextView tv1,tv2,tv3,tv5,tv6;
    ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        final String id = getIntent().getStringExtra("id");
        tv1  = findViewById(R.id.student_id);
        tv2  = findViewById(R.id.student_name);
        tv3  = findViewById(R.id.student_class);
        tv6  = findViewById(R.id.student_att_tv);
        tv5  = findViewById(R.id.student_att_percent);
        iv = findViewById(R.id.student_iv);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading data . . .");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Hiding the progress dialog after all task complete.
                        progressDialog.dismiss();
                        // Showing response message coming from server.
                        try {
                            JSONArray jsonArray  = new JSONArray(response);
                            //here length is always one
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject o = jsonArray.getJSONObject(i);
                                tv1.setText(o.getString("student_id"));
                                tv2.setText(o.getString("student_name"));
                                tv3.setText(o.getString("student_class"));
                                tv6.setText("Attended "+o.getString("attended")+" of "+o.getString("total")+" classes.");
                                int per =( Integer.parseInt(o.getString("attended"))*100 )/ Integer.parseInt(o.getString("total"));
                                if (per > 75){
                                    tv5.setTextColor(getResources().getColor(R.color.green));
                                }else if(per > 65){
                                    tv5.setTextColor(getResources().getColor(R.color.yellow));
                                }else {
                                    tv5.setTextColor(getResources().getColor(R.color.red));
                                }
                                tv5.setText((Math.round(per * 100.0) / 100.0)+"%");
                                Picasso.with(getApplicationContext()).load(o.getString("student_image")).into(iv);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                params.put("id", id);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.getCache().clear();//needed as might be called multiple times
        requestQueue.add(stringRequest);
    }
}
