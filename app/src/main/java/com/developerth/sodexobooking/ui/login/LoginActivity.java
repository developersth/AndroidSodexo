package com.developerth.sodexobooking.ui.login;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.developerth.sodexobooking.MainActivity;
import com.developerth.sodexobooking.R;
import com.developerth.sodexobooking.data.model.LoginRequest;
import com.developerth.sodexobooking.data.model.LoginResponse;
import com.developerth.sodexobooking.services.ApiClient;
import com.google.firebase.iid.FirebaseInstanceId;

import java.security.KeyStore;
import java.util.concurrent.Executor;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL;


public class LoginActivity extends AppCompatActivity {

    private static final String MY_PREFS = "my_prefs";
    private FingerprintManager mFingerprintManager;
    private KeyguardManager mKeyguardManager;
    //Alias for our key in the Android Key Store
    private static final String KEY_NAME = "key_name";
    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private static  Cipher cipher;
    private Executor executor;
    private static BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final TextView loginButton = findViewById(R.id.btnLogin);
        ImageView biometric_login = (ImageView) findViewById(R.id.biometric_login);
        final SharedPreferences sharedPref = getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                boolean vCheck =sharedPref.getBoolean("is_login",false);
                if (vCheck){
                    biometric_login.setVisibility(View.VISIBLE);
                }else{
                    biometric_login.setVisibility(View.GONE);
                }
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("MY_APP_TAG", "No biometric features available on this device.");
                biometric_login.setVisibility(View.GONE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                biometric_login.setVisibility(View.GONE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                biometric_login.setVisibility(View.GONE);
                break;
        }
             executor = ContextCompat.getMainExecutor(this);
            final BiometricPrompt biometricPrompt = new BiometricPrompt(LoginActivity.this,
                    executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode,
                                                  @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    Toast.makeText(getApplicationContext(),
                            "Authentication error: " + errString, Toast.LENGTH_SHORT)
                            .show();
                }

                @Override
                public void onAuthenticationSucceeded(
                        @NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    String username =sharedPref.getString("username","");
                    String password =sharedPref.getString("password","");
                    login(username,password);
                    //Toast.makeText(getApplicationContext(), "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Login")
                            .setContentText("failed!")
                            .show();
                    Toast.makeText(getApplicationContext(), "Authentication failed",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });

            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("เข้าใช้งานระบบ")
                    .setSubtitle("เข้าสู่ระบบใช้ลายน้ำมือ")
                    .setNegativeButtonText("ยกเลิก")
                    .build();

            // Prompt appears when user clicks "Log in".
            // Consider integrating with the keystore to unlock cryptographic operations,
            // if needed by your app.

        biometric_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    biometricPrompt.authenticate(promptInfo);
                }
            });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loadingProgressBar.setVisibility(View.VISIBLE);
                login(usernameEditText.getText().toString(),passwordEditText.getText().toString());
            }
        });

        Button btnGetToken = (Button) findViewById(R.id.btn_get_token);
        btnGetToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastToken();
            }
        });
    }
    public void toastToken(){
        String token = FirebaseInstanceId.getInstance().getToken();

        Toast.makeText(LoginActivity.this,
                "TOKEN = "+token,
                Toast.LENGTH_LONG).show();
        Log.d("TOKEN = ",""+token);
    }
    private boolean checkValidate(final String username, final String password){
        if (username.isEmpty()){
            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Warning")
                    .setContentText(getString(R.string.invalid_username))
                    .show();
            return  false;
        }
        if (password.isEmpty()){
            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Warning")
                    .setContentText(getString(R.string.invalid_password))
                    .show();
            return  false;
        }
        return  true;
    }
    public void login(final String username, final String password) {
        //check validate
        if (!checkValidate(username,password)){
            return;
        }
        final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
        final LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);
        Call<LoginResponse> loginResponseCall = ApiClient.getService().userLogin(loginRequest);
        loginResponseCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                final LoginResponse loginResponse = response.body();

                if (response.isSuccessful()) {
                    if(loginResponse.getSuccess()){
                        pDialog.cancel();
                        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("User Login")
                                .setContentText(loginResponse.getMessage())
                                .show();
                        String token = loginResponse.getToken();
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                        Toast.makeText(LoginActivity.this, "Token:"+token, Toast.LENGTH_LONG).show();
                        SharedPreferences sharedPref = getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("is_login", true);
                        editor.putString("token", loginResponse.getToken());
                        editor.putInt("user_id",loginResponse.getUser_id());
                        editor.putString("name", loginResponse.getName());
                        editor.putString("email", loginResponse.getEmail());
                        editor.putString("username", username);
                        editor.putString("password", password);
                        editor.putString("email", loginResponse.getEmail());
                        editor.putInt("role_id",loginResponse.getRole_id());
                        editor.putString("role_name", loginResponse.getRole_name());
                        editor.putString("role_type", loginResponse.getRole_type());
                        editor.commit();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        //Complete and destroy login activity once successful
                        finish();
                    }else{
                        pDialog.cancel();
                        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText(loginResponse.getMessage())
                                .show();
                        Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }

                } else {
                    pDialog.cancel();
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Login Failed")
                            .show();
                    Toast.makeText(LoginActivity.this, "Login Failed:", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                pDialog.cancel();
                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText(getString(R.string.error_internet))
                        .show();
                //Toast.makeText(LoginActivity.this, "Throwable " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }


    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}