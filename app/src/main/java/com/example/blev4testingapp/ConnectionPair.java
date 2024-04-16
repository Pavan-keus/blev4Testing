package com.example.blev4testingapp;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class ConnectionPair extends Fragment {
    Button connect;
    Button pair;
    CardView card;
    private TextView bleAddress,bleName,rssi;
    connectionCommuniation communication;
    private String Tag= "appOutput";


    public interface  connectionCommuniation{
        void sendDataToActivityFromConnection(Object[] data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            communication = (connectionCommuniation) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_connection_pair, container, false);
        connect = view.findViewById(R.id.cp_connect);
        pair = view.findViewById(R.id.cp_pair);
        rssi = (TextView) view.findViewById(R.id.cp_rssi);
        bleName = (TextView) view.findViewById(R.id.cp_name);
        bleAddress = (TextView) view.findViewById(R.id.cp_bleAddress);
        card = view. findViewById(R.id.card1);
        card.setVisibility(View.INVISIBLE);
        connect.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Object[] data = new Object[1];
                connect.setBackgroundColor(connect.getContext().getResources().getColor(R.color. gray));
                connect.setTextColor(Color.BLACK);
                if(connect.getText().equals("connect")== true){
                    data[0] = new Integer(1);
                    communication.sendDataToActivityFromConnection(data);
                }
                else{
                    data[0] = new Integer(2);
                    Log.d(Tag,"disconnected");
                    communication.sendDataToActivityFromConnection(data);

                }
                connect.setClickable(false);

            }
        });
        pair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(Tag,"pair clicked");
                if(pair.getText().equals("paired") == true){
                    Toast.makeText(getActivity(),"Already Paired",Toast.LENGTH_SHORT).show();
                    return;
                }
                pair.setBackgroundColor(pair.getContext().getResources().getColor(R.color. gray));
                pair.setTextColor(Color.BLACK);
                pair.setClickable(false);
                Object[] data = new Object[1];
                data[0] = new Integer(3);
                communication.sendDataToActivityFromConnection(data);
            }
        });
        return view;
    }
    void ReceiveDataFromActivity(Object[] data){
        if(card.getVisibility() == View.INVISIBLE){
            card.setVisibility(View.VISIBLE);
        }
         if((Integer)data[0] == 1){
                rssi.setText((String)data[1]);
                bleName.setText((String)data[2]);
                bleAddress.setText((String)data[3]);
         }
         else if((Integer)data[0] ==2){
              connect.setBackgroundColor(connect.getContext().getResources().getColor(R.color.green));
              connect.setTextColor(Color.WHITE);
              connect.setText("Disconnect");
             connect.setClickable(true);
         }
         else if((Integer)data[0]==3){
             connect.setBackgroundColor(connect.getContext().getResources().getColor(R.color.normal));
             connect.setText("connect");
             connect.setTextColor(Color.WHITE);
             connect.setClickable(true);
         }
         else if((Integer)data[0] == 4){
             // device not connected or pair failed
             pair.setBackgroundColor(pair.getContext().getResources().getColor(R.color.normal));
             pair.setText("pair");
             pair.setTextColor(Color.WHITE);
             pair.setClickable(true);
         }
         else if((Integer)data[0] == 5){
             // device is paired
             pair.setText("paired");
             pair.setTextColor(Color.WHITE);
             pair.setBackgroundColor(pair.getContext().getResources().getColor(R.color.green));
             pair.setClickable(true);
         }
    }
}