package uk.co.perspective.app.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
import uk.co.perspective.app.joins.JoinProjectTask;

public class ProjectTasksRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<JoinProjectTask> dataList;
    private Context context;
    private int rowIndex;

    RecyclerView mRecyclerView;

    ProjectTasksRecyclerViewAdapter.TaskListener mListener;

    private final FragmentManager fragmentManager;

    public interface TaskListener {
        public void CreateNewTask();
        public void EditTask(int id);
        public void EditPhase(int id);
        public void CompleteTask(int id, boolean isComplete);
        public void RemoveTask();
    }

    public ProjectTasksRecyclerViewAdapter(List<JoinProjectTask> dataList, FragmentManager fragmentManager, Context context, TaskListener listener){
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

        if (this.dataList.get(position).getSubject().equals("New Task"))
        {
            return 0;
        }
        else if (this.dataList.get(position).getHeader())
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

        if (viewType == 2) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_task, parent, false);
            viewHolder = new RecordViewHolder(view);
        }
        else if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_phase, parent, false);
            viewHolder = new PhaseRecordViewHolder(view);
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

            PhaseRecordViewHolder holder = (PhaseRecordViewHolder) viewHolder;

            holder.mItem = dataList.get(position);
            holder.mPhaseName.setText(dataList.get(position).getPhaseName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                mListener.EditPhase(dataList.get(position).getPhaseLocalID());
                }
            });
        }
        else if (viewHolder.getItemViewType() == 2) {

            final RecordViewHolder holder = (RecordViewHolder) viewHolder;

            holder.mItem = dataList.get(position);

            holder.mSubject.setText(dataList.get(position).getSubject());
            holder.mNotes.setText(dataList.get(position).getNotes());
            holder.mComplete.setChecked(dataList.get(position).getComplete());

            Date convertedDate = new Date();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);
            SimpleDateFormat targetFormat = new SimpleDateFormat("EEE, d MMM yyyy", Locale.UK);
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.UK);

            String DateString = dataList.get(position).getDueDate();

            try {
                convertedDate = dateFormat.parse(DateString);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            assert convertedDate != null;
            if (!timeFormat.format(convertedDate).equals("00:00")) {
                holder.mDueDate.setText(String.format("%s %s", targetFormat.format(convertedDate), timeFormat.format(convertedDate)));
            } else {
                holder.mDueDate.setText(targetFormat.format(convertedDate));
            }

            holder.mComplete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    dataList.get(position).setComplete(holder.mComplete.isChecked());

                    int pL = holder.mView.getPaddingLeft();
                    int pT = holder.mView.getPaddingTop();
                    int pR = holder.mView.getPaddingRight();
                    int pB = holder.mView.getPaddingBottom();

                    if (holder.mComplete.isChecked())
                    {
                        holder.mView.setBackgroundResource(R.drawable.actionholder_done);
                    }
                    else
                    {
                        holder.mView.setBackgroundResource(R.drawable.actionholder);
                    }

                    holder.mView.setPadding(pL, pT, pR, pB);

                    mListener.CompleteTask(dataList.get(position).getId(), holder.mComplete.isChecked());

                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    rowIndex = position;
                    notifyDataSetChanged();
                    mListener.EditTask(dataList.get(position).getId());
                }
            });

            holder.mDeleteTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                new AlertDialog.Builder(context)
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to remove this task?")
                    .setIcon(android.R.drawable.ic_delete)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {

                            //Remove the item

                            DatabaseClient.getInstance(context).getAppDatabase()
                                    .projectDao()
                                    .deleteProjectTask(dataList.get(position).getId(), dataList.get(position).getLocalProjectID());

                            DatabaseClient.getInstance(context).getAppDatabase()
                                    .taskDao()
                                    .deleteTask(dataList.get(position).getId());

                            mListener.RemoveTask();

                            dataList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, dataList.size());
                            notifyDataSetChanged();

                            Toast.makeText(context, "Task Deleted", Toast.LENGTH_SHORT).show();

                        }})
                    .setNegativeButton(android.R.string.no, null).show();
                }
            });

            int pL = holder.mView.getPaddingLeft();
            int pT = holder.mView.getPaddingTop();
            int pR = holder.mView.getPaddingRight();
            int pB = holder.mView.getPaddingBottom();

            if (!dataList.get(position).getComplete()) {
                if (rowIndex == position) {
                    holder.mView.setBackgroundResource(R.drawable.actionholder_selected);
                }
                else
                {
                    holder.mView.setBackgroundResource(R.drawable.actionholder);
                }
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
                    mListener.CreateNewTask();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder {

        public JoinProjectTask mItem;
        public int thisTaskID;

        public final View mView;
        public final TextView mSubject;
        public final TextView mNotes;
        public final TextView mDueDate;
        public final CheckBox mComplete;
        public final ImageView mDeleteTask;

        public RecordViewHolder(View view) {
            super(view);
            mView = view;
            mSubject = view.findViewById(R.id.task_title);
            mDueDate = view.findViewById(R.id.task_due_date);
            mNotes = view.findViewById(R.id.task_details);
            mComplete = view.findViewById(R.id.done);
            mDeleteTask = view.findViewById(R.id.delete_task_button);
        }

        @NotNull
        @Override
        public String toString() {
            return super.toString() + " '" + mSubject.getText() + "'";
        }
    }

    public static class PhaseRecordViewHolder extends RecyclerView.ViewHolder {

        public JoinProjectTask mItem;
        public int thisPhaseID;

        public final View mView;
        public final TextView mPhaseName;

        public PhaseRecordViewHolder(View view) {
            super(view);
            mView = view;
            mPhaseName = view.findViewById(R.id.phase_name);
        }
    }

//    public void updateList(List<JoinProjectTask> newList) {
//        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new TaskDiffCallback(newList, this.dataList));
//        diffResult.dispatchUpdatesTo(this);
//        this.dataList.clear();
//        this.dataList.addAll(newList);
//    }

    public static class AddRecordViewHolder extends RecyclerView.ViewHolder {

        public JoinProjectTask mItem;
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
