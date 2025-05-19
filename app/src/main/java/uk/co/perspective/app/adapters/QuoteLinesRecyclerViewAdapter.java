package uk.co.perspective.app.adapters;

import static uk.co.perspective.app.helpers.Utilities.GetCurrencySymbol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.MotionEventCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import uk.co.perspective.app.R;
import uk.co.perspective.app.entities.QuoteLine;
import uk.co.perspective.app.interfaces.ItemTouchHelperAdapter;
import uk.co.perspective.app.interfaces.ItemTouchHelperViewHolder;

public class QuoteLinesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {

    private final List<QuoteLine> dataList;
    private Context context;
    RecyclerView mRecyclerView;
    private final FragmentManager fragmentManager;

    private String Currency;
    private float ExchangeRate;

    QuoteLinesRecyclerViewAdapter.QuoteListener mListener;

    public interface QuoteListener {
        public void CreateNewQuoteLine();
        public void EditLine(int position, QuoteLine line);
        public void RemoveLine(int position, int id);
    }

    public QuoteLinesRecyclerViewAdapter(List<QuoteLine> dataList, String currency, float exchangeRate, FragmentManager fragmentManager, Context context, QuoteListener listener){
        this.context = context;
        this.dataList = dataList;
        this.fragmentManager = fragmentManager;
        this.mListener = listener;
        this.Currency = currency;
        this.ExchangeRate = exchangeRate;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public int getItemViewType(int position) {

        if (this.dataList.get(position).getDescription().equals("New Line"))
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_quote_line, parent, false);
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

            float LineTotal = 0.00f;
            float LineSubTotal = 0.00f;
            float LineDiscount = 0.00f;
            float LineTaxRate = 0.00f;

            final RecordViewHolder holder = (RecordViewHolder) viewHolder;

            holder.mItem = dataList.get(position);
            holder.mPartNumber.setText(dataList.get(position).getPartNumber());
            holder.mDescription.setText(dataList.get(position).getDescription());
            holder.mQuantity.setText(String.format(Locale.UK, "%,.2f", dataList.get(position).getQuantity()));
            holder.mPrice.setText(String.format(Locale.UK, "£%,.2f", dataList.get(position).getValue()));

            LineSubTotal = dataList.get(position).getQuantity() * dataList.get(position).getValue();

            if (dataList.get(position).getDiscount() != null) {
                if (dataList.get(position).getDiscount() != 0) {
                    LineDiscount = dataList.get(position).getDiscount();

                    //Calculate Discount

                    holder.mDiscount.setText(String.format(Locale.UK, "%.2f%% Discount (£%,.2f)", dataList.get(position).getDiscount(), (LineSubTotal / 100) * LineDiscount));
                    holder.mDiscount.setVisibility(View.VISIBLE);

                    LineSubTotal -= (LineSubTotal / 100) * LineDiscount;
                }
                else
                {
                    holder.mDiscount.setVisibility(View.GONE);
                }
            }
            else
            {
                holder.mDiscount.setVisibility(View.GONE);
            }

            if (dataList.get(position).getTaxRate() != null) {
                if (dataList.get(position).getTaxRate() != 0) {
                    LineTaxRate = dataList.get(position).getTaxRate();

                    //Calculate Tax

                    LineTotal = LineSubTotal + ((LineSubTotal / 100) * LineTaxRate);

                    holder.mTaxRate.setText(String.format(Locale.UK, "%.2f%% Tax (£%,.2f)", dataList.get(position).getTaxRate(), ((LineSubTotal / 100) * LineTaxRate)));
                    holder.mTaxRate.setVisibility(View.VISIBLE);

                }
                else
                {
                    holder.mTaxRate.setVisibility(View.GONE);
                    LineTotal = LineSubTotal;
                }
            }
            else
            {
                holder.mTaxRate.setVisibility(View.GONE);
                LineTotal = LineSubTotal;
            }

            holder.mLineTotal.setText(String.format(Locale.UK, "£%,.2f", LineTotal));

            int pL = holder.mLineTotal.getPaddingLeft();
            int pT = holder.mLineTotal.getPaddingTop();
            int pR = holder.mLineTotal.getPaddingRight();
            int pB = holder.mLineTotal.getPaddingBottom();

            if (ExchangeRate != 0) {
                if (ExchangeRate < 1 || ExchangeRate > 1) {

                    holder.mLineAlternativeTotal.setVisibility(View.VISIBLE);
                    holder.mLineTotal.setBackground(ContextCompat.getDrawable(context, R.drawable.total_alternative_background_land_left));
                    holder.mLineAlternativeTotal.setText(String.format(Locale.UK, "%s%.2f", GetCurrencySymbol(Currency), LineTotal * ExchangeRate));
                }
                else
                {
                    holder.mLineAlternativeTotal.setVisibility(View.GONE);
                    holder.mLineTotal.setBackground(ContextCompat.getDrawable(context, R.drawable.text_input_background));
                }
            }
            else
            {
                holder.mLineAlternativeTotal.setVisibility(View.GONE);
                holder.mLineTotal.setBackground(ContextCompat.getDrawable(context, R.drawable.text_input_background));
            }

            holder.mLineTotal.setPadding(pL, pT, pR, pB);

            if (dataList.get(position).getQuoteLineID() != null) {
                holder.thisQuoteLineID = dataList.get(position).getQuoteLineID();
            } else {
                holder.thisQuoteLineID = 0;
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                notifyItemChanged(position);
                mListener.EditLine(position, dataList.get(position));
                }
            });

            holder.handleView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    //listener.onStartDrag(holder);
                }
                return false;
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
                mListener.CreateNewQuoteLine();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(dataList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(dataList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {

        if (dataList != null) {
            if (dataList.size() > 0) {

                try {
                    mListener.RemoveLine(position, dataList.get(position).getId());
                }
                catch(Exception ignored){}

                dataList.remove(position);
                notifyItemRemoved(position);

            }
        }
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        public QuoteLine mItem;
        public int thisQuoteLineID;

        public final View mView;
        public final TextView mPartNumber;
        public final TextView mDescription;
        public final TextView mQuantity;
        public final TextView mPrice;
        public final TextView mDiscount;
        public final TextView mTaxRate;
        public final TextView mLineTotal;
        public final TextView mLineAlternativeTotal;

        public final ImageView handleView;

        public RecordViewHolder(View view) {
            super(view);
            mView = view;
            mPartNumber = view.findViewById(R.id.part_number);
            mDescription = view.findViewById(R.id.description);
            mQuantity = view.findViewById(R.id.quantity);
            mPrice = view.findViewById(R.id.price);
            mDiscount = view.findViewById(R.id.discount);
            mTaxRate = view.findViewById(R.id.tax);
            mLineTotal = view.findViewById(R.id.line_total);
            mLineAlternativeTotal = view.findViewById(R.id.line_total_alternative_currency);
            handleView = (ImageView) itemView.findViewById(R.id.handle);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDescription.getText() + "'";
        }

        @Override
        public void onItemSelected() {
            //itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            //itemView.setBackgroundColor(0);
        }


    }

    public static class AddRecordViewHolder extends RecyclerView.ViewHolder {

        public QuoteLine mItem;
        public int thisQuoteLineID;

        public final View mView;
        public final ImageView mAddRecord;

        public AddRecordViewHolder(View view) {
            super(view);
            mView = view;
            mAddRecord = view.findViewById(R.id.add_record);
        }
    }

}
