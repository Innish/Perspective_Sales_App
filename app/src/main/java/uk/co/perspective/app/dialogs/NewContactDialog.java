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
import android.widget.CheckBox;
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
import uk.co.perspective.app.entities.Contact;
import uk.co.perspective.app.entities.Customer;
import uk.co.perspective.app.models.SpinnerItem;

public class NewContactDialog extends DialogFragment implements ChangeCustomerDialog.ChangeCustomerListener {

    private View root;

    private int LocalCustomerID;
    private String CustomerName;

    private EditText contactName;
    private Spinner salutation;
    private EditText jobTitle;
    private EditText telephone;
    private EditText mobile;
    private EditText email;
    private EditText notes;
    private CheckBox defaultContact;

    private ConstraintLayout customerLookup;
    private TextView customerLookupName;

    Button okSelected;
    Button cancelSelected;

    NewContactDialog.NewContactListener mListener;

    public interface NewContactListener {
        public void NewContact(String ContactName);
    }

    public NewContactDialog() {
        // Required empty public constructor
    }

    public void setListener(NewContactListener listener) {
        mListener = listener;
    }

    public void setCustomer(int ID, String customerName) {
        LocalCustomerID = ID;
        CustomerName = customerName;
    }

    public static NewContactDialog newInstance(NewContactListener mListener, int customerID, String customerName) {
        NewContactDialog frag = new NewContactDialog();
        Bundle args = new Bundle();
        frag.setListener(mListener);
        frag.setCustomer(customerID, customerName);
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

        root = inflater.inflate(R.layout.dialog_new_contact, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        root.setBackgroundResource(R.drawable.dialog_rounded);

        customerLookup = root.findViewById(R.id.customer_details);
        customerLookupName = root.findViewById(R.id.customer_name_label);

        contactName = root.findViewById(R.id.contact_name);
        salutation = root.findViewById(R.id.salutation);
        jobTitle = root.findViewById(R.id.job_title);
        telephone = root.findViewById(R.id.telephone_number);
        mobile = root.findViewById(R.id.mobile_number);
        email= root.findViewById(R.id.email_address);
        notes = root.findViewById(R.id.notes);
        defaultContact = root.findViewById(R.id.default_contact);

        //Buttons

        okSelected = root.findViewById(R.id.save);
        cancelSelected = root.findViewById(R.id.cancel);

        //Setup Spinners

        //Salutation

        final ArrayList<SpinnerItem> salutationSpinnerArray =  new ArrayList<>();

        salutationSpinnerArray.add(new SpinnerItem(1,""));
        salutationSpinnerArray.add(new SpinnerItem(2,"Mx"));
        salutationSpinnerArray.add(new SpinnerItem(3,"Mr"));
        salutationSpinnerArray.add(new SpinnerItem(4,"Mrs"));
        salutationSpinnerArray.add(new SpinnerItem(5,"Miss"));
        salutationSpinnerArray.add(new SpinnerItem(6,"Dr"));
        salutationSpinnerArray.add(new SpinnerItem(7,"Ms"));
        salutationSpinnerArray.add(new SpinnerItem(8,"Prof"));
        salutationSpinnerArray.add(new SpinnerItem(9,"Rev"));
        salutationSpinnerArray.add(new SpinnerItem(10,"Lady"));
        salutationSpinnerArray.add(new SpinnerItem(11,"Sir"));
        salutationSpinnerArray.add(new SpinnerItem(12,"The Hon"));
        salutationSpinnerArray.add(new SpinnerItem(13,"Judge"));
        salutationSpinnerArray.add(new SpinnerItem(14,"Lord"));

        ArrayAdapter<SpinnerItem> salutationAdapter = new ArrayAdapter<SpinnerItem>(requireContext(), R.layout.dropdown_list_item, salutationSpinnerArray);
        salutationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        salutation.setAdapter(salutationAdapter);

        customerLookup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            ChangeCustomerDialog newFragment = ChangeCustomerDialog.newInstance(NewContactDialog.this);
            newFragment.show(getChildFragmentManager(), "Select Customer");
            }
        });

        okSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!contactName.getText().toString().equals("")) {

                    //Get customerID from localID

                    int customerID = 0;

                    Customer customer = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                            .customerDao()
                            .getCustomer(LocalCustomerID);

                    if (customer != null)
                    {
                        customerID = customer.getCustomerID();
                    }

                    //Save Contact

                    Contact newContact = new Contact();

                    newContact.setContactID(0);
                    newContact.setCustomerID(customerID);
                    newContact.setLocalCustomerID(LocalCustomerID);
                    newContact.setCustomerName(CustomerName);

                    newContact.setContactName(contactName.getText().toString());

                    SpinnerItem selectedSalutation = (SpinnerItem) salutation.getSelectedItem();
                    newContact.setSalutation(selectedSalutation.getText());
                    newContact.setJobTitle(jobTitle.getText().toString());
                    newContact.setTelephone(telephone.getText().toString());
                    newContact.setMobile(mobile.getText().toString());
                    newContact.setEmail(email.getText().toString());
                    newContact.setNotes(notes.getText().toString());

                    newContact.setIsChanged(false);
                    newContact.setIsNew(true);

                    Date updatedDate = new Date();

                    SimpleDateFormat targetDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                    newContact.setUpdated(targetDateFormat.format(updatedDate));

                    DatabaseClient.getInstance(requireContext()).getAppDatabase()
                            .contactDao()
                            .insert(newContact);

                    //is this the default contact?

                    if (defaultContact.isChecked())
                    {
                        if (LocalCustomerID != 0) {
                            DatabaseClient.getInstance(requireContext()).getAppDatabase()
                                    .customerDao()
                                    .updateCustomerContactName(contactName.getText().toString(), LocalCustomerID);

                            mListener.NewContact(contactName.getText().toString());
                        }
                        else
                        {
                            mListener.NewContact("");
                        }
                    }
                    else
                    {
                        mListener.NewContact(contactName.getText().toString());
                    }

                    dismiss();
                }
                else
                {
                    int pL = contactName.getPaddingLeft();
                    int pT = contactName.getPaddingTop();
                    int pR = contactName.getPaddingRight();
                    int pB = contactName.getPaddingBottom();
                    contactName.setBackgroundResource(R.drawable.text_input_background_required);
                    contactName.setPadding(pL, pT, pR, pB);
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