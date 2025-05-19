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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import uk.co.perspective.app.R;
import uk.co.perspective.app.entities.Project;
import uk.co.perspective.app.helpers.ProjectDiffCallback;

public class ProjectsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Project> dataList;
    private Context context;

    private int rowIndex;

    RecyclerView mRecyclerView;

    ProjectsRecyclerViewAdapter.ProjectListener mListener;

    private final FragmentManager fragmentManager;

    public interface ProjectListener {
        public void CreateNewProject();
        public void EditProject(int id);
        public void RemoveProject();
    }

    public ProjectsRecyclerViewAdapter(List<Project> dataList, FragmentManager fragmentManager, Context context, ProjectListener listener){
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

        if (this.dataList.get(position).getProjectName().equals("@NewProject"))
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_project, parent, false);
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
            holder.mProjectName.setText(dataList.get(position).getProjectName());
            holder.mDescription.setText(dataList.get(position).getDetails());
            holder.mInitials.setText(getInitals(dataList.get(position).getProjectName()));
            holder.mStatus.setText(dataList.get(position).getStatus());

            if (dataList.get(position).getDetails().equals(""))
            {
                holder.mDescription.setVisibility(View.GONE);
            }
            else {
                holder.mDescription.setVisibility(View.VISIBLE);
            }

            if (dataList.get(position).getProjectID() != null) {
                holder.thisProjectID = dataList.get(position).getProjectID();
            } else {
                holder.thisProjectID = 0;
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    rowIndex = position;
                    notifyDataSetChanged();
                    mListener.EditProject(dataList.get(position).getId());
                }
            });

            int pL = holder.itemView.getPaddingLeft();
            int pT = holder.itemView.getPaddingTop();
            int pR = holder.itemView.getPaddingRight();
            int pB = holder.itemView.getPaddingBottom();

            if (dataList.get(position).getIsArchived()) {
                holder.itemView.setBackgroundResource(R.drawable.actionholder_flat_archived);
                holder.mProjectName.setTextColor(Color.LTGRAY);
                holder.mDescription.setTextColor(Color.LTGRAY);
                holder.mStatus.setTextColor(Color.LTGRAY);
                holder.mStatus.setTextColor(Color.LTGRAY);
            }
            else
            {
                if (rowIndex == position) {
                    holder.itemView.setBackgroundResource(R.drawable.actionholder_selected_flat);
                }
                else
                {
                    holder.itemView.setBackgroundResource(R.drawable.actionholder_flat);
                }

                holder.mProjectName.setTextColor(Color.DKGRAY);
                holder.mDescription.setTextColor(Color.DKGRAY);
                holder.mStatus.setTextColor(Color.DKGRAY);
                holder.mStatus.setTextColor(Color.DKGRAY);
            }

            holder.itemView.setPadding(pL, pT, pR, pB);
        }
        else
        {
            AddRecordViewHolder holder = (AddRecordViewHolder) viewHolder;

            holder.mItem = dataList.get(position);

            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                mListener.CreateNewProject();
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

        public Project mItem;
        public int thisProjectID;

        public final View mView;
        public final TextView mProjectName;
        public final TextView mDescription;
        public final TextView mStatus;
        public final TextView mInitials;

        public RecordViewHolder(View view) {
            super(view);
            mView = view;
            mProjectName = view.findViewById(R.id.project_name);
            mDescription = view.findViewById(R.id.description);
            mStatus = view.findViewById(R.id.project_status);
            mInitials = view.findViewById(R.id.initials);
        }

        @NotNull
        @Override
        public String toString() {
            return super.toString() + " '" + mProjectName.getText() + "'";
        }
    }

    public void updateList(List<Project> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ProjectDiffCallback(newList, this.dataList));
        diffResult.dispatchUpdatesTo(this);
        this.dataList.clear();
        this.dataList.addAll(newList);
    }

    public static class AddRecordViewHolder extends RecyclerView.ViewHolder {

        public Project mItem;
        public int thisProjectID;

        public final View mView;
        public final ImageView mAddRecord;

        public AddRecordViewHolder(View view) {
            super(view);
            mView = view;
            mAddRecord = view.findViewById(R.id.add_record);
        }
    }
}
