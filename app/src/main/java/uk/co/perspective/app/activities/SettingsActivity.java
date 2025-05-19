package uk.co.perspective.app.activities;

import androidx.appcompat.app.AlertDialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import uk.co.perspective.app.R;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.models.SpinnerItem;

public class SettingsActivity extends Activity {

    TextInputEditText CompanyName;
    TextInputEditText UserName;
    TextInputEditText DisplayName;
    TextInputEditText EndPointAddress;
    Spinner SyncInterval;

    TextView Version;

    Button ResetAllButton;
    Button PurgeAllButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        String version = "Vega Alpha";
        int verCode = 1;

        CompanyName = findViewById(R.id.company_name);
        UserName = findViewById(R.id.user_name);
        DisplayName = findViewById(R.id.display_name);
        EndPointAddress = findViewById(R.id.endpoint_address);
        SyncInterval = findViewById(R.id.sync_interval);

        Version = findViewById(R.id.version);

        ResetAllButton = findViewById(R.id.btn_reset);
        PurgeAllButton = findViewById(R.id.btn_purge);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        CompanyName.setText(sharedPreferences.getString("company_name", ""));
        UserName.setText(sharedPreferences.getString("user_name", ""));
        DisplayName.setText(sharedPreferences.getString("display_name", ""));
        EndPointAddress.setText(sharedPreferences.getString("endpoint_address", "api.myperspective.cloud"));

        EndPointAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
                prefsEditor.putString("endpoint_address", EndPointAddress.getText().toString());
                prefsEditor.commit();
            }
        });

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
            verCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Version.setText(String.format("Version: %s (%s)",verCode, version));

        //Sync Interval

        final ArrayList<SpinnerItem> intervalSpinnerArray =  new ArrayList<>();

        intervalSpinnerArray.add(new SpinnerItem(30,"Every 30 Minutes"));
        intervalSpinnerArray.add(new SpinnerItem(60,"Every Hour (Default)"));
        intervalSpinnerArray.add(new SpinnerItem(120,"Every 2 Hours"));
        intervalSpinnerArray.add(new SpinnerItem(480,"Every 8 Hours"));
        intervalSpinnerArray.add(new SpinnerItem(1440,"Every 24 Hours"));

        ArrayAdapter<SpinnerItem> intervalAdapter = new ArrayAdapter<SpinnerItem>(this, R.layout.dropdown_list_item, intervalSpinnerArray);
        intervalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SyncInterval.setAdapter(intervalAdapter);

        ResetAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("userID", 0);
                editor.putString("user_name", "");
                editor.putString("display_name", "");
                editor.putString("device_key", "");
                editor.putString("token", "");
                editor.putInt("peopleID", 0);
                editor.putInt("resourceID", 0);
                editor.putString("company_name", "");
                editor.apply();

                Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginIntent);

                finish();
            }
        });

        PurgeAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(SettingsActivity.this)
                        .setTitle("Purge Archives")
                        .setMessage("Are you sure you want to permanently delete all archived records? This will immediately remove all archived data despite the sync")
                        .setIcon(android.R.drawable.ic_delete)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                                        .quoteDao()
                                        .deleteAllArchivedQuotes();

                                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                                        .leadDao()
                                        .deleteAllArchivedLeads();

                                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                                        .opportunityDao()
                                        .deleteAllArchivedOpportunities();

                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

    }
}