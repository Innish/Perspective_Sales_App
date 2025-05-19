package uk.co.perspective.app.helpers;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import uk.co.perspective.app.entities.Customer;

public class CustomerDiffCallback extends DiffUtil.Callback  {

    List<Customer> oldList;
    List<Customer> newList;

    public CustomerDiffCallback(List<Customer> newList, List<Customer> oldList) {
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
        return oldList.get(oldItemPosition).getCustomerName().equals(newList.get(newItemPosition).getCustomerName()) || oldList.get(oldItemPosition).getContactName().equals(newList.get(newItemPosition).getContactName());
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
