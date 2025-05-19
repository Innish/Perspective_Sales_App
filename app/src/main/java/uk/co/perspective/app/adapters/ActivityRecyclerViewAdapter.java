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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import uk.co.perspective.app.R;
import uk.co.perspective.app.entities.Activity;

public class ActivityRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Activity> dataList;
    private Context context;
    RecyclerView mRecyclerView;

    ActivityRecyclerViewAdapter.ActivityListener mListener;

    private final FragmentManager fragmentManager;

    public interface ActivityListener {
        public void CreateNewActivity();
        public void EditActivity(int id);
        public void RemoveActivity();
    }

    public ActivityRecyclerViewAdapter(List<Activity> dataList, FragmentManager fragmentManager, Context context, ActivityListener listener){
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

        if (this.dataList.get(position).getJournalEntryType().equals("New Activity"))
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_activity, parent, false);
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

            holder.mItem = dataList.get(viewHolder.getBindingAdapterPosition());
            holder.mSubject.setText(dataList.get(position).getSubject());
            holder.mNotes.setText(dataList.get(position).getNotes());

            //Format the date

            Date activityDate = new Date();

            SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);
            SimpleDateFormat targetFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.UK);

            try {
                activityDate = sourceFormat.parse(dataList.get(position).getStartDate());
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            assert activityDate != null;
            holder.mActivityDate.setText(targetFormat.format(activityDate));

            if (dataList.get(position).getJournalEntryID() != null) {
                holder.thisJournalEntryID = dataList.get(position).getJournalEntryID();
            } else {
                holder.thisJournalEntryID = 0;
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //rowIndex = position;
                    notifyDataSetChanged();
                    mListener.EditActivity(dataList.get(position).getId());
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
                mListener.CreateNewActivity();
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

        public Activity mItem;
        public int thisJournalEntryID;

        public final View mView;
        public final TextView mSubject;
        public final TextView mNotes;
        public final TextView mActivityDate;

        public RecordViewHolder(View view) {
            super(view);
            mView = view;
            mSubject = view.findViewById(R.id.activity_title);
            mNotes = view.findViewById(R.id.activity_details);
            mActivityDate = view.findViewById(R.id.activity_date);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mSubject.getText() + "'";
        }
    }

    public static class AddRecordViewHolder extends RecyclerView.ViewHolder {

        public Activity mItem;
        public int thisJournalEntryID;

        public final View mView;
        public final ImageView mAddRecord;

        public AddRecordViewHolder(View view) {
            super(view);
            mView = view;
            mAddRecord = view.findViewById(R.id.add_record);
        }
    }
}
