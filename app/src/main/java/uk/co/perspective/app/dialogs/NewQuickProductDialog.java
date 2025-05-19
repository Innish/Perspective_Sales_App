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

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import uk.co.perspective.app.R;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.entities.Product;

public class NewQuickProductDialog extends DialogFragment {

    private View root;

    private EditText partNumber;
    private EditText description;
    private EditText unitPrice;
    private EditText taxRate;

    Button okSelected;
    Button cancelSelected;

    NewQuickProductDialog.NewProductListener mListener;

    public interface NewProductListener {
        public void NewProduct(int LocalProductID, String PartNumber, String Description, Float UnitPrice, Float TaxRate);
    }

    public NewQuickProductDialog() {
        // Required empty public constructor
    }

    public void setListener(NewProductListener listener) {
        mListener = listener;
    }

    public static NewQuickProductDialog newInstance(NewProductListener mListener) {
        NewQuickProductDialog frag = new NewQuickProductDialog();
        Bundle args = new Bundle();
        frag.setListener(mListener);
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

        root = inflater.inflate(R.layout.dialog_new_quick_product, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        root.setBackgroundResource(R.drawable.dialog_rounded);

        partNumber = root.findViewById(R.id.partNumber);
        description = root.findViewById(R.id.description);
        unitPrice = root.findViewById(R.id.unit_price);
        taxRate = root.findViewById(R.id.tax_rate);

        //Buttons

        okSelected = root.findViewById(R.id.save);
        cancelSelected = root.findViewById(R.id.cancel);

        okSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!description.getText().toString().equals("")) {

                    //Save Contact

                    Product newProduct = new Product();

                    newProduct.setProductID(0);

                    newProduct.setPartNumber(partNumber.getText().toString());
                    newProduct.setDescription(description.getText().toString());

                    Float newProductUnitPrice = 0.00f;
                    Float newProductTaxtRate = 0.00f;

                    if (!unitPrice.getText().toString().equals("")) {
                        newProduct.setSalePrice(Float.parseFloat(unitPrice.getText().toString()));
                    }

                    if (!taxRate.getText().toString().equals("")) {
                        newProduct.setTaxRate(Float.parseFloat(taxRate.getText().toString()));
                    }

                    newProduct.setCostPrice(0.00f);

//                    Date updatedDate = new Date();
//
//                    SimpleDateFormat targetDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);
//
//                    newProduct.setUpdated(targetDateFormat.format(updatedDate));

                    long ID = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                            .productDao()
                            .insert(newProduct);

                    mListener.NewProduct((int)ID, partNumber.getText().toString(), description.getText().toString(), newProductUnitPrice, newProductTaxtRate);

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

    }

}