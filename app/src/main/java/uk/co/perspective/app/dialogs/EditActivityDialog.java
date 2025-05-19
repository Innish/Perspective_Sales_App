package uk.co.perspective.app.dialogs;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
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
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

public class EditActivityDialog extends DialogFragment {

    private View root;

    private int ID;

    private int LocalCustomerID;

    private Spinner activityType;
    private EditText subject;
    private EditText startDate;
    private EditText endDate;
    private EditText details;

    Button okSelected;
    Button deleteSelected;
    Button cancelSelected;

    String date_time = "";

    int mYear;
    int mMonth;
    int mDay;
    int mHour;
    int mMinute;

    EditActivityDialog.UpdatedActivityListener mListener;

    public interface UpdatedActivityListener {
        public void ActivityUpdated();
    }

    public EditActivityDialog() {
        // Required empty public constructor
    }

    public void setListener(UpdatedActivityListener listener) {
        mListener = listener;
    }


    public static EditActivityDialog newInstance(UpdatedActivityListener mListener) {
        EditActivityDialog frag = new EditActivityDialog();
        Bundle args = new Bundle();
        frag.setArguments(args);
        frag.setListener(mListener);
        return frag;
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

        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.dialog_edit_activity, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        root.setBackgroundResource(R.drawable.dialog_rounded);

        activityType = root.findViewById(R.id.activity_type);
        subject = root.findViewById(R.id.subject);
        startDate = root.findViewById(R.id.start_date);
        endDate = root.findViewById(R.id.end_date);
        details = root.findViewById(R.id.details);

        //Buttons

        okSelected = root.findViewById(R.id.save);
        deleteSelected = root.findViewById(R.id.delete);
        cancelSelected = root.findViewById(R.id.cancel);

        //Setup Spinners

        //Activity Type

        final ArrayList<String> activityTypeSpinnerArray =  new ArrayList<>();

        activityTypeSpinnerArray.add("Note");
        activityTypeSpinnerArray.add("Phone Call");
        activityTypeSpinnerArray.add("Meeting");
        activityTypeSpinnerArray.add("Document");
        activityTypeSpinnerArray.add("Email");

        ArrayAdapter<String> activityTypeAdapter = new ArrayAdapter<String>(requireContext(), R.layout.dropdown_list_item, activityTypeSpinnerArray);
        activityTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityType.setAdapter(activityTypeAdapter);

        final Calendar startTime = Calendar.getInstance();
        final String myFormat = "dd/MM/yyyy kk:mm"; //In which you need put here
        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);

        //Get Activity

        if (ID != 0) {

            Activity activity = DatabaseClient
                    .getInstance(requireContext())
                    .getAppDatabase()
                    .activityDao()
                    .getActivity(ID);

            Date activityStartDate = new Date();
            Date activityEndDate = new Date();

            SimpleDateFormat sourceDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);
            SimpleDateFormat targetDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK);

            //LocalCustomerID = activity.getLocalCustomerID();
            subject.setText(activity.getSubject());

            try {
                if (!activity.getStartDate().equals("")) {
                    activityStartDate = sourceDateFormat.parse(activity.getStartDate());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            try {
                if (!activity.getEndDate().equals("")) {
                    activityEndDate = sourceDateFormat.parse(activity.getEndDate());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            assert activityStartDate != null;
            startDate.setText(targetDateFormat.format(activityStartDate));

            assert activityEndDate != null;
            endDate.setText(targetDateFormat.format(activityEndDate));

            details.setText(activity.getNotes());

            //Set Spinner Values

            int activityTypeIndex = activityTypeAdapter.getPosition(activity.getJournalEntryType());

            activityType.setSelection(activityTypeIndex);
        }

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

        okSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!subject.getText().toString().equals("")) {
                    if (!details.getText().toString().equals("")) {

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

                        Date updatedDate = new Date();

                        assert activityStartDate != null;
                        assert activityEndDate != null;
                        DatabaseClient.getInstance(requireContext()).getAppDatabase()
                                .activityDao()
                                .updateActivity((String)activityType.getSelectedItem(), targetDateFormat.format(activityStartDate), targetDateFormat.format(activityEndDate), subject.getText().toString(), details.getText().toString(), targetDateFormat.format(updatedDate), ID);

                        mListener.ActivityUpdated();

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

        deleteSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(requireContext())
                        .setTitle("Delete Activity")
                        .setMessage("Are you sure you want to remove this activity?")
                        .setIcon(android.R.drawable.ic_delete)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                //Remove the item

                                DatabaseClient.getInstance(requireContext()).getAppDatabase()
                                        .activityDao()
                                        .deleteActivity(ID);

                                dismiss();

                                mListener.ActivityUpdated();

                        }})
                        .setNegativeButton(android.R.string.no, null).show();

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