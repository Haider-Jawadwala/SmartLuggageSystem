package com.nbossard.packlist.gui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nbossard.packlist.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class GPSFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    private GoogleMap googleMap;
    private Marker marker;
    private MarkerOptions markerOptions;
    private FloatingActionButton currentLocationFab;
    private MapView mapView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gps, container, false);

        mapView = view.findViewById(R.id.map_fragment);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        currentLocationFab = view.findViewById(R.id.current_location_fab);
        currentLocationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocationFromServer();
            }
        });

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        markerOptions = new MarkerOptions().title("Location");

        // Show a random initial location on the map
        LatLng randomLocation = getRandomLocation();
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(randomLocation, 10));
        this.googleMap.setOnMapLoadedCallback(this);
    }

    @Override
    public void onMapLoaded() {
        // Map loaded, you can do additional setup here if needed
    }

    private LatLng getRandomLocation() {
        Random random = new Random();
        double latitude = random.nextDouble() * 180 - 90; // Random latitude between -90 and 90
        double longitude = random.nextDouble() * 360 - 180; // Random longitude between -180 and 180
        return new LatLng(latitude, longitude);
    }

    private void getLocationFromServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Replace with the IP address of your Raspberry Pi
                    String ipAddress = "192.168.168.177";
                    int port = 8000;

                    // Establish socket connection with Raspberry Pi
                    Socket socket = new Socket(ipAddress, port);
                    PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    // Send the "get_location" command to the server
                    output.println("get_location");

                    String locationData = input.readLine();
                    if (locationData != null) {
                        String[] latLng = locationData.split(",");
                        double latitude = Double.parseDouble(latLng[0]);
                        double longitude = Double.parseDouble(latLng[1]);

                        // Update the marker position on the UI thread
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LatLng location = new LatLng(latitude, longitude);
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15));

                                // Update the marker position
                                markerOptions.position(location);

                                // Remove the previous marker, if any
                                if (marker != null) {
                                    marker.remove();
                                }

                                // Add the marker to the map with animation
                                marker = googleMap.addMarker(markerOptions);
                                marker.showInfoWindow();
                            }
                        });
                    }

                    // Clean up resources
                    output.close();
                    input.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}