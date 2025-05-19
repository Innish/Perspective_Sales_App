package uk.co.perspective.app.helpers;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import uk.co.perspective.app.entities.Project;

public class ProjectDiffCallback extends DiffUtil.Callback  {

    List<Project> oldList;
    List<Project> newList;

    public ProjectDiffCallback(List<Project> newList, List<Project> oldList) {
        this.newList = newList;
        this.oldList = oldList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getCustomerName().equals(newList.get(newItemPosition).getCustomerName()) || oldList.get(oldItemPosition).getProjectName().equals(newList.get(newItemPosition).getProjectName())  || oldList.get(oldItemPosition).getStatus().equals(newList.get(newItemPosition).getStatus());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}

