package uk.co.perspective.app.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
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

import uk.co.perspective.app.R;
import uk.co.perspective.app.entities.Order;
import uk.co.perspective.app.entities.Quote;
import uk.co.perspective.app.helpers.OrderDiffCallback;
import uk.co.perspective.app.helpers.QuoteDiffCallback;

public class OrdersRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Order> dataList;
    private Context context;

    private int rowIndex;

    RecyclerView mRecyclerView;

    OrdersRecyclerViewAdapter.OrderListener mListener;

    private final FragmentManager fragmentManager;

    public interface OrderListener {
        public void CreateNewOrder();
        public void EditOrder(int id);
        public void DownloadOrder(int id);
        public void RemoveOrder(int id);
        public void ExpandOrderSearch();
    }

    public OrdersRecyclerViewAdapter(List<Order> dataList, FragmentManager fragmentManager, Context context, OrderListener listener){
        this.context = context;
        this.dataList = dataList;
        this.fragmentManager=fragmentManager;
        this.mListener = listener;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public int getItemViewType(int position) {

        if (this.dataList.get(position).getSubject().equals("New Order"))
        {
            return 0;
        }
        else if (this.dataList.get(position).getSubject().equals("Expand Search"))
        {
            return 1;
        }
        else
        {
            return 2;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        RecyclerView.ViewHolder viewHolder;

        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_expand_search, parent, false);
            viewHolder = new ExpandSearchViewHolder(view);
        }
        else if (viewType == 2) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_order, parent, false);
            viewHolder = new RecordViewHolder(view);
        }
        else
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_add, parent, false);
            viewHolder = new AddRecordViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {

        if (viewHolder.getItemViewType() == 2) {

            RecordViewHolder holder = (RecordViewHolder) viewHolder;

            holder.mItem = dataList.get(position);

            if (dataList.get(position).getSubject() != null) {
                if (!dataList.get(position).getSubject().equals("")) {
                    holder.mSubject.setText(dataList.get(position).getSubject());
                    holder.mSubject.setVisibility(View.VISIBLE);
                }
                else
                {
                    holder.mSubject.setVisibility(View.GONE);
                }
            }
            else
            {
                holder.mSubject.setVisibility(View.GONE);
            }

            holder.mCustomer.setText(dataList.get(position).getCustomerName());
            holder.mStatus.setText(dataList.get(position).getStatus());

//            holder.mStatusIcon.setText("O");

            if (dataList.get(position).getStatus() != null) {
                if (dataList.get(position).getStatus().equals("New (Not Issued)")) {
                    holder.mStatusIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#8DD3EC")));
                } else if (dataList.get(position).getStatus().equals("Issued")) {
                    holder.mStatusIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#BAEABF")));
                } else if (dataList.get(position).getStatus().equals("Under Review")) {
                    holder.mStatusIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E8D0FA")));
                } else if (dataList.get(position).getStatus().equals("Accepted")) {
                    holder.mStatusIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ABE411")));
                } else if (dataList.get(position).getStatus().equals("Rejected")) {
                    holder.mStatusIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFC6A7")));
                } else {
                    holder.mStatusIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D0E8FA")));
                }
            }
            else
            {
                holder.mStatusIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#8DD3EC")));
            }

            if (dataList.get(position).getOrderID() != null) {
                holder.thisOrderID = dataList.get(position).getOrderID();
            } else {
                holder.thisOrderID = 0;
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    rowIndex = position;
                    notifyDataSetChanged();

                    if (dataList.get(position).getId() != 0) {
                        mListener.EditOrder(dataList.get(position).getId());
                    }
                    else
                    {
                        if (dataList.get(position).getOrderID() != 0)
                        {
                            mListener.DownloadOrder(dataList.get(position).getOrderID());
                        }
                    }
                }
            });

            int pL = holder.itemView.getPaddingLeft();
            int pT = holder.itemView.getPaddingTop();
            int pR = holder.itemView.getPaddingRight();
            int pB = holder.itemView.getPaddingBottom();

            if (dataList.get(position).getIsArchived()) {
                holder.itemView.setBackgroundResource(R.drawable.actionholder_flat_archived);
                holder.mSubject.setTextColor(Color.LTGRAY);
                holder.mCustomer.setTextColor(Color.LTGRAY);
                holder.mStatus.setTextColor(Color.LTGRAY);
            }
            else
            {
                if (rowIndex == position) {
                    holder.itemView.setBackgroundResource(R.drawable.actionholder_selected_flat);
                }
                else
                {
                    holder.itemView.setBackgroundResource(R.drawable.actionholder_flat);
                }

                holder.mSubject.setTextColor(Color.DKGRAY);
                holder.mCustomer.setTextColor(Color.DKGRAY);
                holder.mStatus.setTextColor(Color.DKGRAY);
            }

            holder.itemView.setPadding(pL, pT, pR, pB);

        }
        else if (viewHolder.getItemViewType() == 1) {

            ExpandSearchViewHolder holder = (ExpandSearchViewHolder) viewHolder;

            holder.mItem = dataList.get(position);

            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mListener.ExpandOrderSearch();
                }
            });
        }
        else
        {
            AddRecordViewHolder holder = (AddRecordViewHolder) viewHolder;

            holder.mItem = dataList.get(position);

            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mListener.CreateNewOrder();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder {

        public Order mItem;
        public int thisOrderID;

        public final View mView;
        public final TextView mStatusIcon;
        public final TextView mSubject;
        public final TextView mCustomer;
        public final TextView mStatus;

        public RecordViewHolder(View view) {
            super(view);
            mView = view;
            mStatusIcon = view.findViewById(R.id.status);
            mSubject = view.findViewById(R.id.order_name);
            mCustomer = view.findViewById(R.id.customer_name);
            mStatus = view.findViewById(R.id.order_stage);
        }

        @NotNull
        @Override
        public String toString() {
            return super.toString() + " '" + mSubject.getText() + "'";
        }
    }

    public void updateList(List<Order> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new OrderDiffCallback(newList, this.dataList));
        diffResult.dispatchUpdatesTo(this);
        this.dataList.clear();
        this.dataList.addAll(newList);
    }

    public static class AddRecordViewHolder extends RecyclerView.ViewHolder {

        public Order mItem;
        public int thisOrderID;

        public final View mView;
        public final ImageView mAddRecord;

        public AddRecordViewHolder(View view) {
            super(view);
            mView = view;
            mAddRecord = view.findViewById(R.id.add_record);
        }
    }

    public static class ExpandSearchViewHolder extends RecyclerView.ViewHolder {

        public Order mItem;
        public int thisOrderID;

        public final View mView;
        public final ImageView mExpandSearch;

        public ExpandSearchViewHolder(View view) {
            super(view);
            mView = view;
            mExpandSearch = view.findViewById(R.id.search_records);
        }
    }
}
