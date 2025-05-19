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
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import uk.co.perspective.app.R;
import uk.co.perspective.app.entities.OrderLine;
import uk.co.perspective.app.entities.QuoteLine;

public class NewOrderLineDialog extends DialogFragment implements ChangeProductDialog.ChangeProductListener {

    private View root;

    private int LocalOrderID;
    private int LocalProductID;
    private float CostPrice;

    private EditText description;
    private EditText partNumber;
    private EditText unitPrice;
    private EditText quantity;
    private EditText discount;
    private EditText taxRate;
    private EditText notes;

    private ImageView productLookup;

    Button okSelected;
    Button cancelSelected;

    NewOrderLineDialog.NewOrderLineListener mListener;

    public interface NewOrderLineListener {
        public void NewOrderLineAdded(OrderLine orderLine);
    }

    public NewOrderLineDialog() {
        // Required empty public constructor
    }

    public void setListener(NewOrderLineListener listener) {
        mListener = listener;
    }

    public void setOrderID(int localOrderID) {
        LocalOrderID = localOrderID;
    }

    public static NewOrderLineDialog newInstance(NewOrderLineListener mListener, int localOrderID) {
        NewOrderLineDialog frag = new NewOrderLineDialog();
        Bundle args = new Bundle();
        frag.setListener(mListener);
        frag.setOrderID(localOrderID);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.dialog_new_order_line, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        root.setBackgroundResource(R.drawable.dialog_rounded);

        productLookup = root.findViewById(R.id.lookup_product);

        description = root.findViewById(R.id.description);
        partNumber = root.findViewById(R.id.part_number);
        unitPrice = root.findViewById(R.id.unit_price);
        quantity = root.findViewById(R.id.quantity);
        discount = root.findViewById(R.id.discount);
        taxRate = root.findViewById(R.id.tax_rate);
        notes = root.findViewById(R.id.notes);

        //Buttons

        okSelected = root.findViewById(R.id.save);
        cancelSelected = root.findViewById(R.id.cancel);

        LocalProductID = 0;

        productLookup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeProductDialog newFragment = ChangeProductDialog.newInstance(NewOrderLineDialog.this);
                newFragment.show(getChildFragmentManager(), "Select Product");
            }
        });

        okSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            if (!description.getText().toString().equals("")) {

                float theUnitPrice = 0.00f;
                float theCostPrice = 0.00f;
                float theQuantity = 1.00f;
                float theDiscount = 0.00f;
                float theTaxRate = 0.00f;

                if (!unitPrice.getText().toString().equals("")) {
                    theUnitPrice = Float.parseFloat(unitPrice.getText().toString());
                }

                if (!quantity.getText().toString().equals("")) {
                    theQuantity = Float.parseFloat(quantity.getText().toString());
                }

                if (!discount.getText().toString().equals("")) {
                    theDiscount = Float.parseFloat(discount.getText().toString());
                }

                if (!taxRate.getText().toString().equals("")) {
                    theTaxRate = Float.parseFloat(taxRate.getText().toString());
                }

                //Save Line

                OrderLine thisLine = new OrderLine();

                thisLine.setLocalOrderID(LocalOrderID);
                thisLine.setDescription(description.getText().toString());
                thisLine.setPartNumber(partNumber.getText().toString());
                thisLine.setValue(theUnitPrice);
                thisLine.setCostPrice(theCostPrice);
                thisLine.setQuantity(theQuantity);
                thisLine.setDiscount(theDiscount);
                thisLine.setTaxRate(theTaxRate);
                thisLine.setNotes(notes.getText().toString());

                Date updatedDate = new Date();

                SimpleDateFormat targetDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                thisLine.setUpdated(targetDateFormat.format(updatedDate));

                thisLine.setIsChanged(false);
                thisLine.setIsNew(true);

                mListener.NewOrderLineAdded(thisLine);

                dismiss();
            }
            else
            {
                int pL = description.getPaddingLeft();
                int pT = description.getPaddingTop();
                int pR = description.getPaddingRight();
                int pB = description.getPaddingBottom();
                description.setBackgroundResource(R.drawable.text_input_background_required);
                description.setPadding(pL, pT, pR, pB);
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

//        if (LocalCustomerID != 0 && !CustomerName.equals(""))
//        {
//            customerLookupName.setText(CustomerName);
//            customerLookupName.setTextColor(Color.DKGRAY);
//        }
    }


    @Override
    public void ProductChanged(int ID, String PartNumber, String Description, Float UnitPrice, Float TaxRate) {

        description.setText(Description);
        partNumber.setText(PartNumber);
        unitPrice.setText(String.format(Locale.UK, "%.2f", UnitPrice));
        taxRate.setText(String.format(Locale.UK, "%.2f", TaxRate));
    }
}