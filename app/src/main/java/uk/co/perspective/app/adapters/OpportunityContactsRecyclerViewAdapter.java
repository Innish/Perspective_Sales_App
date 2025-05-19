package uk.co.perspective.app.adapters;

import android.content.Context;
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

import uk.co.perspective.app.R;
import uk.co.perspective.app.interfaces.ItemTouchHelperAdapter;
import uk.co.perspective.app.interfaces.ItemTouchHelperViewHolder;
import uk.co.perspective.app.joins.JoinOpportunityContact;

public class OpportunityContactsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {

    private final List<JoinOpportunityContact> dataList;
    private Context context;

    RecyclerView mRecyclerView;

    OpportunityContactsRecyclerViewAdapter.ContactListener mListener;

    private final FragmentManager fragmentManager;

    public interface ContactListener {
        public void CreateNewJoinedContact();
        public void EditJoinedContact(int id);
        public void RemoveJoinedContact(int id);
    }

    public OpportunityContactsRecyclerViewAdapter(List<JoinOpportunityContact> dataList, FragmentManager fragmentManager, Context context, ContactListener listener){
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

        if (this.dataList.get(position).getContactName() != null) {
            if (this.dataList.get(position).getContactName().equals("New Contact")) {
                return 0;
            } else {
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_linked_contact, parent, false);
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

            holder.mContactName.setText(dataList.get(position).getContactName());
            holder.mCustomerName.setText(dataList.get(position).getCustomerName());
            holder.mJobTitle.setText(dataList.get(position).getJobTitle());
            holder.mInitials.setText(getInitals(dataList.get(position).getContactName()));

            holder.thisContactID = dataList.get(position).getId();

            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //rowIndex = position;
                    notifyDataSetChanged();
                    mListener.EditJoinedContact(dataList.get(position).getId());
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
                    mListener.CreateNewJoinedContact();
                }
            });
        }
    }

    public String getInitals(String Name)
    {
        if (Name != null) {
            if (!Name.equals("")) {

                String[] strArray = Name.split(" ");

                StringBuilder builder = new StringBuilder();

                if (strArray.length > 0)
                    builder.append(strArray[0], 0, 1);
                if (strArray.length > 1)
                    builder.append(strArray[1], 0, 1);

                return builder.toString();
            } else {
                return "";
            }
        }
        else
        {
            return "";
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
        mListener.RemoveJoinedContact(dataList.get(position).getId());
        dataList.remove(position);
        notifyItemRemoved(position);
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        public JoinOpportunityContact mItem;
        public int thisContactID;

        public final View mView;
        public final TextView mContactName;
        public final TextView mCustomerName;
        public final TextView mJobTitle;
        public final TextView mInitials;

        public RecordViewHolder(View view) {
            super(view);
            mView = view;
            mContactName = view.findViewById(R.id.contact_name);
            mCustomerName= view.findViewById(R.id.customer_name);
            mJobTitle = view.findViewById(R.id.job_title);
            mInitials = view.findViewById(R.id.initials);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContactName.getText() + "'";
        }

        @Override
        public void onItemSelected() {

        }

        @Override
        public void onItemClear() {

        }
    }

    public static class AddRecordViewHolder extends RecyclerView.ViewHolder {

        public JoinOpportunityContact mItem;
        public int thisContactID;

        public final View mView;
        public final ImageView mAddRecord;

        public AddRecordViewHolder(View view) {
            super(view);
            mView = view;
            mAddRecord = view.findViewById(R.id.add_record);
        }
    }
}
