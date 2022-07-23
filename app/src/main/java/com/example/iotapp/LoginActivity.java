package com.example.iotapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iotapp.api.ApiService;
import com.example.iotapp.models.Account;
import com.example.iotapp.models.LoginResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        editTextEmail = findViewById(R.id.edit_txt_email);
        editTextPassword = findViewById(R.id.edit_txt_password);
        buttonLogin = findViewById(R.id.btn_login);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                Account account = new Account(email, password);
                Log.d("Acc", account.getEmail() + ' ' + account.getPassword());
                callLoginApi(account);
            }
        });

    }

    private void callLoginApi(Account account) {
        ApiService.apiService.login(account).enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                if (response.code() == 200) {
                    LoginResult loginResult = response.body();
                    Bundle bundle = new Bundle();
                    bundle.putString("name", loginResult.getUser().getName());
                    bundle.putString("api_token", loginResult.getToken());
                    bundle.putString("home_id", loginResult.getHomeId());
                    bundle.putString("broker_password", loginResult.getBrokerPassword());
                    bundle.putString("broker_host", loginResult.getBrokerHost());
                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);

                } else if (response.code() == 400) {
                    Toast.makeText(LoginActivity.this, "Please enter valid email and password!", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 401) {
                    Toast.makeText(LoginActivity.this, "Login failed: Email or password is incorrect", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Login Failed: Status code " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Login Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}