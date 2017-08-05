package tech.linard.android.unleash.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import tech.linard.android.unleash.R;
import tech.linard.android.unleash.Util;
import tech.linard.android.unleash.activities.MainActivity;
import tech.linard.android.unleash.model.Orderbook;
import tech.linard.android.unleash.network.VolleySingleton;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OrderbookFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class OrderbookFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ArrayList<Double> mAskPrices = null;
    private ArrayList<Double> mBidPrices = null;
    LineChart mChart;
    public OrderbookFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
         mChart = getActivity().findViewById(R.id.chart);
        fetchOrderbookFromNetwork();

    }

    private double[] populateDoubleArray(ArrayList<Double> arraylist) {
        double[] d = new double[arraylist.size()];
        for (int x = 0; x<arraylist.size(); x++) {
            d[x] = arraylist.get(x);
        }
        return d;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_orderbook, container, false);
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


    private void fetchOrderbookFromNetwork() {
        String url = "https://www.mercadobitcoin.net/api/orderbook/";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Orderbook orderbook = Util.orderbookFromJSon(response);

                        mAskPrices = orderbook.getPrices(orderbook.getAsks());
                        mBidPrices = orderbook.getPrices(orderbook.getBids());

                        Collections.sort(mAskPrices);
                        Collections.reverse(mBidPrices);

                        fillGraphic();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Orderbook", "Volley on response error!");
            }
        });

        VolleySingleton.getInstance(getContext()).addToRequestQueue(jsObjRequest);
    }

    private void fillGraphic() {
        if (mChart != null) {
            mChart.setDrawGridBackground(false);
            mChart.getDescription().setEnabled(false);
            mChart.setDrawBorders(false);

            mChart.getAxisLeft().setEnabled(false);
            mChart.getAxisRight().setDrawAxisLine(false);
            mChart.getAxisRight().setDrawGridLines(false);
            mChart.getXAxis().setDrawAxisLine(false);
            mChart.getXAxis().setDrawGridLines(false);

            // enable touch gestures
            mChart.setTouchEnabled(true);

            // enable scaling and dragging
            mChart.setDragEnabled(true);
            mChart.setScaleEnabled(true);

            // if disabled, scaling can be done on x- and y-axis separately
            mChart.setPinchZoom(false);

            Legend l = mChart.getLegend();
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            l.setOrientation(Legend.LegendOrientation.VERTICAL);
            l.setDrawInside(false);


            double[] asks = populateDoubleArray(mAskPrices);
            double[] bids = populateDoubleArray(mBidPrices);
            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(generateLineDataSet(asks, "asks", RED));
            dataSets.add(generateLineDataSet(bids, "bids", GREEN));

            LineData data = new LineData(dataSets);
            mChart.setData(data);
            mChart.invalidate();

        }

    }

    private LineDataSet generateLineDataSet(double[] doubleArray, String label, int color) {
        Statistics statistics = new Statistics(doubleArray);

        double q1 = statistics.getQuartile(doubleArray, 25);
        double q3 = statistics.getQuartile(doubleArray, 75);
        double iqr = q3 - q1;
        double upperOutliers = q3 + 1.5 * iqr;
        double lowerOutliers = q1 - 1.5 * iqr;


        List<Entry> entries = new ArrayList<Entry>();
        int x = 0;
        for (int y = 0; y < doubleArray.length; y++) {
            if (doubleArray[y] > lowerOutliers && doubleArray[y] < upperOutliers) {
                entries.add(new Entry(y, (float) doubleArray[x]));
                x++;
            }
        }
        LineDataSet dataSet = new LineDataSet(entries, label); // add entries to dataset
        dataSet.setColor(color);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(color);
        dataSet.setDrawCircles(false);
        dataSet.setDrawCircleHole(false);

        return dataSet;
    }


    public class Statistics
    {
        double[] data;
        int size;

        public Statistics(double[] data)
        {
            this.data = data;
            size = data.length;
        }

        double getMean()
        {
            double sum = 0.0;
            for(double a : data)
                sum += a;
            return sum/size;
        }

        double getVariance()
        {
            double mean = getMean();
            double temp = 0;
            for(double a :data)
                temp += (a-mean)*(a-mean);
            return temp/(size-1);
        }

        double getStdDev()
        {
            return Math.sqrt(getVariance());
        }

        public double median()
        {
            Arrays.sort(data);

            if (data.length % 2 == 0)
            {
                return (data[(data.length / 2) - 1] + data[data.length / 2]) / 2.0;
            }
            return data[data.length / 2];
        }
        public double getQuartile(double[] values, double percent){
            if (values == null || values.length == 0) {
                throw new IllegalArgumentException("The data array either is null or does not contain any data.");
            }
            double[] v = new double[values.length];
            System.arraycopy(values, 0, v, 0, values.length);
            Arrays.sort(v);
            int n = (int) Math.round(v.length * percent / 100);
            return v[n];
        }
    }
}

