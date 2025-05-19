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
import android.widget.ImageView;
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
import uk.co.perspective.app.entities.Lead;
import uk.co.perspective.app.models.SpinnerItem;

public class NewLeadDialog extends DialogFragment implements ChangeCustomerDialog.ChangeCustomerListener, ChangeContactDialog.ChangeContactListener {

    private View root;

    private int LocalCustomerID;
    private String CustomerName;

    private EditText subject;
    private Spinner rating;
    private Spinner leadStatus;
    private EditText value;
    private EditText contactName;
    private EditText generalTelephone;
    private EditText generalEmail;
    private EditText notes;

    private ImageView lookupContact;

    private ConstraintLayout customerLookup;
    private TextView customerLookupName;

    Button okSelected;
    Button cancelSelected;

    NewLeadDialog.NewLeadListener mListener;



    public interface NewLeadListener {
        public void NewLead();
    }

    public NewLeadDialog() {
        // Required empty public constructor
    }

    public void setListener(NewLeadListener listener) {
        mListener = listener;
    }

    public void setCustomer(int ID, String customerName) {
        LocalCustomerID = ID;
        CustomerName = customerName;
    }

    public static NewLeadDialog newInstance(NewLeadListener mListener, int customerID, String customerName) {
        NewLeadDialog frag = new NewLeadDialog();
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

        root = inflater.inflate(R.layout.dialog_new_lead, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        root.setBackgroundResource(R.drawable.dialog_rounded);

        customerLookup = root.findViewById(R.id.customer_details);
        customerLookupName = root.findViewById(R.id.customer_name_label);

        subject = root.findViewById(R.id.subject);
        rating = root.findViewById(R.id.rating);
        leadStatus = root.findViewById(R.id.lead_status);
        value = root.findViewById(R.id.value);
        contactName = root.findViewById(R.id.contact_name);
        generalTelephone = root.findViewById(R.id.telephone_number);
        generalEmail = root.findViewById(R.id.general_email);
        notes = root.findViewById(R.id.notes);

        lookupContact = root.findViewById(R.id.lookup_contact);

        //Buttons

        okSelected = root.findViewById(R.id.save);
        cancelSelected = root.findViewById(R.id.cancel);

        //Setup Spinners

        //Rating

        final ArrayList<SpinnerItem> ratingSpinnerArray =  new ArrayList<>();

        ratingSpinnerArray.add(new SpinnerItem(100, "Hot"));
        ratingSpinnerArray.add(new SpinnerItem(80,"Warm"));
        ratingSpinnerArray.add(new SpinnerItem(60,"Moderate"));
        ratingSpinnerArray.add(new SpinnerItem(40,"Cool"));
        ratingSpinnerArray.add(new SpinnerItem(20,"Cold"));

        ArrayAdapter<SpinnerItem> ratingAdapter = new ArrayAdapter<SpinnerItem>(requireContext(), R.layout.dropdown_list_item, ratingSpinnerArray);
        ratingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rating.setAdapter(ratingAdapter);

        //Lead Status

        final ArrayList<SpinnerItem> leadStatusSpinnerArray =  new ArrayList<>();

        leadStatusSpinnerArray.add(new SpinnerItem(1,"New (No Contact)"));
        leadStatusSpinnerArray.add(new SpinnerItem(2, "Attempted Contact"));
        leadStatusSpinnerArray.add(new SpinnerItem(3, "Contacted"));
        leadStatusSpinnerArray.add(new SpinnerItem(4, "Establishing Qualification"));
        leadStatusSpinnerArray.add(new SpinnerItem(5, "Opportunity Identified"));
        leadStatusSpinnerArray.add(new SpinnerItem(6,"Disqualified"));

        ArrayAdapter<SpinnerItem> leadStatusAdapter = new ArrayAdapter<SpinnerItem>(requireContext(), R.layout.dropdown_list_item, leadStatusSpinnerArray);
        leadStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        leadStatus.setAdapter(leadStatusAdapter);

        customerLookup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            ChangeCustomerDialog newFragment = ChangeCustomerDialog.newInstance(NewLeadDialog.this);
            newFragment.show(getChildFragmentManager(), "Select Customer");
            }
        });

        lookupContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeContactDialog newFragment = ChangeContactDialog.newInstance(NewLeadDialog.this, LocalCustomerID, CustomerName);
                newFragment.show(getChildFragmentManager(), "Select Contact");
            }
        });

        okSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            if (!subject.getText().toString().equals("")) {

                //Save Customer

                Lead newLead = new Lead();

                newLead.setLeadID(0);
                newLead.setContactID(0);
                newLead.setCustomerID(0);
                newLead.setLocalCustomerID(LocalCustomerID);
                newLead.setCustomerName(CustomerName);

                newLead.setSubject(subject.getText().toString());

                SpinnerItem selectedRating = (SpinnerItem) rating.getSelectedItem();
                newLead.setRating(selectedRating.getValue());

                SpinnerItem selectedStatus = (SpinnerItem) leadStatus.getSelectedItem();
                newLead.setStatus(selectedStatus.getText());

                newLead.setValue(value.getText().toString());

                newLead.setContactName(contactName.getText().toString());
                newLead.setTelephone(generalTelephone.getText().toString());
                newLead.setEmail(generalEmail.getText().toString());
                newLead.setNotes(notes.getText().toString());
                newLead.setIsChanged(false);
                newLead.setIsNew(true);
                newLead.setIsArchived(false);

                Date updatedDate = new Date();

                SimpleDateFormat targetDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                newLead.setUpdated(targetDateFormat.format(updatedDate));

                DatabaseClient.getInstance(requireContext()).getAppDatabase()
                        .leadDao()
                        .insert(newLead);

                mListener.NewLead();

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

    @Override
    public void ContactChanged(int ID, int contactID, String ContactName) {

        contactName.setText(ContactName);

        if (ID != 0) {

            Contact contact = DatabaseClient
                    .getInstance(requireContext())
                    .getAppDatabase()
                    .contactDao()
                    .getContact(ID);

            if (contact.getTelephone() != null) {
                generalTelephone.setText(contact.getTelephone());
            } else {
                generalTelephone.setText("");
            }

            if (contact.getEmail() != null) {
                generalEmail.setText(contact.getEmail());
            } else {
                generalEmail.setText("");
            }
        }
    }
}