package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.myapplication.MeasureActivity;
import com.example.myapplication.R;

public class Home extends Fragment {
    public Home(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        Button measureButton = (Button)view.findViewById(R.id.measureButton);
        getActivity().setTitle("측정");
        measureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent measureIntent = new Intent(getActivity(), MeasureActivity.class);
                startActivity(measureIntent);
            }
        });
        return view;
    }


}