package com.nbossard.packlist.gui;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.nbossard.packlist.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class FollowMeFragment extends Fragment {

    private Socket socket;
    private BufferedReader input;
    private BufferedWriter output;
    private TextView speedTextView;
    private SeekBar speedSlider;
    private GraphView speedGraphView;
    private LineGraphSeries<DataPoint> series;
    private Button autonomousButton;
    private Button leftButton, forwardButton, rightButton, backwardButton,stopButton;
    private boolean isAutonomousMode = false;
    private double lastXValue = 0;
    private int lastSpeed = 0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_followme, container, false);

        speedTextView = view.findViewById(R.id.speedTextView);
        speedSlider = view.findViewById(R.id.speedSlider);
        speedGraphView = view.findViewById(R.id.speedGraphView);

        leftButton = view.findViewById(R.id.leftButton);
        forwardButton = view.findViewById(R.id.forwardButton);
        rightButton = view.findViewById(R.id.rightButton);
        backwardButton = view.findViewById(R.id.backwardButton);
        autonomousButton = view.findViewById(R.id.autonomousButton);
        stopButton = view.findViewById(R.id.stopButton);

        setupButtonListener(leftButton, "left");
        setupButtonListener(forwardButton, "forward");
        setupButtonListener(rightButton, "right");
        setupButtonListener(backwardButton, "backward");
        setupButtonListener(stopButton, "stop");
        setupAutonomousButtonListener();

        series = new LineGraphSeries<>();
        speedGraphView.addSeries(series);
        speedGraphView.getViewport().setXAxisBoundsManual(true);
        speedGraphView.getViewport().setMinX(0);
        speedGraphView.getViewport().setMaxX(100);
        speedGraphView.getViewport().setYAxisBoundsManual(true);
        speedGraphView.getViewport().setMinY(0);
        speedGraphView.getViewport().setMaxY(100);
        speedGraphView.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        speedGraphView.getGridLabelRenderer().setVerticalLabelsVisible(false);
        speedGraphView.setTitle("Speed Over Time");
        speedGraphView.setTitleColor(Color.BLACK);
        speedGraphView.setTitleTextSize(24);

        speedSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speedTextView.setText("Speed: " + progress + "%");
                sendCommand("set_speed," + progress);
                updateGraph(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // No action required
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // No action required
            }
        });

        // Start a separate thread to update the graph continuously
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(100); // Adjust the delay as needed
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateGraph(lastSpeed);
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        return view;
    }

    private void setupButtonListener(Button button, final String command) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAutonomousMode) {
                    sendCommand(command);
                }
            }
        });
    }

    private void setupAutonomousButtonListener() {
        autonomousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAutonomousMode = !isAutonomousMode;
                if (isAutonomousMode) {
                    autonomousButton.setText("Manual");
                    leftButton.setEnabled(false);
                    leftButton.setBackgroundColor(Color.parseColor("#808080"));
                    forwardButton.setEnabled(false);
                    forwardButton.setBackgroundColor(Color.parseColor("#808080"));
                    rightButton.setEnabled(false);
                    rightButton.setBackgroundColor(Color.parseColor("#808080"));
                    backwardButton.setEnabled(false);
                    backwardButton.setBackgroundColor(Color.parseColor("#808080"));
                    sendCommand("autonomous");
                } else {
                    autonomousButton.setText("Auto");
                    leftButton.setEnabled(true);
                    leftButton.setBackground(getResources().getDrawable(R.drawable.rounded_button));
                    forwardButton.setEnabled(true);
                    forwardButton.setBackground(getResources().getDrawable(R.drawable.rounded_button));
                    rightButton.setEnabled(true);
                    rightButton.setBackground(getResources().getDrawable(R.drawable.rounded_button));
                    backwardButton.setEnabled(true);
                    backwardButton.setBackground(getResources().getDrawable(R.drawable.rounded_button));
                    sendCommand("manual");
                }
            }
        });
    }

    private void sendCommand(String command) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Replace with the IP address of your Raspberry Pi
                    socket = new Socket("192.168.168.177", 8000);
                    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    output.write(command + "\n");
                    output.flush();
                    final String response = input.readLine();

                    // Check if the Fragment is still attached to an Activity

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

    private void updateGraph(int speed) {
        lastXValue += 1;
        lastSpeed = speed;
        series.appendData(new DataPoint(lastXValue, speed), true, 100);
        speedGraphView.onDataChanged(true, true);
    }
}