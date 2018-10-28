package com.fekrah.driver.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fekrah.driver.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class TawselFragment extends Fragment {


    @BindView(R.id.from_location)
    TextView from;

    @BindView(R.id.to_location)
    TextView to;

    @BindView(R.id.distance_location)
    TextView distance;

    @BindView(R.id.cost_location)
    TextView cost;

    public TawselFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_tawsel, container, false);
        ButterKnife.bind(this, mainView);

        return mainView;
    }

    public void change(String receiver, String arrival, float result, int costkm) {
        float finalDistance = 0f;
        int distance1 = Math.round(result);
        float smal = result - ((float) distance1);
        String dis = String.valueOf(smal);
        if (dis.length() >= 2) {
            char s = dis.charAt(2);
            float newsmallDistance = (float) (s / 10);
            finalDistance = ((float) distance1) + newsmallDistance;
        }

        float km = finalDistance / 1000.0f;
        from.setText(receiver);
        to.setText(arrival);
        cost.setText(String.valueOf(km * costkm + getString(R.string.omla)));
        distance.setText(String.valueOf(km) + getString(R.string.k_m));
        //  distanceText.setText(getString(R.string.order_detail_1) +" "+ arrival + getString(R.string.order_detail_2)+" " + receiver + getString(R.string.order_detail_3)+" " + km + getString(R.string.order_detail_4) + getString(R.string.order_detail_5)+" " + "8 "+getString(R.string.order_detail_6));
    }
}
