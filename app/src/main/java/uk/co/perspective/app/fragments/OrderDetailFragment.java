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
import uk.co.perspective.app.adapters.ActivityRecyclerViewAdapter;
import uk.co.perspective.app.adapters.OrderLinesRecyclerViewAdapter;
import uk.co.perspective.app.database.AppDatabase;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.dialogs.ChangeContactDialog;
import uk.co.perspective.app.dialogs.ChangeCustomerDialog;
import uk.co.perspective.app.dialogs.CopyOrderDialog;
import uk.co.perspective.app.dialogs.EditActivityDialog;
//import uk.co.perspective.app.dialogs.EditOrderLineDialog;
import uk.co.perspective.app.dialogs.NewActivityDialog;
//import uk.co.perspective.app.dialogs.NewOrderLineDialog;
import uk.co.perspective.app.entities.Activity;
import uk.co.perspective.app.entities.Contact;
import uk.co.perspective.app.entities.Currency;
import uk.co.perspective.app.entities.Customer;
import uk.co.perspective.app.entities.Order;
import uk.co.perspective.app.entities.OrderLine;
import uk.co.perspective.app.helpers.OrderLineTouchHelper;
import uk.co.perspective.app.models.SpinnerItem;

public class OrderDetailFragment extends Fragment implements
        NewActivityDialog.NewActivityListener,
        ActivityRecyclerViewAdapter.ActivityListener,
        EditActivityDialog.UpdatedActivityListener,
        ChangeCustomerDialog.ChangeCustomerListener,
        ChangeContactDialog.ChangeContactListener,
        OrderLinesRecyclerViewAdapter.OrderListener,
        //NewOrderLineDialog.NewOrderLineListener,
        //EditOrderLineDialog.UpdatedOrderLineListener,
        CopyOrderDialog.CopyOrderListener {

    private View root;

    private int ID;
    private int OrderID;
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
    private Spinner OrderType;
    private Spinner OrderStatus;
    private EditText notes;

    private TextView totalDiscount;
    private TextView totalTax;
    private TextView totalOrderValue;
    private TextView totalOrderValueAlternative;
    private TextView currencyName;

    private Spinner selectedCurrencyName;
    private TextView selectedExchangeRate;

    private ConstraintLayout customerContactDetails;
    private TextView customerLookupName;
    private TextView contactLookupName;

    private RelativeLayout activityContainer;

    private OrderLinesRecyclerViewAdapter mOrderLinesAdapter;
    private ActivityRecyclerViewAdapter mActivityAdapter;

    private ItemTouchHelper mItemTouchHelper;

    private boolean drawVisible;

    private Button save;

    private ArrayAdapter<SpinnerItem> OrderStatusAdapter;
    private ArrayAdapter<SpinnerItem> adapterCurrency;

    OrderDetailFragment.ChangeOrderListener mListener;

    public interface ChangeOrderListener {
        void OrderChanged();
    }

    public void setListener(final OrderDetailFragment.ChangeOrderListener listener) {
        mListener = listener;
    }

    public OrderDetailFragment() {
        // Required empty public constructor
    }

    public static OrderDetailFragment newInstance(ChangeOrderListener mListener) {
        OrderDetailFragment fragment = new OrderDetailFragment();
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

        root =  inflater.inflate(R.layout.fragment_order_detail, container, false);

        final AppDatabase database = DatabaseClient.getInstance(requireContext()).getAppDatabase();

        customerContactDetails = root.findViewById(R.id.customer_contact_details);

        customerLookupName = root.findViewById(R.id.customer_name_label);
        contactLookupName = root.findViewById(R.id.contact_name_label);

        reference = root.findViewById(R.id.reference);
        subject = root.findViewById(R.id.subject);
        OrderStatus = root.findViewById(R.id.order_status);
        selectedCurrencyName = root.findViewById(R.id.selected_currency_name);
        selectedExchangeRate = root.findViewById(R.id.exchangeRate);
        notes  = root.findViewById(R.id.notes);

        totalDiscount = root.findViewById(R.id.total_discount);
        totalTax = root.findViewById(R.id.total_tax);
        totalOrderValue = root.findViewById(R.id.order_total);
        totalOrderValueAlternative = root.findViewById(R.id.order_total_alternative_currency);
        currencyName = root.findViewById(R.id.currency_name);

        save = root.findViewById(R.id.save);

        //Set-up Spinners

        final ArrayList<SpinnerItem> spinnerArray =  new ArrayList<>();


        spinnerArray.add(new SpinnerItem(1, "New (Not Issued)"));
        spinnerArray.add(new SpinnerItem(2, "Under Review"));
        spinnerArray.add(new SpinnerItem(3,"Confirmed"));
        spinnerArray.add(new SpinnerItem(4,"Cancelled"));
        spinnerArray.add(new SpinnerItem(5,"Invoiced"));
        spinnerArray.add(new SpinnerItem(6,"Sent"));

        OrderStatusAdapter = new ArrayAdapter<SpinnerItem>(requireContext(), R.layout.dropdown_list_item, spinnerArray);
        OrderStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        OrderStatus.setAdapter(OrderStatusAdapter);

//        final ArrayList<SpinnerItem> spinnerArrayOrderType =  new ArrayList<>();
//
//        spinnerArrayOrderType.add(new SpinnerItem(1, "Estimate"));
//        spinnerArrayOrderType.add(new SpinnerItem(2, "Order"));
//
//        final ArrayAdapter<SpinnerItem> adapterOrderType = new ArrayAdapter<SpinnerItem>(requireContext(), R.layout.dropdown_list_item, spinnerArrayOrderType);
//        adapterOrderType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        OrderType.setAdapter(adapterOrderType);

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

                    int pL = totalOrderValue.getPaddingLeft();
                    int pT = totalOrderValue.getPaddingTop();
                    int pR = totalOrderValue.getPaddingRight();
                    int pB = totalOrderValue.getPaddingBottom();

                    if (currency.getRate() != 0) {
                        if (currency.getRate() < 1 || currency.getRate() > 1)
                        {
                            totalOrderValueAlternative.setVisibility(View.VISIBLE);
                            totalOrderValue.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.total_alternative_background_land_left));
                        }
                        else
                        {
                            totalOrderValueAlternative.setVisibility(View.GONE);
                            totalOrderValue.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.text_input_background));
                        }
                    }
                    else
                    {
                        totalOrderValueAlternative.setVisibility(View.GONE);
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

        customerContactDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            ChangeCustomerDialog newFragment = ChangeCustomerDialog.newInstance(OrderDetailFragment.this);
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

        final RecyclerView itemsRecyclerView = root.findViewById(R.id.order_items_list);
        Context contactsRecyclerViewContext = itemsRecyclerView.getContext();

        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(contactsRecyclerViewContext));
        itemsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOrder();
                Snackbar saveSnackbar = Snackbar.make(v, "Order was saved", Snackbar.LENGTH_SHORT);
                saveSnackbar.show();
            }
        });

        return root;
    }

    private void saveOrder()
    {
        //Save Order

        Order theUpdatedOrder = new Order();

        theUpdatedOrder.setId(ID);
        theUpdatedOrder.setOrderID(OrderID);
        theUpdatedOrder.setCustomerID(CustomerID);
        theUpdatedOrder.setContactID(ContactID);
        //theUpdatedOrder.setOpportunityID(0);
        theUpdatedOrder.setLocalCustomerID(LocalCustomerID);
        theUpdatedOrder.setOpportunityID(LocalOpportunityID);
        theUpdatedOrder.setReference("");
        theUpdatedOrder.setCustomerName(customerName);
        theUpdatedOrder.setContactName(contactLookupName.getText().toString());

        theUpdatedOrder.setReference(reference.getText().toString());
        theUpdatedOrder.setSubject(subject.getText().toString());

        //SpinnerItem selectedOrderType = (SpinnerItem)OrderType.getSelectedItem();
//        theUpdatedOrder.setOrderType(selectedOrderType.getText());

        SpinnerItem selectedOrderStatus = (SpinnerItem)OrderStatus.getSelectedItem();
        theUpdatedOrder.setStatus(selectedOrderStatus.getText());

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
        theUpdatedOrder.setClosingDate(targetDateFormat.format(theTargetDate));

        theUpdatedOrder.setNotes(notes.getText().toString());
        theUpdatedOrder.setCurrency(Currency);
        theUpdatedOrder.setExchangeRate(Float.parseFloat(selectedExchangeRate.getText().toString()));
        theUpdatedOrder.setCreatedByDisplayName(CreatedByDisplayName);
        theUpdatedOrder.setIsChanged(true);
        theUpdatedOrder.setIsNew(false);
        theUpdatedOrder.setIsArchived(false);

        Date updatedDate = new Date();

        theUpdatedOrder.setUpdated(targetDateFormat.format(updatedDate));

        DatabaseClient.getInstance(requireContext()).getAppDatabase()
                .orderDao()
                .update(theUpdatedOrder);

        refreshActivity();

        if (mListener != null) {
            mListener.OrderChanged();
        }
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {

        drawVisible = false;

        if (ID != 0) {

            Order theOrder = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .orderDao()
                    .getOrder(ID);

            if (theOrder.getOrderID()!= null) {
                OrderID = theOrder.getOrderID();
            }
            else
            {
                OrderID = 0;
            }

            if (theOrder.getOpportunityID()!= null) {
                LocalOpportunityID = theOrder.getOpportunityID();
            }
            else
            {
                LocalOpportunityID = 0;
            }

            CustomerID = theOrder.getCustomerID();
            ContactID = theOrder.getContactID();
            LocalCustomerID = theOrder.getLocalCustomerID();
            customerName = theOrder.getCustomerName();
            Currency = theOrder.getCurrency();
            ExchangeRate = theOrder.getExchangeRate();
            selectedExchangeRate.setText(String.format(Locale.UK, "%.2f", theOrder.getExchangeRate()));
            CreatedByDisplayName = theOrder.getCreatedByDisplayName();

            customerLookupName.setText(theOrder.getCustomerName());
            contactLookupName.setText(theOrder.getContactName());

            reference.setText(theOrder.getReference());
            subject.setText(theOrder.getSubject());
            notes.setText(theOrder.getNotes());

            //Is this GBP?

            int pL = totalOrderValue.getPaddingLeft();
            int pT = totalOrderValue.getPaddingTop();
            int pR = totalOrderValue.getPaddingRight();
            int pB = totalOrderValue.getPaddingBottom();

            int orientation = getResources().getConfiguration().orientation;

            if (ExchangeRate != 0) {
                if (ExchangeRate < 1 || ExchangeRate > 1)
                {
//                    currencyName.setVisibility(View.VISIBLE);
                    totalOrderValueAlternative.setVisibility(View.VISIBLE);

                    totalOrderValue.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.total_alternative_background_land_left));

//                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                        totalOrderValue.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.total_alternative_background_land_left));
//                    } else {
//                        totalOrderValue.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.total_background));
//                    }

                    currencyName.setText(Currency);
                }
                else
                {
                    currencyName.setVisibility(View.GONE);
                    totalOrderValueAlternative.setVisibility(View.GONE);
                    totalOrderValue.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.text_input_background));
                }
            }
            else
            {
                currencyName.setVisibility(View.GONE);
                totalOrderValueAlternative.setVisibility(View.GONE);
                totalOrderValue.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.text_input_background));
            }

            totalOrderValue.setPadding(pL, pT, pR, pB);

            if (theOrder.getStatus() != null) {
                int statusIndex = 0;
                int n = OrderStatusAdapter.getCount();

                for (int i = 0; i < n; i++) {
                    SpinnerItem status = (SpinnerItem) OrderStatusAdapter.getItem(i);

                    if (status.getText().equals(theOrder.getStatus())) {
                        statusIndex = i;
                    }
                }

                OrderStatus.setSelection(statusIndex);
            }

