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

import java.util.List;

import uk.co.perspective.app.R;
import uk.co.perspective.app.database.AppDatabase;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.entities.Quote;
import uk.co.perspective.app.entities.QuoteLine;

public class CopyQuoteDialog extends DialogFragment implements ChangeCustomerDialog.ChangeCustomerListener, ChangeContactDialog.ChangeContactListener {

    private View root;

    private int LocalQuoteID;
    private int LocalCustomerID;
    private String CustomerName;

    private EditText subject;
    private EditText notes;

    private ConstraintLayout customerLookup;
    private TextView customerLookupName;
    private TextView contactLookupName;

    Button okSelected;
    Button cancelSelected;

    CopyQuoteDialog.CopyQuoteListener mListener;

    public interface CopyQuoteListener {
        public void QuoteDupliated(int ID);
    }

    public CopyQuoteDialog() {

        // Required empty public constructor

    }

    public void setListener(CopyQuoteListener listener) {
        mListener = listener;
    }

    public void setQuote(int ID) {
        LocalQuoteID = ID;
    }

    public static CopyQuoteDialog newInstance(CopyQuoteListener mListener, int localQuoteID) {
        CopyQuoteDialog frag = new CopyQuoteDialog();
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

        root = inflater.inflate(R.layout.dialog_duplicate_quote, container, false);

        final AppDatabase database = DatabaseClient.getInstance(requireContext()).getAppDatabase();

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        root.setBackgroundResource(R.drawable.dialog_rounded);

        customerLookup = root.findViewById(R.id.customer_details);
        customerLookupName = root.findViewById(R.id.customer_name_label);
        contactLookupName = root.findViewById(R.id.contact_name_label);
        subject = root.findViewById(R.id.subject);
        notes = root.findViewById(R.id.notes);

        contactLookupName.setVisibility(View.GONE);

        //Buttons

        okSelected = root.findViewById(R.id.save);
        cancelSelected = root.findViewById(R.id.cancel);

        customerLookup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeCustomerDialog newFragment = ChangeCustomerDialog.newInstance(CopyQuoteDialog.this);
                newFragment.show(getChildFragmentManager(), "Change Customer");
            }
        });

        okSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Quote theOriginalQuote = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                        .quoteDao()
                        .getQuote(LocalQuoteID);

                if (!theOriginalQuote.getReference().equals("")) {
                    theOriginalQuote.setReference(theOriginalQuote.getReference() + "/N");
                }

                theOriginalQuote.setId(null);
                theOriginalQuote.setLocalCustomerID(LocalCustomerID);
                theOriginalQuote.setCustomerID(0);
                theOriginalQuote.setContactID(0);
                theOriginalQuote.setCustomerName(customerLookupName.getText().toString());
                theOriginalQuote.setContactName(contactLookupName.getText().toString());
                theOriginalQuote.setSubject(subject.getText().toString());
                theOriginalQuote.setNotes(notes.getText().toString());
                theOriginalQuote.setIsNew(true);
                theOriginalQuote.setIsChanged(false);
                theOriginalQuote.setIsArchived(false);

                long newID = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                        .quoteDao()
                        .insert(theOriginalQuote);

                //Copy lines

                List<QuoteLine> originalQuoteLines = DatabaseClient.getInstance(requireContext())
                        .getAppDatabase()
                        .quoteLineDao()
                        .getQuoteLinesFromLocalID(LocalQuoteID);

                for(QuoteLine quoteLine: originalQuoteLines)
                {
                    quoteLine.setId(null);
                    quoteLine.setLocalQuoteID((int)newID);
                    quoteLine.setQuoteID(0);
                    quoteLine.setQuoteLineID(0);

                    DatabaseClient.getInstance(requireContext())
                            .getAppDatabase()
                            .quoteLineDao()
                            .insert(quoteLine);
                }

                mListener.QuoteDupliated(0);

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

            LocalCustomerID = theQuote.getLocalCustomerID();

            customerLookupName.setText(theQuote.getCustomerName());

            if (!theQuote.getContactName().equals("")) {
                contactLookupName.setVisibility(View.VISIBLE);
                contactLookupName.setText(theQuote.getContactName());
            }

            subject.setText(theQuote.getSubject());
            notes.setText(theQuote.getNotes());
        }
    }

    @Override
    public void CustomerChanged(int ID, String customerName) {

        this.LocalCustomerID = ID;
        this.CustomerName = customerName;

        customerLookupName.setText(customerName);
        customerLookupName.setTextColor(Color.DKGRAY);

        //Lookup Contact

        ChangeContactDialog newFragment = ChangeContactDialog.newInstance(CopyQuoteDialog.this, LocalCustomerID, customerLookupName.getText().toString());
        newFragment.show(getChildFragmentManager(), "Select Contact");
    }

    @Override
    public void ContactChanged(int ID, int contactID, String contactName) {
        contactLookupName.setVisibility(View.VISIBLE);
        contactLookupName.setText(contactName);
    }
}