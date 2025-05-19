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
import uk.co.perspective.app.adapters.OrderLinesRecyclerViewAdapter;
import uk.co.perspective.app.adapters.QuoteLinesRecyclerViewAdapter;
import uk.co.perspective.app.database.AppDatabase;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.entities.Currency;
import uk.co.perspective.app.entities.Order;
import uk.co.perspective.app.entities.OrderLine;

import uk.co.perspective.app.helpers.OrderLineTouchHelper;
import uk.co.perspective.app.models.SpinnerItem;

public class NewOrderDialog extends DialogFragment implements ChangeCustomerDialog.ChangeCustomerListener, ChangeContactDialog.ChangeContactListener, OrderLinesRecyclerViewAdapter.OrderListener, NewOrderLineDialog.NewOrderLineListener, EditOrderLineDialog.UpdatedOrderLineListener {

    private View root;

    private int LocalCustomerID;
    private String CustomerName;
    private int LocalOpportunityID;
    private float ExchangeRate = 1.00f;
    private String CurrencyName = "Pounds Sterling";
    private String CurrencySymbol = "£";

    private ArrayList<OrderLine> orderLines;

    private EditText subject;
    private Spinner currencyName;
    private EditText exchangeRate;
    private EditText notes;

    private TextView totalDiscount;
    private TextView totalTax;
    private TextView totalOrderValue;
    private TextView totalOrderAlternativeValue;

    private ConstraintLayout customerLookup;
    private TextView customerLookupName;
    private TextView contactLookupName;

    private OrderLinesRecyclerViewAdapter mOrderLinesAdapter;

    private ItemTouchHelper mItemTouchHelper;

    Button okSelected;
    Button cancelSelected;

    NewOrderDialog.NewOrderListener mListener;

    public interface NewOrderListener {
        public void NewOrderAdded(int ID);
    }

    public NewOrderDialog() {
        // Required empty public constructor
    }

    public void setListener(NewOrderListener listener) {
        mListener = listener;
    }

    public void setCustomer(int ID, String customerName, int localOpportunityID) {
        LocalCustomerID = ID;
        CustomerName = customerName;
        LocalOpportunityID = localOpportunityID;
    }

