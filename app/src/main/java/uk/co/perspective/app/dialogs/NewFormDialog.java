package uk.co.perspective.app.dialogs;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import uk.co.perspective.app.R;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.entities.Currency;
import uk.co.perspective.app.entities.OpportunityForm;
import uk.co.perspective.app.entities.OrderLine;
import uk.co.perspective.app.models.SpinnerItem;

public class NewFormDialog extends DialogFragment {

    private View root;

    private int localOpportunityID;
    private int opportunityID;

    private Spinner formTemplate;
    private EditText formName;
    private EditText formDescription;

    Button okSelected;
    Button cancelSelected;

    NewFormDialog.NewFormListener mListener;

    public interface NewFormListener {
        public void NewFormAdded(int ID);
    }

    public NewFormDialog() {
        // Required empty public constructor
    }

    public void setListener(NewFormDialog.NewFormListener listener) {
        mListener = listener;
    }

    public void setLocalOpportunityID(int localOpportunityID, int opportunityID) {
        this.localOpportunityID = localOpportunityID;
        this.opportunityID = opportunityID;
    }

    public static NewFormDialog newInstance(NewFormDialog.NewFormListener mListener, int localOpportunityID, int opportunityID) {
        NewFormDialog fragment = new NewFormDialog();
        Bundle args = new Bundle();
        fragment.setListener(mListener);
        fragment.setLocalOpportunityID(localOpportunityID, opportunityID);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        setCancelable(false);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_new_form_dialog, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        root.setBackgroundResource(R.drawable.dialog_rounded);

        formTemplate = root.findViewById(R.id.form_template);
        formName = root.findViewById(R.id.form_name);
        formDescription = root.findViewById(R.id.form_description);

        okSelected = root.findViewById(R.id.save);
        cancelSelected = root.findViewById(R.id.cancel);

        //Templates

        String [] fileNames;
        List<OpportunityForm> forms = new ArrayList<>();

        AssetManager assetManager = this.requireContext().getAssets();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.requireContext().getApplicationContext());

        final String subFolder = sharedPreferences.getString("company_name", "");

        final ArrayList<SpinnerItem> spinnerArrayTemplates =  new ArrayList<>();

        try {

            if (!Objects.equals(subFolder, "")) {
                fileNames = assetManager.list("Templates/" + subFolder);
            }
            else
            {
                fileNames = assetManager.list("Templates");
            }

            for (int i = 0; i < fileNames.length; i++) {

                if (fileNames[i].contains(".html"))
                {
                    spinnerArrayTemplates.add(new SpinnerItem(0, fileNames[i].replace(".html","")));
                }
            }

        } catch (IOException e) {
            Log.e("Error", "Unable to list files", e);
            spinnerArrayTemplates.add(new SpinnerItem(1, "No Templates Available"));
        }

        ArrayAdapter<SpinnerItem> adapterTemplates = new ArrayAdapter<SpinnerItem>(requireContext(), R.layout.dropdown_list_item, spinnerArrayTemplates);
        adapterTemplates.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        formTemplate.setAdapter(adapterTemplates);

        formTemplate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                if (formTemplate.getSelectedItem() != null)
                {
                    formName.setText(((SpinnerItem)formTemplate.getSelectedItem()).getText());
                    formDescription.setText(((SpinnerItem)formTemplate.getSelectedItem()).getText());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
                if (formTemplate.getSelectedItem() != null)
                {
                    formName.setText(((SpinnerItem)formTemplate.getSelectedItem()).getText());
                    formDescription.setText(((SpinnerItem)formTemplate.getSelectedItem()).getText());
                }
            }
        });

        okSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                OpportunityForm opportunityForm = new OpportunityForm();

                opportunityForm.setLocalOpportunityID(localOpportunityID);
                opportunityForm.setOpportunityID(opportunityID);
                opportunityForm.setFormTemplate(((SpinnerItem)formTemplate.getSelectedItem()).getText() + ".html");
                opportunityForm.setFormID(1); //This needs setting somewhere
                opportunityForm.setFormName(formName.getText().toString());
                opportunityForm.setDescription(formDescription.getText().toString());

                opportunityForm.setHasData(false);
                opportunityForm.setIsNew(true);
                opportunityForm.setIsComplete(false);

                long newFormID = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                        .opportunityFormDao()
                        .insert(opportunityForm);

                mListener.NewFormAdded((int)newFormID);

                dismiss();
            }
        });

        cancelSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {

    }
}