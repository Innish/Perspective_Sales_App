package uk.co.perspective.app.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import uk.co.perspective.app.R;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.entities.Customer;
import uk.co.perspective.app.models.SpinnerItem;

public class NewCustomerDialog extends DialogFragment {

    private View root;

    private EditText customerName;
    private EditText customerReference;
    private Spinner customerType;
    private Spinner customerStatus;
    private EditText generalTelephone;
    private EditText generalEmail;
    private EditText notes;

    Button okSelected;
    Button cancelSelected;

    NewCustomerDialog.NewCustomerListener mListener;

    public interface NewCustomerListener {
        public void NewCustomer();
    }

    public NewCustomerDialog() {
        // Required empty public constructor
    }

    public void setListener(NewCustomerListener listener) {
        mListener = listener;
    }

    public static NewCustomerDialog newInstance(NewCustomerListener mListener) {
        NewCustomerDialog frag = new NewCustomerDialog();
        Bundle args = new Bundle();
        frag.setListener(mListener);
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

        root = inflater.inflate(R.layout.dialog_new_customer, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        root.setBackgroundResource(R.drawable.dialog_rounded);

        customerName = root.findViewById(R.id.customer_name);
        customerReference = root.findViewById(R.id.customer_reference);
        customerType = root.findViewById(R.id.customer_type);
        customerStatus = root.findViewById(R.id.customer_status);
        generalTelephone = root.findViewById(R.id.general_telephone);
        generalEmail = root.findViewById(R.id.general_email);
        notes = root.findViewById(R.id.notes);

        //Buttons

        okSelected = root.findViewById(R.id.save);
        cancelSelected = root.findViewById(R.id.cancel);

        //Setup Spinners

        //Customer Type

        final ArrayList<SpinnerItem> customerTypeSpinnerArray =  new ArrayList<>();

        customerTypeSpinnerArray.add(new SpinnerItem(1, "Limted Company"));
        customerTypeSpinnerArray.add(new SpinnerItem(2,"Limited Liability Partnership"));
        customerTypeSpinnerArray.add(new SpinnerItem(3,"Sole Trader"));
        customerTypeSpinnerArray.add(new SpinnerItem(4,"Individual"));

        ArrayAdapter<SpinnerItem> customerTypeAdapter = new ArrayAdapter<SpinnerItem>(requireContext(), R.layout.dropdown_list_item, customerTypeSpinnerArray);
        customerTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        customerType.setAdapter(customerTypeAdapter);

        //Customer Status

        final ArrayList<SpinnerItem> customerStatusSpinnerArray =  new ArrayList<>();

        customerStatusSpinnerArray.add(new SpinnerItem(1, "Active"));
        customerStatusSpinnerArray.add(new SpinnerItem(2,"Non Customer"));
        customerStatusSpinnerArray.add(new SpinnerItem(3,"Pending"));

        ArrayAdapter<SpinnerItem> customerStatusAdapter = new ArrayAdapter<SpinnerItem>(requireContext(), R.layout.dropdown_list_item, customerStatusSpinnerArray);
        customerStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        customerStatus.setAdapter(customerStatusAdapter);

        okSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!customerName.getText().toString().equals("")) {

                    //Save Customer

                    Customer newCustomer = new Customer();

                    newCustomer.setCustomerID(0);
                    newCustomer.setCustomerName(customerName.getText().toString());
                    newCustomer.setCustomerReference(customerReference.getText().toString());

                    SpinnerItem selectedCustomerType = (SpinnerItem) customerType.getSelectedItem();
                    newCustomer.setCustomerStatus(selectedCustomerType.getText());

                    SpinnerItem selectedCustomerStatus = (SpinnerItem) customerStatus.getSelectedItem();
                    newCustomer.setCustomerStatus(selectedCustomerStatus.getText());

                    newCustomer.setGeneralTelephone(generalTelephone.getText().toString());
                    newCustomer.setGeneralEmail(generalEmail.getText().toString());
                    newCustomer.setNotes(notes.getText().toString());
                    newCustomer.setIsChanged(false);
                    newCustomer.setIsNew(true);

                    Date updatedDate = new Date();

                    SimpleDateFormat targetDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                    newCustomer.setUpdated(targetDateFormat.format(updatedDate));

                    DatabaseClient.getInstance(requireContext()).getAppDatabase()
                            .customerDao()
                            .insert(newCustomer);

                    mListener.NewCustomer();

                    dismiss();
                }
                else
                {
                    int pL = customerName.getPaddingLeft();
                    int pT = customerName.getPaddingTop();
                    int pR = customerName.getPaddingRight();
                    int pB = customerName.getPaddingBottom();
                    customerName.setBackgroundResource(R.drawable.text_input_background_required);
                    customerName.setPadding(pL, pT, pR, pB);
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