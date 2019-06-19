package com.santosh.facialrecognition;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class ReportFragment extends Fragment {

    private TextInputEditText et_title,et_desc;
    private Button bt_submit;

    private ProgressDialog progressDialog;
    final private String URL = "https://thecodont-tears.000webhostapp.com/fras/fras_feedback.php";
    private final String PREF_NAME = "FR_SP";
    private String title,desc,sp_email;

    private TextView tv1,tv2;
    private GifImageView iv;
    private TextInputLayout tet1,tet2;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_report, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        doJob(view);
    }

    private void doJob(View view) {
        et_title = view.findViewById(R.id.report_title);
        et_desc = view.findViewById(R.id.report_desc);
        bt_submit = view.findViewById(R.id.report_submit);

        tv1 = view.findViewById(R.id.report_thanks);
        tv2 = view.findViewById(R.id.report_thanks_desc);
        iv = view.findViewById(R.id.report_gif);
        tet1 = view.findViewById(R.id.textInputLayout_report_1);
        tet2 = view.findViewById(R.id.textInputLayout_report_2);

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        progressDialog = new ProgressDialog(getContext());

        SharedPreferences sp = getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sp_email = sp.getString("email", null);

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    progressDialog.setMessage("Please Wait, Your Report Is Being Submitted");
                    progressDialog.show();
                    doSubmit();
                }
            }
        });
    }

    private void doSubmit() {

        // Creating string request with post method.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        // Hiding the progress dialog after all task complete.
                        progressDialog.dismiss();
                        // Showing response message coming from server.
                        et_title.setVisibility(View.INVISIBLE);
                        et_desc.setVisibility(View.INVISIBLE);
                        bt_submit.setVisibility(View.INVISIBLE);
                        tet1.setVisibility(View.INVISIBLE);
                        tet2.setVisibility(View.INVISIBLE);
                        iv.setVisibility(View.VISIBLE);
                        tv2.setVisibility(View.VISIBLE);
                        tv1.setVisibility(View.VISIBLE);
                        //Toast.makeText(getContext(), "Report Successfully Submitted.", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        // Hiding the progress dialog after all task complete.
                        progressDialog.dismiss();
                        // Showing error message if something goes wrong.
                        Toast.makeText(getContext(), volleyError.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Creating Map String Params.
                Map<String, String> params = new HashMap<>();

                // Adding All values to Params.
                params.put("report_timestamp", getTime());
                params.put("report_user_email",sp_email);
                params.put("report_user_title", title);
                params.put("report_user_description", desc);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return sdf.format(new Date());
    }

    private boolean validate() {
        title = et_title.getText().toString();
        desc = et_desc.getText().toString();
        if(TextUtils.isEmpty(title)){
            et_title.setError("Can not be empty.");
            return false;
        }
        if(TextUtils.isEmpty(desc)){
            et_desc.setError("Can not be empty.");
            return false;
        }
        if (title.length() > 250){
            et_title.setError("No more than 250 characters.");
            return false;
        }
        if (desc.length() > 5000){
            et_desc.setError("No more than 5000 characters.");
            return false;
        }
        return true;
    }
}