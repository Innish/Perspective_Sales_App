package uk.co.perspective.app.helpers;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import uk.co.perspective.app.database.AppDatabase;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.entities.OpportunityForm;

public class JavaScriptInterface {

    Context mContext;

    public JavaScriptInterface(Context c) {
        mContext = c;
    }

    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void SaveFormData(int ID, String json) {

        Toast.makeText(mContext, "Saving...", Toast.LENGTH_SHORT).show();

        final AppDatabase client = DatabaseClient
                .getInstance(mContext)
                .getAppDatabase();

        client.opportunityFormDao().updateOpportunityForm(json, ID);

        //final int mID = ID;

//        //Write to a file, finish & return a result
//
//        new AlertDialog.Builder(mContext)
//                .setTitle("Form Complete")
//                .setMessage("Is this form now complete?")
//                .setIcon(android.R.drawable.ic_dialog_info)
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//
//                        client.opportunityFormDao().updateIsComplete(mID, true);
//                        ((Activity) mContext).setResult(RESULT_OK, null);
//                        ((Activity) mContext).finish();
//
//                    }})
//                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        ((Activity) mContext).setResult(RESULT_OK, null);
//                        ((Activity) mContext).finish();
//                    }
//                }).show();
    }

    @JavascriptInterface
    public String LoadFormData(int ID) {

        OpportunityForm form = DatabaseClient
                .getInstance(mContext)
                .getAppDatabase()
                .opportunityFormDao()
                .getOpportunityForm(ID);

        return form.getFormData();
    }

    @JavascriptInterface
    public void CloseForm() {
        ((Activity) mContext).finish();
    }

    @JavascriptInterface
    public String GetDisplayName() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        return sharedPreferences.getString("display_name", "");
    }
}
