package com.santosh.facialrecognition;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class AddStudentFragment extends Fragment {

    private final String PREF_NAME = "FR_SP";
    EditText et1,et2,et3,et4;
    Button bt1,bt2;
    ImageView iv;

    String className = "CSE-3A";
    Bitmap bitmap;
    int flag = 0;

    ProgressDialog progressDialog;
    RequestQueue requestQueue;
    final private String URL = "https://thecodont-tears.000webhostapp.com/fras/frs_student_add.php";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_add_student, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        doJob(view);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            bitmap = (Bitmap) data.getExtras().get("data");
            flag = 1;
            iv.setImageBitmap(bitmap);
        }catch (NullPointerException e){

        }
        iv.setVisibility(View.VISIBLE);
    }

    private void doJob(View view) {
        et1 = view.findViewById(R.id.add_student_id);
        et2 = view.findViewById(R.id.add_student_name);
        et3 = view.findViewById(R.id.add_student_pw1);
        et4 = view.findViewById(R.id.add_student_pw2);
        bt1 = view.findViewById(R.id.add_student_selectimage);
        bt2 = view.findViewById(R.id.add_student_submit);

        iv = view.findViewById(R.id.add_student_imageView);

        requestQueue = Volley.newRequestQueue(getContext());
        progressDialog = new ProgressDialog(getContext());

        iv.setVisibility(View.GONE);

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),531);
            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = "Student ID : "+et1.getText()+"\nStudent Name : "+et2.getText()+"\nStudent Class : "+className;
                if(validateForm()){
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext())
                            .setTitle("Confirm")
                            .setMessage(str)
                            .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    progressDialog.setMessage("Adding new student details...");
                                    progressDialog.show();
                                    uploadToDatabase();
                                }
                            })
                            .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                    builder.create().show();
                }
            }
        });


    }

    private void uploadToDatabase() {
        // Creating string request with post method.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        // Hiding the progress dialog after all task complete.
                        progressDialog.dismiss();
                        // Showing response message coming from server.
                        Toast.makeText(getContext(), "Student Added Successfully", Toast.LENGTH_LONG).show();
                        resetFields();
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
                params.put("student_id", String.valueOf(et1.getText()));
                params.put("student_name", String.valueOf(et2.getText()));
                params.put("student_class", className);
                params.put("student_image", imageToString(bitmap));
                params.put("student_password", String.valueOf(et4.getText()));

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        progressDialog.setMessage("Uploading profile image...");
        requestQueue.add(stringRequest);
    }

    private void resetFields() {
        et1.setText("");
        et2.setText("");
        et3.setText("");
        et4.setText("");
        iv.setVisibility(View.GONE);
    }

    private boolean validateForm() {
        if (TextUtils.isEmpty(et1.getText())){
            et1.setError("Enter Student RollNumber");
            return false;
        }
        if (TextUtils.isEmpty(et2.getText())){
            et2.setError("Enter Student Name");
            return false;
        }
        if (TextUtils.isEmpty(et3.getText())){
            et3.setError("Enter Student Password");
            return false;
        }
        if (TextUtils.isEmpty(et4.getText())){
            et4.setError("Enter Student Password");
            return false;
        }
//        if(et4.getText().length() < 8){
//            et4.setError("Password should be minimum of 8 characters");
//            return false;
//        }
        if (!TextUtils.equals(et3.getText(),et4.getText())){
            et4.setError("Password did not match");
            return false;
        }
        if(flag == 0){
            bt1.setError("Student Image is mandatory");
            return false;
        }
        return true;
    }

    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
        byte [] imgBytes = stream.toByteArray();
        return Base64.encodeToString(imgBytes,Base64.DEFAULT);
    }
}