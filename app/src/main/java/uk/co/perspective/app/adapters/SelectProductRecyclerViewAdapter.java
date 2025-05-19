package uk.co.perspective.app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uk.co.perspective.app.R;
import uk.co.perspective.app.entities.Product;

public class SelectProductRecyclerViewAdapter extends RecyclerView.Adapter<SelectProductRecyclerViewAdapter.ViewHolder>  {

    private final List<Product> dataList;
    private Context context;
    private int rowIndex;

    RecyclerView mRecyclerView;
    private final FragmentManager fragmentManager;

    SelectProductListener mListener;

    public interface SelectProductListener{
        void ProductSelected(int ID, String partNumber, String description, Float unitPrice, Float taxRate);
    }

    public void setListener(SelectProductListener listener) {
        mListener = listener;
    }

    public SelectProductRecyclerViewAdapter(List<Product> dataList, FragmentManager fragmentManager, Context context, SelectProductListener listener){
        this.context = context;
        this.dataList = dataList;
        this.fragmentManager = fragmentManager;
        setListener(listener);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @NonNull
    @Override
    public SelectProductRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_lookup_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SelectProductRecyclerViewAdapter.ViewHolder holder, final int position) {

        holder.mItem = dataList.get(position);

        holder.mDescription.setText(dataList.get(position).getDescription());
        holder.mPartNumber.setText(dataList.get(position).getPartNumber());

        if (dataList.get(position).getId() != null) {
            holder.thisID = dataList.get(position).getId();
            holder.thisPartNumber = dataList.get(position).getPartNumber();
            holder.thisDescription = dataList.get(position).getDescription();
            holder.thisUnitPrice = dataList.get(position).getSalePrice();
            holder.thisTaxRate = dataList.get(position).getTaxRate();
        }
        else
        {
            holder.thisID = 0;
            holder.thisPartNumber = "";
            holder.thisDescription = "";
            holder.thisUnitPrice = 0.00f;
            holder.thisTaxRate = 0.00f;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) { rowIndex = position;
            notifyDataSetChanged();
            mListener.ProductSelected(holder.thisID, holder.thisPartNumber, holder.thisDescription, holder.thisUnitPrice, holder.thisTaxRate);
            }
        });

        int pL = holder.mView.getPaddingLeft();
        int pT = holder.mView.getPaddingTop();
        int pR = holder.mView.getPaddingRight();
        int pB = holder.mView.getPaddingBottom();

        if(rowIndex == position){
            holder.mView.setBackgroundResource(R.drawable.selectedlistitem);
            holder.thisID = dataList.get(position).getId();
            holder.thisPartNumber = dataList.get(position).getPartNumber();
            holder.thisDescription = dataList.get(position).getDescription();
            mListener.ProductSelected(holder.thisID, holder.thisPartNumber, holder.thisDescription, holder.thisUnitPrice, holder.thisTaxRate);
        }
        else
        {
            holder.mView.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.mView.setPadding(pL, pT, pR, pB);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public Product mItem;
        public int thisID;
        public String thisPartNumber;
        public String thisDescription;
        public Float thisUnitPrice;
        public Float thisTaxRate;

        public final View mView;
        public final TextView mDescription;
        public final TextView mPartNumber;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mDescription = view.findViewById(R.id.description);
            mPartNumber = view.findViewById(R.id.part_number);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDescription.getText() + "'";
        }
    }

}
