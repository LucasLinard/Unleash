package tech.linard.android.unleash.fragments;

import android.database.Cursor;
import android.icu.text.RelativeDateTimeFormatter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import tech.linard.android.unleash.R;
import tech.linard.android.unleash.Util;
import tech.linard.android.unleash.data.UnleashContract;
import tech.linard.android.unleash.fragments.TradeFragment.OnListFragmentInteractionListener;
import tech.linard.android.unleash.model.Trade;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

/**
 * {@link RecyclerView.Adapter} that can display a {@link tech.linard.android.unleash.model.Trade}
 * and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class TradeRecyclerViewAdapter extends RecyclerView.Adapter<TradeRecyclerViewAdapter.ViewHolder> {


    private final Cursor mValues;
    private final OnListFragmentInteractionListener mListener;

    public TradeRecyclerViewAdapter(Cursor data, OnListFragmentInteractionListener listener) {
        mValues = data;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_trade, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // populate the list
        mValues.moveToPosition(position);
        holder.mPreco.setText(String.valueOf(mValues.getDouble(mValues.getColumnIndex(UnleashContract.TradeEntry.COLUMN_PRICE))));
        holder.mQuantidade.setText(String.valueOf(mValues.getDouble(mValues.getColumnIndex(UnleashContract.TradeEntry.COLUMN_AMMOUNT))));
        String type = String.valueOf(mValues.getString(mValues.getColumnIndex(UnleashContract.TradeEntry.COLUMN_TYPE)));
        holder.mType.setText(type);
        if (type.contains("buy")) {
            holder.mImageView.setColorFilter(GREEN);
            holder.mType.setHint(tech.linard.android.unleash.R.string.buy_operation);
        } else {
            holder.mImageView.setColorFilter(RED);
            holder.mType.setHint(tech.linard.android.unleash.R.string.sell_operation);
        }
        String readableDate = Util.getReadableDateFromUnixTime(mValues.getInt(mValues.getColumnIndex(UnleashContract.TradeEntry.COLUMN_DATE)));
        holder.mTimestamp.setText(readableDate);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public final TextView mPreco;
        public final TextView mQuantidade;
        public final TextView mTimestamp;
        public final TextView mType;
        public Trade mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mPreco = (TextView) view.findViewById(R.id.trade_value_valor);
            mQuantidade = (TextView) view.findViewById(R.id.trade_value_quantidade);
            mImageView = view.findViewById(R.id.trade_icon);
            mTimestamp = view.findViewById(R.id.trade_timestamp);
            mType = view.findViewById(R.id.trade_value_type);
        }
    }
}
