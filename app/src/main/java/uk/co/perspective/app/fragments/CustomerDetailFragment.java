package uk.co.perspective.app.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import uk.co.perspective.app.R;
import uk.co.perspective.app.adapters.ActivityRecyclerViewAdapter;
import uk.co.perspective.app.adapters.AddressRecyclerViewAdapter;
import uk.co.perspective.app.adapters.ContactsRecyclerViewAdapter;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.dialogs.EditActivityDialog;
import uk.co.perspective.app.dialogs.EditAddressDialog;
import uk.co.perspective.app.dialogs.EditContactDialog;
import uk.co.perspective.app.dialogs.NewActivityDialog;
import uk.co.perspective.app.dialogs.NewAddressDialog;
import uk.co.perspective.app.dialogs.NewContactDialog;
import uk.co.perspective.app.entities.Activity;
import uk.co.perspective.app.entities.Address;
import uk.co.perspective.app.entities.Contact;
import uk.co.perspective.app.entities.Customer;

public class CustomerDetailFragment extends Fragment implements NewContactDialog.NewContactListener,
        ContactsRecyclerViewAdapter.ContactListener,
        NewAddressDialog.NewAddressListener,
        AddressRecyclerViewAdapter.AddressListener,
        NewActivityDialog.NewActivityListener,
        ActivityRecyclerViewAdapter.ActivityListener,
        EditContactDialog.UpdatedContactListener,
        EditAddressDialog.UpdatedAddressListener,
        EditActivityDialog.UpdatedActivityListener {

    private View root;

    private int ID;
    private int CustomerID;
    private String primaryContactName;
    private String parentCustomerName;

    private TextView customerNameLabel;
    private EditText customerName;
    private EditText generalTelephone;
    private EditText generalEmail;
    private EditText reference;
    private EditText notes;

    private ImageView callCustomer;
    private ImageView sendMessage;

    private RelativeLayout activityContainer;

    private ContactsRecyclerViewAdapter mContactsAdapter;
    private AddressRecyclerViewAdapter mAddressAdapter;
    private ActivityRecyclerViewAdapter mActivityAdapter;

    private boolean drawVisible;

    private Button save;

    CustomerDetailFragment.ChangeCustomerListener mListener;

    public CustomerDetailFragment() {
        // Required empty public constructor
    }

    public interface ChangeCustomerListener {
        void CustomerChanged();
    }

    public void setListener(final CustomerDetailFragment.ChangeCustomerListener listener) {
        mListener = listener;
    }

    public static CustomerDetailFragment newInstance(ChangeCustomerListener mListener) {
        CustomerDetailFragment fragment = new CustomerDetailFragment();
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

        root =  inflater.inflate(R.layout.fragment_customer_detail, container, false);

        customerNameLabel = root.findViewById(R.id.customer_name_label);
        customerName = root.findViewById(R.id.customer_name);
        generalTelephone = root.findViewById(R.id.general_telephone);
        generalEmail = root.findViewById(R.id.general_email);
        reference = root.findViewById(R.id.reference);
        notes = root.findViewById(R.id.notes);

        callCustomer = root.findViewById(R.id.callCustomer);
        sendMessage = root.findViewById(R.id.sendMessage);

        save = root.findViewById(R.id.save);

        customerName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                customerNameLabel.setText(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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

        //Contacts

        final RecyclerView contactsRecyclerView = (RecyclerView) root.findViewById(R.id.contact_list);
        Context contactsRecyclerViewContext = contactsRecyclerView.getContext();

        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(contactsRecyclerViewContext));
        contactsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //Address

        final RecyclerView addressRecyclerView = (RecyclerView) root.findViewById(R.id.address_list);
        Context addressRecyclerViewContext = contactsRecyclerView.getContext();

        addressRecyclerView.setLayoutManager(new LinearLayoutManager(addressRecyclerViewContext));
        addressRecyclerView.setItemAnimator(new DefaultItemAnimator());

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
                saveCustomer();
                Snackbar saveSnackbar = Snackbar.make(v, "Customer was saved", Snackbar.LENGTH_SHORT);
                saveSnackbar.show();
            }
        });

        return root;
    }

    private void saveCustomer()
    {
        //Save Customer

        Customer theUpdatedCustomer = new Customer();

        theUpdatedCustomer.setId(ID);
        theUpdatedCustomer.setCustomerID(CustomerID);
        theUpdatedCustomer.setContactName(primaryContactName);
        theUpdatedCustomer.setParentCustomerName(parentCustomerName);
        theUpdatedCustomer.setCustomerName(customerName.getText().toString());
        theUpdatedCustomer.setGeneralTelephone(generalTelephone.getText().toString());
        theUpdatedCustomer.setGeneralEmail(generalEmail.getText().toString());
        theUpdatedCustomer.setCustomerReference(reference.getText().toString());
        theUpdatedCustomer.setNotes(notes.getText().toString());
        theUpdatedCustomer.setIsChanged(true);
        theUpdatedCustomer.setIsNew(false);

        Date updatedDate = new Date();

        SimpleDateFormat targetDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

        theUpdatedCustomer.setUpdated(targetDateFormat.format(updatedDate));

        DatabaseClient.getInstance(requireContext()).getAppDatabase()
                .customerDao()
                .update(theUpdatedCustomer);

        if (mListener != null) {
            mListener.CustomerChanged();
        }
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {

        drawVisible = false;

        if (ID != 0) {

            Customer theCustomer = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .customerDao()
                    .getCustomer(ID);

            CustomerID = theCustomer.getCustomerID();
            primaryContactName = theCustomer.getContactName();
            parentCustomerName = theCustomer.getParentCustomerName();
            customerNameLabel.setText(theCustomer.getCustomerName());
            customerName.setText(theCustomer.getCustomerName());
            generalTelephone.setText(theCustomer.getGeneralTelephone());
            generalEmail.setText(theCustomer.getGeneralEmail());
            reference.setText(theCustomer.getCustomerReference());
            notes.setText(theCustomer.getNotes());

            refreshContacts();
            refreshAddresses();

            if (root.findViewById(R.id.activity_list) != null) {
                refreshActivity();
            }
        }
    }

    private void generateContactsList(RecyclerView recyclerView, List<Contact> contacts) {

        contacts.add(new Contact(0, "New Contact"));

        mContactsAdapter = new ContactsRecyclerViewAdapter(contacts, getChildFragmentManager(),this.getContext(), this);
        recyclerView.setAdapter(mContactsAdapter);
    }

    private void generateAddressList(RecyclerView recyclerView, List<Address> addresses) {

        addresses.add(new Address(0, "New Address"));

        mAddressAdapter = new AddressRecyclerViewAdapter(addresses, getChildFragmentManager(),this.getContext(), this);
        recyclerView.setAdapter(mAddressAdapter);
    }

    private void generateActivityList(RecyclerView recyclerView, List<Activity> activities) {

        activities.add(new Activity(0, "New Activity"));

        mActivityAdapter = new ActivityRecyclerViewAdapter(activities, getChildFragmentManager(),this.getContext(), this);
        recyclerView.setAdapter(mActivityAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.customer_detail, menu);

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

        if (id == R.id.action_save)
        {
            saveCustomer();
            Snackbar saveSnackbar = Snackbar.make(requireView(), "Customer was saved", Snackbar.LENGTH_SHORT);
            saveSnackbar.show();
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
                    .setTitle("Archive Customer")
                    .setMessage("Are you sure you want to archive this customer? It will be removed after the next successful sync")
                    .setIcon(android.R.drawable.ic_delete)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {

                            DatabaseClient.getInstance(requireContext()).getAppDatabase()
                                    .customerDao()
                                    .archiveCustomer(ID);

                            if (mListener != null) {
                                mListener.CustomerChanged();
                            }

                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        }
        else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    public void refreshContacts()
    {
        final RecyclerView contactsRecyclerView = (RecyclerView) root.findViewById(R.id.contact_list);

        //Get Customer Contacts

        List<Contact> contacts = DatabaseClient.getInstance(requireContext())
                .getAppDatabase()
                .contactDao()
                .getCustomerContacts(ID);

        generateContactsList(contactsRecyclerView, contacts);
    }

    public void refreshAddresses()
    {
        final RecyclerView addressRecyclerView = (RecyclerView) root.findViewById(R.id.address_list);

        //Add Customer Addresses

//        List<Address> addresses = DatabaseClient.getInstance(requireContext())
//                .getAppDatabase()
//                .addressDao()
//                .getCustomerAddresses(ID);

        List<Address> addresses = DatabaseClient.getInstance(requireContext())
                .getAppDatabase()
                .addressDao()
                .getCustomerAddressesByCustomerID(CustomerID);

        generateAddressList(addressRecyclerView, addresses);
    }

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
        else if (ID != 0) {

            List<Activity> activities = DatabaseClient.getInstance(requireContext())
                    .getAppDatabase()
                    .activityDao()
                    .getCustomerActivity(ID);

            generateActivityList(activityRecyclerView, activities);
        }
    }

    @Override
    public void NewContact(String ContactName)
    {
        if (!ContactName.equals("")) {
            primaryContactName = ContactName;

            if (mListener != null) {
                mListener.CustomerChanged();
            }
        }

        refreshContacts();
    }

    @Override
    public void CreateNewContact() {
        NewContactDialog newDialog = NewContactDialog.newInstance(this, ID, customerName.getText().toString());
        newDialog.show(getChildFragmentManager(), "New Contact");
    }

    @Override
    public void EditContact(int id) {
        Bundle bundle = new Bundle();
        bundle.putInt("ID", id);

        EditContactDialog newDialog = EditContactDialog.newInstance(this);
        newDialog.setArguments(bundle);
        newDialog.show(getChildFragmentManager(), "Edit Contact");
    }

    @Override
    public void RemoveContact() {
        refreshContacts();
    }

    @Override
    public void NewAddressAdded() {
        refreshAddresses();
    }

    @Override
    public void CreateNewAddress() {
        NewAddressDialog newDialog = NewAddressDialog.newInstance(this, ID, customerName.getText().toString());
        newDialog.show(getChildFragmentManager(), "New Address");
    }

    @Override
    public void EditAddress(int id) {
        Bundle bundle = new Bundle();
        bundle.putInt("ID", id);

        EditAddressDialog newDialog = EditAddressDialog.newInstance(this);
        newDialog.setArguments(bundle);
        newDialog.show(getChildFragmentManager(), "Edit Address");
    }

    @Override
    public void RemoveAddress() {
        refreshAddresses();
    }

    @Override
    public void NewActivityAdded() {
        refreshActivity();
    }

    @Override
    public void CreateNewActivity() {
        NewActivityDialog newDialog = NewActivityDialog.newInstance(this, ID, customerName.getText().toString());
        newDialog.show(getChildFragmentManager(), "New Activity");
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

    @Override
    public void ContactUpdated() {
        refreshContacts();
    }

    @Override
    public void DefaultContactUpdated(String ContactName)
    {
        if(getParentFragment() != null) {
            Snackbar saveSnackbar = Snackbar.make(getParentFragment().requireView(), "Default contact was changed", Snackbar.LENGTH_SHORT);
            saveSnackbar.show();
        }
        else
        {
            Snackbar saveSnackbar = Snackbar.make(requireView(), "Default contact was changed", Snackbar.LENGTH_SHORT);
            saveSnackbar.show();
        }

        if (mListener != null) {
            primaryContactName = ContactName;
            mListener.CustomerChanged();
        }
    }

    @Override
    public void AddressUpdated() {
        refreshAddresses();
    }

    @Override
    public void ActivityUpdated() {
        refreshActivity();
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static float dpToPx(int dp) {
        return (int)dp * Resources.getSystem().getDisplayMetrics().density;
    }
}