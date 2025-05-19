package uk.co.perspective.app.dialogs;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import uk.co.perspective.app.R;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.entities.Activity;
import uk.co.perspective.app.models.SpinnerItem;

public class NewActivityDialog extends DialogFragment implements ChangeCustomerDialog.ChangeCustomerListener {

    private View root;

    private int LocalCustomerID;
    private String CustomerName;

    private Spinner activityType;
    private EditText subject;
    private EditText startDate;
    private EditText endDate;
    private EditText details;

    private ConstraintLayout customerLookup;
    private TextView customerLookupName;

    Button okSelected;
    Button cancelSelected;

    String date_time = "";

    int mYear;
    int mMonth;
    int mDay;
    int mHour;
    int mMinute;

    NewActivityDialog.NewActivityListener mListener;

    public interface NewActivityListener {
        public void NewActivityAdded();
    }

    public NewActivityDialog() {
        // Required empty public constructor
    }

    public void setListener(NewActivityListener listener) {
        mListener = listener;
    }

    public void setCustomer(int ID, String customerName) {
        LocalCustomerID = ID;
        CustomerName = customerName;
    }

    public static NewActivityDialog newInstance(NewActivityListener mListener, int ID, String customerName) {
        NewActivityDialog frag = new NewActivityDialog();
        Bundle args = new Bundle();
        frag.setListener(mListener);
        frag.setCustomer(ID, customerName);
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

        root = inflater.inflate(R.layout.dialog_new_activity, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        root.setBackgroundResource(R.drawable.dialog_rounded);

        customerLookup = root.findViewById(R.id.customer_details);
        customerLookupName = root.findViewById(R.id.customer_name_label);

        activityType = root.findViewById(R.id.activity_type);
        subject = root.findViewById(R.id.subject);
        startDate = root.findViewById(R.id.start_date);
        endDate = root.findViewById(R.id.end_date);
        details = root.findViewById(R.id.details);

        //Buttons

        okSelected = root.findViewById(R.id.save);
        cancelSelected = root.findViewById(R.id.cancel);

        //Setup Spinners

        //Activity Type

        final ArrayList<SpinnerItem> activityTypeSpinnerArray =  new ArrayList<>();

        activityTypeSpinnerArray.add(new SpinnerItem(1,"Note"));
        activityTypeSpinnerArray.add(new SpinnerItem(2,"Phone Call"));
        activityTypeSpinnerArray.add(new SpinnerItem(3,"Meeting"));
        activityTypeSpinnerArray.add(new SpinnerItem(4,"Document"));
        activityTypeSpinnerArray.add(new SpinnerItem(5,"Email"));

        ArrayAdapter<SpinnerItem> activityTypeAdapter = new ArrayAdapter<SpinnerItem>(requireContext(), R.layout.dropdown_list_item, activityTypeSpinnerArray);
        activityTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityType.setAdapter(activityTypeAdapter);

        final Calendar startTime = Calendar.getInstance();
        final String myFormat = "dd/MM/yyyy kk:mm"; //In which you need put here
        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);

        DatePickerDialog.OnDateSetListener dateTimeListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                startTime.set(Calendar.YEAR, year);
                startTime.set(Calendar.MONTH, monthOfYear);
                startTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                //expenseTime.setText(sdf.format(startTime.getTime()));
            }

        };

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

        customerLookup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            ChangeCustomerDialog newFragment = ChangeCustomerDialog.newInstance(NewActivityDialog.this);
            newFragment.show(getChildFragmentManager(), "Select Customer");
            }
        });

        okSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!subject.getText().toString().equals("")) {
                    if (!details.getText().toString().equals("")) {

                        //Save Contact

                        Activity newActivity = new Activity();

                        newActivity.setJournalEntryID(0);
                        newActivity.setCustomerID(0);
                        newActivity.setLocalCustomerID(LocalCustomerID);
                        newActivity.setSubject(subject.getText().toString());

                        SpinnerItem selectedActivityType = (SpinnerItem) activityType.getSelectedItem();
                        newActivity.setJournalEntryType(selectedActivityType.getText());

                        Date activityStartDate = new Date();
                        Date activityEndDate = new Date();

                        SimpleDateFormat sourceDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK);
                        SimpleDateFormat targetDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                        try {
                            if (!startDate.getText().toString().equals("")) {
                                activityStartDate = sourceDateFormat.parse(startDate.getText().toString());
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        try {
                            if (!endDate.getText().toString().equals("")) {
                                activityEndDate = sourceDateFormat.parse(endDate.getText().toString());
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        assert activityStartDate != null;
                        newActivity.setStartDate(targetDateFormat.format(activityStartDate));

                        assert activityEndDate != null;
                        newActivity.setEndDate(targetDateFormat.format(activityEndDate));

                        newActivity.setNotes(details.getText().toString());
                        newActivity.setIsChanged(false);
                        newActivity.setIsNew(true);

                        Date updatedDate = new Date();

                        newActivity.setUpdated(targetDateFormat.format(updatedDate));

                        DatabaseClient.getInstance(requireContext()).getAppDatabase()
                                .activityDao()
                                .insert(newActivity);

                        mListener.NewActivityAdded();

                        dismiss();
                    } else {
                        int pL = details.getPaddingLeft();
                        int pT = details.getPaddingTop();
                        int pR = details.getPaddingRight();
                        int pB = details.getPaddingBottom();
                        details.setBackgroundResource(R.drawable.text_input_background_required);
                        details.setPadding(pL, pT, pR, pB);
                    }
                }
                else {
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

    private void datePicker(final EditText target) {

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this.getContext(), 0,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        date_time = String.format("%1$2s", dayOfMonth).replace(' ', '0') + "/" + String.format("%1$2s", (monthOfYear + 1)).replace(' ', '0') + "/" + String.format("%1$2s", year).replace(' ', '0');
                        tiemPicker(target);
                    }

                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }

    private void tiemPicker(final EditText target){

        final Calendar c = Calendar.getInstance();

        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this.getContext(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        mHour = hourOfDay;
                        mMinute = minute;

                        target.setText(String.format("%s %s:%s", date_time, String.format("%1$2s", hourOfDay).replace(' ', '0'), String.format("%1$2s", minute).replace(' ', '0')));
                    }
                }, mHour, mMinute, false);

        timePickerDialog.show();

    }
}