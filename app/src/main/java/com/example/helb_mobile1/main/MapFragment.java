package com.example.helb_mobile1.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.helb_mobile1.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;


public class MapFragment extends Fragment implements OnMapReadyCallback, OnFragmentVisibleListener{
    /*
    TODO
    add out of bounds checks, hide and show the camera button when appropriate,
    show daily markers once submissions are over, add a center on campus button, add zoom buttons.
     */

    private GoogleMap myMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    private MapViewModel mapViewModel;
    private ActivityResultLauncher<String> locationPermissionLauncher;
    private ActivityResultLauncher<Intent> cameraActivityLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mapViewModel = new ViewModelProvider(requireActivity()).get(MapViewModel.class);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        cameraActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
                        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.getToken())
                                    .addOnSuccessListener(location -> {
                                        if (location != null) {
                                            mapViewModel.setLastPictureLocation(location);
                                            Toast.makeText(requireActivity(),location.toString(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }
                }
        );



        Button cameraRedirectButton = view.findViewById(R.id.map_redirect_camera);
        cameraRedirectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), CameraActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                cameraActivityLauncher.launch(intent);
            }
        });

        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Toast.makeText(requireContext(), "Location permission granted", Toast.LENGTH_SHORT).show();
                        if (myMap != null){
                            myMap.setMyLocationEnabled(true);
                        }
                    } else {
                        Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        checkLocationPermission();

        return view;
    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        /*
        LatLng currentLatLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(currentLatLng).title("My Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        myMap.addMarker(markerOptions);
        myMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
        */


        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            myMap.setMyLocationEnabled(true);
        }
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }



    @Override
    public void onFragmentVisible() {
        checkLocationPermission();

    }
}