package uk.co.perspective.app.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import uk.co.perspective.app.R;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.dialogs.ChangeCustomerDialog;
import uk.co.perspective.app.entities.Customer;
import uk.co.perspective.app.entities.Task;

public class TaskDetailFragment extends Fragment implements ChangeCustomerDialog.ChangeCustomerListener {

    private View root;

    private int ID;
    private int TaskID;
    private int LocalCustomerID;
    private int CustomerID;
    private String CustomerName;

    private CheckBox complete;
    private EditText subject;
    private EditText dueDate;
    private EditText dueTime;
    private EditText notes;

    private String CreatedByDisplayName;

    private ConstraintLayout customerLookup;
    private TextView customerLookupName;

    private Button save;

    TaskDetailFragment.ChangeTaskListener mListener;

    @Override
    public void CustomerChanged(int LocalCustomerID, String customerName) {

        this.LocalCustomerID = LocalCustomerID;
        this.CustomerID = 0;
        this.CustomerName = customerName;

        customerLookupName.setText(customerName);
        customerLookupName.setTextColor(Color.DKGRAY);
    }

    public interface ChangeTaskListener {
        public void TaskChanged();
    }

    public void setListener(ChangeTaskListener listener) {
        mListener = listener;
    }

    public TaskDetailFragment() {
        // Required empty public constructor
    }

