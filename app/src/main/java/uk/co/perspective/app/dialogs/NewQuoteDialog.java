package uk.co.perspective.app.dialogs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import uk.co.perspective.app.R;
import uk.co.perspective.app.adapters.QuoteLinesRecyclerViewAdapter;
import uk.co.perspective.app.database.AppDatabase;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.entities.Currency;
import uk.co.perspective.app.entities.Quote;
import uk.co.perspective.app.entities.QuoteLine;
import uk.co.perspective.app.helpers.QuoteLineTouchHelper;
import uk.co.perspective.app.models.SpinnerItem;

public class NewQuoteDialog extends DialogFragment implements ChangeCustomerDialog.ChangeCustomerListener, ChangeContactDialog.ChangeContactListener, QuoteLinesRecyclerViewAdapter.QuoteListener, NewQuoteLineDialog.NewQuoteLineListener, EditQuoteLineDialog.UpdatedQuoteLineListener {

    private View root;

    private int LocalCustomerID;
    private String CustomerName;
    private int LocalOpportunityID;
    private float ExchangeRate = 1.00f;
    private String CurrencyName = "Pounds Sterling";
    private String CurrencySymbol = "£";

    private ArrayList<QuoteLine> quoteLines;

    private EditText subject;
    private Spinner quoteType;
    private Spinner currencyName;
    private EditText exchangeRate;
    private EditText notes;

    private TextView totalDiscount;
    private TextView totalTax;
    private TextView totalQuoteValue;
    private TextView totalQuoteAlternativeValue;

    private ConstraintLayout customerLookup;
    private TextView customerLookupName;
    private TextView contactLookupName;

    private QuoteLinesRecyclerViewAdapter mQuoteLinesAdapter;

    private ItemTouchHelper mItemTouchHelper;

    Button okSelected;
    Button cancelSelected;

    NewQuoteDialog.NewQuoteListener mListener;

    public interface NewQuoteListener {
        public void NewQuoteAdded(int ID);
    }

    public NewQuoteDialog() {
        // Required empty public constructor
    }

    public void setListener(NewQuoteListener listener) {
        mListener = listener;
    }

    public void setCustomer(int ID, String customerName, int localOpportunityID) {
        LocalCustomerID = ID;
        CustomerName = customerName;
        LocalOpportunityID = localOpportunityID;
    }

    public static NewQuoteDialog newInstance(NewQuoteListener mListener, int localCustomerID, String customerName, int localOpportunityID) {
        NewQuoteDialog frag = new NewQuoteDialog();
        Bundle args = new Bundle();
        frag.setListener(mListener);
        frag.setCustomer(localCustomerID, customerName, localOpportunityID);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        setCancelable(false);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.dialog_new_quote, container, false);

        final AppDatabase database = DatabaseClient.getInstance(requireContext()).getAppDatabase();

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        root.setBackgroundResource(R.drawable.dialog_rounded);

        customerLookup = root.findViewById(R.id.customer_details);
        customerLookupName = root.findViewById(R.id.customer_name_label);
        contactLookupName = root.findViewById(R.id.contact_name_label);

        quoteType = root.findViewById(R.id.quote_type);
        currencyName = root.findViewById(R.id.currency_name);
        exchangeRate = root.findViewById(R.id.exchangeRate);
        subject = root.findViewById(R.id.subject);
        notes = root.findViewById(R.id.notes);

        totalDiscount = root.findViewById(R.id.total_discount);
        totalTax = root.findViewById(R.id.total_tax);
        totalQuoteValue = root.findViewById(R.id.quote_total);
        totalQuoteAlternativeValue = root.findViewById(R.id.quote_total_alternative_currency);

        contactLookupName.setVisibility(View.GONE);

        //Buttons

        okSelected = root.findViewById(R.id.save);
        cancelSelected = root.findViewById(R.id.cancel);


        //Setup Spinners

        //Quote Type

        final ArrayList<SpinnerItem> spinnerArrayQuoteType =  new ArrayList<>();

        spinnerArrayQuoteType.add(new SpinnerItem(1, "Quote"));
        spinnerArrayQuoteType.add(new SpinnerItem(2, "Estimate"));

        ArrayAdapter<SpinnerItem> adapterQuoteType = new ArrayAdapter<SpinnerItem>(requireContext(), R.layout.dropdown_list_item, spinnerArrayQuoteType);
        adapterQuoteType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quoteType.setAdapter(adapterQuoteType);

