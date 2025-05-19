package uk.co.perspective.app.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.MotionEventCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import uk.co.perspective.app.R;
import uk.co.perspective.app.interfaces.ItemTouchHelperAdapter;
import uk.co.perspective.app.interfaces.ItemTouchHelperViewHolder;
import uk.co.perspective.app.joins.JoinOpportunityQuote;

public class OpportunityQuotesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {

    private final List<JoinOpportunityQuote> dataList;
    private Context context;

    RecyclerView mRecyclerView;

    OpportunityQuotesRecyclerViewAdapter.QuoteListener mListener;

    private final FragmentManager fragmentManager;

    public interface QuoteListener {
        public void CreateNewJoinedQuote();
        public void EditJoinedQuote(int id);
        public void RemoveJoinedQuote(int id);
    }

    public OpportunityQuotesRecyclerViewAdapter(List<JoinOpportunityQuote> dataList, FragmentManager fragmentManager, Context context, OpportunityQuotesRecyclerViewAdapter.QuoteListener listener){
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

        if (this.dataList.get(position).getSubject() != null)
        {
            if (this.dataList.get(position).getSubject().equals("New Quote"))
            {
                return 0;
            }
            else
            {
                return 1;
            }
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_opportunity_quote, parent, false);
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

            if (dataList.get(position).getQuoteType() != null) {
                if (dataList.get(position).getQuoteType().equals("Estimate")) {
                    holder.mStatusIcon.setText("E");
                } else {
                    holder.mStatusIcon.setText("Q");
                }
            }

            if (dataList.get(position).getStatus() != null) {
                if (dataList.get(position).getStatus().equals("New (Not Issued)")) {
                    holder.mStatusIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D0E8FA")));
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
                holder.mStatusIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D0E8FA")));
            }

            holder.thisQuoteID = dataList.get(position).getId();

            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //rowIndex = position;
                    notifyDataSetChanged();
                    mListener.EditJoinedQuote(dataList.get(position).getId());
                }
            });

            holder.itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                        //mListener.onStartDrag(holder);
                    }
                    return false;
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
                    mListener.CreateNewJoinedQuote();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {

    }

    @Override
    public void onItemDismiss(int position) {
        mListener.RemoveJoinedQuote(dataList.get(position).getId());
        dataList.remove(position);
        notifyItemRemoved(position);
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        public JoinOpportunityQuote mItem;
        public int thisQuoteID;

        public final View mView;
        public final TextView mStatusIcon;
        public final TextView mSubject;
        public final TextView mCustomer;
        public final TextView mStatus;

        public RecordViewHolder(View view) {
            super(view);
            mView = view;
            mStatusIcon = view.findViewById(R.id.status);
            mSubject = view.findViewById(R.id.quote_name);
            mCustomer = view.findViewById(R.id.customer_name);
            mStatus = view.findViewById(R.id.quote_stage);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mSubject.getText() + "'";
        }

        @Override
        public void onItemSelected() {

        }

        @Override
        public void onItemClear() {

        }
    }

    public static class AddRecordViewHolder extends RecyclerView.ViewHolder {

        public JoinOpportunityQuote mItem;
        public int thisQuoteID;

        public final View mView;
        public final ImageView mAddRecord;

        public AddRecordViewHolder(View view) {
            super(view);
            mView = view;
            mAddRecord = view.findViewById(R.id.add_record);
        }
    }
}