    public static TaskDetailFragment newInstance(ChangeTaskListener mListener) {
        TaskDetailFragment fragment = new TaskDetailFragment();
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

        root =  inflater.inflate(R.layout.fragment_task_detail, container, false);

        final Calendar myCalendar = Calendar.getInstance();

        customerLookup = root.findViewById(R.id.customer_details);
        customerLookupName = root.findViewById(R.id.customer_name_label);

        complete = root.findViewById(R.id.taskComplete);
        subject = root.findViewById(R.id.subject);
        dueDate = root.findViewById(R.id.due_date);
        dueTime = root.findViewById(R.id.due_time);
        notes = root.findViewById(R.id.notes);

        save = root.findViewById(R.id.save);

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
                dueDate.setText(sdf.format(myCalendar.getTime()));
            }
        };

        final TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                dueTime.setText(String.format(Locale.UK,"%02d:%02d", hourOfDay, minute));
            }
        };

        dueDate.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                new DatePickerDialog(requireContext(), dateSetListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        dueTime.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                new TimePickerDialog(requireContext(), timeSetListener, myCalendar
                        .get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show();
            }
        });

        customerLookup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeCustomerDialog newFragment = ChangeCustomerDialog.newInstance(TaskDetailFragment.this);
                newFragment.show(getChildFragmentManager(), "Change Customer");
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTask();
            }
        });

        return root;
    }

    void saveTask()
    {
        Task theUpdatedTask = new Task();

        //Get local customer ID

        if (CustomerID == 0) {
            if (LocalCustomerID != 0) {

                Customer localCustomer = DatabaseClient.getInstance(requireContext())
                        .getAppDatabase()
                        .customerDao()
                        .getCustomer(LocalCustomerID);

                if (localCustomer != null) {
                    CustomerID = localCustomer.getCustomerID();
                }
            }
        }

        theUpdatedTask.setTaskID(TaskID);
        theUpdatedTask.setId(ID);
        theUpdatedTask.setCustomerID(CustomerID);
        theUpdatedTask.setLocalCustomerID(LocalCustomerID);
        theUpdatedTask.setCustomerName(CustomerName);
        theUpdatedTask.setComplete(complete.isChecked());
        theUpdatedTask.setSubject(subject.getText().toString());
        theUpdatedTask.setNotes(notes.getText().toString());
        theUpdatedTask.setIsChanged(true);
        theUpdatedTask.setIsNew(false);
        theUpdatedTask.setCreatedByDisplayName(CreatedByDisplayName);

        Date taskDate = new Date();
        Date updatedDate = new Date();

        SimpleDateFormat sourceDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK);
        SimpleDateFormat targetDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

        try {
            if (!dueTime.getText().toString().equals("")) {
                taskDate = sourceDateFormat.parse(dueDate.getText().toString() + " " + dueTime.getText().toString());
            } else {
                taskDate = sourceDateFormat.parse(dueDate.getText().toString() + " 00:00");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assert taskDate != null;
        theUpdatedTask.setDueDate(targetDateFormat.format(taskDate));
        theUpdatedTask.setUpdated(targetDateFormat.format(updatedDate));

        DatabaseClient.getInstance(requireContext()).getAppDatabase()
                .taskDao()
                .update(theUpdatedTask);

        if (getParentFragment() != null) {
            Snackbar saveSnackbar = Snackbar.make(getParentFragment().requireView(), "Task was saved", Snackbar.LENGTH_SHORT);
            saveSnackbar.show();
        }
        else
        {
            try {
                Snackbar saveSnackbar = Snackbar.make(requireView(), "Task was saved", Snackbar.LENGTH_SHORT);
                saveSnackbar.show();
            } catch(Exception ignored) {}

        }

        if (mListener != null) {
            mListener.TaskChanged();
        }
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {

        if (ID != 0) {

            //Load the data

            Task theTask = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .taskDao()
                    .getTask(ID);

            if (theTask.getTaskID() != null) {
                TaskID = theTask.getTaskID();
            }
            else
            {
                TaskID = 0;
            }

            complete.setChecked(theTask.getComplete());

            CustomerName = theTask.getCustomerName();
            CustomerID = theTask.getCustomerID();

            if (theTask.getLocalCustomerID() != null) {
                LocalCustomerID = theTask.getLocalCustomerID();
            }
            else
            {
                LocalCustomerID = 0;
            }

            if (theTask.getCustomerName() != null) {
                customerLookupName.setText(theTask.getCustomerName());
            }
            else
            {
                customerLookupName.setText("Not Linked");
            }

            subject.setText(theTask.getSubject());
            notes.setText(theTask.getNotes());
            CreatedByDisplayName = theTask.getCreatedByDisplayName();

            //date & time

            Date taskDate = new Date();
            Date taskTime = new Date();

            SimpleDateFormat sourceDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);
            SimpleDateFormat targetDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
            SimpleDateFormat targetTimeFormat = new SimpleDateFormat("HH:mm", Locale.UK);

            try {
                taskDate = sourceDateFormat.parse(theTask.getDueDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            assert taskDate != null;
            dueDate.setText(targetDateFormat.format(taskDate));
            dueTime.setText(targetTimeFormat.format(taskDate));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.task_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items

        int id = item.getItemId();

        if (id == R.id.action_copy) {

            new AlertDialog.Builder(requireContext())
                    .setTitle("Copy Task")
                    .setMessage("Are you sure you want to duplicate this task?")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {

                            Task theOriginalTask = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                                    .taskDao()
                                    .getTask(ID);

                            theOriginalTask.setId(null);
                            theOriginalTask.setTaskID(0);
                            theOriginalTask.setIsNew(true);
                            theOriginalTask.setIsChanged(false);

                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

                            theOriginalTask.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));

                            DatabaseClient.getInstance(requireContext()).getAppDatabase()
                                    .taskDao()
                                    .insert(theOriginalTask);

                            if (mListener != null) {
                                mListener.TaskChanged();
                            }
                        }})

                    .setNegativeButton(android.R.string.no, null).show();

        }
        else if (id == R.id.action_save)
        {
            saveTask();
        }
        else if (id == R.id.action_delete) {

            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete this task?")
                    .setIcon(android.R.drawable.ic_delete)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {

                            DatabaseClient.getInstance(requireContext()).getAppDatabase()
                                    .taskDao()
                                    .deleteTask(ID);

                            if (mListener != null) {
                                mListener.TaskChanged();
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