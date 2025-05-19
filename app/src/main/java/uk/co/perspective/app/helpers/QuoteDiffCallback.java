package uk.co.perspective.app.helpers;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import uk.co.perspective.app.entities.Quote;

public class QuoteDiffCallback extends DiffUtil.Callback  {

    List<Quote> oldList;
    List<Quote> newList;

    public QuoteDiffCallback(List<Quote> newList, List<Quote> oldList) {
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

        if (!oldList.get(oldItemPosition).getSubject().equals("New Quote")) {
            return oldList.get(oldItemPosition).getCustomerName().equals(newList.get(newItemPosition).getCustomerName()) || oldList.get(oldItemPosition).getSubject().equals(newList.get(newItemPosition).getSubject()) || oldList.get(oldItemPosition).getStatus().equals(newList.get(newItemPosition).getStatus());
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
