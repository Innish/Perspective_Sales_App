package uk.co.perspective.app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.facebook.stetho.Stetho;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.perspective.app.MainActivity;
import uk.co.perspective.app.R;
import uk.co.perspective.app.entities.Person;
import uk.co.perspective.app.entities.User;
import uk.co.perspective.app.services.EndPoints;
import uk.co.perspective.app.services.RetrofitClientInstance;

public class LoginActivity extends AppCompatActivity {

    EditText UserEmail;
    EditText UserPassword;
    AppCompatButton LoginButton;
    AppCompatButton CreateAccountButton;
    AppCompatButton ResetPasswordButton;
    LinearLayout LoginDetails;
    ImageView SplashLogo;
    ImageView SettingsButton;
    Button QRLoginButton;

    private int TapCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Stetho.initializeWithDefaults(this);

        LoginButton = findViewById(R.id.btn_login);
        UserEmail = findViewById(R.id.input_email);
        UserPassword = findViewById(R.id.input_password);
        CreateAccountButton = findViewById(R.id.btn_signup);
        ResetPasswordButton = findViewById(R.id.btn_forgot);
        LoginDetails = findViewById(R.id.linearLayout);
        SplashLogo = findViewById(R.id.splash);
        QRLoginButton = findViewById(R.id.qrLogin);
        SettingsButton = findViewById(R.id.logo);
        LoginDetails.setVisibility(View.GONE);
        QRLoginButton.setVisibility(View.GONE);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EndPoints endPoint = RetrofitClientInstance.getRetrofitInstance(getApplicationContext()).create(EndPoints.class);

                Call<User> call = endPoint.getAuthorizationUsername(UserEmail.getText().toString(), UserPassword.getText().toString());
                call.enqueue(new Callback<User>() {

                    @Override
                    public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                        if (response.body() != null) {

                            User TheUser = response.body();

                            if (!TheUser.getDisplayName().equals("Not Found")) {
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt("userID", TheUser.getUserID());
                                editor.putString("user_name", UserEmail.getText().toString());
                                editor.putString("display_name", TheUser.getDisplayName());
                                editor.putString("device_key", "");
                                editor.putString("token", TheUser.getToken());
                                editor.putInt("peopleID", 0);
                                editor.putInt("resourceID", 0);
                                editor.putString("company_name", TheUser.getCompanyName());
                                editor.apply();

                                GetPerson(TheUser.getUserID());
                            }
                            else
                            {
                                Toast.makeText(getBaseContext(),
                                        "Sign-in failed. Email address not found!",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(getBaseContext(),
                                    "Sign-in failed...",
                                    Toast.LENGTH_LONG).show();

                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {

                        Toast.makeText(getBaseContext(),
                                "Something went wrong...",
                                Toast.LENGTH_LONG).show();
                    }
                });


            }
        });

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://onboarding.mybirdy.co.uk/"));
                startActivity(browserIntent);
            }
        });

        ResetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://app.mybirdy.co.uk/ResetPassword.aspx"));
                //startActivity(browserIntent);
            }
        });

        SettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TapCount++;

                if (TapCount > 3) {
                    TapCount = 0;
                    Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivity(intent);
                }
            }
        });

        QRLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (hasCameraPermission()) {
                    Intent intent = new Intent(getApplicationContext(), QRLoginActivity.class);
                    startActivity(intent);

                    finish();
                } else {
                    requestPermission();
                }
            }
        });

        //Objects.requireNonNull(getSupportActionBar()).hide();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Animation animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);

                animFadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        SplashLogo.setVisibility(View.GONE);
                        LoginDetails.setVisibility(View.VISIBLE);
                        QRLoginButton.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                SplashLogo.startAnimation(animFadeOut);
            }
        }, 1000);
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                getApplicationContext(),
                android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

    private void GetPerson(Integer UserID)
    {
        final EndPoints service = RetrofitClientInstance.getRetrofitInstance(getApplicationContext()).create(EndPoints.class);

        Call<Person> call = service.getPerson(UserID);
        call.enqueue(new Callback<Person>() {

            @Override
            public void onResponse(@NotNull Call<Person> call, @NotNull Response<Person> response) {
                if (response.body() != null) {

                    Person ThePerson = response.body();

                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putInt("peopleID", ThePerson.getPeopleID());
                    editor.putInt("resourceID", ThePerson.getResourceID());

                    editor.apply();

                    Toast.makeText(getBaseContext(),
                            "Signed In",
                            Toast.LENGTH_LONG).show();


                    //Temp
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                    finish();
                }
                else
                {
                    Toast.makeText(getBaseContext(),
                            "Sign-in failed...",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<Person> call, @NotNull Throwable t) {
                Toast.makeText(getBaseContext(),
                        "Sign-in failed...",
                        Toast.LENGTH_LONG).show();
            }
        });

    }
}