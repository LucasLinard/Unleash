package tech.linard.android.unleash.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import tech.linard.android.unleash.R;
import tech.linard.android.unleash.Util;
import tech.linard.android.unleash.model.Orderbook;
import tech.linard.android.unleash.model.OrderbookItem;
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
    private Orderbook mOrderbook = new Orderbook();
    private ArrayList<OrderbookItem> mAsks = null;
    private ArrayList<OrderbookItem> mBids = null;

    public OrderbookFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchOrderbookFromNetwork();

    }

    private double[] populateDoubleArray(ArrayList<OrderbookItem> arraylist) {
        double[] d = new double[arraylist.size()];
        for (int x = 0; x<arraylist.size(); x++) {
            float newFloat = arraylist.get(x).getPrice().floatValue();
            d[x] = newFloat;
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
                        Collections.sort(orderbook.getAsks(), new CustomComparator());
                        Collections.sort(orderbook.getBids(), new CustomComparator());
                        mOrderbook = orderbook;
                        Toast.makeText(getActivity(), "COMPARATOR!", Toast.LENGTH_SHORT).show();
                        fillGraphic();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        VolleySingleton.getInstance(getContext()).addToRequestQueue(jsObjRequest);
    }

    private void fillGraphic() {
        LineChart lineChart = getActivity().findViewById(R.id.chart);
        double[] bids = populateDoubleArray(mOrderbook.getBids());
        double[] asks = populateDoubleArray(mOrderbook.getAsks());

        Statistics statistics = new Statistics(asks);

        double q1 = statistics.getQuartile(asks, 25);
        double q3 = statistics.getQuartile(asks, 75);
        double iqr = q3 - q1;
        double upperOutliers = q3 + 1.5 * iqr;
        double lowerOutliers = q1 - 1.5 * iqr;


        List<Entry> entries = new ArrayList<Entry>();
        int x = 0;
        for (int y = 0; y < asks.length; y++) {
            if (asks[y] > lowerOutliers && asks[y] < upperOutliers) {
                entries.add(new Entry(y, (float) asks[x]));
                x++;

            }
        }
        LineDataSet dataSet = new LineDataSet(entries, "Asks"); // add entries to dataset
        dataSet.setColor(RED);
        dataSet.setDrawFilled(true);
        dataSet.setDrawCircles(false);
        dataSet.setDrawCircleHole(false);
        lineChart.getAxisLeft().setAxisMinimum((float) lowerOutliers);
        lineChart.getAxisLeft().setAxisMaximum((float) upperOutliers);
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate(); // refresh
    }

    private class CustomComparator implements Comparator<OrderbookItem> {
        @Override
        public int compare(OrderbookItem o1, OrderbookItem o2) {
            return o1.getPrice().compareTo(o2.getPrice());
        }
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

