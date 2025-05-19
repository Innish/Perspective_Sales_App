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
import uk.co.perspective.app.entities.Contact;

public class SelectContactRecyclerViewAdapter extends RecyclerView.Adapter<SelectContactRecyclerViewAdapter.ViewHolder>  {

    private final List<Contact> dataList;
    private Context context;
    private int rowIndex;

    RecyclerView mRecyclerView;
    private final FragmentManager fragmentManager;

    SelectContactListener mListener;

    public interface SelectContactListener{
        void ContactSelected(int ID, int contactID, String contactName);
    }

    public void setListener(SelectContactListener listener) {
        mListener = listener;
    }

    public SelectContactRecyclerViewAdapter(List<Contact> dataList, FragmentManager fragmentManager, Context context, SelectContactListener listener){
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
    public SelectContactRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_lookup_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SelectContactRecyclerViewAdapter.ViewHolder holder, final int position) {

        holder.mItem = dataList.get(position);

        holder.mContactName.setText(dataList.get(position).getContactName());
        holder.mJobTitle.setText(dataList.get(position).getJobTitle());
        holder.mInitials.setText(getInitals(dataList.get(position).getContactName()));

        if (dataList.get(position).getContactID() != null) {
            holder.thisID = dataList.get(position).getId();
            holder.thisContactID = dataList.get(position).getContactID();
            holder.thisContactName  = dataList.get(position).getContactName();
        }
        else
        {
            holder.thisID = 0;
            holder.thisContactID = 0;
            holder.thisContactName = "";
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) { rowIndex = position;
                notifyDataSetChanged();
                mListener.ContactSelected(holder.thisID, holder.thisContactID, holder.thisContactName);
            }
        });

        int pL = holder.mView.getPaddingLeft();
        int pT = holder.mView.getPaddingTop();
        int pR = holder.mView.getPaddingRight();
        int pB = holder.mView.getPaddingBottom();

        if(rowIndex == position){
            holder.mView.setBackgroundResource(R.drawable.selectedlistitem);
            holder.thisID = dataList.get(position).getId();
            holder.thisContactID = dataList.get(position).getContactID();
            holder.thisContactName  = dataList.get(position).getContactName();
            mListener.ContactSelected(holder.thisID, holder.thisContactID, holder.thisContactName);
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

        if (strArray.length > 0)
            builder.append(strArray[0], 0, 1);
        if (strArray.length > 1)
            builder.append(strArray[1], 0, 1);

        return builder.toString();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public Contact mItem;
        public int thisID;
        public int thisContactID;
        public String thisContactName;

        public final View mView;
        public final TextView mContactName;
        public final TextView mJobTitle;
        public final TextView mInitials;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContactName = view.findViewById(R.id.contact_name);
            mJobTitle = view.findViewById(R.id.job_title);
            mInitials = view.findViewById(R.id.initials);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContactName.getText() + "'";
        }
    }

}
