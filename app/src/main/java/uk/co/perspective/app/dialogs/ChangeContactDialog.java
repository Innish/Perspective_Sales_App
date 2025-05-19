package uk.co.perspective.app.dialogs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uk.co.perspective.app.R;
import uk.co.perspective.app.adapters.SelectContactRecyclerViewAdapter;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.entities.Contact;

public class ChangeContactDialog extends DialogFragment implements SelectContactRecyclerViewAdapter.SelectContactListener, NewQuickContactDialog.NewContactListener {

    private View root;
    private int localCustomerID;
    private int contactID;
    private int selectedLocalContactID;
    private String selectedContactName;
    private String customerName;

    ChangeContactListener mListener;

    SearchView searchView;
    ImageView addCustomer;

    Button okSelected;
    Button cancelSelected;

    public ChangeContactDialog() {

    }

    public interface ChangeContactListener {
        public void ContactChanged(int ID, int contactID, String contactName);
    }

    public void setListener(ChangeContactListener listener) {
        mListener = listener;
    }
    public void setCustomerID(int ID, String CustomerName) {
        localCustomerID = ID;
        customerName = CustomerName;
    }

    @Override
    public void ContactSelected(int ID, int contactID, String contactName) {
        this.contactID = contactID;
        selectedLocalContactID = ID;
        selectedContactName = contactName;
    }

    @Override
    public void NewContact(int LocalContactID, String ContactName) {

        selectedLocalContactID = LocalContactID;
        selectedContactName = ContactName;
        contactID = 0;

        if (selectedLocalContactID != 0) {
            mListener.ContactChanged(selectedLocalContactID, contactID, selectedContactName);
            dismiss();
        }
    }

    public static ChangeContactDialog newInstance(ChangeContactListener mListener, int localCustomerID, String customerName) {
        ChangeContactDialog frag = new ChangeContactDialog();
        Bundle args = new Bundle();
        frag.setListener(mListener);
        frag.setCustomerID(localCustomerID, customerName);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedLocalContactID = getArguments().getInt("ID");
            selectedContactName = getArguments().getString("contactName");
        }
        else
        {
            selectedLocalContactID = 0;
            selectedContactName = "";
        }

        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.dialog_change_contact, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.contact_list);
        Context viewcontext = recyclerView.getContext();

        recyclerView.setLayoutManager(new LinearLayoutManager(viewcontext));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(viewcontext, LinearLayoutManager.VERTICAL));
        recyclerView.setClipToOutline(true);

        searchView = root.findViewById(R.id.searchFor);
        addCustomer = root.findViewById(R.id.addContact);

        //Buttons

        okSelected = root.findViewById(R.id.saveChangeContact);
        cancelSelected = root.findViewById(R.id.cancelSaveChangeContact);

        addCustomer.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               NewQuickContactDialog newDialog = NewQuickContactDialog.newInstance(ChangeContactDialog.this, localCustomerID, customerName);
               newDialog.show(getChildFragmentManager(), "New Contact");
           }
        });

        okSelected.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

            if (selectedLocalContactID != 0) {

                if (customerName.equals("Linked Contact"))
                {
                    mListener.ContactChanged(selectedLocalContactID, contactID, "Linked Contact");
                    dismiss();
                }
                else {
                    mListener.ContactChanged(selectedLocalContactID, contactID, selectedContactName);
                    dismiss();
                }
            }
            }
        });

        cancelSelected.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        root.setBackgroundResource(R.drawable.dialog_rounded);

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.contact_list);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                searchCustomers(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.equals(""))
                {
                    refreshContacts();
                    searchView.setFocusable(false);
                    searchView.setIconified(false);
                    searchView.clearFocus();
                    return false;
                }
                else {
                    return false;
                }
            }

        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
            refreshContacts();
            searchView.setFocusable(false);
            searchView.setIconified(false);
            searchView.clearFocus();
            return false;
            }
        });

        refreshContacts();

    }

    private void refreshContacts() {

        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.contact_list);

        List<Contact> contacts;

        if (localCustomerID != 0) {

            contacts = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .contactDao()
                    .getCustomerContacts(localCustomerID);
        } else
        {
            contacts = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .contactDao()
                    .getAll();
        }

        generateContactList(recyclerView, contacts);
    }

    private void searchCustomers(String searchText)
    {
        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.contact_list);

        List<Contact> contacts;

        if (localCustomerID != 0) {
            contacts = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .contactDao()
                    .searchCustomerContacts(localCustomerID, searchText);
        }
        else
        {
            contacts = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .contactDao()
                    .searchContacts(searchText);
        }

        generateContactList(recyclerView, contacts);
    }

    private void generateContactList(RecyclerView recyclerView, List<Contact> Contacts) {
        SelectContactRecyclerViewAdapter mAdapter = new SelectContactRecyclerViewAdapter(Contacts, getChildFragmentManager(), this.getContext(), this);
        recyclerView.setAdapter(mAdapter);
    }
}
