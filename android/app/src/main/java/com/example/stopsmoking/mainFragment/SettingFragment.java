package com.example.stopsmoking.mainFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.stopsmoking.R;
import com.example.stopsmoking.subFragment.TimePickerFragment;
import com.example.stopsmoking.subFragment.TimePickerFragment2;

public class SettingFragment extends Fragment {
    private Button timeButton1;
    private Button timeButton2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        timeButton1 = (Button)view.findViewById(R.id.timeButton1);
//        timeButton2 = (Button)view.findViewById(R.id.timeButton2);

        timeButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getActivity().getFragmentManager(), "TimePicker1");
            }
        });

//        timeButton2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                TimePickerFragment2 timePickerFragment2 = new TimePickerFragment2();
//                timePickerFragment2.show(getActivity().getFragmentManager(), "TimePicker2");
//            }
//        });

        return view;
    }
}
