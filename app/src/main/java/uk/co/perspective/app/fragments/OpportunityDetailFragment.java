package uk.co.perspective.app.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import uk.co.perspective.app.R;
import uk.co.perspective.app.activities.HostActivity;
import uk.co.perspective.app.adapters.ActivityRecyclerViewAdapter;
import uk.co.perspective.app.adapters.OpportunityContactsRecyclerViewAdapter;
import uk.co.perspective.app.adapters.OpportunityQuotesRecyclerViewAdapter;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.dialogs.ChangeContactDialog;
import uk.co.perspective.app.dialogs.ChangeCustomerDialog;
import uk.co.perspective.app.dialogs.EditActivityDialog;
import uk.co.perspective.app.dialogs.EditContactDialog;
import uk.co.perspective.app.dialogs.NewActivityDialog;
import uk.co.perspective.app.dialogs.NewQuoteDialog;
import uk.co.perspective.app.entities.Activity;
import uk.co.perspective.app.entities.Contact;
import uk.co.perspective.app.entities.Opportunity;
import uk.co.perspective.app.entities.OpportunityContact;

import uk.co.perspective.app.entities.OpportunityQuote;
import uk.co.perspective.app.helpers.GenericTouchHelper;

import uk.co.perspective.app.joins.JoinOpportunityContact;
import uk.co.perspective.app.joins.JoinOpportunityQuote;
import uk.co.perspective.app.models.SpinnerItem;

