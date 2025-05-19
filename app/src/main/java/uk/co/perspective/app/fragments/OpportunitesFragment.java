package uk.co.perspective.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import uk.co.perspective.app.BlankFragment;
import uk.co.perspective.app.R;
import uk.co.perspective.app.activities.HostActivity;
import uk.co.perspective.app.adapters.OpportunitiesRecyclerViewAdapter;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.dialogs.FilterOpportunitiesDialog;
import uk.co.perspective.app.dialogs.NewOpportunityDialog;
import uk.co.perspective.app.entities.Opportunity;

public class OpportunitesFragment extends Fragment implements OpportunitiesRecyclerViewAdapter.OpportunityListener, FilterOpportunitiesDialog.SetFilterListener, NewOpportunityDialog.NewOpportunityListener, OpportunityDetailFragment.ChangeOpportunityListener {

    private View root;

    private SearchView searchView;
    private ImageView filterSelection;
    private OpportunitiesRecyclerViewAdapter mAdapter;

    private String filterStartDate;
    private String filterEndDate;
    private String filterStatus;

    public OpportunitesFragment() {
        // Required empty public constructor
    }

    public static OpportunitesFragment newInstance(String param1, String param2) {
        OpportunitesFragment fragment = new OpportunitesFragment();
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

        root = inflater.inflate(R.layout.fragment_opportunities, container, false);

        searchView = root.findViewById(R.id.searchFor);
        filterSelection = root.findViewById(R.id.filter_selection);

        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.opportunity_list);
        Context viewcontext = recyclerView.getContext();

        recyclerView.setLayoutManager(new LinearLayoutManager(viewcontext));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(viewcontext, LinearLayoutManager.VERTICAL));

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.opportunity_list);

        filterStartDate = "";
        filterEndDate = "";
        filterStatus = "";

        refreshOpportunities();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                searchOpportunities(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.equals(""))
                {
                    refreshOpportunities();
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
                refreshOpportunities();
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
                FilterOpportunitiesDialog newFragment = FilterOpportunitiesDialog.newInstance(OpportunitesFragment.this);
                newFragment.show(fm, "Filter");
            }
        });

        //Set detail as blank

        if (view.findViewById(R.id.opportunity_detail_container) != null) {

            Fragment fragment = new BlankFragment();
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.opportunity_detail_container, fragment);
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
            NewOpportunityDialog newDialog = NewOpportunityDialog.newInstance(this, 0, "");
            newDialog.show(getChildFragmentManager(), "New Opportunity");
        }
        else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void generateOpportunityList(RecyclerView recyclerView, List<Opportunity> Opportunities) {
        Opportunities.add(new Opportunity(0, "New Opportunity"));
        mAdapter = new OpportunitiesRecyclerViewAdapter(Opportunities, getChildFragmentManager(),this.getContext(), this);
        recyclerView.setAdapter(mAdapter);
    }

    private void updateOpportunities()
    {
        List<Opportunity> opportunities;

        if (searchView.getQuery() != "")
        {
            opportunities = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .opportunityDao()
                    .searchOpportunities(searchView.getQuery().toString());
        }
        else
        {
            opportunities = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .opportunityDao()
                    .getAll();
        }

        updateOpportunityList(opportunities);
    }

    private void searchOpportunities(String searchText)
    {
        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.opportunity_list);

        List<Opportunity> leads = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                .opportunityDao()
                .searchOpportunities(searchText);

        generateOpportunityList(recyclerView, leads);
    }

    private void updateOpportunityList(List<Opportunity> opportunities) {

        //sort

        Collections.sort(opportunities);

        //Add new customer

        opportunities.add(new Opportunity(0, "New Opportunity"));

        if (mAdapter != null) {
            mAdapter.updateList(opportunities);
        }

    }

    private void refreshOpportunities() {

        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.opportunity_list);

        List<Opportunity> opportunities;

        if (!filterStartDate.equals("") && !filterEndDate.equals(""))
        {
            opportunities = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .opportunityDao()
                    .getOpportunitiesInRange(filterStartDate, filterEndDate);
        }
        else if (!filterStatus.equals(""))
        {
            opportunities = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .opportunityDao()
                    .getOpportunitiesByStatus(filterStatus);
        }
        else {

            opportunities = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .opportunityDao()
                    .getAll();
        }

        generateOpportunityList(recyclerView, opportunities);
    }

    @Override
    public void CreateNewOpportunity() {
        NewOpportunityDialog newDialog = NewOpportunityDialog.newInstance(this, 0, "");
        newDialog.show(getChildFragmentManager(), "New Opportunity");
    }

    @Override
    public void EditOpportunity(int id) {

        if (root.findViewById(R.id.opportunity_detail_container) != null) {
            Bundle bundle = new Bundle();
            bundle.putInt("ID", id);

            OpportunityPropertiesFragment fragment = OpportunityPropertiesFragment.newInstance();
            fragment.setArguments(bundle);

            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.opportunity_detail_container, fragment);
            ft.commit();
        }
        else
        {
            Intent intent = new Intent(root.getContext(), HostActivity.class);
            intent.putExtra("Target", "OpportunityProperties");
            intent.putExtra("TargetID", id);
            root.getContext().startActivity(intent);
        }
    }

    @Override
    public void SetFilter(String startDate, String endDate, String Label) {
        filterStartDate = startDate;
        filterEndDate = endDate;
        filterStatus = "";

        refreshOpportunities();

        searchView.setFocusable(false);
        searchView.setIconified(false);
        searchView.clearFocus();
    }

    @Override
    public void SetStatusFilter(String status) {
        filterStartDate = "";
        filterEndDate = "";
        filterStatus = status;

        refreshOpportunities();

        searchView.setFocusable(false);
        searchView.setIconified(false);
        searchView.clearFocus();
    }

    @Override
    public void RemoveOpportunity() {
        refreshOpportunities();
    }

    @Override
    public void NewOpportunity() {
        refreshOpportunities();
    }

    @Override
    public void OpportunityChanged() {
        updateOpportunities();
    }
}