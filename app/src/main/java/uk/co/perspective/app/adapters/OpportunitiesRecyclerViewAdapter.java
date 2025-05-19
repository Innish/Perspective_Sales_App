package uk.co.perspective.app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.List;

import uk.co.perspective.app.R;
import uk.co.perspective.app.entities.Opportunity;
import uk.co.perspective.app.helpers.OpportunityDiffCallback;

public class OpportunitiesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Opportunity> dataList;
    private Context context;

    private int rowIndex;

    RecyclerView mRecyclerView;

    OpportunitiesRecyclerViewAdapter.OpportunityListener mListener;

    private final FragmentManager fragmentManager;

    public interface OpportunityListener {
        public void CreateNewOpportunity();
        public void EditOpportunity(int id);
        public void RemoveOpportunity();
    }

    public OpportunitiesRecyclerViewAdapter(List<Opportunity> dataList, FragmentManager fragmentManager, Context context, OpportunityListener listener){
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

        if (this.dataList.get(position).getSubject().equals("New Opportunity"))
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_opportunity, parent, false);
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

        if (viewHolder.getItemViewType() == 1) {

            RecordViewHolder holder = (RecordViewHolder) viewHolder;

            holder.mItem = dataList.get(position);
            holder.mSubject.setText(dataList.get(position).getSubject());
            holder.mCustomer.setText(dataList.get(position).getCustomerName());
            holder.mStatus.setText(dataList.get(position).getStatus());

            if (dataList.get(position).getValue().equals(""))
            {
                holder.mValue.setVisibility(View.GONE);
            }
            else
            {
                //format the value

                float valueOfLead = Float.parseFloat(dataList.get(position).getValue());
                DecimalFormat df = new DecimalFormat("#,###.00");
                df.setMaximumFractionDigits(2);

                holder.mValue.setText(df.format(valueOfLead));

                if (valueOfLead > 0)
                {
                    holder.mValue.setVisibility(View.VISIBLE);
                }
                else {
                    holder.mValue.setVisibility(View.GONE);
                }
            }

            if (dataList.get(position).getRating().equals(100))
            {
                holder.mStatusIcon.setBackgroundResource(R.drawable.opportunity_boiling_background);
            }
            else if (dataList.get(position).getRating().equals(80))
            {
                holder.mStatusIcon.setBackgroundResource(R.drawable.opportunity_hot_background);
            }
            else if (dataList.get(position).getRating().equals(60))
            {
                holder.mStatusIcon.setBackgroundResource(R.drawable.opportunity_warm_background);
            }
            else if (dataList.get(position).getRating().equals(40))
            {
                holder.mStatusIcon.setBackgroundResource(R.drawable.opportunity_moderate_background);
            }
            else if (dataList.get(position).getRating().equals(20))
            {
                holder.mStatusIcon.setBackgroundResource(R.drawable.opportunity_cold_background);
            }
            else
            {
                holder.mStatusIcon.setBackgroundResource(R.drawable.opportunity_moderate_background);
            }

            if (dataList.get(position).getOpportunityID() != null) {
                holder.thisOpportunityID = dataList.get(position).getOpportunityID();
            } else {
                holder.thisOpportunityID = 0;
            }

            holder.mSelectedItem.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    rowIndex = position;
                    notifyDataSetChanged();
                    mListener.EditOpportunity(dataList.get(position).getId());
                }
            });

            int pL = holder.mSelectedItem.getPaddingLeft();
            int pT = holder.mSelectedItem.getPaddingTop();
            int pR = holder.mSelectedItem.getPaddingRight();
            int pB = holder.mSelectedItem.getPaddingBottom();

            if (dataList.get(position).getIsArchived()) {
                holder.mSelectedItem.setBackgroundResource(R.drawable.actionholder_flat_archived);
                holder.mSubject.setTextColor(Color.LTGRAY);
                holder.mCustomer.setTextColor(Color.LTGRAY);
                holder.mStatus.setTextColor(Color.LTGRAY);
                holder.mValue.setTextColor(Color.LTGRAY);
            }
            else
            {
                if (rowIndex == position) {
                    holder.mSelectedItem.setBackgroundResource(R.drawable.actionholder_selected_flat);
                }
                else
                {
                    holder.mSelectedItem.setBackgroundResource(R.drawable.actionholder_flat);
                }

                holder.mSubject.setTextColor(Color.DKGRAY);
                holder.mCustomer.setTextColor(Color.DKGRAY);
                holder.mStatus.setTextColor(Color.DKGRAY);
                holder.mValue.setTextColor(Color.DKGRAY);
            }

            holder.mSelectedItem.setPadding(pL, pT, pR, pB);
        }
        else
        {
            AddRecordViewHolder holder = (AddRecordViewHolder) viewHolder;

            holder.mItem = dataList.get(position);

            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                mListener.CreateNewOpportunity();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder {

        public Opportunity mItem;
        public int thisOpportunityID;

        public final View mView;
        public final RelativeLayout mSelectedItem;
        public final ImageView mStatusIcon;
        public final TextView mSubject;
        public final TextView mCustomer;
        public final TextView mStatus;
        public final TextView mValue;

        public RecordViewHolder(View view) {
            super(view);
            mView = view;
            mSelectedItem = view.findViewById(R.id.selected);
            mStatusIcon = view.findViewById(R.id.status);
            mSubject = view.findViewById(R.id.opportunity_name);
            mCustomer = view.findViewById(R.id.customer_name);
            mStatus = view.findViewById(R.id.opportunity_stage);
            mValue = view.findViewById(R.id.opportunity_value);
        }

        @NotNull
        @Override
        public String toString() {
            return super.toString() + " '" + mSubject.getText() + "'";
        }
    }

    public void updateList(List<Opportunity> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new OpportunityDiffCallback(newList, this.dataList));
        diffResult.dispatchUpdatesTo(this);
        this.dataList.clear();
        this.dataList.addAll(newList);
    }

    public static class AddRecordViewHolder extends RecyclerView.ViewHolder {

        public Opportunity mItem;
        public int thisOpportunityID;

        public final View mView;
        public final ImageView mAddRecord;

        public AddRecordViewHolder(View view) {
            super(view);
            mView = view;
            mAddRecord = view.findViewById(R.id.add_record);
        }
    }
}
