package uk.co.perspective.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import android.widget.ImageView;
import android.widget.SearchView;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import uk.co.perspective.app.BlankFragment;
import uk.co.perspective.app.R;
import uk.co.perspective.app.activities.HostActivity;
import uk.co.perspective.app.adapters.LeadsRecyclerViewAdapter;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.dialogs.FilterLeadsDialog;
import uk.co.perspective.app.dialogs.NewLeadDialog;
import uk.co.perspective.app.entities.Lead;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LeadsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LeadsFragment extends Fragment implements LeadsRecyclerViewAdapter.LeadListener, FilterLeadsDialog.SetFilterListener, NewLeadDialog.NewLeadListener, LeadDetailFragment.ChangeLeadListener {

    private View root;

    private SearchView searchView;
    private ImageView filterSelection;
    private LeadsRecyclerViewAdapter mAdapter;

    private String filterStartDate;
    private String filterEndDate;
    private String filterStatus;

    public LeadsFragment() {
        // Required empty public constructor
    }

    public static LeadsFragment newInstance(String param1, String param2) {
        LeadsFragment fragment = new LeadsFragment();
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

        root = inflater.inflate(R.layout.fragment_leads, container, false);

        searchView = root.findViewById(R.id.searchFor);
        filterSelection = root.findViewById(R.id.filter_selection);

        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.lead_list);
        Context viewcontext = recyclerView.getContext();

        recyclerView.setLayoutManager(new LinearLayoutManager(viewcontext));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(viewcontext, LinearLayoutManager.VERTICAL));

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.lead_list);

        filterStartDate = "";
        filterEndDate = "";
        filterStatus = "";

        refreshLeads();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                searchLeads(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.equals(""))
                {
                    refreshLeads();
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
            refreshLeads();
            searchView.setFocusable(false);
            searchView.setIconified(false);
            searchView.clearFocus();
            return false;
            }
        });

        filterSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            FragmentManager fm = getChildFragmentManager();
            FilterLeadsDialog newFragment = FilterLeadsDialog.newInstance(LeadsFragment.this);
            newFragment.show(fm, "Filter");
            }
        });

        //Set detail as blank

        if (view.findViewById(R.id.lead_detail_container) != null) {

            Fragment fragment = new BlankFragment();
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.lead_detail_container, fragment);
            ft.commit();

        }
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
            NewLeadDialog newDialog = NewLeadDialog.newInstance(this, 0, "");
            newDialog.show(getChildFragmentManager(), "New Lead");
        }
        else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void updateLeads()
    {
        List<Lead> leads;

        if (searchView.getQuery() != "")
        {
            leads = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .leadDao()
                    .searchLeads(searchView.getQuery().toString());
        }
        else
        {
            leads = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .leadDao()
                    .getAll();
        }

        updateLeadList(leads);
    }

    private void searchLeads(String searchText)
    {
        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.lead_list);

        List<Lead> leads = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                .leadDao()
                .searchLeads(searchText);

        generateLeadList(recyclerView, leads);
    }

    private void updateLeadList(List<Lead> leads) {

        //sort

        Collections.sort(leads);

        //Add new customer

        leads.add(new Lead(0, "New Lead"));

        if (mAdapter != null) {
            mAdapter.updateList(leads);
        }

    }

    private void refreshLeads() {

        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.lead_list);

        List<Lead> leads;

        if (!filterStartDate.equals("") && !filterEndDate.equals(""))
        {
            leads = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .leadDao()
                    .getLeadsInRange(filterStartDate, filterEndDate);
        }
        else if (!filterStatus.equals(""))
        {
            leads = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .leadDao()
                    .getLeadsByStatus(filterStatus);
        }
        else {

            leads = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .leadDao()
                    .getAll();
        }

        generateLeadList(recyclerView, leads);
    }

    private void generateLeadList(RecyclerView recyclerView, List<Lead> Leads) {
        Leads.add(new Lead(0, "New Lead"));
        mAdapter = new LeadsRecyclerViewAdapter(Leads, getChildFragmentManager(),this.getContext(), this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void CreateNewLead() {
        NewLeadDialog newDialog = NewLeadDialog.newInstance(this, 0, "");
        newDialog.show(getChildFragmentManager(), "New Lead");
    }

    @Override
    public void EditLead(int id) {

        if (root.findViewById(R.id.lead_detail_container) != null) {
            Bundle bundle = new Bundle();
            bundle.putInt("ID", id);

            LeadDetailFragment fragment = LeadDetailFragment.newInstance(this);
            fragment.setArguments(bundle);

            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.lead_detail_container, fragment);
            ft.commit();
        }
        else
        {
            Intent intent = new Intent(root.getContext(), HostActivity.class);
            intent.putExtra("Target", "LeadDetails");
            intent.putExtra("TargetID", id);
            root.getContext().startActivity(intent);
        }
    }

    @Override
    public void RemoveLead() {

    }

    @Override
    public void SetFilter(String startDate, String endDate, String Label) {
        filterStartDate = startDate;
        filterEndDate = endDate;
        filterStatus = "";

        refreshLeads();

        searchView.setFocusable(false);
        searchView.setIconified(false);
        searchView.clearFocus();
    }

    @Override
    public void SetStatusFilter(String status) {
        filterStartDate = "";
        filterEndDate = "";
        filterStatus = status;

        refreshLeads();

        searchView.setFocusable(false);
        searchView.setIconified(false);
        searchView.clearFocus();
    }

    @Override
    public void NewLead() {
        refreshLeads();
    }

    @Override
    public void LeadChanged() {
        updateLeads();
    }
}