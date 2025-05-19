package uk.co.perspective.app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import uk.co.perspective.app.MainActivity;
import uk.co.perspective.app.R;
import uk.co.perspective.app.helpers.JavaScriptInterface;


public class FormEditorActivity extends AppCompatActivity {

    WebView webView;
    public String Template;
    public int OpportunityFormID;
    public String CustomerName;
    public int ID;
    public boolean HasData;
    public boolean IsComplete;

    private String cam_file_data = null;
    private ValueCallback<Uri[]> file_path;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if(getSupportActionBar()!=null) {
            this.getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_form_editor);

        Bundle extras;

        if (savedInstanceState == null) {
            extras = getIntent().getExtras();
            if (extras == null) {
                Template = "";
                OpportunityFormID = 0;
                CustomerName = "";
                ID = 0;
            } else {
                Template = extras.getString("Template");
                ID =  extras.getInt("ID");
                OpportunityFormID = extras.getInt("OpportunityFormID");
                HasData = extras.getBoolean("HasData");
                IsComplete = extras.getBoolean("IsComplete");
                CustomerName = extras.getString("AssetNumber");
                System.out.println("Template =  " + Template);
            }
        }

        final WebView webView = findViewById(R.id.formEditor);

        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAllowContentAccess(true);

        webSettings.setMixedContentMode(0);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        webView.getSettings().setAllowFileAccess(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {

            /*--
            openFileChooser is not a public Android API and has never been part of the SDK.
            handling input[type="file"] requests for android API 16+; I've removed support below API 21 as it was failing to work along with latest APIs.
            --*/
        /*    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                file_data = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType(file_type);
                if (multiple_files) {
                    i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                }
                startActivityForResult(Intent.createChooser(i, "File Chooser"), file_req_code);
            }
        */
            /*-- handling input[type="file"] requests for android API 21+ --*/
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {

                file_path = filePathCallback;

                Intent takePictureIntent = null;
                Intent takeVideoIntent = null;

                boolean includeVideo = false;
                boolean includePhoto = false;

                /*-- checking the accept parameter to determine which intent(s) to include --*/
                paramCheck:

                for (String acceptTypes : fileChooserParams.getAcceptTypes()) {
                    String[] splitTypes = acceptTypes.split(", ?+"); // although it's an array, it still seems to be the whole value; split it out into chunks so that we can detect multiple values
                    for (String acceptType : splitTypes) {
                        switch (acceptType) {
                            case "*/*":
                                includePhoto = true;
                                includeVideo = true;
                                break paramCheck;
                            case "image/*":
                                includePhoto = true;
                                break;
                            case "video/*":
                                includeVideo = true;
                                break;
                        }
                    }
                }

//                if (fileChooserParams.getAcceptTypes().length == 0) {   //no `accept` parameter was specified, allow both photo and video
//                    includePhoto = true;
//                    includeVideo = true;
//                }
//
//                if (includePhoto) {
//                    takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    if (takePictureIntent.resolveActivity(FormEditorActivity.this.getPackageManager()) != null) {
//                        File photoFile = null;
//                        try {
//                            photoFile = create_image();
//                            takePictureIntent.putExtra("PhotoPath", cam_file_data);
//                        } catch (IOException ex) {
//                            Log.e(TAG, "Image file creation failed", ex);
//                        }
//                        if (photoFile != null) {
//                            cam_file_data = "file:" + photoFile.getAbsolutePath();
//                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
//                        } else {
//                            cam_file_data = null;
//                            takePictureIntent = null;
//                        }
//                    }
//                }
//
//                if (includeVideo) {
//                    takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                    if (takeVideoIntent.resolveActivity(FormEditorActivity.this.getPackageManager()) != null) {
//                        File videoFile = null;
//                        try {
//                            videoFile = create_video();
//                        } catch (IOException ex) {
//                            Log.e(TAG, "Video file creation failed", ex);
//                        }
//                        if (videoFile != null) {
//                            cam_file_data = "file:" + videoFile.getAbsolutePath();
//                            takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
//                        } else {
//                            cam_file_data = null;
//                            takeVideoIntent = null;
//                        }
//                    }
//                }

//                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
//                contentSelectionIntent.setType(file_type);
//                if (multiple_files) {
//                    contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                }
//
//                Intent[] intentArray;
//                if (takePictureIntent != null && takeVideoIntent != null) {
//                    intentArray = new Intent[]{takePictureIntent, takeVideoIntent};
//                } else if (takePictureIntent != null) {
//                    intentArray = new Intent[]{takePictureIntent};
//                } else if (takeVideoIntent != null) {
//                    intentArray = new Intent[]{takeVideoIntent};
//                } else {
//                    intentArray = new Intent[0];
//                }
//
//                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
//                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
//                chooserIntent.putExtra(Intent.EXTRA_TITLE, "File chooser");
//                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
//                startActivityForResult(chooserIntent, file_req_code);
                return true;
            }
        });


        JavaScriptInterface JSAndroidBindingClass = new JavaScriptInterface(this);
        webView.addJavascriptInterface(JSAndroidBindingClass,  "Android");

        //Get Engineer Name

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String AgentName = sharedPreferences.getString("display_name", "");
        String subFolder = sharedPreferences.getString("company_name", "");

        //Load any form data

        if (HasData)
        {
            webView.loadUrl("file:///android_asset/Edit.html?Template=" + Template + "&subFolder=" + subFolder + "&OpportunityFormID=" + OpportunityFormID + "&ID=" + ID);
        }
        else
        {
            webView.loadUrl("file:///android_asset/Edit.html?Template=" + Template + "&subFolder=" + subFolder + "&OpportunityFormID=" + OpportunityFormID + "&ID=" + ID + "&AgentName=" + AgentName);
        }

    }

    public class Callback extends WebViewClient{
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl){
            Toast.makeText(getApplicationContext(), "Failed loading app!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event){

        if (webView != null) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }

    public void destroyWebView() {

        // Make sure you remove the WebView from its parent view before doing anything.
        //mWebContainer.removeAllViews();

        webView.clearHistory();

        // NOTE: clears RAM cache, if you pass true, it will also clear the disk cache.
        // Probably not a great idea to pass true if you have other WebViews still alive.
        webView.clearCache(true);

        // Loading a blank page is optional, but will ensure that the WebView isn't doing anything when you destroy it.
        webView.loadUrl("about:blank");

        webView.onPause();
        webView.removeAllViews();
        webView.destroyDrawingCache();

        // NOTE: This pauses JavaScript execution for ALL WebViews,
        // do not use if you have other WebViews still alive.
        // If you create another WebView after calling this,
        // make sure to call mWebView.resumeTimers().
        webView.pauseTimers();

        // NOTE: This can occasionally cause a segfault below API 17 (4.2)
        webView.destroy();

        // Null out the reference so that you don't end up re-using it.
        webView = null;
    }

    @Override
    public void onBackPressed() { }

    @Override
    public void onPause() {

        if (webView != null) {
            webView.onPause();
            webView.pauseTimers();
        }

        super.onPause();
    }

    @Override
    public void onResume() {

        if (webView != null) {
            webView.resumeTimers();
            webView.onResume();
        }

        super.onResume();
    }

    @Override
    protected void onDestroy() {

        if (webView != null) {
            webView.destroy();
            webView = null;
        }

        super.onDestroy();

/*        webView.removeAllViews();
        webView.clearHistory();
        webView.clearCache(true);
        webView.clearView();
        */
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}