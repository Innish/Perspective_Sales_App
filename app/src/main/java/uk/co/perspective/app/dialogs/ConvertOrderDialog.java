package uk.co.perspective.app.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import uk.co.perspective.app.R;
import uk.co.perspective.app.database.AppDatabase;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.entities.Order;
import uk.co.perspective.app.entities.OrderLine;
import uk.co.perspective.app.entities.Quote;
import uk.co.perspective.app.entities.QuoteLine;

public class ConvertOrderDialog extends DialogFragment {

    private int LocalQuoteID;

    private View root;

    private EditText notes;

    Button okSelected;
    Button cancelSelected;

    ConvertOrderDialog.ConvertOrderListener mListener;

    public interface ConvertOrderListener {
        public void QuoteConverted(int ID);
    }

    public ConvertOrderDialog() {
        // Required empty public constructor
    }

    public void setListener(ConvertOrderListener listener) {
        mListener = listener;
    }

    public void setQuote(int ID) {
        LocalQuoteID = ID;
    }

    public static ConvertOrderDialog newInstance(ConvertOrderListener mListener, int localQuoteID) {
        ConvertOrderDialog frag = new ConvertOrderDialog();
        Bundle args = new Bundle();
        frag.setListener(mListener);
        frag.setQuote(localQuoteID);
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

        root = inflater.inflate(R.layout.dialog_convert_quote, container, false);

        final AppDatabase database = DatabaseClient.getInstance(requireContext()).getAppDatabase();

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        root.setBackgroundResource(R.drawable.dialog_rounded);

        notes = root.findViewById(R.id.notes);

        //Buttons

        okSelected = root.findViewById(R.id.save);
        cancelSelected = root.findViewById(R.id.cancel);

        okSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Quote theOriginalQuote = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                        .quoteDao()
                        .getQuote(LocalQuoteID);

                if (!theOriginalQuote.getReference().equals("")) {
                    theOriginalQuote.setReference(theOriginalQuote.getReference() + "/CQ");
                }

                Order newOrder = new Order();

                newOrder.setId(null);
                newOrder.setLocalCustomerID(theOriginalQuote.getLocalCustomerID());
                newOrder.setCustomerID(theOriginalQuote.getCustomerID());
                newOrder.setContactID(theOriginalQuote.getContactID());
                newOrder.setCustomerName(theOriginalQuote.getCustomerName());
                newOrder.setContactName(theOriginalQuote.getContactName());
                newOrder.setSubject(theOriginalQuote.getSubject());
                newOrder.setNotes(notes.getText().toString());
                newOrder.setCurrency(theOriginalQuote.getCurrency());
                newOrder.setExchangeRate(theOriginalQuote.getExchangeRate());
                newOrder.setStatus("New (Not Issued)");

                Date updatedDate = new Date();

                SimpleDateFormat targetDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                newOrder.setUpdated(targetDateFormat.format(updatedDate));
                newOrder.setClosingDate(targetDateFormat.format(updatedDate));

                newOrder.setIsNew(true);
                newOrder.setIsChanged(false);
                newOrder.setIsArchived(false);

                long newID = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                        .orderDao()
                        .insert(newOrder);

                //Copy lines

                List<QuoteLine> originalQuoteLines = DatabaseClient.getInstance(requireContext())
                        .getAppDatabase()
                        .quoteLineDao()
                        .getQuoteLinesFromLocalID(LocalQuoteID);

                for(QuoteLine quoteLine: originalQuoteLines)
                {
                    OrderLine newOrderLine = new OrderLine();

                    newOrderLine.setLocalOrderID((int)newID);
                    newOrderLine.setOrderID(0);
                    newOrderLine.setId(0);
                    newOrderLine.setOrderLineID(0);

                    newOrderLine.setPartNumber(quoteLine.getPartNumber());
                    newOrderLine.setDescription(quoteLine.getDescription());
                    newOrderLine.setQuantity(quoteLine.getQuantity());
                    newOrderLine.setValue(quoteLine.getValue());
                    newOrderLine.setCostPrice(quoteLine.getCostPrice());
                    newOrderLine.setDiscount(quoteLine.getDiscount());
                    newOrderLine.setTaxRate(quoteLine.getTaxRate());
                    newOrderLine.setNotes(quoteLine.getNotes());

                    newOrderLine.setUpdated(targetDateFormat.format(updatedDate));

                    newOrderLine.setIsNew(true);
                    newOrderLine.setIsChanged(false);

                    DatabaseClient.getInstance(requireContext())
                            .getAppDatabase()
                            .orderLineDao()
                            .insert(newOrderLine);
                }

                mListener.QuoteConverted((int)newID);

                dismiss();
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

        if (LocalQuoteID != 0)
        {
            Quote theQuote = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .quoteDao()
                    .getQuote(LocalQuoteID);

            notes.setText(theQuote.getNotes());
        }
    }
}