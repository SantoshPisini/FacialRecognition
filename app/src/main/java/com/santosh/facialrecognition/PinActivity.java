package com.santosh.facialrecognition;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class PinActivity extends AppCompatActivity {

    TextInputEditText et;
    Button button_login;

    TextInputLayout til;

    private final String PREF_NAME = "FR_SP";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        et = findViewById(R.id.pin_pin);
        til = findViewById(R.id.pin_til);
        Objects.requireNonNull(getSupportActionBar()).hide();

        //todo:for testing purpose
        startActivity(new Intent(PinActivity.this, MainActivity.class));


        button_login = findViewById(R.id.pin_submit);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validatePin() && checkPermission()) {
                    startActivity(new Intent(PinActivity.this, MainActivity.class));
                    finish();
                }
            }
        });
    }

    private boolean validatePin() {
        if (TextUtils.isEmpty(et.getText())) {
            et.setError("PIN is mandatory");
            return false;
        }
        SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String sp_pin = sp.getString("pin", null);
        if (sp_pin == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        if (!TextUtils.equals(sp_pin, et.getText())) {
            Toast.makeText(this, "Incorrect PIN !", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }

    private boolean checkPermission() {
        return true;
    }
}
