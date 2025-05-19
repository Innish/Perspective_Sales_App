package uk.co.perspective.app.dialogs;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import uk.co.perspective.app.R;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.entities.ProjectPhase;

public class NewProjectPhaseDialog extends DialogFragment {

    private View root;

    private EditText phaseName;
    private EditText description;
    private EditText startDate;
    private EditText endDate;

    Button okSelected;
    Button cancelSelected;

    NewProjectPhaseDialog.NewProjectPhaseListener mListener;

    int localProjectID;

    public interface NewProjectPhaseListener {
        public void NewProjectPhase(int LocalPhaseID, String PhaseName);
    }

    public NewProjectPhaseDialog() {
        // Required empty public constructor
    }

    public void setListener(NewProjectPhaseListener listener) {
        mListener = listener;
    }

    public void setLocalProjectID(int id) {
        localProjectID = id;
    }

    public static NewProjectPhaseDialog newInstance(NewProjectPhaseListener mListener, int localProjectID) {
        NewProjectPhaseDialog frag = new NewProjectPhaseDialog();
        Bundle args = new Bundle();
        frag.setListener(mListener);
        frag.setLocalProjectID(localProjectID);
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

        root = inflater.inflate(R.layout.dialog_new_project_phase, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        root.setBackgroundResource(R.drawable.dialog_rounded);

        phaseName = root.findViewById(R.id.phase_name);
        description = root.findViewById(R.id.description);
        startDate = root.findViewById(R.id.start_date);
        endDate = root.findViewById(R.id.end_date);

        //Buttons

        okSelected = root.findViewById(R.id.save);
        cancelSelected = root.findViewById(R.id.cancel);

        final Calendar startTime = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener dateTimeListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                startTime.set(Calendar.YEAR, year);
                startTime.set(Calendar.MONTH, monthOfYear);
                startTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

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

            if (!phaseName.getText().toString().equals("")) {

                //Save Contact

                ProjectPhase newPhase = new ProjectPhase();

                newPhase.setLocalProjectID(localProjectID);
                newPhase.setPhaseName(phaseName.getText().toString());
                newPhase.setDescription(description.getText().toString());

                Date phaseStartDate = new Date();
                Date phaseEndDate = new Date();

                SimpleDateFormat sourceDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
                SimpleDateFormat targetDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                try {
                    if (!startDate.getText().toString().equals("")) {
                        phaseStartDate = sourceDateFormat.parse(startDate.getText().toString());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                try {
                    if (!endDate.getText().toString().equals("")) {
                        phaseEndDate = sourceDateFormat.parse(endDate.getText().toString());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                assert phaseStartDate != null;
                newPhase.setStartDate(targetDateFormat.format(phaseStartDate));

                assert phaseEndDate != null;
                newPhase.setEndDate(targetDateFormat.format(phaseEndDate));

                newPhase.setIsNew(true);
                newPhase.setIsChanged(false);
                newPhase.setIsArchived(false);

                long ID = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                        .projectPhaseDao()
                        .insert(newPhase);

                mListener.NewProjectPhase((int)ID, phaseName.getText().toString());

                dismiss();
            }
            else
            {
                int pL = phaseName.getPaddingLeft();
                int pT = phaseName.getPaddingTop();
                int pR = phaseName.getPaddingRight();
                int pB = phaseName.getPaddingBottom();
                phaseName.setBackgroundResource(R.drawable.text_input_background_required);
                phaseName.setPadding(pL, pT, pR, pB);
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
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {

    }

}