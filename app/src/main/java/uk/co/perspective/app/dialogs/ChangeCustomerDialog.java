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
import uk.co.perspective.app.adapters.SelectCustomerRecyclerViewAdapter;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.entities.Customer;

public class ChangeCustomerDialog extends DialogFragment implements SelectCustomerRecyclerViewAdapter.SelectCustomerListener, NewQuickCustomerDialog.NewCustomerListener {

    private View root;
    private int selectedLocalCustomerID;
    private String selectedCustomerName;

    ChangeCustomerListener mListener;

    SearchView searchView;
    ImageView addCustomer;

    Button okSelected;
    Button cancelSelected;

    public ChangeCustomerDialog() {

    }

    public interface ChangeCustomerListener {
        public void CustomerChanged(int customerID, String customerName);
    }

    public void setListener(ChangeCustomerListener listener) {
        mListener = listener;
    }

    @Override
    public void CustomerSelected(int ID, String customerName) {
        this.selectedLocalCustomerID = ID;
        this.selectedCustomerName = customerName;
    }

    @Override
    public void NewCustomer(int LocalCustomerID, String CustomerName) {

        this.selectedLocalCustomerID = LocalCustomerID;
        this.selectedCustomerName = CustomerName;

        if (selectedLocalCustomerID != 0) {
            mListener.CustomerChanged(selectedLocalCustomerID, selectedCustomerName);
            dismiss();
        }
    }

    public static ChangeCustomerDialog newInstance(ChangeCustomerListener mListener) {
        ChangeCustomerDialog frag = new ChangeCustomerDialog();
        Bundle args = new Bundle();
        frag.setListener(mListener);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedLocalCustomerID = getArguments().getInt("ID");
            selectedCustomerName = getArguments().getString("customerName");
        }
        else
        {
            selectedLocalCustomerID = 0;
            selectedCustomerName = "";
        }

        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.dialog_change_customer, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.customer_list);
        Context viewcontext = recyclerView.getContext();

        recyclerView.setLayoutManager(new LinearLayoutManager(viewcontext));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(viewcontext, LinearLayoutManager.VERTICAL));
        recyclerView.setClipToOutline(true);

        searchView = root.findViewById(R.id.searchFor);
        addCustomer = root.findViewById(R.id.addCustomer);

        //Buttons

        okSelected = root.findViewById(R.id.saveChangeCustomer);
        cancelSelected = root.findViewById(R.id.cancelSaveChangeCustomer);

        addCustomer.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               NewQuickCustomerDialog newDialog = NewQuickCustomerDialog.newInstance(ChangeCustomerDialog.this);
               newDialog.show(getChildFragmentManager(), "New Customer");
           }
        });

        okSelected.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
            if (selectedLocalCustomerID != 0) {
                mListener.CustomerChanged(selectedLocalCustomerID, selectedCustomerName);
                dismiss();
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

        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.customer_list);

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
                    refreshCustomers();
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
                refreshCustomers();
                searchView.setFocusable(false);
                searchView.setIconified(false);
                searchView.clearFocus();
                return false;
            }
        });

        refreshCustomers();

    }

    private void refreshCustomers() {

        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.customer_list);

        List<Customer> customers;

        customers = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                .customerDao()
                .getAll();

        generateCustomerList(recyclerView, customers);
    }

    private void searchCustomers(String searchText)
    {
        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.customer_list);

        List<Customer> customers = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                .customerDao()
                .searchCustomers(searchText);

        generateCustomerList(recyclerView, customers);
    }

    private void generateCustomerList(RecyclerView recyclerView, List<Customer> Customers) {
        SelectCustomerRecyclerViewAdapter mAdapter = new SelectCustomerRecyclerViewAdapter(Customers, getChildFragmentManager(), this.getContext(), this);
        recyclerView.setAdapter(mAdapter);
    }
}
