package uk.co.perspective.app.dialogs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uk.co.perspective.app.R;
import uk.co.perspective.app.adapters.SelectProductRecyclerViewAdapter;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.entities.Product;

public class ChangeProductDialog extends DialogFragment implements SelectProductRecyclerViewAdapter.SelectProductListener, NewQuickProductDialog.NewProductListener{

    private View root;

    private int selectedLocalProductID;
    private String selectedDescription;
    private String selectedPartNumber;
    private Float selectedUnitPrice;
    private Float selectedTaxRate;

    ChangeProductListener mListener;

    SearchView searchView;
    ImageView addProduct;

    Button okSelected;
    Button cancelSelected;

    public ChangeProductDialog() {

    }

    @Override
    public void ProductSelected(int ID, String partNumber, String description, Float UnitPrice, Float TaxRate) {
        this.selectedLocalProductID = ID;
        this.selectedPartNumber = partNumber;
        this.selectedDescription = description;
        this.selectedUnitPrice = UnitPrice;
        this.selectedTaxRate = TaxRate;
    }


    public interface ChangeProductListener {
        public void ProductChanged(int ID, String partNumber, String description, Float UnitPrice, Float TaxRate);
    }

    public void setListener(ChangeProductListener listener) {
        mListener = listener;
    }

    public static ChangeProductDialog newInstance(ChangeProductListener mListener) {
        ChangeProductDialog frag = new ChangeProductDialog();
        Bundle args = new Bundle();
        frag.setListener(mListener);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.dialog_change_product, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.product_list);
        Context viewcontext = recyclerView.getContext();

        recyclerView.setLayoutManager(new LinearLayoutManager(viewcontext));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(viewcontext, LinearLayoutManager.VERTICAL));
        recyclerView.setClipToOutline(true);

        searchView = root.findViewById(R.id.searchFor);
        addProduct = root.findViewById(R.id.addProduct);

        //Buttons

        okSelected = root.findViewById(R.id.saveChangeProduct);
        cancelSelected = root.findViewById(R.id.cancelSaveChangeProduct);

        addProduct.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               NewQuickProductDialog newDialog = NewQuickProductDialog.newInstance(ChangeProductDialog.this);
               newDialog.show(getChildFragmentManager(), "New Product");
           }
        });

        okSelected.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (selectedLocalProductID != 0) {
                    mListener.ProductChanged(selectedLocalProductID, selectedPartNumber, selectedDescription, selectedUnitPrice, selectedTaxRate);
                    dismiss();
                }
            }
        });

        cancelSelected.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        root.setBackgroundResource(R.drawable.dialog_rounded);

        return root;
    }

    @Override
    public void NewProduct(int LocalProductID, String PartNumber, String Description, Float UnitPrice, Float TaxRate) {
        mListener.ProductChanged(LocalProductID, PartNumber, Description, UnitPrice, TaxRate);
        dismiss();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.product_list);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                searchProducts(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.equals(""))
                {
                    refreshProducts();
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
            refreshProducts();
            searchView.setFocusable(false);
            searchView.setIconified(false);
            searchView.clearFocus();
            return false;
            }
        });

        refreshProducts();

    }

    private void refreshProducts() {

        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.product_list);

        List<Product> products = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                .productDao()
                .getAll();

        generateProductList(recyclerView, products);
    }

    private void searchProducts(String searchText)
    {
        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.product_list);

        List<Product> products = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                .productDao()
                .searchProducts(searchText);

        generateProductList(recyclerView, products);
    }

    private void generateProductList(RecyclerView recyclerView, List<Product> products) {
        SelectProductRecyclerViewAdapter mAdapter = new SelectProductRecyclerViewAdapter(products, getChildFragmentManager(), this.getContext(), this);
        recyclerView.setAdapter(mAdapter);
    }
}
