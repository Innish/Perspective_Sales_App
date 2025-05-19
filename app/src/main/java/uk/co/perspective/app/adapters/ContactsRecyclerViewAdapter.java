package uk.co.perspective.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uk.co.perspective.app.R;
import uk.co.perspective.app.entities.Contact;

public class ContactsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Contact> dataList;
    private Context context;
    RecyclerView mRecyclerView;

    ContactsRecyclerViewAdapter.ContactListener mListener;

    private final FragmentManager fragmentManager;

    public interface ContactListener {
        public void CreateNewContact();
        public void EditContact(int id);
        public void RemoveContact();
    }

    public ContactsRecyclerViewAdapter(List<Contact> dataList, FragmentManager fragmentManager, Context context, ContactListener listener){
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

        if (this.dataList.get(position).getContactName().equals("New Contact"))
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_customer_contact, parent, false);
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
            holder.mJobTitle.setText(dataList.get(position).getJobTitle());
            holder.mInitials.setText(getInitals(dataList.get(position).getContactName()));

            if (dataList.get(position).getContactID() != null) {
                holder.thisContactID = dataList.get(position).getContactID();
            } else {
                holder.thisContactID = 0;
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //rowIndex = position;
                    notifyDataSetChanged();
                    mListener.EditContact(dataList.get(position).getId());
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
                    mListener.CreateNewContact();
                }
            });
        }
    }

    public String getInitals(String Name)
    {
        if (!Name.equals("")) {

            String[] strArray = Name.split(" ");

            StringBuilder builder = new StringBuilder();

            if (strArray.length > 0)
                builder.append(strArray[0], 0, 1);
            if (strArray.length > 1)
                builder.append(strArray[1], 0, 1);

            return builder.toString();
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

    public static class RecordViewHolder extends RecyclerView.ViewHolder {

        public Contact mItem;
        public int thisContactID;

        public final View mView;
        public final TextView mContactName;
        public final TextView mJobTitle;
        public final TextView mInitials;

        public RecordViewHolder(View view) {
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

    public static class AddRecordViewHolder extends RecyclerView.ViewHolder {

        public Contact mItem;
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
