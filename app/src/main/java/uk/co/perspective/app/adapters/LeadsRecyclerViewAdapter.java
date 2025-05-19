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

import java.text.DecimalFormat;
import java.util.List;

import uk.co.perspective.app.R;
import uk.co.perspective.app.entities.Lead;
import uk.co.perspective.app.helpers.LeadDiffCallback;

public class LeadsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Lead> dataList;
    private Context context;

    private int rowIndex;

    RecyclerView mRecyclerView;

    LeadsRecyclerViewAdapter.LeadListener mListener;

    private final FragmentManager fragmentManager;

    public interface LeadListener {
        public void CreateNewLead();
        public void EditLead(int id);
        public void RemoveLead();
    }

    public LeadsRecyclerViewAdapter(List<Lead> dataList, FragmentManager fragmentManager, Context context, LeadListener listener){
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

        if (this.dataList.get(position).getSubject().equals("New Lead"))
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_lead, parent, false);
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
            holder.mStatus.setText(dataList.get(position).getStatus());

            if (dataList.get(position).getCustomerName().equals(""))
            {
                holder.mCustomer.setVisibility(View.GONE);
            }
            else
            {
                holder.mCustomer.setText(dataList.get(position).getCustomerName());
                holder.mCustomer.setVisibility(View.VISIBLE);
            }

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

            if (dataList.get(position).getStatus() != null) {
                if (dataList.get(position).getStatus().equals("New (No Contact)")) {
                    holder.mStatusIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#039BE5")));
                } else if (dataList.get(position).getStatus().equals("Attempted Contact")) {
                    holder.mStatusIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#51DCC7")));
                } else if (dataList.get(position).getStatus().equals("Contacted")) {
                    holder.mStatusIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7B51DC")));
                } else if (dataList.get(position).getStatus().equals("Establishing Qualification")) {
                    holder.mStatusIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECC605")));
                } else if (dataList.get(position).getStatus().equals("Opportunity Identified")) {
                    holder.mStatusIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#6FDF33")));
                } else if (dataList.get(position).getStatus().equals("Disqualified")) {
                    holder.mStatusIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EC5605")));
                } else {
                    holder.mStatusIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#039BE5")));
                }
            }
            else
            {
                holder.mStatusIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#039BE5")));
            }

            if (dataList.get(position).getLeadID() != null) {
                holder.thisLeadID = dataList.get(position).getLeadID();
            } else {
                holder.thisLeadID = 0;
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                rowIndex = position;
                notifyDataSetChanged();
                mListener.EditLead(dataList.get(position).getId());
                }
            });

            int pL = holder.mView.getPaddingLeft();
            int pT = holder.mView.getPaddingTop();
            int pR = holder.mView.getPaddingRight();
            int pB = holder.mView.getPaddingBottom();

            if (dataList.get(position).getIsArchived()) {
                holder.mView.setBackgroundResource(R.drawable.actionholder_flat_archived);
                holder.mSubject.setTextColor(Color.LTGRAY);
                holder.mCustomer.setTextColor(Color.LTGRAY);
                holder.mStatus.setTextColor(Color.LTGRAY);
                holder.mValue.setTextColor(Color.LTGRAY);
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

                holder.mSubject.setTextColor(Color.DKGRAY);
                holder.mCustomer.setTextColor(Color.DKGRAY);
                holder.mStatus.setTextColor(Color.DKGRAY);
                holder.mValue.setTextColor(Color.DKGRAY);
            }

            holder.mView.setPadding(pL, pT, pR, pB);
        }
        else
        {
            AddRecordViewHolder holder = (AddRecordViewHolder) viewHolder;

            holder.mItem = dataList.get(position);

            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mListener.CreateNewLead();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder {

        public Lead mItem;
        public int thisLeadID;

        public final View mView;
        public final ImageView mStatusIcon;
        public final TextView mSubject;
        public final TextView mCustomer;
        public final TextView mStatus;
        public final TextView mValue;

        public RecordViewHolder(View view) {
            super(view);
            mView = view;
            mStatusIcon = view.findViewById(R.id.status);
            mSubject = view.findViewById(R.id.lead_name);
            mCustomer = view.findViewById(R.id.customer_name);
            mStatus = view.findViewById(R.id.lead_stage);
            mValue = view.findViewById(R.id.lead_value);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mSubject.getText() + "'";
        }
    }

    public void updateList(List<Lead> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new LeadDiffCallback(newList, this.dataList));
        diffResult.dispatchUpdatesTo(this);
        this.dataList.clear();
        this.dataList.addAll(newList);
    }

    public static class AddRecordViewHolder extends RecyclerView.ViewHolder {

        public Lead mItem;
        public int thisLeadID;

        public final View mView;
        public final ImageView mAddRecord;

        public AddRecordViewHolder(View view) {
            super(view);
            mView = view;
            mAddRecord = view.findViewById(R.id.add_record);
        }
    }
}
