package uk.co.perspective.app.dialogs;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import uk.co.perspective.app.R;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.entities.Project;

public class NewProjectDialog extends DialogFragment implements ChangeCustomerDialog.ChangeCustomerListener {

    private View root;

    private int LocalCustomerID;
    private String CustomerName;

    private EditText projectName;
    private EditText reference;
    private EditText description;
    private EditText notes;

    private ConstraintLayout customerLookup;
    private TextView customerLookupName;

    Button okSelected;
    Button cancelSelected;

    NewProjectDialog.NewProjectListener mListener;

    public interface NewProjectListener {
        public void NewProject(String ProjectName);
    }

    public NewProjectDialog() {
        // Required empty public constructor
    }

    public void setListener(NewProjectListener listener) {
        mListener = listener;
    }

    public void setCustomer(int ID, String customerName) {
        LocalCustomerID = ID;
        CustomerName = customerName;
    }

    public static NewProjectDialog newInstance(NewProjectListener mListener, int customerID, String customerName) {
        NewProjectDialog frag = new NewProjectDialog();
        Bundle args = new Bundle();
        frag.setListener(mListener);
        frag.setCustomer(customerID, customerName);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.dialog_new_project, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        root.setBackgroundResource(R.drawable.dialog_rounded);

        customerLookup = root.findViewById(R.id.customer_details);
        customerLookupName = root.findViewById(R.id.customer_name_label);

        projectName = root.findViewById(R.id.project_name);
        reference = root.findViewById(R.id.project_reference);
        description = root.findViewById(R.id.description);
        notes = root.findViewById(R.id.notes);

        //Buttons

        okSelected = root.findViewById(R.id.save);
        cancelSelected = root.findViewById(R.id.cancel);

        customerLookup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            ChangeCustomerDialog newFragment = ChangeCustomerDialog.newInstance(NewProjectDialog.this);
            newFragment.show(getChildFragmentManager(), "Select Customer");
            }
        });

        okSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!projectName.getText().toString().equals("")) {

                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

                    //Save Project

                    Project newProject = new Project();

                    newProject.setProjectID(0);
                    newProject.setCustomerID(0);
                    newProject.setLocalCustomerID(LocalCustomerID);
                    newProject.setCustomerName(CustomerName);
                    newProject.setStatus("New");

                    newProject.setProjectName(projectName.getText().toString());
                    newProject.setReference(reference.getText().toString());
                    newProject.setDetails(description.getText().toString());
                    newProject.setNotes(notes.getText().toString());
                    newProject.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));

                    newProject.setIsChanged(false);
                    newProject.setIsNew(true);
                    newProject.setIsArchived(false);

                    Date updatedDate = new Date();

                    SimpleDateFormat targetDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                    newProject.setUpdated(targetDateFormat.format(updatedDate));

                    Calendar c = Calendar.getInstance();

                    c.add(Calendar.DATE, 30);

                    newProject.setStartDate(targetDateFormat.format(updatedDate));
                    newProject.setEndDate(targetDateFormat.format(c.getTime()));

                    DatabaseClient.getInstance(requireContext()).getAppDatabase()
                            .projectDao()
                            .insert(newProject);

                    mListener.NewProject(projectName.getText().toString());

                    dismiss();
                }
                else
                {
                    int pL = projectName.getPaddingLeft();
                    int pT = projectName.getPaddingTop();
                    int pR = projectName.getPaddingRight();
                    int pB = projectName.getPaddingBottom();
                    projectName.setBackgroundResource(R.drawable.text_input_background_required);
                    projectName.setPadding(pL, pT, pR, pB);
                }
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

        if (LocalCustomerID != 0 && !CustomerName.equals(""))
        {
            customerLookupName.setText(CustomerName);
            customerLookupName.setTextColor(Color.DKGRAY);
        }
    }

    @Override
    public void CustomerChanged(int ID, String customerName) {

        this.LocalCustomerID = ID;
        this.CustomerName = customerName;

        customerLookupName.setText(customerName);
        customerLookupName.setTextColor(Color.DKGRAY);
    }
}