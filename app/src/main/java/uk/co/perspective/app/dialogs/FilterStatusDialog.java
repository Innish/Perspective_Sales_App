package uk.co.perspective.app.dialogs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import uk.co.perspective.app.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FilterStatusDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterStatusDialog extends DialogFragment {

    Spinner selectedStatus;

    Button applyFilter;
    Button cancelFilter;

    FilterStatusDialog.SetFilterListener mListener;
    ArrayList<String> StatusList;

    public interface SetFilterListener {
        public void SetStatus (String Status);
    }

    public FilterStatusDialog() {
        // Required empty public constructor
    }

    public static FilterStatusDialog newInstance(SetFilterListener mListener, ArrayList<String> statusList) {
        FilterStatusDialog fragment = new FilterStatusDialog();
        fragment.setListener(mListener);
        fragment.setStatusList(statusList);
        return fragment;
    }

    public void setListener(FilterStatusDialog.SetFilterListener listener) {
        mListener = listener;
    }

    public void setStatusList(ArrayList<String> statusList) {
        this.StatusList = statusList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view =  inflater.inflate(R.layout.dialog_filter_by_status, container, false);

        selectedStatus = (Spinner)view.findViewById(R.id.status);
        applyFilter = (Button)view.findViewById(R.id.setFilter);
        cancelFilter = (Button)view.findViewById(R.id.cancelSetFilter);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        String filterSelection = sharedPreferences.getString("lead_status_selection", "Everything");

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<String>(requireContext(), R.layout.dropdown_list_item, StatusList);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectedStatus.setAdapter(statusAdapter);

        //Set Spinner Values

        if (!filterSelection.equals("")) {
            int statusIndex = statusAdapter.getPosition(filterSelection);
            selectedStatus.setSelection(statusIndex);
        }

        applyFilter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putString("lead_status_selection", selectedStatus.getSelectedItem().toString());
//            editor.apply();

                mListener.SetStatus(selectedStatus.getSelectedItem().toString());

                dismiss();
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

}