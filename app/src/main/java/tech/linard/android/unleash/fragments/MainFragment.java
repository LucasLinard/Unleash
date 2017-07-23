package tech.linard.android.unleash.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import tech.linard.android.unleash.R;
import tech.linard.android.unleash.data.UnleashContract;

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

    private TextView mHigh;
    private TextView mLow;
    private TextView mBuy;
    private TextView mSell;
    private TextView mDate;


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
        Toast.makeText(getActivity().getBaseContext(), "LOADER started", Toast.LENGTH_SHORT).show();
        String sortOrder = UnleashContract.TickerEntry.COLUMN_DATE + " DESC";

        String[] columns = {UnleashContract.TickerEntry.COLUMN_HIGH
                , UnleashContract.TickerEntry.COLUMN_LOW
                , UnleashContract.TickerEntry.COLUMN_BUY
                , UnleashContract.TickerEntry.COLUMN_SELL
                , UnleashContract.TickerEntry.COLUMN_DATE};

        return new CursorLoader(getActivity(),
                UnleashContract.TickerEntry.CONTENT_URI,
                columns,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Toast.makeText(getActivity().getBaseContext(), "LOADER FINISHED", Toast.LENGTH_SHORT).show();
        if (data != null && data.moveToFirst()) {
//            mBuy.setText(String.valueOf(
//                    data.getDouble(
//                            data.getColumnIndex(UnleashContract.TickerEntry.COLUMN_BUY))));
//            mSell.setText(String.valueOf(
//                    data.getDouble(
//                            data.getColumnIndex(UnleashContract.TickerEntry.COLUMN_SELL))));
//            mHigh.setText(String.valueOf(
//                    data.getDouble(
//                            data.getColumnIndex(UnleashContract.TickerEntry.COLUMN_HIGH))));
//            mLow.setText(String.valueOf(
//                    data.getDouble(
//                            data.getColumnIndex(UnleashContract.TickerEntry.COLUMN_LOW))));
//
//            int dateUnixTime = data.getInt(
//                    data.getColumnIndex(UnleashContract.TickerEntry.COLUMN_DATE));
//
//            Date date = new Date((long) dateUnixTime*1000);
//            String formatedDate = DateFormat.getDateTimeInstance().format(date);
//            mDate.setText(formatedDate);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
