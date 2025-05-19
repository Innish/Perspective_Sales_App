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
import uk.co.perspective.app.adapters.QuotesRecyclerViewAdapter;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.dialogs.FilterQuotesDialog;
import uk.co.perspective.app.dialogs.NewQuoteDialog;
import uk.co.perspective.app.entities.Quote;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuotesFragment extends Fragment implements QuotesRecyclerViewAdapter.QuoteListener, NewQuoteDialog.NewQuoteListener, FilterQuotesDialog.SetFilterListener, QuoteDetailFragment.ChangeQuoteListener {

    private View root;

    private SearchView searchView;
    private ImageView filterSelection;
    private QuotesRecyclerViewAdapter mAdapter;

    private String filterStartDate;
    private String filterEndDate;
    private String filterStatus;

    public QuotesFragment() {
        // Required empty public constructor
    }

    public static QuotesFragment newInstance(String param1, String param2) {
        QuotesFragment fragment = new QuotesFragment();
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

        root = inflater.inflate(R.layout.fragment_quotes, container, false);

        searchView = root.findViewById(R.id.searchFor);
        filterSelection = root.findViewById(R.id.filter_selection);

        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.quote_list);
        Context viewcontext = recyclerView.getContext();

        recyclerView.setLayoutManager(new LinearLayoutManager(viewcontext));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(viewcontext, LinearLayoutManager.VERTICAL));

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.quote_list);

        filterStartDate = "";
        filterEndDate = "";
        filterStatus = "";

        refreshQuotes();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuotes(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.equals(""))
                {
                    refreshQuotes();
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
                refreshQuotes();
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
                FilterQuotesDialog newFragment = FilterQuotesDialog.newInstance(QuotesFragment.this);
                newFragment.show(fm, "Filter");
            }
        });

        //Set detail as blank

        if (view.findViewById(R.id.quote_detail_container) != null) {

            Fragment fragment = new BlankFragment();
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.quote_detail_container, fragment);
            ft.commit();

        }
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.global, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void generateQuoteList(RecyclerView recyclerView, List<Quote> quotes) {
        quotes.add(new Quote(0, "New Quote"));
        mAdapter = new QuotesRecyclerViewAdapter(quotes, getChildFragmentManager(),this.getContext(), this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items

        int id = item.getItemId();

        if (id == R.id.action_new) {
            NewQuoteDialog newDialog = NewQuoteDialog.newInstance(this, 0, "", 0);
            newDialog.show(getChildFragmentManager(), "New Quote");
        }
        else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void updateQuotes()
    {
        List<Quote> quotes;

        if (searchView.getQuery() != "")
        {
            quotes = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .quoteDao()
                    .searchQuotes(searchView.getQuery().toString());
        }
        else
        {
            quotes = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .quoteDao()
                    .getAll();
        }

        updateQuoteList(quotes);
    }

    private void searchQuotes(String searchText)
    {
        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.opportunity_list);

        List<Quote> quotes = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                .quoteDao()
                .searchQuotes(searchText);

        generateQuoteList(recyclerView, quotes);
    }

    private void updateQuoteList(List<Quote> quotes) {

        //sort

        Collections.sort(quotes);

        //Add new customer

        quotes.add(new Quote(0, "New Quote"));

        if (mAdapter != null) {
            mAdapter.updateList(quotes);
        }

    }

    private void refreshQuotes() {

        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.quote_list);

        List<Quote> quotes;

        if (!filterStartDate.equals("") && !filterEndDate.equals(""))
        {
            quotes = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .quoteDao()
                    .getQuotesInRange(filterStartDate, filterEndDate);
        }
        else if (!filterStatus.equals(""))
        {
            quotes = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .quoteDao()
                    .getQuotesByStatus(filterStatus);
        }
        else {

            quotes = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .quoteDao()
                    .getAll();
        }

        generateQuoteList(recyclerView, quotes);
    }

    @Override
    public void CreateNewQuote() {
        NewQuoteDialog newDialog = NewQuoteDialog.newInstance(this, 0, "", 0);
        newDialog.show(getChildFragmentManager(), "New Quote");
    }

    @Override
    public void EditQuote(int id) {

        if (root.findViewById(R.id.quote_detail_container) != null) {

            Bundle bundle = new Bundle();
            bundle.putInt("ID", id);

            Fragment fragment = QuoteDetailFragment.newInstance(this);
            fragment.setArguments(bundle);

            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.quote_detail_container, fragment);
            ft.commit();
        }
        else
        {
            Intent intent = new Intent(root.getContext(), HostActivity.class);
            intent.putExtra("Target", "QuoteDetails");
            intent.putExtra("TargetID", id);
            root.getContext().startActivity(intent);
        }
    }

    @Override
    public void RemoveQuote(int id) {
        refreshQuotes();
    }

    @Override
    public void NewQuoteAdded(int id) {
        refreshQuotes();
    }

    @Override
    public void SetFilter(String startDate, String endDate, String Label) {
        filterStartDate = startDate;
        filterEndDate = endDate;
        filterStatus = "";

        refreshQuotes();

        searchView.setFocusable(false);
        searchView.setIconified(false);
        searchView.clearFocus();
    }

    @Override
    public void SetStatusFilter(String status) {
        filterStartDate = "";
        filterEndDate = "";
        filterStatus = status;

        refreshQuotes();

        searchView.setFocusable(false);
        searchView.setIconified(false);
        searchView.clearFocus();
    }

    @Override
    public void QuoteChanged() {
        updateQuotes();
    }
}