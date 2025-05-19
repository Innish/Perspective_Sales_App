package uk.co.perspective.app.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.perspective.app.R;
import uk.co.perspective.app.activities.CameraActivity;
import uk.co.perspective.app.adapters.ImageRecyclerAdapter;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.entities.Opportunity;
import uk.co.perspective.app.entities.OpportunityFile;
import uk.co.perspective.app.objects.FileItem;
import uk.co.perspective.app.services.EndPoints;
import uk.co.perspective.app.services.RetrofitClientInstance;

public class OpportunityFilesFragment extends Fragment implements ImageRecyclerAdapter.FileListener  {

    private View root;
    public int ID;
    AlertDialog dialog;

    private static final int REQUEST_CAMERA = 8985;
    private static final int RESULT_LOAD_IMAGE = 8986;
    private static final int RESULT_LOAD_FILE = 8987;

    ActivityResultLauncher<Intent> launchFileSelector  = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
    result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {

            Intent data = result.getData();

            assert data != null;
            Uri selectedFileURI = data.getData();

            assert selectedFileURI != null;
            String fileName = getFileName(selectedFileURI);

            if (fileName.contains(".")) {
                if (saveFile(requireContext(), fileName, selectedFileURI, Objects.requireNonNull(requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)).getAbsolutePath(), fileName))
                {
                    try {
                        Snackbar saveSnackbar = Snackbar.make(requireView(), "There was a problem adding this file to the project", Snackbar.LENGTH_SHORT);
                        saveSnackbar.show();
                    } catch(Exception ignored) {}
                }

                refreshImages();
            }
        }
    });

    ActivityResultLauncher<Intent> launchCamera  = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    refreshImages();
                }
            });

    ActivityResultLauncher<Intent> launchImageSelector  = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {

                    Intent data = result.getData();

                    assert data != null;
                    Uri selectedImageURI = data.getData();

                    assert selectedImageURI != null;
                    String fileName = getFileName(selectedImageURI);

                    if (saveFile(requireContext(), fileName, selectedImageURI, Objects.requireNonNull(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)).getAbsolutePath(), fileName))
                    {
                        try {
                            Snackbar saveSnackbar = Snackbar.make(requireView(), "There was a problem adding this file to the project", Snackbar.LENGTH_SHORT);
                            saveSnackbar.show();
                        } catch(Exception ignored) {}
                    }

                    refreshImages();
                }
            });

    public boolean saveFile(Context context, String name, Uri sourceuri, String destinationDir, String destFileName) {

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        InputStream input = null;
        boolean hasError = false;

        try {
            if (isVirtualFile(context, sourceuri)) {
                input = getInputStreamForVirtualFile(context, sourceuri, getMimeType(name));
            } else {
                input = context.getContentResolver().openInputStream(sourceuri);
            }

            boolean directorySetupResult;
            File destDir = new File(destinationDir);
            if (!destDir.exists()) {
                directorySetupResult = destDir.mkdirs();
            } else if (!destDir.isDirectory()) {
                directorySetupResult = replaceFileWithDir(destinationDir);
            } else {
                directorySetupResult = true;
            }

            if (!directorySetupResult) {
                hasError = true;
            } else {
                String destination = destinationDir + File.separator + destFileName;
                assert input != null;
                int originalsize = input.available();

                bis = new BufferedInputStream(input);
                bos = new BufferedOutputStream(Files.newOutputStream(Paths.get(destination)));

                byte[] buf = new byte[originalsize];

                if (originalsize > 0) {
                    bis.read(buf);
                    do {
                        bos.write(buf);
                    } while (bis.read(buf) != -1);

                    CreateOpportunityFile(name, destinationDir + "/" + name);
                }
                else
                {
                    throw new Exception("Invalid Length");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            hasError = true;
        } finally {
            try {
                if (bos != null) {
                    bos.flush();
                    bos.close();

                }
            } catch (Exception ignored) {
            }
        }

        return hasError;
    }

    private static boolean replaceFileWithDir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return file.mkdirs();
        } else if (file.delete()) {
            File folder = new File(path);
            return folder.mkdirs();
        }
        return false;
    }

    private static boolean isVirtualFile(Context context, Uri uri) {

        if (!DocumentsContract.isDocumentUri(context, uri)) {
            return false;
        }

        Cursor cursor = context.getContentResolver().query(
                uri,
                new String[]{DocumentsContract.Document.COLUMN_FLAGS},
                null, null, null);

        int flags = 0;

        assert cursor != null;
        if (cursor.moveToFirst()) {
            flags = cursor.getInt(0);
        }

        cursor.close();

        return (flags & DocumentsContract.Document.FLAG_VIRTUAL_DOCUMENT) != 0;
    }

    private static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    private static InputStream getInputStreamForVirtualFile(Context context, Uri uri, String mimeTypeFilter)
            throws IOException {

        ContentResolver resolver = context.getContentResolver();
        String[] openableMimeTypes = resolver.getStreamTypes(uri, mimeTypeFilter);
        if (openableMimeTypes == null || openableMimeTypes.length < 1) {
            throw new FileNotFoundException();
        }
        return resolver
                .openTypedAssetFileDescriptor(uri, openableMimeTypes[0], null)
                .createInputStream();
    }

    public OpportunityFilesFragment() {
        // Required empty public constructor
    }

    public static OpportunityFilesFragment newInstance() {
        OpportunityFilesFragment fragment = new OpportunityFilesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            ID = getArguments().getInt("ID");
        }
        else
        {
            ID = 0;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_opportunity_files, container, false);

        int numberOfColumns = 2;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            numberOfColumns = 3;
        }

        RecyclerView recyclerView = (RecyclerView)root.findViewById(R.id.file_list);
        recyclerView.setHasFixedSize(true);

        Context viewcontext = recyclerView.getContext();

        LinearLayoutManager layoutManager = new GridLayoutManager(viewcontext, numberOfColumns, GridLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        return root;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {


        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        refreshImages();

    }

    public void setProgressDialog() {

        int llPadding = 40;
        LinearLayout ll = new LinearLayout(requireContext());
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(requireContext());
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(requireContext());
        tvText.setText("Getting Files...");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(16);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(true);
        builder.setView(ll);

        dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);
        }
    }

    public void refreshImages()
    {
        RecyclerView recyclerView = (RecyclerView)root.findViewById(R.id.file_list);

        ArrayList<FileItem> localfileItems = new ArrayList<>();

        localfileItems.add(new FileItem(0, null, "New Image", ""));

        ArrayList<FileItem> remotefileItems = new ArrayList<>();

        localfileItems.addAll(getFiles());

        Opportunity opportunity = DatabaseClient
                .getInstance(getContext())
                .getAppDatabase()
                .opportunityDao()
                .getOpportunity(ID);

        BitmapFactory.Options options = new BitmapFactory.Options();

        if (opportunity.getOpportunityID() != null) {

            setProgressDialog();

            final EndPoints endPoint = RetrofitClientInstance.getRetrofitInstance(requireContext()).create(EndPoints.class);

            Call<List<OpportunityFile>> remoteDocumentsCall = endPoint.getRemoteOpportunityFiles(opportunity.getOpportunityID());
            remoteDocumentsCall.enqueue(new Callback<List<OpportunityFile>>() {

                @Override
                public void onResponse(@NonNull Call<List<OpportunityFile>> call, @NonNull Response<List<OpportunityFile>> response) {

                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            for (OpportunityFile file : response.body()) {

                                //Add each file sans the bitmap

                                remotefileItems.add(new FileItem(0, null, file.getFilename(), file.getFilename()));
                            }

                            try {
                                ImageRecyclerAdapter adapter = new ImageRecyclerAdapter(requireContext(), getChildFragmentManager(), checkForDuplicates(localfileItems, remotefileItems), OpportunityFilesFragment.this);
                                recyclerView.setAdapter(adapter);
                            }
                            catch (Exception ignored)
                            {}

                            dialog.dismiss();
                        } else {

                            try {
                                ImageRecyclerAdapter adapter = new ImageRecyclerAdapter(requireContext(), getChildFragmentManager(), localfileItems, OpportunityFilesFragment.this);
                                recyclerView.setAdapter(adapter);
                            }
                            catch (Exception ignored)
                            {}

                            dialog.dismiss();
                        }
                    } else {
                        Log.w("Sync - New File Upload Failed", response.message());

                        ImageRecyclerAdapter adapter = new ImageRecyclerAdapter(requireContext(), getChildFragmentManager(), localfileItems, OpportunityFilesFragment.this);
                        recyclerView.setAdapter(adapter);

                        dialog.dismiss();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<OpportunityFile>> call, @NotNull Throwable t) {

                    Log.w("Download Failed", t.getMessage());

                    ImageRecyclerAdapter adapter = new ImageRecyclerAdapter(requireContext(), getChildFragmentManager(), localfileItems, OpportunityFilesFragment.this);
                    recyclerView.setAdapter(adapter);

                    dialog.dismiss();
                }
            });
        }
        else
        {
            ImageRecyclerAdapter adapter = new ImageRecyclerAdapter(requireContext(), getChildFragmentManager(), localfileItems, OpportunityFilesFragment.this);
            recyclerView.setAdapter(adapter);
        }
    }

    private ArrayList<FileItem> checkForDuplicates(ArrayList<FileItem> localList, ArrayList<FileItem> remoteList)
    {
        ArrayList<FileItem> fileItems = new ArrayList<>();

        boolean foundFile = false;

        for (FileItem remoteFile : remoteList) {

            foundFile = false;

            for (FileItem localFile : localList) {
                if (remoteFile.getFilePath() != null) {
                    if (remoteFile.getFilePath().substring(remoteFile.getFilePath().lastIndexOf("/")).contains(Uri.encode(localFile.getTitle()))) {
                        if (!localFile.getFilePath().equals("")) {
                            foundFile = true;
                        }
                    }
                }
            }

            if (!foundFile)
            {
                assert remoteFile.getFilePath() != null;
                remoteFile.setTitle(Uri.decode(remoteFile.getFilePath().substring(remoteFile.getFilePath().lastIndexOf("/") + 1)));
                fileItems.add(remoteFile);
            }
        }

        localList.addAll(fileItems);

        return localList;
    }

    private ArrayList<FileItem> getFiles() {

        final ArrayList<FileItem> fileItems = new ArrayList<>();

        //Get Rows From Room DB

        List<OpportunityFile> fileList = DatabaseClient
                .getInstance(getContext())
                .getAppDatabase()
                .opportunityFileDao()
                .getAllOpportunityFiles(ID);

        String Dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();

        File pathToPicture = new File(Dir);

        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inSampleSize = 300;
        options.inJustDecodeBounds = false;

        if (Build.VERSION.SDK_INT >= 29) {

            for (OpportunityFile theFile : fileList) {

                File Picture = null;

                if (theFile.getFilepath() != null) {
                    if (!theFile.getFilename().equals("")) {
                        Picture = new File(theFile.getFilename());
                    } else {
                        Picture = new File(pathToPicture, theFile.getFilepath());
                    }
                }
                else
                {
                    assert false;
                    Picture = new File(pathToPicture, theFile.getFilepath());
                }

                Log.i("Image Loaded", Objects.requireNonNull(Picture.getAbsolutePath()));

                Uri imageUri= Uri.fromFile(Picture);

                String provider = requireContext().getPackageName();
                requireContext().grantUriPermission(provider, imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                try (ParcelFileDescriptor img = requireContext().getContentResolver().openFileDescriptor(imageUri, "r")) {
                    if (img != null) {
                        //Bitmap bitmap = decodeSampledBitmapFromResource(img, img.getFileDescriptor(), 100,100);
                        Bitmap bitmap = BitmapFactory.decodeFileDescriptor(img.getFileDescriptor());
                        fileItems.add(new FileItem(theFile.getId(), bitmap, theFile.getFilepath(), theFile.getFilename()));
                    }
                } catch (IOException ex) {
                    Log.i("Opportunity Image", Objects.requireNonNull(ex.getMessage()));
                    fileItems.add(new FileItem(theFile.getId(), null, theFile.getFilepath(), theFile.getFilename()));
                }
            }
        }
        else {

            for (OpportunityFile theFile : fileList) {

                File Picture = new File(pathToPicture, theFile.getFilepath());
                Bitmap bitmap = BitmapFactory.decodeFile(Picture.getAbsolutePath(), options);
                fileItems.add(new FileItem(theFile.getId(), bitmap, theFile.getFilepath(), theFile.getFilepath()));
            }
        }

        return fileItems;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {

        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

//        inflater.inflate(R.menu.opportunity_detail, menu);
//
//        MenuItem item = menu.findItem(R.id.action_activity);
//
//        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
//        {
//            item.setVisible(false);
//        }

        super.onCreateOptionsMenu(menu, inflater);

    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

    private void enableCamera() {
        Intent intent = new Intent(this.getContext(), CameraActivity.class);
        intent.putExtra("Source", "Opportunities"); //Optional parameters
        intent.putExtra("ID", ID); //Optional parameters

        launchCamera.launch(intent);
    }

    @Override
    public void AddNewFile() {

        final String takephoto = "Take Photo";
        final String chooseImageFromLibrary = "Choose Image From Library";
        final String chooseFileFromExternal = "Choose File From External Source";
        final String cancel = "Cancel";

        final CharSequence[] items = {takephoto, chooseImageFromLibrary, chooseFileFromExternal, cancel};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Add File(s)");

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals(takephoto))
                {
                    if (hasCameraPermission()) {
                        enableCamera();
                    } else {
                        requestPermission();
                    }
                }
                else if (items[item].equals(chooseImageFromLibrary))
                {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    launchImageSelector.launch(intent);
                }
                else if (items[item].equals(chooseFileFromExternal))
                {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

                    intent.setType("application/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    launchFileSelector.launch(intent);

                }
                else if (items[item].equals(cancel))
                {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void OpenFile(int ID, String path) {

        if (path != null)
        {
            if (path.contains("http")) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
                requireView().getContext().startActivity(browserIntent);
            }
            else
            {
                String provider = this.requireContext().getPackageName() + ".provider";

                File document = new File(path);

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(androidx.core.content.FileProvider.getUriForFile(requireContext(), provider, document), getFileType(document.getName()));

                requireContext().startActivity(intent);
            }
        }
    }

    @Override
    public void RemoveFile(int ID, String path) {

        //Delete Image

        new AlertDialog.Builder(requireContext())
                .setTitle("Remove File")
                .setMessage("Are you sure you want to remove this file from this project?")
                .setIcon(android.R.drawable.ic_delete)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        try {

                            File document = new File(path);

                            if (document.delete()) {

                                //Remove the item from the filesystem & database

                                DatabaseClient.getInstance(requireContext()).getAppDatabase()
                                        .opportunityFileDao()
                                        .deleteOpportunityFile(ID);

                                refreshImages();

                                Toast.makeText(requireContext(), "File Removed", Toast.LENGTH_SHORT).show();
                            }
                            else {

                                Toast.makeText(requireContext(), "File Not Found", Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (Exception ignored)
                        {

                        }
                    }})

                .setNegativeButton(android.R.string.no, null).show();
    }

    public String getFileType(String fileName)
    {
        String mimeType = "application/*";

        switch (fileName.substring(fileName.indexOf(".")))
        {
            case ".jpg":
                mimeType = "image/jpeg";
                break;

            case ".jpeg":
                mimeType = "image/jpeg";
                break;

            case ".png":
                mimeType = "image/png";
                break;

            case ".gif":
                mimeType = "image/gif";
                break;

            case ".doc":
                mimeType = "application/msword";
                break;

            case ".docx":
                mimeType = "application/msword";
                break;

            case ".xls":
                mimeType = "application/vnd.ms-excel";
                break;

            case ".xlsx":
                mimeType = "application/vnd.ms-excel";
                break;

            case ".ppt":
                mimeType = "application/mspowerpoint";
                break;

            case ".pptx":
                mimeType = "application/mspowerpoint";
                break;

            case ".pdf":
                mimeType = "application/pdf";
                break;

            default:
                mimeType = "application/*";
                break;
        }

        return mimeType;
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public  String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
                cursor.close();
            } catch (Exception ex) {
                Log.i("File", Objects.requireNonNull(ex.getMessage()));
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public void CreateOpportunityFile(String path, String filename)
    {
        OpportunityFile newFile = new OpportunityFile();

        newFile.setLocalOpportunityID(ID);
        newFile.setFilepath(path);
        newFile.setFilename(filename);
        newFile.setIsNew(true);

        DatabaseClient.getInstance(requireContext()).getAppDatabase()
                .opportunityFileDao()
                .insert(newFile);
    }
}
