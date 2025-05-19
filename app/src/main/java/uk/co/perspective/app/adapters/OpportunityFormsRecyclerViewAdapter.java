package uk.co.perspective.app.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import uk.co.perspective.app.R;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.entities.OpportunityForm;
import uk.co.perspective.app.joins.JoinOpportunityTask;

public class OpportunityFormsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<OpportunityForm> dataList;
    private Context context;
    private int rowIndex;

    RecyclerView mRecyclerView;

    OpportunityFormsRecyclerViewAdapter.TaskListener mListener;

    private final FragmentManager fragmentManager;

    public interface TaskListener {
        public void CreateNewForm();
        public void EditForm(int id);
        public void RemoveForm();
    }

    public OpportunityFormsRecyclerViewAdapter(List<OpportunityForm> dataList, FragmentManager fragmentManager, Context context, TaskListener listener){
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

        if (this.dataList.get(position).getFormName().equals("New Form"))
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_form, parent, false);
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

            final RecordViewHolder holder = (RecordViewHolder) viewHolder;

            holder.mItem = dataList.get(position);

            holder.mFormName.setText(dataList.get(position).getFormName());
            holder.mFormDetails.setText(dataList.get(position).getDescription());

            if ( dataList.get(position).getIsComplete()) {
                holder.mFormStatus.setText("Complete");
            }
            else
            {
                holder.mFormStatus.setText("Pending");
            }

            holder.mComplete.setChecked(dataList.get(position).getIsComplete());

            holder.mComplete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b)
                {
                    DatabaseClient.getInstance(context).getAppDatabase()
                            .opportunityFormDao()
                            .updateIsComplete(dataList.get(position).getId(), b);
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rowIndex = position;
                    notifyDataSetChanged();
                    mListener.EditForm(dataList.get(position).getId());
                }
            });

            holder.mDeleteForm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                new AlertDialog.Builder(context)
                    .setTitle("Delete Form")
                    .setMessage("Are you sure you want to remove this form?")
                    .setIcon(android.R.drawable.ic_delete)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {

                            //Remove the item

                            DatabaseClient.getInstance(context).getAppDatabase()
                                    .opportunityFormDao()
                                    .deleteOpportunityForm(dataList.get(position).getId());

                            mListener.RemoveForm();

                            dataList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, dataList.size());
                            notifyDataSetChanged();

                            Toast.makeText(context, "Form Deleted", Toast.LENGTH_SHORT).show();

                        }})
                    .setNegativeButton(android.R.string.no, null).show();
                }
            });

            int pL = holder.mView.getPaddingLeft();
            int pT = holder.mView.getPaddingTop();
            int pR = holder.mView.getPaddingRight();
            int pB = holder.mView.getPaddingBottom();

            if (!dataList.get(position).getIsComplete()) {

                holder.mView.setBackgroundResource(R.drawable.actionholder);

            }
            else
            {
                holder.mView.setBackgroundResource(R.drawable.actionholder_done);
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
                    mListener.CreateNewForm();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder {

        public OpportunityForm mItem;

        public final View mView;
        public final TextView mFormName;
        public final TextView mFormDetails;
        public final TextView mFormStatus;
        public final CheckBox mComplete;
        public final ImageView mDeleteForm;

        public RecordViewHolder(View view) {
            super(view);
            mView = view;
            mFormName = view.findViewById(R.id.form_title);
            mFormDetails = view.findViewById(R.id.form_details);
            mFormStatus = view.findViewById(R.id.form_status);
            mComplete = view.findViewById(R.id.done);
            mDeleteForm = view.findViewById(R.id.delete_form_button);
        }

        @NotNull
        @Override
        public String toString() {
            return super.toString() + " '" + mFormName.getText() + "'";
        }
    }

    public static class AddRecordViewHolder extends RecyclerView.ViewHolder {

        public OpportunityForm mItem;
        public int thisTaskID;

        public final View mView;
        public final ImageView mAddRecord;

        public AddRecordViewHolder(View view) {
            super(view);
            mView = view;
            mAddRecord = view.findViewById(R.id.add_record);
        }
    }
}
