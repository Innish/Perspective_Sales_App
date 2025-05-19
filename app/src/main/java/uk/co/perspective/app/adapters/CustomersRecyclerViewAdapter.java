package uk.co.perspective.app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import uk.co.perspective.app.R;
import uk.co.perspective.app.entities.Customer;
import uk.co.perspective.app.helpers.CustomerDiffCallback;

public class CustomersRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Customer> dataList;
    private Context context;
    private int rowIndex;

    RecyclerView mRecyclerView;

    CustomersRecyclerViewAdapter.CustomerListener mListener;

    private final FragmentManager fragmentManager;

    public interface CustomerListener {
        public void CreateNewCustomer();
        public void EditCustomer(int id);
        public void RemoveCustomer();
    }

    public CustomersRecyclerViewAdapter(List<Customer> dataList, FragmentManager fragmentManager, Context context, CustomerListener listener){
        this.context = context;
        this.dataList = dataList;
        this.fragmentManager = fragmentManager;
        this.mListener = listener;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public int getItemViewType(int position) {

        if (this.dataList.get(position).getCustomerName().equals("New Customer"))
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        RecyclerView.ViewHolder viewHolder;

        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_customer, parent, false);
            viewHolder = new CustomersRecyclerViewAdapter.RecordViewHolder(view);
        }
        else
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_add, parent, false);
            viewHolder = new CustomersRecyclerViewAdapter.AddRecordViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {

        if (viewHolder.getItemViewType() == 1) {

            final CustomersRecyclerViewAdapter.RecordViewHolder holder = (CustomersRecyclerViewAdapter.RecordViewHolder) viewHolder;

            holder.mItem = dataList.get(position);

            holder.mCustomerName.setText(dataList.get(position).getCustomerName());
            holder.mContactName.setText(dataList.get(position).getContactName());

            holder.mInitials.setText(getInitals(dataList.get(position).getCustomerName()));

            if (dataList.get(position).getCustomerID() != null) {
                holder.thisCustomerID = dataList.get(position).getCustomerID();
            } else {
                holder.thisCustomerID = 0;
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                rowIndex = position;
                notifyDataSetChanged();
                mListener.EditCustomer(dataList.get(position).getId());
                }
            });

            int pL = holder.mView.getPaddingLeft();
            int pT = holder.mView.getPaddingTop();
            int pR = holder.mView.getPaddingRight();
            int pB = holder.mView.getPaddingBottom();

            if (dataList.get(position).getIsArchived()) {
                holder.mView.setBackgroundResource(R.drawable.actionholder_flat_archived);
                holder.mCustomerName.setTextColor(Color.LTGRAY);
                holder.mContactName.setTextColor(Color.LTGRAY);
            }
            else
            {
                if (rowIndex == position) {
                    holder.mView.setBackgroundResource(R.drawable.actionholder_selected_flat);
                }
                else
                {
                    holder.mView.setBackgroundResource(R.drawable.actionholder_flat);
                }

                holder.mCustomerName.setTextColor(Color.DKGRAY);
                holder.mContactName.setTextColor(Color.DKGRAY);
            }

            holder.mView.setPadding(pL, pT, pR, pB);
        }
        else
        {
            CustomersRecyclerViewAdapter.AddRecordViewHolder holder = (CustomersRecyclerViewAdapter.AddRecordViewHolder) viewHolder;

            holder.mItem = dataList.get(position);

            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mListener.CreateNewCustomer();
                }
            });
        }
    }

    public String getInitals(String Name)
    {
        StringBuilder builder = new StringBuilder();

        if (Name != null) {

            String[] strArray = Name.split(" ");

            if (strArray.length > 1)
                if (!Objects.equals(strArray[0], "")) {
                    builder.append(strArray[0], 0, 1);
                }
            if (strArray.length > 2)
                if (!Objects.equals(strArray[1], "")) {
                    builder.append(strArray[1], 0, 1);
                }
        }

        return builder.toString();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder {

        public Customer mItem;
        public int thisCustomerID;

        public final View mView;
        public final TextView mCustomerName;
        public final TextView mContactName;
        public final TextView mInitials;

        public RecordViewHolder(View view) {
            super(view);
            mView = view;
            mCustomerName = view.findViewById(R.id.customer_name);
            mContactName = view.findViewById(R.id.contact_name);
            mInitials = view.findViewById(R.id.initials);
        }

        @NotNull
        @Override
        public String toString() {
            return super.toString() + " '" + mCustomerName.getText() + "'";
        }
    }

    public void updateList(List<Customer> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new CustomerDiffCallback(newList, this.dataList));
        diffResult.dispatchUpdatesTo(this);
        this.dataList.clear();
        this.dataList.addAll(newList);
    }

    public static class AddRecordViewHolder extends RecyclerView.ViewHolder {

        public Customer mItem;
        public int thisCustomerID;

        public final View mView;
        public final ImageView mAddRecord;

        public AddRecordViewHolder(View view) {
            super(view);
            mView = view;
            mAddRecord = view.findViewById(R.id.add_record);
        }
    }
}
