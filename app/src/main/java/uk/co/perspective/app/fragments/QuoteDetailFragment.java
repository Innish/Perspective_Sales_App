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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import uk.co.perspective.app.R;
import uk.co.perspective.app.activities.HostActivity;
import uk.co.perspective.app.adapters.ActivityRecyclerViewAdapter;
import uk.co.perspective.app.adapters.QuoteLinesRecyclerViewAdapter;
import uk.co.perspective.app.database.AppDatabase;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.dialogs.ChangeContactDialog;
import uk.co.perspective.app.dialogs.ChangeCustomerDialog;
import uk.co.perspective.app.dialogs.ConvertOrderDialog;
import uk.co.perspective.app.dialogs.CopyOrderDialog;
import uk.co.perspective.app.dialogs.CopyQuoteDialog;
import uk.co.perspective.app.dialogs.EditActivityDialog;
import uk.co.perspective.app.dialogs.EditQuoteLineDialog;
import uk.co.perspective.app.dialogs.NewActivityDialog;
import uk.co.perspective.app.dialogs.NewQuoteLineDialog;
import uk.co.perspective.app.entities.Activity;
import uk.co.perspective.app.entities.Contact;
import uk.co.perspective.app.entities.Currency;
import uk.co.perspective.app.entities.Customer;
import uk.co.perspective.app.entities.Quote;
import uk.co.perspective.app.entities.QuoteLine;
import uk.co.perspective.app.helpers.QuoteLineTouchHelper;
import uk.co.perspective.app.models.SpinnerItem;

