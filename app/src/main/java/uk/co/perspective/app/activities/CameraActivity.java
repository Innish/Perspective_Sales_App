package uk.co.perspective.app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Size;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.google.common.util.concurrent.ListenableFuture;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import uk.co.perspective.app.R;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.entities.OpportunityFile;
import uk.co.perspective.app.entities.ProjectFile;

public class CameraActivity extends AppCompatActivity {

    public int ID;
    public String Source;

    private View flash;
    private androidx.camera.view.PreviewView previewView;
    private Button shutter;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (this.getSupportActionBar() != null) {
            (this.getSupportActionBar()).hide();
        }

        setContentView(R.layout.activity_camera);

        Bundle extras;

        if (savedInstanceState == null) {
            extras = getIntent().getExtras();
            if (extras == null) {
                ID = 0;
                Source = "Projects";
            } else {
                ID = extras.getInt("ID", 0);
                Source = extras.getString("Source", "Projects");
            }
        }

        previewView = findViewById(R.id.previewView);
        shutter = findViewById(R.id.shutter);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        flash = findViewById(R.id.cameraFlash);
        flash.setVisibility(View.GONE);

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindImageAnalysis(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars());
            }
        } else {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
        }
    }

    private void bindImageAnalysis(@NonNull ProcessCameraProvider cameraProvider) {

        ImageCapture.Builder builder = new ImageCapture.Builder();

        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder().setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy image) {
                image.close();
            }
        });

        Preview preview = new Preview.Builder().build();

        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        final ImageCapture imageCapture = builder
                .setTargetRotation(this.getWindowManager().getDefaultDisplay().getRotation()).setTargetResolution(new Size(1920, 1080))
                .build();

        //cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, imageAnalysis, preview);

        cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageAnalysis, imageCapture);

        shutter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.UK);
                final File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Photo_" + UUID.randomUUID().toString() + ".jpeg");

                ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
                imageCapture.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback () {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {

                        if (Objects.equals(Source, "Projects")) {
                            if (ID != 0) {
                                CreateProjectFile(file.getName(), file.getPath());
                            }
                        }
                        else
                        {
                            if (ID != 0) {
                                CreateOpportunityFile(file.getName(), file.getPath());
                            }
                        }

                    }
                    @Override
                    public void onError(@NonNull ImageCaptureException error) {
                        error.printStackTrace();
                    }
                });

                Animation animFlash = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.flash);

                animFlash.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        flash.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

                flash.setVisibility(View.VISIBLE);
                flash.startAnimation(animFlash);
            }
        });
    }

    public void CreateProjectFile(String path, String filename)
    {
        ProjectFile newFile = new ProjectFile();

        newFile.setLocalProjectID(ID);
        newFile.setFilepath(path);
        newFile.setFilename(filename);
        newFile.setIsNew(true);

        DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                .projectImageDao()
                .insert(newFile);
    }


    public void CreateOpportunityFile(String path, String filename)
    {
        OpportunityFile newFile = new OpportunityFile();

        newFile.setLocalOpportunityID(ID);
        newFile.setFilepath(path);
        newFile.setFilename(filename);
        newFile.setIsNew(true);

        DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                .opportunityFileDao()
                .insert(newFile);
    }

    @Override
    public boolean onSupportNavigateUp(){
        setResult(RESULT_OK, null);
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK, null);
        super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("ID", ID);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ID = savedInstanceState.getInt("ID");
    }
}