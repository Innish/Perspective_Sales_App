package uk.co.perspective.app.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import uk.co.perspective.app.R;
import uk.co.perspective.app.adapters.ActivityRecyclerViewAdapter;
import uk.co.perspective.app.adapters.ContactsRecyclerViewAdapter;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.dialogs.ChangeContactDialog;
import uk.co.perspective.app.dialogs.ChangeCustomerDialog;
import uk.co.perspective.app.dialogs.EditActivityDialog;
import uk.co.perspective.app.dialogs.NewActivityDialog;
import uk.co.perspective.app.entities.Activity;
import uk.co.perspective.app.entities.Contact;
import uk.co.perspective.app.entities.Customer;
import uk.co.perspective.app.entities.Lead;
import uk.co.perspective.app.models.SpinnerItem;

public class LeadDetailFragment extends Fragment implements
        NewActivityDialog.NewActivityListener,
        ActivityRecyclerViewAdapter.ActivityListener,
        EditActivityDialog.UpdatedActivityListener,
        ChangeCustomerDialog.ChangeCustomerListener,
        ChangeContactDialog.ChangeContactListener{

    private View root;

    private int ID;
    private int LeadID;
    private int LocalCustomerID;
    private String customerName;
    private int CustomerID;
    private String CreatedByDisplayName;

    private EditText subject;
    private Spinner rating;
    private Spinner leadStatus;
    private EditText value;
    private EditText contactName;
    private EditText generalTelephone;
    private EditText generalEmail;
    private EditText notes;

    private ImageView lookupContact;
    private ImageView callCustomer;
    private ImageView sendMessage;

    private ConstraintLayout customerLookup;
    private TextView customerLookupName;

    private RelativeLayout activityContainer;

    private ContactsRecyclerViewAdapter mContactsAdapter;
    private ActivityRecyclerViewAdapter mActivityAdapter;

    private boolean drawVisible;

    private Button save;

    LeadDetailFragment.ChangeLeadListener mListener;

    public LeadDetailFragment() {
        // Required empty public constructor
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
            }
            else
            {
                generalTelephone.setText("");
            }

            if (contact.getEmail() != null) {
                generalEmail.setText(contact.getEmail());
            }
            else
            {
                generalEmail.setText("");
            }
        }
    }

    public interface ChangeLeadListener {
        void LeadChanged();
    }

    public void setListener(final LeadDetailFragment.ChangeLeadListener listener) {
        mListener = listener;
    }

    public static LeadDetailFragment newInstance(ChangeLeadListener mListener) {
        LeadDetailFragment fragment = new LeadDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.setListener(mListener);
        return fragment;
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

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root =  inflater.inflate(R.layout.fragment_lead_detail, container, false);

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

        lookupContact = root.findViewById(R.id.lookupContact);
        callCustomer = root.findViewById(R.id.callCustomer);
        sendMessage = root.findViewById(R.id.sendMessage);

        save = root.findViewById(R.id.save);

        //Activity

        if (root.findViewById(R.id.activity_list) != null) {

            final RecyclerView activityRecyclerView = (RecyclerView) root.findViewById(R.id.activity_list);
            Context activityRecyclerViewContext = activityRecyclerView.getContext();

            activityRecyclerView.setLayoutManager(new LinearLayoutManager(activityRecyclerViewContext));
            activityRecyclerView.setItemAnimator(new DefaultItemAnimator());

            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                activityContainer = root.findViewById(R.id.activity_list_container);
            }

        }

        customerLookup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeCustomerDialog newFragment = ChangeCustomerDialog.newInstance(LeadDetailFragment.this);
                newFragment.show(getChildFragmentManager(), "Change Customer");
            }
        });

        lookupContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeContactDialog newFragment = ChangeContactDialog.newInstance(LeadDetailFragment.this, LocalCustomerID, customerName);
                newFragment.show(getChildFragmentManager(), "Change Contact");
            }
        });

        callCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (!generalTelephone.getText().toString().equals("")) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + generalTelephone.getText().toString()));
                startActivity(intent);
            }
            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (!generalEmail.getText().toString().equals("")) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + generalEmail.getText().toString()));
                intent.putExtra(Intent.EXTRA_EMAIL, generalEmail.getText().toString());
                startActivity(intent);
            }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLead();
                Snackbar saveSnackbar = Snackbar.make(v, "Lead was saved", Snackbar.LENGTH_SHORT);
                saveSnackbar.show();
            }
        });

        return root;
    }

    @Override
    public void CustomerChanged(int customerID, String customerName) {

        this.LocalCustomerID = customerID;
        this.customerName = customerName;

        customerLookupName.setText(customerName);
        customerLookupName.setTextColor(Color.DKGRAY);
    }

    private void saveLead()
    {
        //Save Customer

        Lead theUpdatedLead = new Lead();

        theUpdatedLead.setId(ID);
        theUpdatedLead.setLeadID(LeadID);
        theUpdatedLead.setCustomerID(CustomerID);
        theUpdatedLead.setLocalCustomerID(LocalCustomerID);
        theUpdatedLead.setCustomerName(customerName);

        theUpdatedLead.setSubject(subject.getText().toString());

        SpinnerItem selectedRating = (SpinnerItem)rating.getSelectedItem();

        theUpdatedLead.setRating(selectedRating.getValue());
        theUpdatedLead.setStatus((String)leadStatus.getSelectedItem());

        theUpdatedLead.setContactName(contactName.getText().toString());
        theUpdatedLead.setTelephone(generalTelephone.getText().toString());
        theUpdatedLead.setEmail(generalEmail.getText().toString());
        theUpdatedLead.setValue(value.getText().toString());
        theUpdatedLead.setNotes(notes.getText().toString());
        theUpdatedLead.setCreatedByDisplayName(CreatedByDisplayName);
        theUpdatedLead.setIsChanged(true);
        theUpdatedLead.setIsNew(false);
        theUpdatedLead.setIsArchived(false);

        Date updatedDate = new Date();

        SimpleDateFormat targetDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

        theUpdatedLead.setUpdated(targetDateFormat.format(updatedDate));

        DatabaseClient.getInstance(requireContext()).getAppDatabase()
                .leadDao()
                .update(theUpdatedLead);

        if (mListener != null) {
            mListener.LeadChanged();
        }
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {

        drawVisible = false;

        if (ID != 0) {

            Lead theLead = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .leadDao()
                    .getLead(ID);

            LeadID = theLead.getLeadID();
            CustomerID = theLead.getCustomerID();

            if (theLead.getLocalCustomerID() != null)
            {
                LocalCustomerID = theLead.getLocalCustomerID();
            }
            else
            {
                LocalCustomerID = 0;
            }

            if (LocalCustomerID == 0)
            {
                Customer localCustomer = DatabaseClient.getInstance(requireContext())
                        .getAppDatabase()
                        .customerDao()
                        .getCustomerByCustomerID(CustomerID);

                if (localCustomer != null)
                {
                    LocalCustomerID = localCustomer.getId();
                }
            }

            customerName = theLead.getCustomerName();

            customerLookupName.setText(theLead.getCustomerName());

            subject.setText(theLead.getSubject());
            contactName.setText(theLead.getContactName());
            generalTelephone.setText(theLead.getTelephone());
            generalEmail.setText(theLead.getEmail());
            value.setText(theLead.getValue());
            notes.setText(theLead.getNotes());
            CreatedByDisplayName = theLead.getCreatedByDisplayName();
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

            final ArrayList<String> leadStatusSpinnerArray =  new ArrayList<>();

            leadStatusSpinnerArray.add("New (No Contact)");
            leadStatusSpinnerArray.add("Attempted Contact");
            leadStatusSpinnerArray.add("Contacted");
            leadStatusSpinnerArray.add("Establishing Qualification");
            leadStatusSpinnerArray.add("Opportunity Identified");
            leadStatusSpinnerArray.add("Disqualified");

            ArrayAdapter<String> leadStatusAdapter = new ArrayAdapter<String>(requireContext(), R.layout.dropdown_list_item, leadStatusSpinnerArray);
            leadStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            leadStatus.setAdapter(leadStatusAdapter);

            if (theLead.getRating() != null) {
                int ratingIndex = 0;
                int n = ratingAdapter.getCount();

                for (int i = 0; i < n; i++) {
                    SpinnerItem rating = (SpinnerItem) ratingAdapter.getItem(i);

                    if (rating.getValue() == theLead.getRating()) {
                        ratingIndex = i;
                    }
                }

                rating.setSelection(ratingIndex);
            }

            if (theLead.getStatus() != null) {
                int statusIndex = leadStatusAdapter.getPosition(theLead.getStatus());
                leadStatus.setSelection(statusIndex);
            }

            if (root.findViewById(R.id.activity_list) != null) {
                refreshActivity();
            }
        }
    }

//    private void generateContactsList(RecyclerView recyclerView, List<Contact> contacts) {
//
//        contacts.add(new Contact(0, "New Contact"));
//
//        mContactsAdapter = new ContactsRecyclerViewAdapter(contacts, getChildFragmentManager(),this.getContext(), this);
//        recyclerView.setAdapter(mContactsAdapter);
//    }

    private void generateActivityList(RecyclerView recyclerView, List<Activity> activities) {

        activities.add(new Activity(0, "New Activity"));

        mActivityAdapter = new ActivityRecyclerViewAdapter(activities, getChildFragmentManager(),this.getContext(), this);
        recyclerView.setAdapter(mActivityAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.lead_detail, menu);

        MenuItem item = menu.findItem(R.id.action_activity);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            item.setVisible(false);
        }

        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items

        int id = item.getItemId();

        if (id == R.id.action_convert) {

            //Do the conversion to an opportunity

            if (getParentFragment() != null) {

                Snackbar saveSnackbar = Snackbar.make(getParentFragment().requireView(), "Converted", Snackbar.LENGTH_SHORT);
                saveSnackbar.show();
            }
        }
        else if (id == R.id.action_save)
        {
            saveLead();

            if (getParentFragment() != null) {
                Snackbar saveSnackbar = Snackbar.make(getParentFragment().requireView(), "Lead was saved", Snackbar.LENGTH_SHORT);
                saveSnackbar.show();
            }
            else
            {
                try {
                    Snackbar saveSnackbar = Snackbar.make(requireView(), "Lead was saved", Snackbar.LENGTH_SHORT);
                    saveSnackbar.show();
                } catch(Exception ignored) {}

            }
        }
        else if (id == R.id.action_activity) {

            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){

                if (drawVisible)
                {
                    activityContainer.animate().translationX(dpToPx(335)).setDuration(150).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            drawVisible = false;
                        }
                    }).setInterpolator(new LinearInterpolator());
                }
                else {
                    activityContainer.animate().translationX(-dpToPx(335)).setDuration(150).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            drawVisible = true;
                        }
                    }).setInterpolator(new LinearInterpolator());
                }
            }

        }
        else if (id == R.id.action_archive) {

            new AlertDialog.Builder(requireContext())
                    .setTitle("Archive Lead")
                    .setMessage("Are you sure you want to archive this lead? It will be removed after the next successful sync")
                    .setIcon(android.R.drawable.ic_delete)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {

                            DatabaseClient.getInstance(requireContext()).getAppDatabase()
                                    .leadDao()
                                    .archiveLead(ID);

                            if (mListener != null) {
                                mListener.LeadChanged();
                            }

                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        }
        else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

//    public void refreshContacts()
//    {
//        final RecyclerView contactsRecyclerView = (RecyclerView) root.findViewById(R.id.contact_list);
//
//        //Get Customer Contacts
//
//        List<Contact> contacts = DatabaseClient.getInstance(requireContext())
//                .getAppDatabase()
//                .contactDao()
//                .getCustomerContacts(LocalCustomerID);
//
//        generateContactsList(contactsRecyclerView, contacts);
//    }

    public void refreshActivity()
    {
        final RecyclerView activityRecyclerView = (RecyclerView) root.findViewById(R.id.activity_list);

        if (CustomerID != 0) {

            List<Activity> activities = DatabaseClient.getInstance(requireContext())
                    .getAppDatabase()
                    .activityDao()
                    .getCustomerActivityByCustomerID(CustomerID);

            generateActivityList(activityRecyclerView, activities);
        }
        else if (LocalCustomerID != 0) {

            List<Activity> activities = DatabaseClient.getInstance(requireContext())
                    .getAppDatabase()
                    .activityDao()
                    .getCustomerActivity(LocalCustomerID);

            generateActivityList(activityRecyclerView, activities);
        }
    }

//    @Override
//    public void NewContact(String ContactName)
//    {
//        refreshContacts();
//    }
//
//    @Override
//    public void CreateNewContact() {
//        NewContactDialog newDialog = NewContactDialog.newInstance(this, 0, "");
//        newDialog.show(getChildFragmentManager(), "New Contact");
//    }
//
//    @Override
//    public void EditContact(int id) {
//        Bundle bundle = new Bundle();
//        bundle.putInt("ID", id);
//
//        EditContactDialog newDialog = EditContactDialog.newInstance(this);
//        newDialog.setArguments(bundle);
//        newDialog.show(getChildFragmentManager(), "Edit Contact");
//    }

//    @Override
//    public void RemoveContact() {
//        refreshContacts();
//    }

    @Override
    public void NewActivityAdded() {
        refreshActivity();
    }

    @Override
    public void CreateNewActivity() {

        if (!customerName.equals("")) {
            NewActivityDialog newDialog = NewActivityDialog.newInstance(this, LocalCustomerID, customerName);
            newDialog.show(getChildFragmentManager(), "New Activity");
        }
    }

    @Override
    public void EditActivity(int id) {
        Bundle bundle = new Bundle();
        bundle.putInt("ID", id);

        EditActivityDialog newDialog = EditActivityDialog.newInstance(this);
        newDialog.setArguments(bundle);
        newDialog.show(getChildFragmentManager(), "Edit Activity");
    }

    @Override
    public void RemoveActivity() {
        refreshActivity();
    }

//    @Override
//    public void ContactUpdated() {
//        refreshContacts();
//    }

//    @Override
//    public void DefaultContactUpdated(String ContactName) {
//
//    }

    @Override
    public void ActivityUpdated() {
        refreshActivity();
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int dpToPx(int dp) {
        return (int)dp * ((int) Resources.getSystem().getDisplayMetrics().density);
    }
}