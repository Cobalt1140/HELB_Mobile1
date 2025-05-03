package com.example.helb_mobile1.main;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.helb_mobile1.TimeConfig;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;



public class MapViewModel extends ViewModel {

    private final LocalTime LOCATION_PUBLISH_TIME = LocalTime.of(TimeConfig.PUBLISH_TIME_HOUR,0);
    private final LocalTime NEW_WORD_TIME = LocalTime.of(TimeConfig.NEW_WORD_TIME_HOUR,0);

    private final MutableLiveData<Boolean> isPublishingTime = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isResultsTime = new MutableLiveData<>();
    
    private final MutableLiveData<List<MarkerOptions>> markers = new MutableLiveData<>(new ArrayList<>());
    private Location lastPictureLocation;

    public MapViewModel() {
        checkTimeAndUpdateUI();
    }

    private void checkTimeAndUpdateUI() {
        //TODO tbh this code kinda sucks
        LocalTime now = LocalTime.now();

        this.isPublishingTime.setValue(now.isAfter(NEW_WORD_TIME) &&
                now.isBefore(LOCATION_PUBLISH_TIME));
        this.isResultsTime.setValue(now.isBefore(NEW_WORD_TIME) || now.isAfter(LOCATION_PUBLISH_TIME));




    }

    public LiveData<Boolean> getIsPublishingTime() {
        return isPublishingTime;
    }

    public LiveData<Boolean> getIsResultsTime(){
        return isResultsTime;
    }

    public LiveData<List<MarkerOptions>> getMarkers() {
        return markers;
    }

    public void setLastPictureLocation(Location location) {
        this.lastPictureLocation = location;
    }

    public Location getLastPictureLocation() {
        return lastPictureLocation;
    }

    private void loadMarkersFromDatabase() {
        // TODO: Replace this with actual DB fetch logic (e.g. Firestore or Room)
        List<MarkerOptions> dummyMarkers = new ArrayList<>();
        dummyMarkers.add(new MarkerOptions().position(new LatLng(1.234, 5.678)).title("Sample Marker"));
        markers.setValue(dummyMarkers);
    }

    public void refreshUIBasedOnTime() {
        checkTimeAndUpdateUI();
    }
}
