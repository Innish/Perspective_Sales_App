package uk.co.perspective.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import uk.co.perspective.app.BlankFragment;
import uk.co.perspective.app.R;
import uk.co.perspective.app.activities.HostActivity;
import uk.co.perspective.app.adapters.CustomersRecyclerViewAdapter;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.dialogs.NewCustomerDialog;
import uk.co.perspective.app.entities.Customer;

public class CustomersFragment extends Fragment implements NewCustomerDialog.NewCustomerListener, CustomersRecyclerViewAdapter.CustomerListener, CustomerDetailFragment.ChangeCustomerListener {

    private View root;
    private SearchView searchView;
    private CustomersRecyclerViewAdapter mAdapter;

    public CustomersFragment() {
        // Required empty public constructor
    }

    public static CustomersFragment newInstance(String param1, String param2) {
        CustomersFragment fragment = new CustomersFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_customers, container, false);

        searchView = root.findViewById(R.id.searchFor);

        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.customer_list);
        Context viewcontext = recyclerView.getContext();

        recyclerView.setLayoutManager(new LinearLayoutManager(viewcontext));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(viewcontext, LinearLayoutManager.VERTICAL));

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.customer_list);

        refreshCustomers();

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

        //Set detail as blank

        if (view.findViewById(R.id.customer_detail_container) != null) {

            Fragment fragment = new BlankFragment();
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.customer_detail_container, fragment);
            ft.commit();

        }
    }

    private void refreshCustomers() {

        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.customer_list);

        List<Customer> customers;

        customers = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                .customerDao()
                .getAll();

        generateCustomerList(recyclerView, customers);
    }

    private void updateCustomers()
    {
        List<Customer> customers;

        if (searchView.getQuery() != "")
        {
            customers = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .customerDao()
                    .searchCustomers(searchView.getQuery().toString());
        }
        else
        {
            customers = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .customerDao()
                    .getAll();
        }

        updateCustomerList(customers);
    }

    private void searchCustomers(String searchText)
    {
        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.customer_list);

        List<Customer> customers = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                .customerDao()
                .searchCustomers(searchText);

        generateCustomerList(recyclerView, customers);
    }

    private void updateCustomerList(List<Customer> customers) {

        //sort

        Collections.sort(customers);

        //Add new customer

        customers.add(new Customer(0, "New Customer"));

        if (mAdapter != null) {
            mAdapter.updateList(customers);
        }

    }

    private void generateCustomerList(RecyclerView recyclerView, List<Customer> Customers) {

        Collections.sort(Customers);

        //Add new customer

        Customers.add(new Customer(0, "New Customer"));

        mAdapter = new CustomersRecyclerViewAdapter(Customers, getChildFragmentManager(),this.getContext(), this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.global, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items

        int id = item.getItemId();

        if (id == R.id.action_new) {
            NewCustomerDialog newDialog = NewCustomerDialog.newInstance(this);
            newDialog.show(getChildFragmentManager(), "New Customer");
        }
        else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void NewCustomer() {
        refreshCustomers();
    }

    @Override
    public void CustomerChanged() {
        updateCustomers();
    }

    @Override
    public void CreateNewCustomer() {
        NewCustomerDialog newDialog = NewCustomerDialog.newInstance(this);
        newDialog.show(getChildFragmentManager(), "New Customer");
    }

    @Override
    public void EditCustomer(int id) {

        if (root.findViewById(R.id.customer_detail_container) != null) {

            Bundle bundle = new Bundle();
            bundle.putInt("ID", id);

            Fragment fragment = CustomerDetailFragment.newInstance(this);
            fragment.setArguments(bundle);

            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.customer_detail_container, fragment);
            ft.commit();
        } else {
            Intent intent = new Intent(requireContext(), HostActivity.class);
            intent.putExtra("Target", "CustomerDetails");
            intent.putExtra("TargetID", id);
            root.getContext().startActivity(intent);
        }

    }

    @Override
    public void RemoveCustomer() {
        refreshCustomers();
    }
}