    public static NewOrderDialog newInstance(NewOrderListener mListener, int localCustomerID, String customerName, int localOpportunityID) {
        NewOrderDialog frag = new NewOrderDialog();
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

        root = inflater.inflate(R.layout.dialog_new_order, container, false);

        final AppDatabase database = DatabaseClient.getInstance(requireContext()).getAppDatabase();

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        root.setBackgroundResource(R.drawable.dialog_rounded);

        customerLookup = root.findViewById(R.id.customer_details);
        customerLookupName = root.findViewById(R.id.customer_name_label);
        contactLookupName = root.findViewById(R.id.contact_name_label);

        currencyName = root.findViewById(R.id.currency_name);
        exchangeRate = root.findViewById(R.id.exchangeRate);
        subject = root.findViewById(R.id.subject);
        notes = root.findViewById(R.id.notes);

        totalDiscount = root.findViewById(R.id.total_discount);
        totalTax = root.findViewById(R.id.total_tax);
        totalOrderValue = root.findViewById(R.id.order_total);
        totalOrderAlternativeValue = root.findViewById(R.id.order_total_alternative_currency);

        contactLookupName.setVisibility(View.GONE);

        //Buttons

        okSelected = root.findViewById(R.id.save);
        cancelSelected = root.findViewById(R.id.cancel);

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

        final RecyclerView orderLinesRecyclerView = (RecyclerView) root.findViewById(R.id.order_items_list);
        Context contactsRecyclerViewContext = orderLinesRecyclerView.getContext();

        orderLinesRecyclerView.setLayoutManager(new LinearLayoutManager(contactsRecyclerViewContext));
        orderLinesRecyclerView.setItemAnimator(new DefaultItemAnimator());

        customerLookup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            ChangeCustomerDialog newFragment = ChangeCustomerDialog.newInstance(NewOrderDialog.this);
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

                    int pL = totalOrderValue.getPaddingLeft();
                    int pT = totalOrderValue.getPaddingTop();
                    int pR = totalOrderValue.getPaddingRight();
                    int pB = totalOrderValue.getPaddingBottom();

                    if (currency.getRate() != 0) {
                        if (currency.getRate() < 1 || currency.getRate() > 1)
                        {
                            totalOrderAlternativeValue.setVisibility(View.VISIBLE);
                            totalOrderValue.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.total_alternative_background_land_left));
                        }
                        else
                        {
                            totalOrderAlternativeValue.setVisibility(View.GONE);
                            totalOrderValue.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.text_input_background));
                        }
                    }
                    else
                    {
                        totalOrderAlternativeValue.setVisibility(View.GONE);
                        totalOrderValue.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.text_input_background));
                    }

                    totalOrderValue.setPadding(pL, pT, pR, pB);

                    refreshOrderLines();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });

        okSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            if (orderLines.size() > 1) {

                //Save Order

                Order newOrder = new Order();

                newOrder.setOrderID(0);
                newOrder.setCustomerID(0);
                newOrder.setContactID(0);
                newOrder.setOpportunityID(LocalOpportunityID);

                newOrder.setLocalCustomerID(LocalCustomerID);
                newOrder.setCustomerName(CustomerName);
                newOrder.setContactName(contactLookupName.getText().toString());
                newOrder.setSubject(subject.getText().toString());
                newOrder.setNotes(notes.getText().toString());
                newOrder.setReference("");
                newOrder.setStatus("New (Not Issued)");


                newOrder.setCurrency(CurrencyName);
                newOrder.setExchangeRate(ExchangeRate);

                newOrder.setIsChanged(false);
                newOrder.setIsNew(true);
                newOrder.setIsArchived(false);

                Date updatedDate = new Date();

                SimpleDateFormat targetDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                newOrder.setUpdated(targetDateFormat.format(updatedDate));
                newOrder.setClosingDate(targetDateFormat.format(updatedDate));

                long ID = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                        .orderDao()
                        .insert(newOrder);

                //Save Lines

                if (ID != 0)
                {
                    for (OrderLine line : orderLines) {

                        if (!line.getDescription().equals("New Line")) {

                            line.setLocalOrderID((int) ID);
                            line.setOrderLineID(0);
                            line.setOrderID(0);

                            DatabaseClient.getInstance(requireContext()).getAppDatabase()
                                    .orderLineDao()
                                    .insert(line);
                        }
                    }
                }

                mListener.NewOrderAdded((int)ID);

                dismiss();
            }
            else
            {
                int pL = orderLinesRecyclerView.getPaddingLeft();
                int pT = orderLinesRecyclerView.getPaddingTop();
                int pR = orderLinesRecyclerView.getPaddingRight();
                int pB = orderLinesRecyclerView.getPaddingBottom();
                orderLinesRecyclerView.setBackgroundResource(R.drawable.calculated_list_background_required);
                orderLinesRecyclerView.setPadding(pL, pT, pR, pB);
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

        orderLines = new ArrayList<>();

        orderLines.add(new OrderLine(0, "New Line"));

        totalOrderAlternativeValue.setVisibility(View.GONE);

        refreshOrderLines();
    }

    @Override
    public void CustomerChanged(int ID, String customerName) {

        this.LocalCustomerID = ID;
        this.CustomerName = customerName;

        customerLookupName.setText(customerName);
        customerLookupName.setTextColor(Color.DKGRAY);

        //Lookup Contact

        ChangeContactDialog newFragment = ChangeContactDialog.newInstance(NewOrderDialog.this, LocalCustomerID, customerLookupName.getText().toString());
        newFragment.show(getChildFragmentManager(), "Select Contact");
    }

    @Override
    public void ContactChanged(int ID, int contactID, String contactName) {
        contactLookupName.setVisibility(View.VISIBLE);
        contactLookupName.setText(contactName);
    }

    private void refreshOrderLines() {
        final RecyclerView orderLinesRecyclerView = root.findViewById(R.id.order_items_list);
        generateOrderLineList(orderLinesRecyclerView, orderLines);
        calculateOrderTotal(orderLines);
    }

    private void generateOrderLineList(RecyclerView recyclerView, List<OrderLine> orderLines) {

        mOrderLinesAdapter = new OrderLinesRecyclerViewAdapter(orderLines, CurrencyName, ExchangeRate, getChildFragmentManager(),this.getContext(), this);
        recyclerView.setAdapter(mOrderLinesAdapter);

        ItemTouchHelper.Callback callback = new OrderLineTouchHelper(mOrderLinesAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void CreateNewOrderLine() {
        NewOrderLineDialog newDialog = NewOrderLineDialog.newInstance(this, 0);
        newDialog.show(getChildFragmentManager(), "New Line");
    }

    @Override
    public void EditLine(int position, OrderLine Line) {
        EditOrderLineDialog newDialog = EditOrderLineDialog.newInstance(this, position, Line);
        newDialog.show(getChildFragmentManager(), "Edit Line");
    }

    @Override
    public void RemoveLine(int position, int id) {
        orderLines.remove(position);
        orderLines.add(new OrderLine(0, "New Line"));
        refreshOrderLines();
    }

    @Override
    public void NewOrderLineAdded(OrderLine newLine) {
        orderLines.add((orderLines.size() - 1), newLine);
        refreshOrderLines();
    }

    @Override
    public void UpdatedOrderLine(int position, OrderLine updatedLine) {
        orderLines.set(position, updatedLine);
        refreshOrderLines();
    }

    @Override
    public void RemoveOrderLine(int position, OrderLine line) {
        orderLines.remove(position);
        refreshOrderLines();
    }

    private void calculateOrderTotal(List<OrderLine> orderLines) {

        float Total;
        float SubTotal = 0.00f;
        float Discount = 0.00f;
        float Tax = 0.00f;

        for (OrderLine line : orderLines) {

            if (line.getValue()!= null) {

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

        totalOrderValue.setText(String.format(Locale.UK, "£%,.2f", Total));

        if (ExchangeRate != 0) {
            if (ExchangeRate < 1 || ExchangeRate > 1) {
                totalOrderAlternativeValue.setText(String.format(Locale.UK, "%s%.2f", CurrencySymbol, Total * ExchangeRate));
            }
        }
    }

}