public class QuoteDetailFragment extends Fragment implements
        NewActivityDialog.NewActivityListener,
        ActivityRecyclerViewAdapter.ActivityListener,
        EditActivityDialog.UpdatedActivityListener,
        ChangeCustomerDialog.ChangeCustomerListener,
        ChangeContactDialog.ChangeContactListener,
        QuoteLinesRecyclerViewAdapter.QuoteListener,
        NewQuoteLineDialog.NewQuoteLineListener,
        EditQuoteLineDialog.UpdatedQuoteLineListener,
        CopyQuoteDialog.CopyQuoteListener,
        ConvertOrderDialog.ConvertOrderListener {

    private View root;

    private int ID;
    private int QuoteID;
    private int LocalCustomerID;
    private int LocalOpportunityID;
    private String customerName;
    private int CustomerID;
    private int ContactID;
    private int OpportunityID;
    private String Currency;
    private float ExchangeRate;
    private String CurrencySymbol = "£";
    private String CreatedByDisplayName;

    private EditText reference;
    private EditText subject;
    private Spinner quoteType;
    private Spinner quoteStatus;
    private EditText notes;

    private TextView totalDiscount;
    private TextView totalTax;
    private TextView totalQuoteValue;
    private TextView totalQuoteValueAlternative;
    private TextView currencyName;

    private Spinner selectedCurrencyName;
    private TextView selectedExchangeRate;

    private ConstraintLayout customerContactDetails;
    private TextView customerLookupName;
    private TextView contactLookupName;

    private RelativeLayout activityContainer;

    private QuoteLinesRecyclerViewAdapter mQuoteLinesAdapter;
    private ActivityRecyclerViewAdapter mActivityAdapter;

    private ItemTouchHelper mItemTouchHelper;

    private boolean drawVisible;

    private Button save;

    private ArrayAdapter<SpinnerItem> quoteStatusAdapter;
    private ArrayAdapter<SpinnerItem> adapterCurrency;

    QuoteDetailFragment.ChangeQuoteListener mListener;

    @Override
    public void QuoteConverted(int ID) {

        new AlertDialog.Builder(requireContext())
                .setTitle("Open Order")
                .setMessage("Do you want to open the converted order?")
                .setIcon(android.R.drawable.ic_menu_share)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

//                        OrderDetailFragment newFragment = OrderDetailFragment.newInstance(OrderDetailFragment.this);
//                        newFragment.show(getChildFragmentManager(), "Order Details");


                        Intent intent = new Intent(root.getContext(), HostActivity.class);
                        intent.putExtra("Target", "OrderDetails");
                        intent.putExtra("TargetID", ID);
                        root.getContext().startActivity(intent);


                    }})
                .setNegativeButton(android.R.string.no, null).show();

    }

    public interface ChangeQuoteListener {
        void QuoteChanged();
    }

    public void setListener(final QuoteDetailFragment.ChangeQuoteListener listener) {
        mListener = listener;
    }

    public QuoteDetailFragment() {
        // Required empty public constructor
    }

    public static QuoteDetailFragment newInstance(ChangeQuoteListener mListener) {
        QuoteDetailFragment fragment = new QuoteDetailFragment();
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

        root =  inflater.inflate(R.layout.fragment_quote_detail, container, false);

        final AppDatabase database = DatabaseClient.getInstance(requireContext()).getAppDatabase();

        customerContactDetails = root.findViewById(R.id.customer_contact_details);

        customerLookupName = root.findViewById(R.id.customer_name_label);
        contactLookupName = root.findViewById(R.id.contact_name_label);

        reference = root.findViewById(R.id.reference);
        subject = root.findViewById(R.id.subject);
        quoteStatus = root.findViewById(R.id.quote_status);
        quoteType = root.findViewById(R.id.quote_type);
        selectedCurrencyName = root.findViewById(R.id.selected_currency_name);
        selectedExchangeRate = root.findViewById(R.id.exchangeRate);
        notes  = root.findViewById(R.id.notes);

        totalDiscount = root.findViewById(R.id.total_discount);
        totalTax = root.findViewById(R.id.total_tax);
        totalQuoteValue = root.findViewById(R.id.quote_total);
        totalQuoteValueAlternative = root.findViewById(R.id.quote_total_alternative_currency);
        currencyName = root.findViewById(R.id.currency_name);

        save = root.findViewById(R.id.save);

        //Set-up Spinners

        final ArrayList<SpinnerItem> spinnerArray =  new ArrayList<>();

        spinnerArray.add(new SpinnerItem(1, "New (Not Issued)"));
        spinnerArray.add(new SpinnerItem(2, "Issued"));
        spinnerArray.add(new SpinnerItem(3,"Under Review"));
        spinnerArray.add(new SpinnerItem(4,"Accepted"));
        spinnerArray.add(new SpinnerItem(5,"Rejected"));

        quoteStatusAdapter = new ArrayAdapter<SpinnerItem>(requireContext(), R.layout.dropdown_list_item, spinnerArray);
        quoteStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quoteStatus.setAdapter(quoteStatusAdapter);

        final ArrayList<SpinnerItem> spinnerArrayQuoteType =  new ArrayList<>();

        spinnerArrayQuoteType.add(new SpinnerItem(1, "Estimate"));
        spinnerArrayQuoteType.add(new SpinnerItem(2, "Quote"));

        final ArrayAdapter<SpinnerItem> adapterQuoteType = new ArrayAdapter<SpinnerItem>(requireContext(), R.layout.dropdown_list_item, spinnerArrayQuoteType);
        adapterQuoteType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quoteType.setAdapter(adapterQuoteType);

        //Currency

        final List<uk.co.perspective.app.entities.Currency> currencies = database
                .currencyDao()
                .getAll();

        final ArrayList<SpinnerItem> spinnerArrayCurrencies =  new ArrayList<>();

        for (Currency currency: currencies)
        {
            spinnerArrayCurrencies.add(new SpinnerItem(currency.getId(), currency.getCurrencyName()));
        }

        adapterCurrency = new ArrayAdapter<SpinnerItem>(requireContext(), R.layout.dropdown_list_item, spinnerArrayCurrencies);
        adapterCurrency.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectedCurrencyName.setAdapter(adapterCurrency);


        selectedCurrencyName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                //Get ID

                SpinnerItem selectedItem = (SpinnerItem) selectedCurrencyName.getSelectedItem();

                Currency currency = database
                        .currencyDao()
                        .getCurrency(selectedItem.getValue());

                if (currency != null)
                {
                    ExchangeRate = currency.getRate();
                    Currency = currency.getCurrencyName();
                    CurrencySymbol = currency.getShortSymbol();

                    selectedExchangeRate.setText(String.format(Locale.UK, "%.2f", currency.getRate()));

                    int pL = totalQuoteValue.getPaddingLeft();
                    int pT = totalQuoteValue.getPaddingTop();
                    int pR = totalQuoteValue.getPaddingRight();
                    int pB = totalQuoteValue.getPaddingBottom();

                    if (currency.getRate() != 0) {
                        if (currency.getRate() < 1 || currency.getRate() > 1)
                        {
                            totalQuoteValueAlternative.setVisibility(View.VISIBLE);
                            totalQuoteValue.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.total_alternative_background_land_left));
                        }
                        else
                        {
                            totalQuoteValueAlternative.setVisibility(View.GONE);
                            totalQuoteValue.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.text_input_background));
                        }
                    }
                    else
                    {
                        totalQuoteValueAlternative.setVisibility(View.GONE);
                        totalQuoteValue.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.text_input_background));
                    }

                    totalQuoteValue.setPadding(pL, pT, pR, pB);

                    refreshQuoteLines();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });

        customerContactDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            ChangeCustomerDialog newFragment = ChangeCustomerDialog.newInstance(QuoteDetailFragment.this);
            newFragment.show(getChildFragmentManager(), "Change Customer");
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

        //items

        final RecyclerView contactsRecyclerView = (RecyclerView) root.findViewById(R.id.quote_items_list);
        Context contactsRecyclerViewContext = contactsRecyclerView.getContext();

        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(contactsRecyclerViewContext));
        contactsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveQuote();
                Snackbar saveSnackbar = Snackbar.make(v, "Quote was saved", Snackbar.LENGTH_SHORT);
                saveSnackbar.show();
            }
        });

        return root;
    }

    private void saveQuote()
    {
        //Save Quote

        Quote theUpdatedQuote = new Quote();

        theUpdatedQuote.setId(ID);
        theUpdatedQuote.setQuoteID(QuoteID);
        theUpdatedQuote.setCustomerID(CustomerID);
        theUpdatedQuote.setContactID(ContactID);
        //theUpdatedQuote.setOpportunityID(0);
        theUpdatedQuote.setLocalCustomerID(LocalCustomerID);
        theUpdatedQuote.setOpportunityID(LocalOpportunityID);
        theUpdatedQuote.setReference("");
        theUpdatedQuote.setCustomerName(customerName);
        theUpdatedQuote.setContactName(contactLookupName.getText().toString());

        theUpdatedQuote.setReference(reference.getText().toString());
        theUpdatedQuote.setSubject(subject.getText().toString());

        SpinnerItem selectedQuoteType = (SpinnerItem)quoteType.getSelectedItem();
        theUpdatedQuote.setQuoteType(selectedQuoteType.getText());

        SpinnerItem selectedQuoteStatus = (SpinnerItem)quoteStatus.getSelectedItem();
        theUpdatedQuote.setStatus(selectedQuoteStatus.getText());

        SimpleDateFormat sourceDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
        SimpleDateFormat targetDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

        Date theTargetDate = new Date();

//        try {
//            theTargetDate = sourceDateFormat.parse(targetDate.getText().toString());
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

//        assert theTargetDate != null;
        theUpdatedQuote.setTargetDate(targetDateFormat.format(theTargetDate));

        theUpdatedQuote.setNotes(notes.getText().toString());
        theUpdatedQuote.setCurrency(Currency);
        theUpdatedQuote.setExchangeRate(Float.parseFloat(selectedExchangeRate.getText().toString()));
        theUpdatedQuote.setCreatedByDisplayName(CreatedByDisplayName);
        theUpdatedQuote.setIsChanged(true);
        theUpdatedQuote.setIsNew(false);
        theUpdatedQuote.setIsArchived(false);

        Date updatedDate = new Date();

        theUpdatedQuote.setUpdated(targetDateFormat.format(updatedDate));

        DatabaseClient.getInstance(requireContext()).getAppDatabase()
                .quoteDao()
                .update(theUpdatedQuote);

        refreshActivity();

        if (mListener != null) {
            mListener.QuoteChanged();
        }
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {

        drawVisible = false;

        if (ID != 0) {

            Quote theQuote = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .quoteDao()
                    .getQuote(ID);

            if (theQuote.getQuoteID()!= null) {
                QuoteID = theQuote.getQuoteID();
            }
            else
            {
                QuoteID = 0;
            }

            if (theQuote.getOpportunityID()!= null) {
                LocalOpportunityID = theQuote.getOpportunityID();
            }
            else
            {
                LocalOpportunityID = 0;
            }

            CustomerID = theQuote.getCustomerID();
            ContactID = theQuote.getContactID();
            OpportunityID = theQuote.getOpportunityID();
            LocalCustomerID = theQuote.getLocalCustomerID();
            customerName = theQuote.getCustomerName();
            Currency = theQuote.getCurrency();
            ExchangeRate = theQuote.getExchangeRate();
            selectedExchangeRate.setText(String.format(Locale.UK, "%.2f", theQuote.getExchangeRate()));
            CreatedByDisplayName = theQuote.getCreatedByDisplayName();

            customerLookupName.setText(theQuote.getCustomerName());
            contactLookupName.setText(theQuote.getContactName());

            reference.setText(theQuote.getReference());
            subject.setText(theQuote.getSubject());
            notes.setText(theQuote.getNotes());

            //Is this GBP?

            int pL = totalQuoteValue.getPaddingLeft();
            int pT = totalQuoteValue.getPaddingTop();
            int pR = totalQuoteValue.getPaddingRight();
            int pB = totalQuoteValue.getPaddingBottom();

            int orientation = getResources().getConfiguration().orientation;

            if (ExchangeRate != 0) {
                if (ExchangeRate < 1 || ExchangeRate > 1)
                {
//                    currencyName.setVisibility(View.VISIBLE);
                    totalQuoteValueAlternative.setVisibility(View.VISIBLE);

                    totalQuoteValue.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.total_alternative_background_land_left));

//                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                        totalQuoteValue.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.total_alternative_background_land_left));
//                    } else {
//                        totalQuoteValue.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.total_background));
//                    }

                    currencyName.setText(Currency);
                }
                else
                {
                    currencyName.setVisibility(View.GONE);
                    totalQuoteValueAlternative.setVisibility(View.GONE);
                    totalQuoteValue.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.text_input_background));
                }
            }
            else
            {
                currencyName.setVisibility(View.GONE);
                totalQuoteValueAlternative.setVisibility(View.GONE);
                totalQuoteValue.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.text_input_background));
            }

            totalQuoteValue.setPadding(pL, pT, pR, pB);

            if (theQuote.getStatus() != null) {
                int statusIndex = 0;
                int n = quoteStatusAdapter.getCount();

                for (int i = 0; i < n; i++) {
                    SpinnerItem status = (SpinnerItem) quoteStatusAdapter.getItem(i);

                    if (status.getText().equals(theQuote.getStatus())) {
                        statusIndex = i;
                    }
                }

                quoteStatus.setSelection(statusIndex);
            }

            if (theQuote.getQuoteType() != null) {

                if (theQuote.getQuoteType().equals("Estimate"))
                {
                    quoteType.setSelection(0);
                }
                else
                {
                    quoteType.setSelection(1);
                }
            }

            if (theQuote.getCurrency() != null) {
                int statusIndex = 0;
                int n = adapterCurrency.getCount();

                for (int i = 0; i < n; i++) {
                    SpinnerItem status = (SpinnerItem) adapterCurrency.getItem(i);

                    if (status.getText().equals(theQuote.getCurrency())) {
                        statusIndex = i;
                    }
                }

                selectedCurrencyName.setSelection(statusIndex);
            }

            refreshQuoteLines();

            if (root.findViewById(R.id.activity_list) != null) {
                refreshActivity();
            }

        }
    }

    public void refreshQuoteLines()
    {
        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.quote_items_list);

        //Get linked quotes

        List<QuoteLine> quoteLines = DatabaseClient.getInstance(requireContext())
                .getAppDatabase()
                .quoteLineDao()
                .getQuoteLinesFromLocalID(ID);

        generateQuoteLineList(recyclerView, quoteLines);

        calculateQuoteTotal(quoteLines);
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
        else if (LocalCustomerID != 0) {

            List<Activity> activities = DatabaseClient.getInstance(requireContext())
                    .getAppDatabase()
                    .activityDao()
                    .getCustomerActivity(LocalCustomerID);

            generateActivityList(activityRecyclerView, activities);
        }

    }

    private void generateQuoteLineList(RecyclerView recyclerView, List<QuoteLine> quoteLines) {

        quoteLines.add(new QuoteLine(0, "New Line"));

        mQuoteLinesAdapter = new QuoteLinesRecyclerViewAdapter(quoteLines, Currency, ExchangeRate, getChildFragmentManager(), this.getContext(), this);
        recyclerView.setAdapter(mQuoteLinesAdapter);

        ItemTouchHelper.Callback callback = new QuoteLineTouchHelper(mQuoteLinesAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void calculateQuoteTotal(List<QuoteLine> quoteLines) {

        float Total;
        float SubTotal = 0.00f;
        float Discount = 0.00f;
        float Tax = 0.00f;

        for (QuoteLine line : quoteLines) {

            if (line.getQuantity() != null && line.getValue() != null) {
                SubTotal += line.getQuantity() * line.getValue();

                if (line.getDiscount() != null) {
                    Discount += ((line.getQuantity() * line.getValue()) / 100) * line.getDiscount();
                }

                if (line.getTaxRate() != null) {
                    if (line.getDiscount() != null) {
                        Tax += (((line.getQuantity() * line.getValue()) - (((line.getQuantity() * line.getValue()) / 100) * line.getDiscount())) / 100) * line.getTaxRate();
                    } else {
                        Tax += ((line.getQuantity() * line.getValue()) / 100) * line.getTaxRate();
                    }
                }
            }
        }

        if (Discount > 0) {
            totalDiscount.setText(String.format(Locale.UK, "£%,.2f Discounted", Discount));
            totalDiscount.setVisibility(View.VISIBLE);
        }
        else
        {
            totalDiscount.setVisibility(View.GONE);
        }

        if (Tax > 0) {
            totalTax.setText(String.format(Locale.UK, "£%,.2f Tax", Tax));
            totalTax.setVisibility(View.VISIBLE);
        }
        else
        {
            if (ExchangeRate != 0) {
                if (ExchangeRate < 1 || ExchangeRate > 1) {
                    totalTax.setText("No Taxes");
                }
            }
            else
            {
                totalTax.setVisibility(View.GONE);
            }
        }

        Total = (SubTotal + Tax) - Discount;

        totalQuoteValue.setText(String.format(Locale.UK, "£%,.2f", Total));

        if (ExchangeRate != 0) {
            if (ExchangeRate < 1 || ExchangeRate > 1) {
                totalQuoteValueAlternative.setText(String.format(Locale.UK, "%s%.2f", CurrencySymbol, Total * ExchangeRate));
            }
        }
    }

    private void generateActivityList(RecyclerView recyclerView, List<Activity> activities) {

        activities.add(new Activity(0, "New Activity"));

        mActivityAdapter = new ActivityRecyclerViewAdapter(activities, getChildFragmentManager(),this.requireContext(), this);
        recyclerView.setAdapter(mActivityAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.quote_detail, menu);

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

        if (id == R.id.action_send) {
            SendQuoteLink();
        }
        else if (id == R.id.action_copy) {

            CopyQuoteDialog newDialog = CopyQuoteDialog.newInstance(this, ID);
            newDialog.show(getChildFragmentManager(), "Copy Quote");
        }
        else if (id == R.id.action_convert) {
            ConvertOrderDialog newDialog = ConvertOrderDialog.newInstance(this, ID);
            newDialog.show(getChildFragmentManager(), "Convert To Order");
        }
        else if (id == R.id.action_save)
        {
            saveQuote();

            if (getParentFragment() != null) {
                Snackbar saveSnackbar = Snackbar.make(getParentFragment().requireView(), "Quote was saved", Snackbar.LENGTH_SHORT);
                saveSnackbar.show();
            }
            else
            {
                try {
                    Snackbar saveSnackbar = Snackbar.make(requireView(), "Quote was saved", Snackbar.LENGTH_SHORT);
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
                    .setTitle("Archive Quote")
                    .setMessage("Are you sure you want to archive this quote? It will be removed after the next successful sync")
                    .setIcon(android.R.drawable.ic_delete)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {

                            DatabaseClient.getInstance(requireContext()).getAppDatabase()
                                    .quoteDao()
                                    .archiveQuote(ID);

                            if (mListener != null) {
                                mListener.QuoteChanged();
                            }

                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        }
        else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    public void SendQuoteLink()
    {
        Quote theQuote = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                .quoteDao()
                .getQuote(ID);

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this

        String Random = UUID.randomUUID().toString();

        if (theQuote.getContactID() != null) {

            Contact theContact = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .contactDao()
                    .getContactByContactID(theQuote.getContactID());

            if (theContact != null) {

                if (theContact.getEmail() != null)
                {
                    if (!theContact.getEmail().equals("")) {
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{theContact.getEmail()});
                    }
                    else
                    {
                        if (theQuote.getId() != null)
                        {
                            Customer theCustomer = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                                    .customerDao()
                                    .getCustomer(theQuote.getLocalCustomerID());

                            if (theCustomer != null) {
                                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{theCustomer.getGeneralEmail()});
                            }
                        }
                    }
                }
                else
                {
                    if (theQuote.getId() != null)
                    {
                        Customer theCustomer = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                                .customerDao()
                                .getCustomer(theQuote.getLocalCustomerID());

                        if (theCustomer != null) {
                            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{theCustomer.getGeneralEmail()});
                        }
                    }
                }
            }
            else
            {
                if (theQuote.getId() != null) {

                    Customer theCustomer = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                            .customerDao()
                            .getCustomer(theQuote.getLocalCustomerID());

                    if (theCustomer != null) {
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{theCustomer.getGeneralEmail()});
                    }
                }
            }
        }
        else
        {
            if (theQuote.getId() != null) {

                Customer theCustomer = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                        .customerDao()
                        .getCustomer(theQuote.getLocalCustomerID());

                if (theCustomer != null) {
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{theCustomer.getGeneralEmail()});
                }
            }
        }

        intent.putExtra(Intent.EXTRA_SUBJECT, "Your " + ((SpinnerItem)quoteType.getSelectedItem()).getText());

        String HTMLbody = "Dear " + theQuote.getContactName() + "</br></br>" +
                "Please follow the link below to view your " + ((SpinnerItem) quoteType.getSelectedItem()).getText() + "</br></br>" +
                "<a href=\"http://portal.mybirdy.co.uk?token=" + Random + "\">View Your Document(s)</a>";
        intent.putExtra(Intent.EXTRA_HTML_TEXT, HTMLbody);

        String TextBody = "Dear " + theQuote.getContactName() + "\n" + "\n" +
                "Please follow the link below to view your " + ((SpinnerItem) quoteType.getSelectedItem()).getText() + "\n" + "\n" +
                "http://portal.mybirdy.co.uk?token=" + Random;
        intent.putExtra(Intent.EXTRA_TEXT, TextBody);

        //if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
        startActivity(intent);
        //}
    }

//    public void generatePDF()
//    {
//        int pageHeight = 842; //A4
//        int pagewidth = 595; //A4
//
//        int numColumns, numRows;
//        int cellWidth, cellHeight;
//
//        Bitmap bmp, scaledbmp;
//
//        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.logo_grey);
//        scaledbmp = Bitmap.createScaledBitmap(bmp, 60, 60, false);
//
//        PdfDocument pdfDocument = new PdfDocument();
//
//        Paint paint = new Paint();
//        Paint title = new Paint();
//
//        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 1).create();
//
//        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);
//
//        Canvas canvas = myPage.getCanvas();
//
//        canvas.drawBitmap(scaledbmp, 40, 40, paint);
//
//        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
//        title.setTextSize(15);
//        title.setColor(ContextCompat.getColor(requireContext(), R.color.colorAccent));
//
//        canvas.drawText("MyBirdy.co.uk", 120, 40, title);
//        canvas.drawText("1 Henley Way", 120, 60, title);
//        canvas.drawText("Lincoln", 120, 80, title);
//        canvas.drawText("LN6 3QR", 120, 100, title);
//
//        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
//        title.setColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark));
//        title.setTextSize(15);
//
//        title.setTextAlign(Paint.Align.LEFT);
//        canvas.drawText("Please find the details of your estimate below:", 40, 140, title);
//
//        //Add Grid
//
//        numColumns = 4;
//        numRows = 6;
//
//        cellWidth = 400 / numColumns;
//        cellHeight = 600 / numRows;
//
//        Paint blackPaint = new Paint();
//
////        for (int i = 0; i < numColumns; i++) {
////            for (int j = 0; j < numRows; j++) {
////                if (cellChecked[i][j]) {
////
////                    canvas.drawRect(i * cellWidth, j * cellHeight,
////                            (i + 1) * cellWidth, (j + 1) * cellHeight,
////                            blackPaint);
////                }
////            }
////        }
//
//        for (int i = 1; i < numColumns; i++) {
//            canvas.drawLine(i * cellWidth + 40, 160, i * cellWidth, 600, blackPaint);
//        }
//
//        for (int i = 1; i < numRows; i++) {
//            canvas.drawLine(40, i * cellHeight + 160, 400, i * cellHeight, blackPaint);
//        }
//
//        pdfDocument.finishPage(myPage);
//
//        //File file = new File(Environment.getExternalStorageDirectory(), "Quote.pdf");
//        final File file = new File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Quote_" + UUID.randomUUID().toString() + ".pdf");
//
//        try {
//
//            pdfDocument.writeTo(new FileOutputStream(file));
//            Toast.makeText(requireContext(), "PDF file generated successfully.", Toast.LENGTH_SHORT).show();
//
//            String provider = this.requireContext().getPackageName() + ".provider";
//
//            Intent intent = new Intent();
//            intent.setAction(Intent.ACTION_VIEW);
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.setDataAndType(androidx.core.content.FileProvider.getUriForFile(requireContext(), provider, file), "application/pdf");
//            requireContext().startActivity(intent);
//
//        } catch (IOException e) {
//            // below line is used
//            // to handle error
//            e.printStackTrace();
//        }
//
//    }

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

    @Override
    public void CreateNewQuoteLine() {
        NewQuoteLineDialog newDialog = NewQuoteLineDialog.newInstance(this, ID);
        newDialog.show(getChildFragmentManager(), "New Line");
    }

    @Override
    public void EditLine(int position, QuoteLine line) {
        EditQuoteLineDialog newDialog = EditQuoteLineDialog.newInstance(this, position, line);
        newDialog.show(getChildFragmentManager(), "Edit Line");
    }

    @Override
    public void RemoveLine(int position, int id) {

        DatabaseClient.getInstance(requireContext())
                .getAppDatabase()
                .quoteLineDao()
                .deleteQuoteLine(id);

        refreshQuoteLines();
    }

    @Override
    public void UpdatedQuoteLine(int position, QuoteLine updatedLine) {

        updatedLine.setQuoteID(QuoteID);

        DatabaseClient.getInstance(requireContext())
                .getAppDatabase()
                .quoteLineDao()
                .update(updatedLine);

        refreshQuoteLines();
    }

    @Override
    public void RemoveQuoteLine(int position, QuoteLine updatedLine) {

        DatabaseClient.getInstance(requireContext())
                .getAppDatabase()
                .quoteLineDao()
                .deleteQuoteLine(updatedLine.getId());

        refreshQuoteLines();
    }

    @Override
    public void NewQuoteLineAdded(QuoteLine quoteLine) {

        quoteLine.setQuoteID(QuoteID);

        DatabaseClient.getInstance(requireContext())
                .getAppDatabase()
                .quoteLineDao()
                .insert(quoteLine);

        refreshQuoteLines();
    }

    @Override
    public void ContactChanged(int ID, int contactID, String contactName) {
        contactLookupName.setText(contactName);
    }

    @Override
    public void CustomerChanged(int customerID, String CustomerName) {
        customerLookupName.setText(CustomerName);
        customerName = CustomerName;
        LocalCustomerID = customerID;

        //Lookup Contact

        ChangeContactDialog newFragment = ChangeContactDialog.newInstance(QuoteDetailFragment.this, LocalCustomerID, customerLookupName.getText().toString());
        newFragment.show(getChildFragmentManager(), "Select Contact");

    }

    @Override
    public void ActivityUpdated() {
        refreshActivity();
    }

    @Override
    public void NewActivityAdded() {
        refreshActivity();
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static float dpToPx(int dp) {
        return (int)dp * Resources.getSystem().getDisplayMetrics().density;
    }

    @Override
    public void QuoteDupliated(int ID) {

        if (mListener != null) {
            mListener.QuoteChanged();
        }
    }
}