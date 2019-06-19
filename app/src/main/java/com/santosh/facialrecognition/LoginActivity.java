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

import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputEditText et1, et2, et3, et4;
    Button button_login, button_pin;

    HashMap<String, String> credentials;

    TextInputLayout til1, til2, til3, til4;

    private final String PREF_NAME = "FR_SP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        credentials = new HashMap<>();
        credentials.put("santoshsrisai36@gmail.com", "qwerty");
        credentials.put("test@gmail.com", "12345");

        autoLogin();

        Objects.requireNonNull(getSupportActionBar()).hide();

        et1 = findViewById(R.id.login_email);
        et2 = findViewById(R.id.login_password);
        et3 = findViewById(R.id.login_pin);
        et4 = findViewById(R.id.login_pin_reenter);

        til1 = findViewById(R.id.login_til1);
        til2 = findViewById(R.id.login_til2);
        til3 = findViewById(R.id.login_til3);
        til4 = findViewById(R.id.login_til4);

        button_login = findViewById(R.id.login_submit);
        button_pin = findViewById(R.id.login_pin_submit);
        button_login.setOnClickListener(this);
        button_pin.setOnClickListener(this);

        setLoginVisibility(View.VISIBLE);
        setPinVisibility(View.INVISIBLE);
    }

    private void autoLogin() {
        SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String sp_email = sp.getString("email", null);
        String sp_pass = sp.getString("password", null);
        String sp_pin = sp.getString("pin", null);
        if (sp_email == null || sp_pass == null || sp_pin == null) {
            return;
        }
        startActivity(new Intent(this, PinActivity.class));
        this.finish();
    }

    private void setPinVisibility(int visibility) {
        et3.setVisibility(visibility);
        et4.setVisibility(visibility);
        button_pin.setVisibility(visibility);
        til3.setVisibility(visibility);
        til4.setVisibility(visibility);
    }

    private void setLoginVisibility(int visibility) {
        et1.setVisibility(visibility);
        et2.setVisibility(visibility);
        button_login.setVisibility(visibility);
        til1.setVisibility(visibility);
        til2.setVisibility(visibility);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_submit:
                if (verifyCredentials(0)) {
                    String email = et1.getText().toString();
                    if (credentials.containsKey(email)) {
                        if (credentials.get(email).equals(et2.getText().toString())) {
                            setLoginVisibility(View.INVISIBLE);
                            setPinVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(this, "Credentials mismatch.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Credentials mismatch.", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            case R.id.login_pin_submit:
                if (verifyCredentials(1)) {
                    SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                    sp.edit().putString("email", et1.getText().toString()).apply();
                    sp.edit().putString("password", et2.getText().toString()).apply();
                    sp.edit().putString("pin", et3.getText().toString()).apply();
                    startActivity(new Intent(this, PinActivity.class));
                    this.finish();
                }
                break;
        }
    }

    private boolean verifyCredentials(int id) {
        if (id == 0) {
            if (TextUtils.isEmpty(et1.getText())) {
                et1.setError("Email is mandatory");
                return false;
            }
            if (TextUtils.isEmpty(et2.getText())) {
                et2.setError("Password is mandatory");
                return false;
            }
            if (!isValid(et1.getText().toString())) {
                et1.setError("Invalid Email");
                return false;
            }
            return true;
        } else {
            if (TextUtils.isEmpty(et3.getText())) {
                et3.setError("PIN is mandatory");
                return false;
            }
            if (TextUtils.isEmpty(et4.getText())) {
                et4.setError("PIN is mandatory");
                return false;
            }
            if (et3.getText().length() < 4) {
                et4.setError("PIN should be minimum of 4 numbers");
                return false;
            }
            if (!TextUtils.equals(et3.getText(), et4.getText())) {
                et4.setError("PIN doesn't match");
                return false;
            }
            return true;
        }
    }

    public static boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
}
