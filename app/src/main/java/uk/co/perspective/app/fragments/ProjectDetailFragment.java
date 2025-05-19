package uk.co.perspective.app.fragments;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import uk.co.perspective.app.R;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.dialogs.ChangeCustomerDialog;
import uk.co.perspective.app.entities.Project;

public class ProjectDetailFragment extends Fragment implements ChangeCustomerDialog.ChangeCustomerListener {

    private View root;

    private int ID;
    private int ProjectID;
    private int LocalCustomerID;
    private String customerName;
    private String CreatedByDisplayName;
    private int CustomerID;

    private EditText projectName;
    private Spinner projectStatus;
    private EditText projectReference;
    private EditText startDate;
    private EditText endDate;
    private EditText details;
    private EditText notes;

    private ConstraintLayout customerLookup;
    private TextView customerLookupName;

    private Button save;

    ProjectDetailFragment.ChangeProjectListener mListener;

    public ProjectDetailFragment() {
        // Required empty public constructor
    }

    public interface ChangeProjectListener {
        void ProjectChanged();
    }

    public void setListener(final ProjectDetailFragment.ChangeProjectListener listener) {
        mListener = listener;
    }

    public static ProjectDetailFragment newInstance(ChangeProjectListener mListener) {
        ProjectDetailFragment fragment = new ProjectDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.setListener(mListener);
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

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root =  inflater.inflate(R.layout.fragment_project_detail, container, false);

        customerLookup = root.findViewById(R.id.customer_details);
        customerLookupName = root.findViewById(R.id.customer_name_label);

        projectName = root.findViewById(R.id.project_name);
        projectStatus = root.findViewById(R.id.project_status);
        projectReference = root.findViewById(R.id.reference);
        startDate = root.findViewById(R.id.start_date);
        endDate = root.findViewById(R.id.end_date);
        details = root.findViewById(R.id.details);
        notes = root.findViewById(R.id.notes);

        save = root.findViewById(R.id.save);


        final Calendar startTime = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener dateTimeListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                startTime.set(Calendar.YEAR, year);
                startTime.set(Calendar.MONTH, monthOfYear);
                startTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            }

        };

        customerLookup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeCustomerDialog newFragment = ChangeCustomerDialog.newInstance(ProjectDetailFragment.this);
                newFragment.show(getChildFragmentManager(), "Change Customer");
            }
        });

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(startDate);
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(endDate);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProject();
                Snackbar saveSnackbar = Snackbar.make(v, "Project was saved", Snackbar.LENGTH_SHORT);
                saveSnackbar.show();
            }
        });

        return root;
    }

    private void datePicker(final EditText target) {

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this.getContext(), 0,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        target.setText(String.format("%1$2s", dayOfMonth).replace(' ', '0') + "/" + String.format("%1$2s", (monthOfYear + 1)).replace(' ', '0') + "/" + String.format("%1$2s", year).replace(' ', '0'));

                    }

                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }

    @Override
    public void CustomerChanged(int customerID, String customerName) {

        this.LocalCustomerID = customerID;
        this.customerName = customerName;

        if (customerName.length() > 17)
        {
            customerLookupName.setText(String.format("%s...", customerName.substring(0, 16)));
        }
        else {
            customerLookupName.setText(customerName);
        }

        customerLookupName.setTextColor(Color.DKGRAY);
    }

    private void saveProject()
    {
        //Save Customer

        Project theUpdatedProject = new Project();

        theUpdatedProject.setProjectID(ProjectID);
        theUpdatedProject.setId(ID);
        theUpdatedProject.setCustomerID(CustomerID);
        theUpdatedProject.setLocalCustomerID(LocalCustomerID);
        theUpdatedProject.setCustomerName(customerName);

        theUpdatedProject.setProjectName(projectName.getText().toString());
        theUpdatedProject.setStatus((String)projectStatus.getSelectedItem());
        theUpdatedProject.setReference(projectReference.getText().toString());

        Date projectStartDate = new Date();
        Date projectEndDate = new Date();

        SimpleDateFormat sourceDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
        SimpleDateFormat targetDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

        try {
            if (!startDate.getText().toString().equals("")) {
                projectStartDate = sourceDateFormat.parse(startDate.getText().toString());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            if (!endDate.getText().toString().equals("")) {
                projectEndDate = sourceDateFormat.parse(endDate.getText().toString());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assert projectStartDate != null;
        theUpdatedProject.setStartDate(targetDateFormat.format(projectStartDate));

        assert projectEndDate != null;
        theUpdatedProject.setEndDate(targetDateFormat.format(projectEndDate));

        theUpdatedProject.setCreatedByDisplayName(CreatedByDisplayName);
        theUpdatedProject.setDetails(details.getText().toString());
        theUpdatedProject.setNotes(notes.getText().toString());
        theUpdatedProject.setIsChanged(true);
        theUpdatedProject.setIsNew(false);
        theUpdatedProject.setIsArchived(false);

        Date updatedDate = new Date();

        theUpdatedProject.setUpdated(targetDateFormat.format(updatedDate));

        DatabaseClient.getInstance(requireContext()).getAppDatabase()
                .projectDao()
                .update(theUpdatedProject);

        if (mListener != null) {
            mListener.ProjectChanged();
        }
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {

        if (ID != 0) {

            Project theProject = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .projectDao()
                    .getProject(ID);

            CustomerID = theProject.getCustomerID();
            CreatedByDisplayName = theProject.getCreatedByDisplayName();

            if (theProject.getLocalCustomerID() != null) {
                LocalCustomerID = theProject.getLocalCustomerID();
            }
            else
            {
                LocalCustomerID = 0;
            }

            ProjectID = theProject.getProjectID();

            customerName = theProject.getCustomerName();

            if (theProject.getCustomerName().length() > 17)
            {
                customerLookupName.setText(String.format("%s...", theProject.getCustomerName().substring(0, 16)));
            }
            else {
                customerLookupName.setText(theProject.getCustomerName());
            }

            projectName.setText(theProject.getProjectName());
            projectReference.setText(theProject.getReference());
            details.setText(theProject.getDetails());
            notes.setText(theProject.getNotes());

            Date projectStartDate = new Date();
            Date projectEndDate = new Date();

            SimpleDateFormat sourceDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);
            SimpleDateFormat targetDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);

            try {
                if (theProject.getStartDate() != null) {
                    if (!theProject.getStartDate().equals("")) {
                        projectStartDate = sourceDateFormat.parse(theProject.getStartDate());
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            try {
                if (theProject.getEndDate() != null) {
                    if (!theProject.getEndDate().equals("")) {
                        projectEndDate = sourceDateFormat.parse(theProject.getEndDate());
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            assert projectStartDate != null;
            startDate.setText(targetDateFormat.format(projectStartDate));

            assert projectEndDate != null;
            endDate.setText(targetDateFormat.format(projectEndDate));

            //Setup Spinners

            //Project Status

            final ArrayList<String> projectStatusSpinnerArray =  new ArrayList<>();

            projectStatusSpinnerArray.add("New");
            projectStatusSpinnerArray.add("Planning");
            projectStatusSpinnerArray.add("Active");
            projectStatusSpinnerArray.add("On Hold");
            projectStatusSpinnerArray.add("Cancelled");
            projectStatusSpinnerArray.add("Closed");

            ArrayAdapter<String> projectStatusAdapter = new ArrayAdapter<String>(requireContext(), R.layout.dropdown_list_item, projectStatusSpinnerArray);
            projectStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            projectStatus.setAdapter(projectStatusAdapter);

            if (theProject.getStatus() != null) {
                int statusIndex = projectStatusAdapter.getPosition(theProject.getStatus());
                projectStatus.setSelection(statusIndex);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.project_detail, menu);

        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items

        int id = item.getItemId();

        if (id == R.id.action_save)
        {
            saveProject();

            if (getParentFragment() != null) {
                Snackbar saveSnackbar = Snackbar.make(getParentFragment().requireView(), "Project was saved", Snackbar.LENGTH_SHORT);
                saveSnackbar.show();
            }
            else
            {
                try {
                    Snackbar saveSnackbar = Snackbar.make(requireView(), "Project was saved", Snackbar.LENGTH_SHORT);
                    saveSnackbar.show();
                } catch(Exception ignored) {}

            }
        }
        else if (id == R.id.action_archive) {

            new AlertDialog.Builder(requireContext())
                    .setTitle("Archive Project")
                    .setMessage("Are you sure you want to archive this project? It will be removed after the next successful sync")
                    .setIcon(android.R.drawable.ic_delete)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {

                            DatabaseClient.getInstance(requireContext()).getAppDatabase()
                                    .projectDao()
                                    .archiveProject(ID);

                            if (mListener != null) {
                                mListener.ProjectChanged();
                            }

                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        }
        else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }
}