        //Currency

        final List<Currency> currencies = database
                .currencyDao()
                .getAll();


        final ArrayList<SpinnerItem> spinnerArrayCurrencies =  new ArrayList<>();

        for (Currency currency: currencies)
        {
            spinnerArrayCurrencies.add(new SpinnerItem(currency.getId(), currency.getCurrencyName()));
        }

        ArrayAdapter<SpinnerItem> adapterCurrency = new ArrayAdapter<SpinnerItem>(requireContext(), R.layout.dropdown_list_item, spinnerArrayCurrencies);
        adapterCurrency.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencyName.setAdapter(adapterCurrency);

        final RecyclerView quoteLinesRecyclerView = (RecyclerView) root.findViewById(R.id.quote_items_list);
        Context contactsRecyclerViewContext = quoteLinesRecyclerView.getContext();

        quoteLinesRecyclerView.setLayoutManager(new LinearLayoutManager(contactsRecyclerViewContext));
        quoteLinesRecyclerView.setItemAnimator(new DefaultItemAnimator());

        customerLookup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            ChangeCustomerDialog newFragment = ChangeCustomerDialog.newInstance(NewQuoteDialog.this);
            newFragment.show(getChildFragmentManager(), "Change Customer");
            }
        });

        currencyName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                //Get ID

                SpinnerItem selectedItem = (SpinnerItem) currencyName.getSelectedItem();

                Currency currency = database
                        .currencyDao()
                        .getCurrency(selectedItem.getValue());

                if (currency != null)
                {
                    ExchangeRate = currency.getRate();
                    CurrencyName = currency.getCurrencyName();
                    CurrencySymbol = currency.getShortSymbol();

                    exchangeRate.setText(String.format(Locale.UK, "%.2f", currency.getRate()));

                    int pL = totalQuoteValue.getPaddingLeft();
                    int pT = totalQuoteValue.getPaddingTop();
                    int pR = totalQuoteValue.getPaddingRight();
                    int pB = totalQuoteValue.getPaddingBottom();

                    if (currency.getRate() != 0) {
                        if (currency.getRate() < 1 || currency.getRate() > 1)
                        {
                            totalQuoteAlternativeValue.setVisibility(View.VISIBLE);
                            totalQuoteValue.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.total_alternative_background_land_left));
                        }
                        else
                        {
                            totalQuoteAlternativeValue.setVisibility(View.GONE);
                            totalQuoteValue.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.text_input_background));
                        }
                    }
                    else
                    {
                        totalQuoteAlternativeValue.setVisibility(View.GONE);
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

        okSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            if (quoteLines.size() > 1) {

                //Save Quote

                Quote newQuote = new Quote();

                newQuote.setQuoteID(0);
                newQuote.setCustomerID(0);
                newQuote.setContactID(0);
                newQuote.setOpportunityID(LocalOpportunityID);

                newQuote.setLocalCustomerID(LocalCustomerID);
                newQuote.setCustomerName(CustomerName);
                newQuote.setContactName(contactLookupName.getText().toString());
                newQuote.setSubject(subject.getText().toString());
                newQuote.setNotes(notes.getText().toString());
                newQuote.setReference("");
                newQuote.setStatus("New (Not Issued)");

                SpinnerItem selectedQuoteType = (SpinnerItem) quoteType.getSelectedItem();
                newQuote.setQuoteType(selectedQuoteType.getText());

                newQuote.setCurrency(CurrencyName);
                newQuote.setExchangeRate(ExchangeRate);

                newQuote.setIsChanged(false);
                newQuote.setIsNew(true);
                newQuote.setIsArchived(false);

                Date updatedDate = new Date();

                SimpleDateFormat targetDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                newQuote.setUpdated(targetDateFormat.format(updatedDate));
                newQuote.setTargetDate(targetDateFormat.format(updatedDate));

                long ID = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                        .quoteDao()
                        .insert(newQuote);

                //Save Lines

                if (ID != 0)
                {
                    for (QuoteLine line : quoteLines) {

                        if (!line.getDescription().equals("New Line")) {

                            line.setLocalQuoteID((int) ID);
                            line.setQuoteLineID(0);
                            line.setQuoteID(0);

                            DatabaseClient.getInstance(requireContext()).getAppDatabase()
                                    .quoteLineDao()
                                    .insert(line);
                        }
                    }
                }

                mListener.NewQuoteAdded((int)ID);

                dismiss();
            }
            else
            {
                int pL = quoteLinesRecyclerView.getPaddingLeft();
                int pT = quoteLinesRecyclerView.getPaddingTop();
                int pR = quoteLinesRecyclerView.getPaddingRight();
                int pB = quoteLinesRecyclerView.getPaddingBottom();
                quoteLinesRecyclerView.setBackgroundResource(R.drawable.calculated_list_background_required);
                quoteLinesRecyclerView.setPadding(pL, pT, pR, pB);
            }
            }
        });

        cancelSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {

        if (LocalCustomerID != 0 && !CustomerName.equals(""))
        {
            customerLookupName.setText(CustomerName);
            customerLookupName.setTextColor(Color.DKGRAY);
        }

        quoteLines = new ArrayList<>();

        quoteLines.add(new QuoteLine(0, "New Line"));

        totalQuoteAlternativeValue.setVisibility(View.GONE);

        refreshQuoteLines();
    }

    @Override
    public void CustomerChanged(int ID, String customerName) {

        this.LocalCustomerID = ID;
        this.CustomerName = customerName;

        customerLookupName.setText(customerName);
        customerLookupName.setTextColor(Color.DKGRAY);

        //Lookup Contact

        ChangeContactDialog newFragment = ChangeContactDialog.newInstance(NewQuoteDialog.this, LocalCustomerID, customerLookupName.getText().toString());
        newFragment.show(getChildFragmentManager(), "Select Contact");
    }

    @Override
    public void ContactChanged(int ID, int contactID, String contactName) {
        contactLookupName.setVisibility(View.VISIBLE);
        contactLookupName.setText(contactName);
    }

    private void refreshQuoteLines() {
        final RecyclerView quoteLinesRecyclerView = root.findViewById(R.id.quote_items_list);
        generateQuoteLineList(quoteLinesRecyclerView, quoteLines);
        calculateQuoteTotal(quoteLines);
    }

    private void generateQuoteLineList(RecyclerView recyclerView, List<QuoteLine> quoteLines) {

        mQuoteLinesAdapter = new QuoteLinesRecyclerViewAdapter(quoteLines, CurrencyName, ExchangeRate, getChildFragmentManager(),this.getContext(), this);
        recyclerView.setAdapter(mQuoteLinesAdapter);

        ItemTouchHelper.Callback callback = new QuoteLineTouchHelper(mQuoteLinesAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void CreateNewQuoteLine() {
        NewQuoteLineDialog newDialog = NewQuoteLineDialog.newInstance(this, 0);
        newDialog.show(getChildFragmentManager(), "New Line");
    }

    @Override
    public void EditLine(int position, QuoteLine Line) {
        EditQuoteLineDialog newDialog = EditQuoteLineDialog.newInstance(this, position, Line);
        newDialog.show(getChildFragmentManager(), "Edit Line");
    }

    @Override
    public void RemoveLine(int position, int id) {
        quoteLines.remove(position);
        quoteLines.add(new QuoteLine(0, "New Line"));
        refreshQuoteLines();
    }

    @Override
    public void NewQuoteLineAdded(QuoteLine newLine) {
        quoteLines.add((quoteLines.size() - 1), newLine);
        refreshQuoteLines();
    }

    @Override
    public void UpdatedQuoteLine(int position, QuoteLine updatedLine) {
        quoteLines.set(position, updatedLine);
        refreshQuoteLines();
    }

    @Override
    public void RemoveQuoteLine(int position, QuoteLine line) {
        quoteLines.remove(position);
        refreshQuoteLines();
    }

    private void calculateQuoteTotal(List<QuoteLine> quoteLines) {

        float Total;
        float SubTotal = 0.00f;
        float Discount = 0.00f;
        float Tax = 0.00f;

        for (QuoteLine line : quoteLines) {

            if (line.getValue() != null) {

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
            totalTax.setVisibility(View.GONE);
        }

        Total = (SubTotal + Tax) - Discount;

        totalQuoteValue.setText(String.format(Locale.UK, "£%,.2f", Total));

        if (ExchangeRate != 0) {
            if (ExchangeRate < 1 || ExchangeRate > 1) {
                totalQuoteAlternativeValue.setText(String.format(Locale.UK, "%s%.2f", CurrencySymbol, Total * ExchangeRate));
            }
        }
    }

}