//            if (theOrder.getOrderType() != null) {
//
//                if (theOrder.getOrderType().equals("Estimate"))
//                {
//                    OrderType.setSelection(0);
//                }
//                else
//                {
//                    OrderType.setSelection(1);
//                }
//            }

            if (theOrder.getCurrency() != null) {
                int statusIndex = 0;
                int n = adapterCurrency.getCount();

                for (int i = 0; i < n; i++) {
                    SpinnerItem status = (SpinnerItem) adapterCurrency.getItem(i);

                    if (status.getText().equals(theOrder.getCurrency())) {
                        statusIndex = i;
                    }
                }

                selectedCurrencyName.setSelection(statusIndex);
            }

            refreshOrderLines();

            if (root.findViewById(R.id.activity_list) != null) {
                refreshActivity();
            }

        }
    }

    public void refreshOrderLines()
    {
        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.order_items_list);

        //Get linked Orders

        List<OrderLine> OrderLines = DatabaseClient.getInstance(requireContext())
                .getAppDatabase()
                .orderLineDao()
                .getOrderLinesFromLocalID(ID);

        generateOrderLineList(recyclerView, OrderLines);

        calculateOrderTotal(OrderLines);
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

    private void generateOrderLineList(RecyclerView recyclerView, List<OrderLine> OrderLines) {

        OrderLines.add(new OrderLine(0, "New Line"));

        mOrderLinesAdapter = new OrderLinesRecyclerViewAdapter(OrderLines, Currency, ExchangeRate, getChildFragmentManager(), this.getContext(), this);
        recyclerView.setAdapter(mOrderLinesAdapter);

        ItemTouchHelper.Callback callback = new OrderLineTouchHelper(mOrderLinesAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void calculateOrderTotal(List<OrderLine> OrderLines) {

        float Total;
        float SubTotal = 0.00f;
        float Discount = 0.00f;
        float Tax = 0.00f;

        for (OrderLine line : OrderLines) {

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

        totalOrderValue.setText(String.format(Locale.UK, "£%,.2f", Total));

        if (ExchangeRate != 0) {
            if (ExchangeRate < 1 || ExchangeRate > 1) {
                totalOrderValueAlternative.setText(String.format(Locale.UK, "%s%.2f", CurrencySymbol, Total * ExchangeRate));
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
        inflater.inflate(R.menu.order_detail, menu);

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
            SendOrderLink();
        }
        else if (id == R.id.action_copy) {

            CopyOrderDialog newDialog = CopyOrderDialog.newInstance(this, ID);
            newDialog.show(getChildFragmentManager(), "Copy Order");

        }
        else if (id == R.id.action_save)
        {
            saveOrder();

            if (getParentFragment() != null) {
                Snackbar saveSnackbar = Snackbar.make(getParentFragment().requireView(), "Order was saved", Snackbar.LENGTH_SHORT);
                saveSnackbar.show();
            }
            else
            {
                try {
                    Snackbar saveSnackbar = Snackbar.make(requireView(), "Order was saved", Snackbar.LENGTH_SHORT);
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
                    .setTitle("Archive Order")
                    .setMessage("Are you sure you want to archive this Order? It will be removed after the next successful sync")
                    .setIcon(android.R.drawable.ic_delete)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {

                            DatabaseClient.getInstance(requireContext()).getAppDatabase()
                                    .orderDao()
                                    .archiveOrder(ID);

                            if (mListener != null) {
                                mListener.OrderChanged();
                            }

                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        }
        else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    public void SendOrderLink()
    {
        Order theOrder = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                .orderDao()
                .getOrder(ID);

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this

        String Random = UUID.randomUUID().toString();

        if (theOrder.getContactID() != null) {

            Contact theContact = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .contactDao()
                    .getContactByContactID(theOrder.getContactID());

            if (theContact != null) {

                if (theContact.getEmail() != null)
                {
                    if (!theContact.getEmail().equals("")) {
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{theContact.getEmail()});
                    }
                    else
                    {
                        if (theOrder.getId() != null)
                        {
                            Customer theCustomer = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                                    .customerDao()
                                    .getCustomer(theOrder.getLocalCustomerID());

                            if (theCustomer != null) {
                                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{theCustomer.getGeneralEmail()});
                            }
                        }
                    }
                }
                else
                {
                    if (theOrder.getId() != null)
                    {
                        Customer theCustomer = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                                .customerDao()
                                .getCustomer(theOrder.getLocalCustomerID());

                        if (theCustomer != null) {
                            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{theCustomer.getGeneralEmail()});
                        }
                    }
                }
            }
            else
            {
                if (theOrder.getId() != null) {

                    Customer theCustomer = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                            .customerDao()
                            .getCustomer(theOrder.getLocalCustomerID());

                    if (theCustomer != null) {
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{theCustomer.getGeneralEmail()});
                    }
                }
            }
        }
        else
        {
            if (theOrder.getId() != null) {

                Customer theCustomer = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                        .customerDao()
                        .getCustomer(theOrder.getLocalCustomerID());

                if (theCustomer != null) {
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{theCustomer.getGeneralEmail()});
                }
            }
        }

        intent.putExtra(Intent.EXTRA_SUBJECT, "Your Order");

        String HTMLbody = "Dear " + theOrder.getContactName() + "</br></br>" +
                "Please follow the link below to view your Order</br></br>" +
                "<a href=\"http://portal.mybirdy.co.uk?token=" + Random + "\">View Your Document(s)</a>";
        intent.putExtra(Intent.EXTRA_HTML_TEXT, HTMLbody);

        String TextBody = "Dear " + theOrder.getContactName() + "\n" + "\n" +
                "Please follow the link below to view your Order\n" + "\n" +
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
//        //File file = new File(Environment.getExternalStorageDirectory(), "Order.pdf");
//        final File file = new File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Order_" + UUID.randomUUID().toString() + ".pdf");
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
    public void CreateNewOrderLine() {
//        NewOrderLineDialog newDialog = NewOrderLineDialog.newInstance(this, ID);
//        newDialog.show(getChildFragmentManager(), "New Line");
    }

    @Override
    public void EditLine(int position, OrderLine line) {
//        EditOrderLineDialog newDialog = EditOrderLineDialog.newInstance(this, position, line);
//        newDialog.show(getChildFragmentManager(), "Edit Line");
    }

    @Override
    public void RemoveLine(int position, int id) {

        DatabaseClient.getInstance(requireContext())
                .getAppDatabase()
                .orderLineDao()
                .deleteOrderLine(id);

        refreshOrderLines();
    }

//    @Override
//    public void UpdatedOrderLine(int position, OrderLine updatedLine) {
//
//        updatedLine.setOrderID(OrderID);
//
//        DatabaseClient.getInstance(requireContext())
//                .getAppDatabase()
//                .orderLineDao()
//                .update(updatedLine);
//
//        refreshOrderLines();
//    }
//
//    @Override
//    public void RemoveOrderLine(int position, OrderLine updatedLine) {
//
//        DatabaseClient.getInstance(requireContext())
//                .getAppDatabase()
//                .orderLineDao()
//                .deleteOrderLine(updatedLine.getId());
//
//        refreshOrderLines();
//    }
//
//    @Override
//    public void NewOrderLineAdded(OrderLine OrderLine) {
//
//        OrderLine.setOrderID(OrderID);
//
//        DatabaseClient.getInstance(requireContext())
//                .getAppDatabase()
//                .orderLineDao()
//                .insert(OrderLine);
//
//        refreshOrderLines();
//    }

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

        ChangeContactDialog newFragment = ChangeContactDialog.newInstance(OrderDetailFragment.this, LocalCustomerID, customerLookupName.getText().toString());
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
    public void OrderDupliated(int ID) {

        if (mListener != null) {
            mListener.OrderChanged();
        }
    }
}