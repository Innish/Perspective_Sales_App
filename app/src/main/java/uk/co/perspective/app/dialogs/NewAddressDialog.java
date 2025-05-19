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
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import uk.co.perspective.app.R;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.entities.Address;
import uk.co.perspective.app.entities.Customer;
import uk.co.perspective.app.models.SpinnerItem;

public class NewAddressDialog extends DialogFragment implements ChangeCustomerDialog.ChangeCustomerListener {

    private View root;

    private int LocalCustomerID;
    private String CustomerName;

    private EditText address;
    private Spinner addressType;

    private ConstraintLayout customerLookup;
    private TextView customerLookupName;

    Button okSelected;
    Button cancelSelected;

    NewAddressDialog.NewAddressListener mListener;

    public interface NewAddressListener {
        public void NewAddressAdded();
    }

    public NewAddressDialog() {
        // Required empty public constructor
    }

    public void setListener(NewAddressListener listener) {
        mListener = listener;
    }

    public void setCustomer(int ID, String customerName) {
        LocalCustomerID = ID;
        CustomerName = customerName;
    }

    public static NewAddressDialog newInstance(NewAddressListener mListener, int ID, String customerName) {
        NewAddressDialog frag = new NewAddressDialog();
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

        root = inflater.inflate(R.layout.dialog_new_address, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        root.setBackgroundResource(R.drawable.dialog_rounded);

        customerLookup = root.findViewById(R.id.customer_details);
        customerLookupName = root.findViewById(R.id.customer_name_label);

        addressType = root.findViewById(R.id.address_type);
        address = root.findViewById(R.id.address);

        //Buttons

        okSelected = root.findViewById(R.id.save);
        cancelSelected = root.findViewById(R.id.cancel);

        //Setup Spinners

        //Salutation

        final ArrayList<SpinnerItem> addressTypeSpinnerArray =  new ArrayList<>();

        addressTypeSpinnerArray.add(new SpinnerItem(1,"Primary"));
        addressTypeSpinnerArray.add(new SpinnerItem(2,"Billing Address"));
        addressTypeSpinnerArray.add(new SpinnerItem(3,"Delivery Address"));
        addressTypeSpinnerArray.add(new SpinnerItem(4,"Departmental"));
        addressTypeSpinnerArray.add(new SpinnerItem(5,"Other"));

        ArrayAdapter<SpinnerItem> addressTypeAdapter = new ArrayAdapter<SpinnerItem>(requireContext(), R.layout.dropdown_list_item, addressTypeSpinnerArray);
        addressTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addressType.setAdapter(addressTypeAdapter);

        customerLookup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            ChangeCustomerDialog newFragment = ChangeCustomerDialog.newInstance(NewAddressDialog.this);
            newFragment.show(getChildFragmentManager(), "Select Customer");
            }
        });

        okSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!address.getText().toString().equals("")) {

                    //Get CustomerID From LocalID

                    int customerID = 0;

                    Customer customer = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                            .customerDao()
                            .getCustomer(LocalCustomerID);

                    if (customer != null)
                    {
                        customerID = customer.getCustomerID();
                    }

                    //Save Addreess

                    Address newAddress = new Address();

                    newAddress.setAddressID(0);
                    newAddress.setCustomerID(customerID);
                    newAddress.setLocalCustomerID(LocalCustomerID);

                    SpinnerItem selectedAddressType = (SpinnerItem) addressType.getSelectedItem();
                    newAddress.setAddressType(selectedAddressType.getText());

                    newAddress.setAddress(address.getText().toString());
                    newAddress.setIsChanged(false);
                    newAddress.setIsNew(true);

                    Date updatedDate = new Date();

                    SimpleDateFormat targetDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                    newAddress.setUpdated(targetDateFormat.format(updatedDate));

                    DatabaseClient.getInstance(requireContext()).getAppDatabase()
                            .addressDao()
                            .insert(newAddress);

                    mListener.NewAddressAdded();

                    dismiss();
                }
                else
                {
                    int pL = address.getPaddingLeft();
                    int pT = address.getPaddingTop();
                    int pR = address.getPaddingRight();
                    int pB = address.getPaddingBottom();
                    address.setBackgroundResource(R.drawable.text_input_background_required);
                    address.setPadding(pL, pT, pR, pB);
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