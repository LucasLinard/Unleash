package tech.linard.android.unleash.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import tech.linard.android.unleash.R;
import tech.linard.android.unleash.fragments.StopLossFragment.OnListFragmentInteractionListener;
import tech.linard.android.unleash.fragments.dummy.DummyContent.DummyItem;
import tech.linard.android.unleash.model.StopLoss;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class StopLossRecyclerViewAdapter extends RecyclerView.Adapter<StopLossRecyclerViewAdapter.ViewHolder> {

    private final List<StopLoss> mValues;
    private final OnListFragmentInteractionListener mListener;

    public StopLossRecyclerViewAdapter(List<StopLoss> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_stoploss, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mCotacao.setText(String.valueOf(mValues.get(position).getCotacaoBTC()));
        holder.mQuantidade.setText(String.valueOf(mValues.get(position).getCotacaoBTC()));
        int id = mValues.get(position).getExchangeId();
        switch (id) {
            case 0:
                holder.mExchange.setText("Mercado Bitcoin");
                break;
            case 1:
                holder.mExchange.setText("Foxbit");
                break;
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem, v);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mExchange;
        public final TextView mCotacao;
        public final TextView mQuantidade;
        public final ImageView mEdit;

        public StopLoss mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mExchange = (TextView) view.findViewById(R.id.txt_exchange);
            mCotacao = (TextView) view.findViewById(R.id.txt_cotacao);
            mQuantidade = (TextView) view.findViewById(R.id.txt_quantidade);
            mEdit = view.findViewById(R.id._stop_loss_edit_btn);
        }

    }

}
