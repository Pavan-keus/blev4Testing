package com.example.blev4testingapp;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class OfflineCommissioning extends Fragment {
    offlineComissioningCommuniation communication;
    Button pair;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            communication = (offlineComissioningCommuniation) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }
    public interface  offlineComissioningCommuniation{
        void sendDataToActivityFromOfflineCommunication(String data);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_offline_commissioning, container, false);
        pair = view.findViewById(R.id.co_pair);
        pair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }
    void ReceiveDataFromActivity(String data){

    }
}