package com.nbossard.packlist.gui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.nbossard.packlist.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.DecimalFormat;

public class WeightSensingFragment extends Fragment {

    private Socket socket;
    private BufferedReader input;
    private BufferedWriter output;
    private TextView weightTextView;
    private ArcProgress weightGauge;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weight, container, false);

        weightTextView = view.findViewById(R.id.weightTextView);
        Button measureWeightButton = view.findViewById(R.id.measureWeightButton);
        weightGauge = view.findViewById(R.id.weightGauge);

        // Set up the gauge properties
        weightGauge.setMax(100);
        weightGauge.setTextSize(70);
        weightGauge.setFinishedStrokeColor(getResources().getColor(R.color.colorPrimary));
        weightGauge.setUnfinishedStrokeColor(getResources().getColor(R.color.colorAccent));

        measureWeightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // Replace with the IP address of your Raspberry Pi
                            socket = new Socket("192.168.168.177", 8000);
                            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                            output.write("measure_weight");
                            output.flush();
                            final String response = input.readLine();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(response!=null) {
                                    double weight = Double.parseDouble(response);
                                    if(weight<=0){
                                        weight = 0;
                                    }
                                    double kg = weight/1000;
                                    kg = Math.round(kg * 100.0) / 100.0;
                                    weightTextView.setText(kg+"kg");
                                    double per = (weight/20000)*100;
                                    weightGauge.setProgress((int) per);
                                    }
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (input != null) input.close();
                                if (output != null) output.close();
                                if (socket != null) socket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });

        return view;
    }
}