public class OpportunityDetailFragment extends Fragment implements
        //NewActivityDialog.NewActivityListener,
        //ActivityRecyclerViewAdapter.ActivityListener,
        //EditActivityDialog.UpdatedActivityListener,
        EditContactDialog.UpdatedContactListener,
        ChangeCustomerDialog.ChangeCustomerListener,
        ChangeContactDialog.ChangeContactListener,
        OpportunityContactsRecyclerViewAdapter.ContactListener,
        OpportunityQuotesRecyclerViewAdapter.QuoteListener,
        NewQuoteDialog.NewQuoteListener {

    private View root;

    private int ID;
    private int OpportunityID;
    private int LocalCustomerID;
    private String customerName;
    private int CustomerID;
    private int ContactID;
    private String CreatedByDisplayName;

    private EditText subject;
    private Spinner rating;
    private Spinner opportunityStatus;
    private EditText value;
    private EditText contactName;
    private EditText generalTelephone;
    private EditText generalEmail;
    private EditText notes;

    private Spinner probability;
    private EditText details;
    private EditText targetDate;

    private ImageView lookupContact;
    private ImageView callCustomer;
    private ImageView sendMessage;

    private ConstraintLayout customerLookup;
    private TextView customerLookupName;

    private RelativeLayout activityContainer;

    private OpportunityQuotesRecyclerViewAdapter mQuotesAdapter;
    private OpportunityContactsRecyclerViewAdapter mContactsAdapter;
    private ActivityRecyclerViewAdapter mActivityAdapter;

    private ItemTouchHelper mItemTouchHelper;

    private boolean drawVisible;

    private Button save;

    OpportunityDetailFragment.ChangeOpportunityListener mListener;

    public OpportunityDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void ContactChanged(int selectedLocalContactID, int contactID, String ContactName) {

        if (ContactName.equals("Linked Contact"))
        {
            if (selectedLocalContactID != 0) {

                OpportunityContact newOpportunityContact = new OpportunityContact();

                newOpportunityContact.setLocalContactID(selectedLocalContactID);
                newOpportunityContact.setLocalOpportunityID(ID);

                DatabaseClient.getInstance(requireContext())
                        .getAppDatabase()
                        .opportunityDao()
                        .insert(newOpportunityContact);

                refreshContacts();
            }
        }
        else {

            contactName.setText(ContactName);

            if (selectedLocalContactID != 0) {

                Contact contact = DatabaseClient
                        .getInstance(requireContext())
                        .getAppDatabase()
                        .contactDao()
                        .getContact(selectedLocalContactID);

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

    public interface ChangeOpportunityListener {
        void OpportunityChanged();
    }

    public void setListener(final OpportunityDetailFragment.ChangeOpportunityListener listener) {
        mListener = listener;
    }

    public static OpportunityDetailFragment newInstance(ChangeOpportunityListener mListener) {
        OpportunityDetailFragment fragment = new OpportunityDetailFragment();
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

        root =  inflater.inflate(R.layout.fragment_opportunity_detail, container, false);

        customerLookup = root.findViewById(R.id.customer_details);
        customerLookupName = root.findViewById(R.id.customer_name_label);

        subject = root.findViewById(R.id.subject);
        rating = root.findViewById(R.id.rating);
        opportunityStatus = root.findViewById(R.id.opportunity_status);
        value = root.findViewById(R.id.value);
        contactName = root.findViewById(R.id.contact_name);
        generalTelephone = root.findViewById(R.id.telephone_number);
        generalEmail = root.findViewById(R.id.general_email);
        notes = root.findViewById(R.id.notes);

        probability = root.findViewById(R.id.probability);
        details = root.findViewById(R.id.details);
        targetDate = root.findViewById(R.id.target_date);

        lookupContact = root.findViewById(R.id.lookupContact);
        callCustomer = root.findViewById(R.id.callCustomer);
        sendMessage = root.findViewById(R.id.sendMessage);

        save = root.findViewById(R.id.save);

        //Activity

//        if (root.findViewById(R.id.activity_list) != null) {
//
//            final RecyclerView activityRecyclerView = (RecyclerView) root.findViewById(R.id.activity_list);
//            Context activityRecyclerViewContext = activityRecyclerView.getContext();
//
//            activityRecyclerView.setLayoutManager(new LinearLayoutManager(activityRecyclerViewContext));
//            activityRecyclerView.setItemAnimator(new DefaultItemAnimator());
//
//            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
//                activityContainer = root.findViewById(R.id.activity_list_container);
//            }
//
//        }

        //Quotes

        final RecyclerView quotesRecyclerView = (RecyclerView) root.findViewById(R.id.quote_list);
        Context quotessRecyclerViewContext = quotesRecyclerView.getContext();

        quotesRecyclerView.setLayoutManager(new LinearLayoutManager(quotessRecyclerViewContext));
        quotesRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //Other Contacts

        final RecyclerView contactsRecyclerView = (RecyclerView) root.findViewById(R.id.contact_list);
        Context contactsRecyclerViewContext = contactsRecyclerView.getContext();

        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(contactsRecyclerViewContext));
        contactsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        customerLookup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            ChangeCustomerDialog newFragment = ChangeCustomerDialog.newInstance(OpportunityDetailFragment.this);
            newFragment.show(getChildFragmentManager(), "Change Customer");
            }
        });

        lookupContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            ChangeContactDialog newFragment = ChangeContactDialog.newInstance(OpportunityDetailFragment.this, LocalCustomerID, customerName);
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
            saveOpportunity();
            Snackbar saveSnackbar = Snackbar.make(v, "Opportunity was saved", Snackbar.LENGTH_SHORT);
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

    private void saveOpportunity()
    {
        //Save Customer

        Opportunity theUpdatedOpportunity = new Opportunity();

        theUpdatedOpportunity.setId(ID);
        theUpdatedOpportunity.setOpportunityID(OpportunityID);
        theUpdatedOpportunity.setCustomerID(CustomerID);
        theUpdatedOpportunity.setContactID(ContactID);
        theUpdatedOpportunity.setLocalCustomerID(LocalCustomerID);
        theUpdatedOpportunity.setCustomerName(customerName);

        theUpdatedOpportunity.setSubject(subject.getText().toString());

        SpinnerItem selectedRating = (SpinnerItem)rating.getSelectedItem();

        theUpdatedOpportunity.setRating(selectedRating.getValue());
        theUpdatedOpportunity.setStatus((String)opportunityStatus.getSelectedItem());

        theUpdatedOpportunity.setContactName(contactName.getText().toString());
        theUpdatedOpportunity.setTelephone(generalTelephone.getText().toString());
        theUpdatedOpportunity.setEmail(generalEmail.getText().toString());
        theUpdatedOpportunity.setValue(value.getText().toString());

        SpinnerItem selectedProbabilty = (SpinnerItem)probability.getSelectedItem();

        theUpdatedOpportunity.setProbability(selectedProbabilty.getValue());

        SimpleDateFormat sourceDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
        SimpleDateFormat targetDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

        Date theTargetDate = new Date();

        try {
            theTargetDate = sourceDateFormat.parse(targetDate.getText().toString());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        assert theTargetDate != null;
        theUpdatedOpportunity.setTargetDate(targetDateFormat.format(theTargetDate));

        theUpdatedOpportunity.setDetails(details.getText().toString());
        theUpdatedOpportunity.setNotes(notes.getText().toString());
        theUpdatedOpportunity.setCreatedByDisplayName(CreatedByDisplayName);
        theUpdatedOpportunity.setIsChanged(true);
        theUpdatedOpportunity.setIsNew(false);
        theUpdatedOpportunity.setIsArchived(false);

        Date updatedDate = new Date();

        theUpdatedOpportunity.setUpdated(targetDateFormat.format(updatedDate));

        DatabaseClient.getInstance(requireContext()).getAppDatabase()
                .opportunityDao()
                .update(theUpdatedOpportunity);

        if (mListener != null) {
            mListener.OpportunityChanged();
        }
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {

        drawVisible = false;

        if (ID != 0) {

            Opportunity theOpportunity = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .opportunityDao()
                    .getOpportunity(ID);

            OpportunityID = theOpportunity.getOpportunityID();
            CustomerID = theOpportunity.getCustomerID();
            ContactID = theOpportunity.getContactID();
            CreatedByDisplayName = theOpportunity.getCreatedByDisplayName();
            LocalCustomerID = theOpportunity.getLocalCustomerID();
            customerName = theOpportunity.getCustomerName();

            customerLookupName.setText(theOpportunity.getCustomerName());

            subject.setText(theOpportunity.getSubject());
            contactName.setText(theOpportunity.getContactName());
            generalTelephone.setText(theOpportunity.getTelephone());
            generalEmail.setText(theOpportunity.getEmail());
            value.setText(theOpportunity.getValue());
            details.setText(theOpportunity.getDetails());
            notes.setText(theOpportunity.getNotes());

            final Calendar myCalendar = Calendar.getInstance();

            final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    String myFormat = "dd/MM/yyyy";
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
                    targetDate.setText(sdf.format(myCalendar.getTime()));
                }
            };

            Date theTargetDate = new Date();

            SimpleDateFormat sourceDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);
            SimpleDateFormat targetDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);

            if (theOpportunity.getTargetDate() != null) {
                try {
                    theTargetDate = sourceDateFormat.parse(theOpportunity.getTargetDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            assert theTargetDate != null;
            targetDate.setText(targetDateFormat.format(theTargetDate));

            targetDate.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    new DatePickerDialog(requireContext(), dateSetListener, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });

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

            final ArrayList<String> opportunityStatusSpinnerArray =  new ArrayList<>();

            opportunityStatusSpinnerArray.add("Qualification");
            opportunityStatusSpinnerArray.add("Needs Analysis");
            opportunityStatusSpinnerArray.add("Proposal/Quote");
            opportunityStatusSpinnerArray.add("Negotiation/Review");
            opportunityStatusSpinnerArray.add("Follow-Up");
            opportunityStatusSpinnerArray.add("Closed Won");
            opportunityStatusSpinnerArray.add("Closed Lost");

            ArrayAdapter<String> opportunityStatusAdapter = new ArrayAdapter<String>(requireContext(), R.layout.dropdown_list_item, opportunityStatusSpinnerArray);
            opportunityStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            opportunityStatus.setAdapter(opportunityStatusAdapter);

            //Probability

            final ArrayList<SpinnerItem> probabilitySpinnerArray =  new ArrayList<>();

            probabilitySpinnerArray.add(new SpinnerItem(100, "100%"));
            probabilitySpinnerArray.add(new SpinnerItem(75,"75%"));
            probabilitySpinnerArray.add(new SpinnerItem(50,"50%"));
            probabilitySpinnerArray.add(new SpinnerItem(25,"25%"));
            probabilitySpinnerArray.add(new SpinnerItem(0,"0%"));

            ArrayAdapter<SpinnerItem> probabilityAdapter = new ArrayAdapter<SpinnerItem>(requireContext(), R.layout.dropdown_list_item, probabilitySpinnerArray);
            probabilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            probability.setAdapter(probabilityAdapter);

            if (theOpportunity.getRating() != null) {
                int ratingIndex = 0;
                int n = ratingAdapter.getCount();

                for (int i = 0; i < n; i++) {
                    SpinnerItem rating = (SpinnerItem) ratingAdapter.getItem(i);

                    if (rating.getValue() == theOpportunity.getRating()) {
                        ratingIndex = i;
                    }
                }

                rating.setSelection(ratingIndex);
            }

            if (theOpportunity.getStatus() != null) {
                int statusIndex = opportunityStatusAdapter.getPosition(theOpportunity.getStatus());
                opportunityStatus.setSelection(statusIndex);
            }

            if (theOpportunity.getProbability() != null) {
                int probabilityIndex = 0;
                int n = probabilityAdapter.getCount();

                for (int i = 0; i < n; i++) {
                    SpinnerItem prob = (SpinnerItem) probabilityAdapter.getItem(i);

                    if (prob.getValue() == theOpportunity.getProbability()) {
                        probabilityIndex = i;
                    }
                }

                probability.setSelection(probabilityIndex);
            }

            refreshQuotes();
            refreshContacts();

//            if (root.findViewById(R.id.activity_list) != null) {
//                refreshActivity();
//            }
        }
    }

    private void generateQuotesList(RecyclerView recyclerView, List<JoinOpportunityQuote> quotes) {

        quotes.add(new JoinOpportunityQuote(0, "New Quote"));

        mQuotesAdapter = new OpportunityQuotesRecyclerViewAdapter(quotes, getChildFragmentManager(), this.requireContext(), this);
        recyclerView.setAdapter(mQuotesAdapter);
    }

    private void generateContactsList(RecyclerView recyclerView, List<JoinOpportunityContact> contacts) {

        contacts.add(new JoinOpportunityContact(0, "New Contact"));

        mContactsAdapter = new OpportunityContactsRecyclerViewAdapter(contacts, getChildFragmentManager(), this.requireContext(), this);
        recyclerView.setAdapter(mContactsAdapter);

        ItemTouchHelper.Callback callback = new GenericTouchHelper(mContactsAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

//    private void generateActivityList(RecyclerView recyclerView, List<Activity> activities) {
//
//        activities.add(new Activity(0, "New Activity"));
//
//        mActivityAdapter = new ActivityRecyclerViewAdapter(activities, getChildFragmentManager(),this.requireContext(), this);
//        recyclerView.setAdapter(mActivityAdapter);
//    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.opportunity_detail, menu);

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

        if (id == R.id.action_won) {

            //Do the conversion

            if (getParentFragment() != null) {
                Snackbar saveSnackbar = Snackbar.make(getParentFragment().requireView(), "Closed Won", Snackbar.LENGTH_SHORT);
                saveSnackbar.show();
            }
        }
        else if (id == R.id.action_lost) {

            //Do the conversion

            if (getParentFragment() != null) {
                Snackbar saveSnackbar = Snackbar.make(getParentFragment().requireView(), "Closed Lost", Snackbar.LENGTH_SHORT);
                saveSnackbar.show();
            }
        }
        else if (id == R.id.action_save)
        {
            saveOpportunity();

            if (getParentFragment() != null) {
                Snackbar saveSnackbar = Snackbar.make(getParentFragment().requireView(), "Opportunity was saved", Snackbar.LENGTH_SHORT);
                saveSnackbar.show();
            }
            else
            {
                try {
                    Snackbar saveSnackbar = Snackbar.make(requireView(), "Opportunity was saved", Snackbar.LENGTH_SHORT);
                    saveSnackbar.show();
                } catch(Exception ignored) {}

            }
        }
        else if (id == R.id.action_activity) {

            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){

                if (drawVisible)
                {
                    activityContainer.animate().translationX(dpToPx(300)).setDuration(150).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            drawVisible = false;
                        }
                    }).setInterpolator(new LinearInterpolator());
                }
                else {
                    activityContainer.animate().translationX(-dpToPx(300)).setDuration(150).setListener(new AnimatorListenerAdapter() {
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
                    .setTitle("Archive Opportunity")
                    .setMessage("Are you sure you want to archive this opportunity? It will be removed after the next successful sync")
                    .setIcon(android.R.drawable.ic_delete)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {

                            DatabaseClient.getInstance(requireContext()).getAppDatabase()
                                    .opportunityDao()
                                    .archiveOpportunity(ID);

                            if (mListener != null) {
                                mListener.OpportunityChanged();
                            }

                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        }
        else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    public void refreshQuotes()
    {
        final RecyclerView contactsRecyclerView = (RecyclerView) root.findViewById(R.id.quote_list);

        //Get linked quotes

        List<JoinOpportunityQuote> quotes = DatabaseClient.getInstance(requireContext())
                .getAppDatabase()
                .opportunityDao()
                .getOpportunityQuotesByOpportunityID(OpportunityID);

        generateQuotesList(contactsRecyclerView, quotes);
    }

    public void refreshContacts()
    {
        final RecyclerView contactsRecyclerView = (RecyclerView) root.findViewById(R.id.contact_list);

        //Get Customer Contacts (temp)

        List<JoinOpportunityContact> contacts = DatabaseClient.getInstance(requireContext())
                .getAppDatabase()
                .opportunityDao()
                .getOpportunityContactsByOpportunityID(OpportunityID);

        generateContactsList(contactsRecyclerView, contacts);
    }

//    public void refreshActivity()
//    {
//        final RecyclerView activityRecyclerView = (RecyclerView) root.findViewById(R.id.activity_list);
//
//        if (CustomerID != 0) {
//
//            List<Activity> activities = DatabaseClient.getInstance(requireContext())
//                    .getAppDatabase()
//                    .activityDao()
//                    .getCustomerActivityByCustomerID(CustomerID);
//
//            generateActivityList(activityRecyclerView, activities);
//        }
//        else if (LocalCustomerID != 0) {
//
//            List<Activity> activities = DatabaseClient.getInstance(requireContext())
//                    .getAppDatabase()
//                    .activityDao()
//                    .getCustomerActivity(LocalCustomerID);
//
//            generateActivityList(activityRecyclerView, activities);
//        }
//    }

    @Override
    public void CreateNewJoinedContact() {
        ChangeContactDialog newFragment = ChangeContactDialog.newInstance(OpportunityDetailFragment.this, 0, "Linked Contact");
        newFragment.show(getChildFragmentManager(), "Add Contact");
    }

    @Override
    public void EditJoinedContact(int id) {
        Bundle bundle = new Bundle();
        bundle.putInt("ID", id);

        EditContactDialog newDialog = EditContactDialog.newInstance(this);
        newDialog.setArguments(bundle);
        newDialog.show(getChildFragmentManager(), "Edit Contact");
    }

    @Override
    public void RemoveJoinedContact(int localContactID) {

        DatabaseClient.getInstance(requireContext())
                .getAppDatabase()
                .opportunityDao()
                .deleteOpportunityContact(ID, localContactID);

    }

//    @Override
//    public void NewActivityAdded() {
//        refreshActivity();
//    }
//
//    @Override
//    public void CreateNewActivity() {
//
//        if (!customerName.equals("")) {
//            NewActivityDialog newDialog = NewActivityDialog.newInstance(this, LocalCustomerID, customerName);
//            newDialog.show(getChildFragmentManager(), "New Activity");
//        }
//    }
//
//    @Override
//    public void EditActivity(int id) {
//        Bundle bundle = new Bundle();
//        bundle.putInt("ID", id);
//
//        EditActivityDialog newDialog = EditActivityDialog.newInstance(this);
//        newDialog.setArguments(bundle);
//        newDialog.show(getChildFragmentManager(), "Edit Activity");
//    }
//
//    @Override
//    public void RemoveActivity() {
//        refreshActivity();
//    }

    @Override
    public void ContactUpdated() {
        refreshContacts();
    }

    @Override
    public void DefaultContactUpdated(String ContactName) {
        //ignore here
    }

    @Override
    public void CreateNewJoinedQuote() {
        NewQuoteDialog newDialog = NewQuoteDialog.newInstance(this, LocalCustomerID, customerName, ID);
        newDialog.show(getChildFragmentManager(), "New Quote");
    }

    @Override
    public void EditJoinedQuote(int id) {
        Intent intent = new Intent(root.getContext(), HostActivity.class);
        intent.putExtra("Target", "QuoteDetails");
        intent.putExtra("TargetID", id);
        root.getContext().startActivity(intent);
    }

    @Override
    public void RemoveJoinedQuote(int localQuoteID) {

        DatabaseClient.getInstance(requireContext())
                .getAppDatabase()
                .opportunityDao()
                .deleteOpportunityQuote(ID, localQuoteID);
    }

    @Override
    public void NewQuoteAdded(int LocalQuoteID) {

        //Add to Opportunity Quotes

        OpportunityQuote newOpportunityQuote = new OpportunityQuote();

        newOpportunityQuote.setLocalQuoteID(LocalQuoteID);
        newOpportunityQuote.setLocalOpportunityID(ID);

        DatabaseClient.getInstance(requireContext())
                .getAppDatabase()
                .opportunityDao()
                .insert(newOpportunityQuote);

        refreshQuotes();
    }

//    @Override
//    public void ActivityUpdated() {
//        refreshActivity();
//    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static float dpToPx(int dp) {
        return (int)dp * Resources.getSystem().getDisplayMetrics().density;
    }
}