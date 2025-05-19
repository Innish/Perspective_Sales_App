package uk.co.perspective.app.helpers;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import uk.co.perspective.app.entities.Task;

public class TaskDiffCallback  extends DiffUtil.Callback  {

    List<Task> oldList;
    List<Task> newList;

    public TaskDiffCallback(List<Task> newList, List<Task> oldList) {
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
        if (!oldList.get(oldItemPosition).getSubject().equals("New Task")) {
            return oldList.get(oldItemPosition).getSubject().equals(newList.get(newItemPosition).getSubject()) || oldList.get(oldItemPosition).getNotes().equals(newList.get(newItemPosition).getNotes()) || oldList.get(oldItemPosition).getDueDate().equals(newList.get(newItemPosition).getDueDate());
        }
        else
        {
            return false;
        }
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
