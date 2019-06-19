package com.santosh.facialrecognition;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TakeAttendanceFragment extends Fragment {

    private final String PREF_NAME = "FR_SP";
    Button select_btn,list_btn;
    EditText period;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_take_attendance, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        doJob(view);
    }

    private void doJob(View view) {
        select_btn = view.findViewById(R.id.take_att_btn);
        list_btn = view.findViewById(R.id.take_att_list);
        period = view.findViewById(R.id.take_att_period);

        select_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),FaceTrackerActivity.class));
            }
        });

        list_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(),ListActivity.class);
                i.putExtra("period",period.getText().toString());
                startActivity(i);
                //todo:class period number selection code
            }
        });


    }
}