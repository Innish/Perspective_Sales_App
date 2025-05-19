package uk.co.perspective.app.dialogs;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import uk.co.perspective.app.R;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.entities.Task;

public class NewTaskDialog extends DialogFragment implements ChangeCustomerDialog.ChangeCustomerListener {

    private View root;
    private int LocalCustomerID;
    private String CustomerName;

    private EditText subject;
    private EditText dueDate;
    private EditText dueTime;
    private EditText notes;

    private ConstraintLayout customerLookup;
    private TextView customerLookupName;

    Button okSelected;
    Button cancelSelected;

    NewTaskDialog.NewTaskListener mListener;

    @Override
    public void CustomerChanged(int localCustomerID, String customerName) {

        this.LocalCustomerID = localCustomerID;
        this.CustomerName = customerName;

        customerLookupName.setText(customerName);
        customerLookupName.setTextColor(Color.DKGRAY);
    }

    public interface NewTaskListener {
        public void NewTask();
    }

    public NewTaskDialog() {
        // Required empty public constructor
    }

    public void setListener(NewTaskListener listener) {
        mListener = listener;
    }

    public static NewTaskDialog newInstance(NewTaskListener mListener) {
        NewTaskDialog frag = new NewTaskDialog();
        Bundle args = new Bundle();
        frag.setListener(mListener);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            LocalCustomerID = getArguments().getInt("CustomerID");
        }
        else
        {
            LocalCustomerID = 0;
        }

        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.dialog_new_task, container, false);

        final Calendar myCalendar = Calendar.getInstance();

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        root.setBackgroundResource(R.drawable.dialog_rounded);

        customerLookup = root.findViewById(R.id.customer_details);
        customerLookupName = root.findViewById(R.id.customer_name_label);

        subject = root.findViewById(R.id.subject);
        dueDate = root.findViewById(R.id.due_date);
        dueTime = root.findViewById(R.id.due_time);
        notes = root.findViewById(R.id.notes);

        //Buttons

        okSelected = root.findViewById(R.id.save);
        cancelSelected = root.findViewById(R.id.cancel);

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {

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
                ChangeCustomerDialog newFragment = ChangeCustomerDialog.newInstance(NewTaskDialog.this);
                newFragment.show(getChildFragmentManager(), "Select Customer");
            }
        });

        okSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!subject.getText().toString().equals("")) {

                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

                    //Save Task

                    Task newTask = new Task();

                    newTask.setCustomerID(0);
                    newTask.setLocalCustomerID(LocalCustomerID);
                    newTask.setCustomerName(CustomerName);
                    newTask.setSubject(subject.getText().toString());
                    newTask.setNotes(notes.getText().toString());
                    newTask.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));

                    newTask.setComplete(false);
                    newTask.setIsChanged(false);
                    newTask.setIsNew(true);

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
                    newTask.setDueDate(targetDateFormat.format(taskDate));
                    newTask.setUpdated(targetDateFormat.format(updatedDate));

                    DatabaseClient.getInstance(requireContext()).getAppDatabase()
                            .taskDao()
                            .insert(newTask);

                    mListener.NewTask();

                    dismiss();
                }
                else
                {
                    int pL = subject.getPaddingLeft();
                    int pT = subject.getPaddingTop();
                    int pR = subject.getPaddingRight();
                    int pB = subject.getPaddingBottom();
                    subject.setBackgroundResource(R.drawable.text_input_background_required);
                    subject.setPadding(pL, pT, pR, pB);
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

    }

}