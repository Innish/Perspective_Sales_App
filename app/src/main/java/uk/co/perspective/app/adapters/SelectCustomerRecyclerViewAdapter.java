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
import java.util.Objects;

import uk.co.perspective.app.R;
import uk.co.perspective.app.entities.Customer;

public class SelectCustomerRecyclerViewAdapter extends RecyclerView.Adapter<SelectCustomerRecyclerViewAdapter.ViewHolder>  {

    private final List<Customer> dataList;
    private Context context;
    private int rowIndex;

    RecyclerView mRecyclerView;
    private final FragmentManager fragmentManager;

    SelectCustomerListener mListener;

    public interface SelectCustomerListener{
        void CustomerSelected(int customerID, String customerName);
    }

    public void setListener(SelectCustomerListener listener) {
        mListener = listener;
    }

    public SelectCustomerRecyclerViewAdapter(List<Customer> dataList, FragmentManager fragmentManager, Context context, SelectCustomerListener listener){
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
    public SelectCustomerRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_customer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SelectCustomerRecyclerViewAdapter.ViewHolder holder, final int position) {

        holder.mItem = dataList.get(position);

        holder.mCustomerName.setText(dataList.get(position).getCustomerName());
        holder.mContactName.setText(dataList.get(position).getContactName());


        holder.mInitials.setText(getInitals(dataList.get(position).getCustomerName()));

        if (dataList.get(position).getCustomerID() != null) {
            holder.thisID = dataList.get(position).getId();
            holder.thisCustomerID = dataList.get(position).getCustomerID();
            holder.thisCustomerName  = dataList.get(position).getCustomerName();
        }
        else
        {
            holder.thisID = 0;
            holder.thisCustomerID = 0;
            holder.thisCustomerName = "";
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) { rowIndex = position;
                notifyDataSetChanged();
                mListener.CustomerSelected(holder.thisID, holder.thisCustomerName);
            }
        });

        int pL = holder.mView.getPaddingLeft();
        int pT = holder.mView.getPaddingTop();
        int pR = holder.mView.getPaddingRight();
        int pB = holder.mView.getPaddingBottom();

        if(rowIndex == position){
            holder.mView.setBackgroundResource(R.drawable.selectedlistitem);
            holder.thisID = dataList.get(position).getId();
            holder.thisCustomerID = dataList.get(position).getCustomerID();
            holder.thisCustomerName  = dataList.get(position).getCustomerName();
            mListener.CustomerSelected(holder.thisID, holder.thisCustomerName);
        }
        else
        {
            holder.mView.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.mView.setPadding(pL, pT, pR, pB);
    }

    public String getInitals(String Name)
    {
        String[] strArray = Name.split(" ");

        StringBuilder builder = new StringBuilder();

        if (strArray.length > 1)
            if (!Objects.equals(strArray[0], "")) {
                builder.append(strArray[0], 0, 1);
            }
        if (strArray.length > 2)
            if (!Objects.equals(strArray[1], "")) {
                builder.append(strArray[1], 0, 1);
            }
        return builder.toString();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public Customer mItem;
        public int thisID;
        public int thisCustomerID;
        public String thisCustomerName;

        public final View mView;
        public final TextView mCustomerName;
        public final TextView mContactName;
        public final TextView mInitials;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCustomerName = view.findViewById(R.id.customer_name);
            mContactName = view.findViewById(R.id.contact_name);
            mInitials = view.findViewById(R.id.initials);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mCustomerName.getText() + "'";
        }
    }

}
