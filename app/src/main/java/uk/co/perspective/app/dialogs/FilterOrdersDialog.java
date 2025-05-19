package uk.co.perspective.app.dialogs;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import uk.co.perspective.app.R;
import uk.co.perspective.app.models.SpinnerItem;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FilterOrdersDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterOrdersDialog extends DialogFragment implements FilterStatusDialog.SetFilterListener {

    RadioGroup radioFilter;
    RadioButton radioFilterButton;
    Button applyFilter;
    Button cancelFilter;

    RadioButton radioEverything;
    RadioButton radioStatus;
    RadioButton radioThisWeek;
    RadioButton radioThisMonth;
    RadioButton radioCustom;

    FilterOrdersDialog.SetFilterListener mListener;

    public interface SetFilterListener {
        public void SetFilter(String startDate, String endDate, String Label);
        public void SetStatusFilter(String Status);
    }

    public FilterOrdersDialog() {
        // Required empty public constructor
    }

    public static FilterOrdersDialog newInstance(SetFilterListener mListener) {
        FilterOrdersDialog fragment = new FilterOrdersDialog();
        fragment.setListener(mListener);
        return fragment;
    }

    public void setListener(FilterOrdersDialog.SetFilterListener listener) {
        mListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view =  inflater.inflate(R.layout.dialog_filter_orders, container, false);

        radioFilter = (RadioGroup)view.findViewById(R.id.radioFilter);
        applyFilter = (Button)view.findViewById(R.id.setFilter);
        cancelFilter = (Button)view.findViewById(R.id.cancelSetFilter);

        radioEverything = (RadioButton) view.findViewById(R.id.radioEverything);
        radioStatus = (RadioButton) view.findViewById(R.id.radioByStatus);
        radioThisWeek = (RadioButton) view.findViewById(R.id.radioThisWeek);
        radioThisMonth = (RadioButton) view.findViewById(R.id.radioThisMonth);
        radioCustom = (RadioButton) view.findViewById(R.id.radioCustom);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        String filterSelection = sharedPreferences.getString("order_filter_selection", "Everything");

        switch(Objects.requireNonNull(filterSelection))
        {
            case "Everything":
                radioEverything.setChecked(true);
                break;

            case "By Status":
                radioStatus.setChecked(true);
                break;

            case "This Week":
                radioThisWeek.setChecked(true);
                break;

            case "This Month":
                radioThisMonth.setChecked(true);
                break;

            case "Custom Range":
                radioCustom.setChecked(true);
                break;

        }

        applyFilter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final Calendar calendar = Calendar.getInstance();

                final int mYear = calendar.get(Calendar.YEAR);
                final int mMonth = calendar.get(Calendar.MONTH);
                final int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                String startDate;
                String endDate;

                // get selected radio button from radioGroup
                int selectedId = radioFilter.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                radioFilterButton = (RadioButton) view.findViewById(selectedId);

                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("order_filter_selection", radioFilterButton.getText().toString());
                editor.apply();

                switch(radioFilterButton.getText().toString())
                {
                    case "Everything":
                        startDate = "";
                        endDate = "";
                        mListener.SetFilter(startDate, endDate, "Everything");
                        dismiss();
                        break;

                    case "By Status":
                        SetByStatusFilter();
                        break;

                    case "This Week":
                        calendar.set(mYear, mMonth, mDay);
                        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                        startDate = sdf.format(calendar.getTime()).toString();
                        calendar.set(Calendar.DAY_OF_WEEK, 8);
                        endDate = sdf.format(calendar.getTime()).toString();
                        mListener.SetFilter(startDate, endDate, "This Week");
                        dismiss();
                        break;

                    case "This Month":
                        calendar.set(mYear, mMonth, mDay);
                        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                        startDate = sdf.format(calendar.getTime()).toString();
                        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                        endDate = sdf.format(calendar.getTime()).toString();
                        mListener.SetFilter(startDate, endDate, "This Month");
                        dismiss();
                        break;

                    case "Custom Range":
                        SetCustomFilter();
                        break;

                }
            }
        });

        cancelFilter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // Inflate the layout for this fragment

        return view;
    }

    public void SetByStatusFilter()
    {
        //Build List of Lead Status

        ArrayList<String> opportunityStatus =  new ArrayList<>();

        opportunityStatus.add("New (Not Issued)");
        opportunityStatus.add("Under Review");
        opportunityStatus.add("Confirmed");
        opportunityStatus.add("Cancelled");
        opportunityStatus.add("Invoiced");
        opportunityStatus.add("Sent");

        FragmentManager fm = getChildFragmentManager();
        FilterStatusDialog newFragment = FilterStatusDialog.newInstance(FilterOrdersDialog.this, opportunityStatus);
        newFragment.show(fm, "Filter Status");
    }

    @Override
    public void SetStatus(String status) {
        mListener.SetStatusFilter(status);
        dismiss();
    }

    public void SetCustomFilter() {

        final Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);

        String startDate;
        String endDate;

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                        c.set(year, monthOfYear, dayOfMonth);
                        final String startDate = sdf.format(c.getTime()).toString();

                        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                                new DatePickerDialog.OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                        @SuppressLint("SimpleDateFormat")
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                                        c.set(year, monthOfYear, dayOfMonth);
                                        final String endDate = sdf.format(c.getTime()).toString();

                                        //Send Back

                                        mListener.SetFilter(startDate, endDate, "Custom Date Range");
                                        dismiss();
                                    }
                                }, mYear, mMonth, mDay);

                        datePickerDialog.setTitle("End Date");
                        datePickerDialog.setMessage("End Date");
                        datePickerDialog.show();
                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.setTitle("Start Date");
        datePickerDialog.setMessage("Start Date");
        datePickerDialog.show();
    }
}