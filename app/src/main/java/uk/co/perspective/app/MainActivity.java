package uk.co.perspective.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import uk.co.perspective.app.activities.LoginActivity;
import uk.co.perspective.app.activities.SettingsActivity;
import uk.co.perspective.app.dialogs.NewCustomerDialog;
import uk.co.perspective.app.dialogs.NewLeadDialog;
import uk.co.perspective.app.dialogs.NewOpportunityDialog;
import uk.co.perspective.app.dialogs.NewProjectDialog;
import uk.co.perspective.app.dialogs.NewQuoteDialog;
import uk.co.perspective.app.dialogs.NewTaskDialog;
import uk.co.perspective.app.services.SyncService;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fab, fab1, fab2, fab3, fab4, fab5, fab6;
    LinearLayout fabLayout1, fabLayout2, fabLayout3, fabLayout4, fabLayout5, fabLayout6;
    View fabBGLayout;

    private static final String CHANNEL_ID = "MBRDY7242119782912";

    boolean isFABOpen=false;

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (sharedPreferences.getString("token", "").equals(""))
        {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            this.startActivity(loginIntent);

            this.finish();
        }
        else
        {
            if(savedInstanceState == null)
            {
                createNotificationChannel();

                //Start a new thread

                Thread runner = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        StartSyncService();
                    }
                });

                runner.setName("Auto Sync");
                runner.start();
            }
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fabLayout1= findViewById(R.id.fabLayout1);
        fabLayout2= findViewById(R.id.fabLayout2);
        fabLayout3= findViewById(R.id.fabLayout3);
        fabLayout4= findViewById(R.id.fabLayout4);
        fabLayout5= findViewById(R.id.fabLayout5);
        fabLayout6= findViewById(R.id.fabLayout6);

        fab = findViewById(R.id.fab);

        fab1 = findViewById(R.id.fab1);
        fab2 = findViewById(R.id.fab2);
        fab3 = findViewById(R.id.fab3);
        fab4 = findViewById(R.id.fab4);
        fab5 = findViewById(R.id.fab5);
        fab6 = findViewById(R.id.fab6);

        fabBGLayout=findViewById(R.id.fabBGLayout);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }
            }

        });

        fabBGLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
            }
        });

        fabLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NewTaskDialog newDialog = new NewTaskDialog();
                newDialog.show(getSupportFragmentManager(), "New Task");

                closeFABMenu();
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NewTaskDialog newDialog = new NewTaskDialog();
                newDialog.show(getSupportFragmentManager(), "New Task");

                closeFABMenu();
            }
        });

        fabLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NewProjectDialog newDialog = new NewProjectDialog();
                newDialog.show(getSupportFragmentManager(), "New Project");

                closeFABMenu();
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NewProjectDialog newDialog = new NewProjectDialog();
                newDialog.show(getSupportFragmentManager(), "New Project");

                closeFABMenu();
            }
        });

        fabLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NewQuoteDialog newDialog = new NewQuoteDialog();
                newDialog.show(getSupportFragmentManager(), "New Quote");

                closeFABMenu();
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NewQuoteDialog newDialog = new NewQuoteDialog();
                newDialog.show(getSupportFragmentManager(), "New Quote");

                closeFABMenu();
            }
        });

        fabLayout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NewOpportunityDialog newDialog = new NewOpportunityDialog();
                newDialog.show(getSupportFragmentManager(), "New Opportunity");

                closeFABMenu();
            }
        });

        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NewOpportunityDialog newDialog = new NewOpportunityDialog();
                newDialog.show(getSupportFragmentManager(), "New Opportunity");

                closeFABMenu();
            }
        });

        fabLayout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NewLeadDialog newDialog = new NewLeadDialog();
                newDialog.show(getSupportFragmentManager(), "New Lead");

                closeFABMenu();
            }
        });

        fab5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NewLeadDialog newDialog = new NewLeadDialog();
                newDialog.show(getSupportFragmentManager(), "New Lead");

                closeFABMenu();
            }
        });

        fabLayout6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NewCustomerDialog newDialog = new NewCustomerDialog();
                newDialog.show(getSupportFragmentManager(), "New Customer");

                closeFABMenu();
            }
        });

        fab6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NewCustomerDialog newDialog = new NewCustomerDialog();
                newDialog.show(getSupportFragmentManager(), "New Customer");

                closeFABMenu();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_customers, R.id.nav_leads, R.id.nav_opportunites, R.id.nav_quotes, R.id.nav_orders, R.id.nav_tasks, R.id.nav_projects)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller,
                                             @NonNull NavDestination destination, @Nullable Bundle arguments) {

            }
        });


    }

    private void createNotificationChannel() {

        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void StartSyncService()
    {
        final int mResourceID = 0;

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleWithFixedDelay(new Runnable() {

            @Override
            public void run() {

                try {

                    if (Looper.myLooper() == null)
                    {
                        Looper.prepare();
                    }

                    WorkRequest syncWorkRequest =
                            new OneTimeWorkRequest.Builder(SyncService.class)
                                    .build();

                    WorkManager
                            .getInstance(getApplicationContext())
                            .enqueue(syncWorkRequest);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, 1, 60, TimeUnit.MINUTES);


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_sync) {
            doManualSync();
            Snackbar saveSnackbar = Snackbar.make(findViewById(android.R.id.content), "Sync Started", Snackbar.LENGTH_SHORT);
            saveSnackbar.show();
        }

        if (id == R.id.action_settings)
        {
            Intent loginIntent = new Intent(this, SettingsActivity.class);
            this.startActivity(loginIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void showFABMenu(){

        isFABOpen=true;

        //Fade all Fab's in

        fabLayout1.setVisibility(View.VISIBLE);
        fabLayout2.setVisibility(View.VISIBLE);
        fabLayout3.setVisibility(View.VISIBLE);
        fabLayout4.setVisibility(View.VISIBLE);
        fabLayout5.setVisibility(View.VISIBLE);
        fabLayout6.setVisibility(View.VISIBLE);

        fabBGLayout.setVisibility(View.VISIBLE);

        fabBGLayout.setAlpha(0);

        fabLayout1.setAlpha(0);
        fabLayout2.setAlpha(0);
        fabLayout3.setAlpha(0);
        fabLayout4.setAlpha(0);
        fabLayout5.setAlpha(0);
        fabLayout6.setAlpha(0);

        fabLayout1.animate().setDuration(200).alpha(1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                fabLayout1.setVisibility(View.VISIBLE);
            }
        });

        fabLayout2.animate().setDuration(200).alpha(1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                fabLayout2.setVisibility(View.VISIBLE);
            }
        });

        fabLayout3.animate().setDuration(200).alpha(1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                fabLayout3.setVisibility(View.VISIBLE);
            }
        });

        fabLayout4.animate().setDuration(200).alpha(1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                fabLayout4.setVisibility(View.VISIBLE);
            }
        });

        fabLayout5.animate().setDuration(200).alpha(1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                fabLayout5.setVisibility(View.VISIBLE);
            }
        });

        fabLayout6.animate().setDuration(200).alpha(1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                fabLayout6.setVisibility(View.VISIBLE);
            }
        });

        fabBGLayout.animate().setDuration(300).alpha(1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                fabBGLayout.setVisibility(View.VISIBLE);
            }
        });

        fab.animate().rotationBy(180);

        fabLayout1.animate().translationY(-getResources().getDimension(R.dimen.standard_50));
        fabLayout2.animate().translationY(-getResources().getDimension(R.dimen.standard_120));
        fabLayout3.animate().translationY(-getResources().getDimension(R.dimen.standard_190));
        fabLayout4.animate().translationY(-getResources().getDimension(R.dimen.standard_260));
        fabLayout5.animate().translationY(-getResources().getDimension(R.dimen.standard_330));
        fabLayout6.animate().translationY(-getResources().getDimension(R.dimen.standard_400));
    }

    private void closeFABMenu(){

        isFABOpen=false;

        fab.animate().rotationBy(-180);
        fabLayout1.animate().translationY(0).alpha(0);
        fabLayout2.animate().translationY(0).alpha(0);
        fabLayout3.animate().translationY(0).alpha(0);
        fabLayout4.animate().translationY(0).alpha(0);
        fabLayout5.animate().translationY(0).alpha(0);
        fabLayout6.animate().translationY(0).alpha(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if(!isFABOpen){

                    fabLayout1.setVisibility(View.GONE);
                    fabLayout2.setVisibility(View.GONE);
                    fabLayout3.setVisibility(View.GONE);
                    fabLayout4.setVisibility(View.GONE);
                    fabLayout5.setVisibility(View.GONE);
                    fabLayout6.setVisibility(View.GONE);

                    //Fade BG Out

                    fabBGLayout.animate().setDuration(300).alpha(0).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            fabBGLayout.setVisibility(View.GONE);
                        }
                    });
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void doManualSync()
    {
        try {

            final Context appContext = getApplicationContext();

            if (Looper.myLooper() == null)
            {
                Looper.prepare();
            }

            //Start a new thread

            Thread runner = new Thread(new Runnable() {
                @Override
                public void run() {

                    WorkRequest syncWorkRequest =
                            new OneTimeWorkRequest.Builder(SyncService.class)
                                    .build();

                    Operation enqueue = WorkManager
                            .getInstance(appContext)
                            .enqueue(syncWorkRequest);


                }
            });

            runner.setName("Single Sync");
            runner.start();



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if(!isFABOpen){
                super.onBackPressed();
            }else{
                closeFABMenu();
            }

        }
    }
}