package com.example.helb_mobile1.main.map;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.example.helb_mobile1.main.AppViewModelFactory;
import com.example.helb_mobile1.R;
import com.example.helb_mobile1.main.IOnFragmentVisibleListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationTokenSource;


public class MapFragment extends Fragment implements OnMapReadyCallback, IOnFragmentVisibleListener {
    /*
    TODO
    add out of bounds checks, hide and show the camera button when appropriate,
    show daily markers once submissions are over, add a center on campus button
     */

    private GoogleMap myMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private MapViewModel mapViewModel;
    private Button cameraRedirectButton;
    private ActivityResultLauncher<String> locationPermissionLauncher;
    private ActivityResultLauncher<Intent> cameraActivityLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        AppViewModelFactory factory = new AppViewModelFactory(requireContext());
        mapViewModel = new ViewModelProvider(this, factory).get(MapViewModel.class);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }


        cameraRedirectButton = view.findViewById(R.id.map_redirect_camera);
        cameraActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK ) {
                        CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
                        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.getToken())
                                    .addOnSuccessListener(location -> {
                                        if (location != null) {

                                            double lat = location.getLatitude();
                                            double lng = location.getLongitude();
                                            Toast.makeText(requireActivity(), "Lat:"+lat+" Lng:"+lng,Toast.LENGTH_SHORT).show();
                                            myMap.clear();
                                            mapViewModel.setPersonalMarker(lat,lng);
                                        }
                                    });
                        }
                    }
                }
        );

        cameraRedirectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(requireActivity().getPackageManager()) != null){
                    cameraActivityLauncher.launch(intent);
                }
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

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            myMap.setMyLocationEnabled(true);
        }
        observeViewModel();
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void observeViewModel(){
        mapViewModel.getPersonalMarkerLiveData().observe(getViewLifecycleOwner(), marker -> {
            if (marker != null){
                myMap.clear();
                myMap.addMarker(marker);
            }
        });
        mapViewModel.getMarkersLiveData().observe(getViewLifecycleOwner(), markerOptions -> {
            if (!markerOptions.isEmpty()){

                for (MarkerOptions marker : markerOptions){
                    myMap.addMarker(marker);
                }
            }
        });


        mapViewModel.getNotifLiveData().observe(getViewLifecycleOwner(), notif ->{
            if (notif != null){
                Toast.makeText(requireActivity(), notif, Toast.LENGTH_SHORT).show();
            }
        });
        mapViewModel.getIsCameraVisible().observe(getViewLifecycleOwner(), isCameraVisible ->{
            if (isCameraVisible){
                cameraRedirectButton.setVisibility(View.VISIBLE);
            } else {
                cameraRedirectButton.setVisibility(View.INVISIBLE);
            }
        });



    }

    @Override
    public void onFragmentVisible() {
        checkLocationPermission();
        mapViewModel.checkTimeAndHandleResults();

    }
}