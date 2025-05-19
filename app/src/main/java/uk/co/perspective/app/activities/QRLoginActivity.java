package uk.co.perspective.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.journeyapps.barcodescanner.camera.CameraSettings;

import androidx.appcompat.app.AppCompatActivity;

import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.perspective.app.MainActivity;
import uk.co.perspective.app.databinding.ActivityQrloginBinding;

import uk.co.perspective.app.R;
import uk.co.perspective.app.entities.Person;
import uk.co.perspective.app.entities.User;
import uk.co.perspective.app.services.EndPoints;
import uk.co.perspective.app.services.RetrofitClientInstance;

public class QRLoginActivity extends AppCompatActivity {

    DecoratedBarcodeView QRCodeScanner;
    Button CancelButton;
    private CaptureManager captureManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_qrlogin);

        QRCodeScanner = findViewById(R.id.barcode_scanner);
        CancelButton = findViewById(R.id.cancel);

        captureManager = new CaptureManager(this, QRCodeScanner);
        captureManager.initializeFromIntent(getIntent(), savedInstanceState);
        captureManager.decode();

        CameraSettings cameraSettings = QRCodeScanner.getBarcodeView().getCameraSettings();
        cameraSettings.setAutoFocusEnabled(true);
        cameraSettings.setFocusMode(CameraSettings.FocusMode.CONTINUOUS);

        QRCodeScanner.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory());
        QRCodeScanner.setStatusText("");
        QRCodeScanner.getViewFinder().setVisibility(View.VISIBLE);
        QRCodeScanner.decodeContinuous(callback);

        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginIntent);

                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        QRCodeScanner.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        QRCodeScanner.pause();
    }

    public void pause(View view) {
        QRCodeScanner.pause();
    }

    public void resume(View view) {
        QRCodeScanner.resume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return QRCodeScanner.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    private BarcodeCallback callback = new BarcodeCallback() {

        @Override
        public void barcodeResult(BarcodeResult result) {

            pause(getCurrentFocus());

            String lastScan = result.getText();
            String lastToken = "";

            if (lastScan.contains("Birdy-Login"))
            {
                //Grab Token

                lastToken = lastScan.substring(lastScan.indexOf("Birdy-Login:") + 12);

                final EndPoints endPoint = RetrofitClientInstance.getRetrofitInstance(getApplicationContext()).create(EndPoints.class);

                Call<User> call = endPoint.getAuthorizationUseToken(lastToken);
                call.enqueue(new Callback<User>() {

                    @Override
                    public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                        if (response.body() != null) {

                            User TheUser = response.body();

                            if (!TheUser.getDisplayName().equals("Not Found")) {

                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt("userID", TheUser.getUserID());
                                editor.putString("user_name", TheUser.getDisplayName());
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

            resume(getCurrentFocus());

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

                        editor.putInt("people_id", ThePerson.getPeopleID());
                        editor.putString("display_name", ThePerson.getDisplayName());

                        editor.apply();

                        Toast.makeText(getBaseContext(),
                                "Signed In",
                                Toast.LENGTH_LONG).show();


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
        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };
}