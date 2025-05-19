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
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uk.co.perspective.app.R;
import uk.co.perspective.app.entities.Address;

public class AddressRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Address> dataList;
    private Context context;
    RecyclerView mRecyclerView;

    AddressRecyclerViewAdapter.AddressListener mListener;

    private final FragmentManager fragmentManager;

    public interface AddressListener {
        public void CreateNewAddress();
        public void EditAddress(int id);
        public void RemoveAddress();
    }

    public AddressRecyclerViewAdapter(List<Address> dataList, FragmentManager fragmentManager, Context context, AddressListener listener){
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

        if (this.dataList.get(position).getAddressType().equals("New Address"))
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_customer_address, parent, false);
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
            holder.mAddressType.setText(dataList.get(position).getAddressType());

            //Clean up address



            holder.mAddress.setText(dataList.get(position).getAddress().replaceAll("\\r\\r", "\n").replaceAll("\\r", "\n").replaceAll("\\n\\n", "\n"));

            if (dataList.get(position).getAddressID() != null) {
                holder.thisAddressID = dataList.get(position).getAddressID();
            } else {
                holder.thisAddressID = 0;
            }

            int pL = holder.mView.getPaddingLeft();
            int pT = holder.mView.getPaddingTop();
            int pR = holder.mView.getPaddingRight();
            int pB = holder.mView.getPaddingBottom();

            if (dataList.get(position).getIsArchived()) {
                holder.mView.setBackgroundResource(R.drawable.actionholder_archived);
                holder.mAddressType.setTextColor(Color.LTGRAY);
                holder.mAddress.setTextColor(Color.LTGRAY);
            }
            else
            {
                holder.mView.setBackgroundResource(R.drawable.actionholder);
                holder.mAddressType.setTextColor(Color.DKGRAY);
                holder.mAddress.setTextColor(Color.DKGRAY);
            }

            holder.mView.setPadding(pL, pT, pR, pB);

            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //rowIndex = position;
                    notifyDataSetChanged();
                    mListener.EditAddress(dataList.get(position).getId());
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
                    mListener.CreateNewAddress();
                }
            });
        }
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

    public static class RecordViewHolder extends RecyclerView.ViewHolder {

        public Address mItem;
        public int thisAddressID;

        public final View mView;
        public final TextView mAddressType;
        public final TextView mAddress;

        public RecordViewHolder(View view) {
            super(view);
            mView = view;
            mAddressType = view.findViewById(R.id.address_type);
            mAddress = view.findViewById(R.id.address);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mAddressType.getText() + "'";
        }
    }

    public static class AddRecordViewHolder extends RecyclerView.ViewHolder {

        public Address mItem;
        public int thisAddressID;

        public final View mView;
        public final ImageView mAddRecord;

        public AddRecordViewHolder(View view) {
            super(view);
            mView = view;
            mAddRecord = view.findViewById(R.id.add_record);
        }
    }
}
