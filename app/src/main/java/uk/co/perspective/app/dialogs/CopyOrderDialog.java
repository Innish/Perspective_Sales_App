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
import uk.co.perspective.app.entities.Order;
import uk.co.perspective.app.entities.OrderLine;

public class CopyOrderDialog extends DialogFragment implements ChangeCustomerDialog.ChangeCustomerListener, ChangeContactDialog.ChangeContactListener {

    private View root;

    private int LocalOrderID;
    private int LocalCustomerID;
    private String CustomerName;

    private EditText subject;
    private EditText notes;

    private ConstraintLayout customerLookup;
    private TextView customerLookupName;
    private TextView contactLookupName;

    Button okSelected;
    Button cancelSelected;

    CopyOrderDialog.CopyOrderListener mListener;

    public interface CopyOrderListener {
        public void OrderDupliated(int ID);
    }

    public CopyOrderDialog() {

        // Required empty public constructor

    }

    public void setListener(CopyOrderListener listener) {
        mListener = listener;
    }

    public void setOrder(int ID) {
        LocalOrderID = ID;
    }

    public static CopyOrderDialog newInstance(CopyOrderListener mListener, int localOrderID) {
        CopyOrderDialog frag = new CopyOrderDialog();
        Bundle args = new Bundle();
        frag.setListener(mListener);
        frag.setOrder(localOrderID);
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

        root = inflater.inflate(R.layout.dialog_duplicate_order, container, false);

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
                ChangeCustomerDialog newFragment = ChangeCustomerDialog.newInstance(CopyOrderDialog.this);
                newFragment.show(getChildFragmentManager(), "Change Customer");
            }
        });

        okSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Order theOriginalOrder = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                        .orderDao()
                        .getOrder(LocalOrderID);

                if (!theOriginalOrder.getReference().equals("")) {
                    theOriginalOrder.setReference(theOriginalOrder.getReference() + "/N");
                }

                theOriginalOrder.setId(null);
                theOriginalOrder.setLocalCustomerID(LocalCustomerID);
                theOriginalOrder.setCustomerID(0);
                theOriginalOrder.setContactID(0);
                theOriginalOrder.setCustomerName(customerLookupName.getText().toString());
                theOriginalOrder.setContactName(contactLookupName.getText().toString());
                theOriginalOrder.setSubject(subject.getText().toString());
                theOriginalOrder.setNotes(notes.getText().toString());
                theOriginalOrder.setIsNew(true);
                theOriginalOrder.setIsChanged(false);
                theOriginalOrder.setIsArchived(false);

                long newID = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                        .orderDao()
                        .insert(theOriginalOrder);

                //Copy lines

                List<OrderLine> originalOrderLines = DatabaseClient.getInstance(requireContext())
                        .getAppDatabase()
                        .orderLineDao()
                        .getOrderLinesFromLocalID(LocalOrderID);

                for(OrderLine orderLine: originalOrderLines)
                {
                    orderLine.setId(null);
                    orderLine.setLocalOrderID((int)newID);
                    orderLine.setOrderID(0);
                    orderLine.setOrderLineID(0);

                    DatabaseClient.getInstance(requireContext())
                            .getAppDatabase()
                            .orderLineDao()
                            .insert(orderLine);
                }

                mListener.OrderDupliated(0);

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

        if (LocalOrderID != 0)
        {
            Order theOrder = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .orderDao()
                    .getOrder(LocalOrderID);

            LocalCustomerID = theOrder.getLocalCustomerID();

            customerLookupName.setText(theOrder.getCustomerName());

            if (!theOrder.getContactName().equals("")) {
                contactLookupName.setVisibility(View.VISIBLE);
                contactLookupName.setText(theOrder.getContactName());
            }

            subject.setText(theOrder.getSubject());
            notes.setText(theOrder.getNotes());
        }
    }

    @Override
    public void CustomerChanged(int ID, String customerName) {

        this.LocalCustomerID = ID;
        this.CustomerName = customerName;

        customerLookupName.setText(customerName);
        customerLookupName.setTextColor(Color.DKGRAY);

        //Lookup Contact

        ChangeContactDialog newFragment = ChangeContactDialog.newInstance(CopyOrderDialog.this, LocalCustomerID, customerLookupName.getText().toString());
        newFragment.show(getChildFragmentManager(), "Select Contact");
    }

    @Override
    public void ContactChanged(int ID, int contactID, String contactName) {
        contactLookupName.setVisibility(View.VISIBLE);
        contactLookupName.setText(contactName);
    }
}