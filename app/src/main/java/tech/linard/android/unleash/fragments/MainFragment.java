package tech.linard.android.unleash.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Date;

import tech.linard.android.unleash.R;
import tech.linard.android.unleash.Util;
import tech.linard.android.unleash.data.UnleashContract;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment
implements LoaderManager.LoaderCallbacks<Cursor> {

    private TextView mLast;
    private TextView mHigh;
    private TextView mLow;
    private TextView mBuy;
    private TextView mSell;
    private TextView mDate;
    private TextView mVol;

    private AdView mAdView;


    private static final int TICKER_LOADER = 0;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(TICKER_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);

        //init fields
        mBuy  = getActivity().findViewById(R.id.value_buy);
        mSell = getActivity().findViewById(R.id.value_sell);
        mHigh = getActivity().findViewById(R.id.value_high);
        mLow  = getActivity().findViewById(R.id.value_low);
        mDate = getActivity().findViewById(R.id.value_timestamp);
        mVol  = getActivity().findViewById(R.id.value_vol);
        mLast = getActivity().findViewById(R.id.value_last);

        // AdMob
        mAdView = getActivity().findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = UnleashContract.TickerEntry.COLUMN_DATE + " DESC";

        String[] columns = {UnleashContract.TickerEntry.COLUMN_HIGH
                , UnleashContract.TickerEntry.COLUMN_LOW
                , UnleashContract.TickerEntry.COLUMN_BUY
                , UnleashContract.TickerEntry.COLUMN_SELL
                , UnleashContract.TickerEntry.COLUMN_LAST
                , UnleashContract.TickerEntry.COLUMN_DATE
                , UnleashContract.TickerEntry.COLUMN_VOL };

        return new CursorLoader(getActivity(),
                UnleashContract.TickerEntry.CONTENT_URI,
                columns,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            double lastValue = data.getDouble(
                    data.getColumnIndex(UnleashContract.TickerEntry.COLUMN_LAST));
            mLast.setText("R$ " + String.valueOf(lastValue));

            mBuy.setText("R$ " + String.valueOf(data.getDouble(
                            data.getColumnIndex(UnleashContract.TickerEntry.COLUMN_BUY))));
            mSell.setText("R$ " + String.valueOf(data.getDouble(
                            data.getColumnIndex(UnleashContract.TickerEntry.COLUMN_SELL))));
            mHigh.setText("R$ " + String.valueOf(data.getDouble(
                            data.getColumnIndex(UnleashContract.TickerEntry.COLUMN_HIGH))));
            mLow.setText("R$ " + String.valueOf(data.getDouble(
                            data.getColumnIndex(UnleashContract.TickerEntry.COLUMN_LOW))));

            mVol.setText("BTC: " + String.valueOf(
                    data.getDouble(
                            data.getColumnIndex(UnleashContract.TickerEntry.COLUMN_VOL))));
            int timestamp = data.getInt(data.getColumnIndex(UnleashContract.TickerEntry.COLUMN_DATE));
            String readableDate = Util.getReadableDateFromUnixTime(timestamp);
            mDate.setText(readableDate);

            int yesterdayTime = timestamp - (24 * 3600);
            String arg = String.valueOf(yesterdayTime);

            String sortOrder = UnleashContract.TickerEntry.COLUMN_DATE + " ASC";

            String[] columns = {UnleashContract.TickerEntry.COLUMN_LAST
                    , UnleashContract.TickerEntry.COLUMN_DATE};

            String selection = UnleashContract.TickerEntry.COLUMN_DATE + " > ?";
            String[] selectionArgs = {arg};

            Cursor cursorYesterday = getActivity().getContentResolver()
                    .query(UnleashContract.TickerEntry.CONTENT_URI,
                    columns,
                    selection,
                    selectionArgs,
                    sortOrder);

            if (cursorYesterday.moveToFirst() && yesterdayTime < timestamp ) {
                double yesterdayLastValue = cursorYesterday.
                        getDouble(cursorYesterday.getColumnIndex(
                                UnleashContract.TickerEntry.COLUMN_LAST));

                if (yesterdayLastValue > lastValue) {
                    mLast.setTextColor(RED);
                } else {
                    mLast.setTextColor(GREEN);
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
