package com.example.stopsmoking.mainFragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.stopsmoking.ConnectActivity;
import com.example.stopsmoking.R;
import com.example.stopsmoking.data.UserData;

public class MeasureFragment extends Fragment {
    Button measureBtn;
    UserData user;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            user = getArguments().getParcelable("OBJECT");
//            Toast.makeText(getContext(),user.getName()+"  "+user.getUserId(),Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.fragment_measure, container,false);

        AnimationDrawable anim = (AnimationDrawable) view.findViewById(R.id.process).getBackground();
        anim.start();


        measureBtn = (Button) view.findViewById(R.id.btn_measure);
        measureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ConnectActivity.class);
                intent.putExtra("OBJECT",user);
                startActivity(intent);
            }
        });
        return view;
    }
}