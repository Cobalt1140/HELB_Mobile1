package com.example.helb_mobile1.main.map;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.helb_mobile1.managers.AuthManager;
import com.example.helb_mobile1.managers.DatabaseManager;
import com.example.helb_mobile1.managers.db_callbacks.IMarkerListCallback;
import com.example.helb_mobile1.managers.db_callbacks.IPersonalMarkerCallback;
import com.example.helb_mobile1.managers.db_callbacks.ISubmitMarkerCallback;
import com.example.helb_mobile1.managers.PreferencesManager;
import com.example.helb_mobile1.managers.TimeConfig;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MapViewModel extends ViewModel {

    private final LocalTime LOCATION_PUBLISH_TIME = LocalTime.of(TimeConfig.PUBLISH_TIME_HOUR,0);
    private final LocalTime NEW_WORD_TIME = LocalTime.of(TimeConfig.NEW_WORD_TIME_HOUR,0);

    private final MutableLiveData<Boolean> isPublishingTime = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isResultsTime = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isCameraVisible = new MutableLiveData<>();
    private final MutableLiveData<MarkerOptions> personalMarker = new MutableLiveData<>();
    private final MutableLiveData<List<MarkerOptions>> markers = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> notifLiveData = new MutableLiveData<>();
    private final PreferencesManager prefs;

    public MapViewModel(Context context) {
        this.prefs = PreferencesManager.getInstance(context.getApplicationContext());
        checkTimeAndHandleResults();
    }

    public void checkTimeAndHandleResults() {
        LocalTime now = LocalTime.now();

        this.isPublishingTime.setValue(now.isAfter(NEW_WORD_TIME) &&
                now.isBefore(LOCATION_PUBLISH_TIME));
        this.isResultsTime.setValue(now.isBefore(NEW_WORD_TIME) || now.isAfter(LOCATION_PUBLISH_TIME));
        if (Boolean.TRUE.equals(isResultsTime.getValue())){
            prefs.resetPersonalMarkerInCache();
            isCameraVisible.setValue(false);
            loadMarkersFromDatabase();
        } else if (Boolean.TRUE.equals(isPublishingTime.getValue())){
            setPersonalMarkerFromPrefsIntoMap();
            isCameraVisible.setValue(true);
        }
    }

    public LiveData<String> getNotifLiveData (){
        return notifLiveData;
    }
    public LiveData<List<MarkerOptions>> getMarkersLiveData() {
        return markers;
    }
    public LiveData<MarkerOptions> getPersonalMarkerLiveData(){
        return personalMarker;
    }
    public LiveData<Boolean> getIsCameraVisible(){return isCameraVisible;}

    public void setPersonalMarker(double lat, double lng){ //For use with camera in fragment
        if (Boolean.TRUE.equals(isPublishingTime.getValue())){

            DatabaseManager.getInstance().submitMarker(AuthManager.getInstance().getCurrentUid(),
                     lat,  lng, new ISubmitMarkerCallback() {
                @Override
                public void onError(String message) {
                    notifLiveData.setValue("Error: "+message);
                }

                @Override
                public void onSuccess() {
                    notifLiveData.setValue("Marker submitted!");
                    prefs.savePersonalMarkerInCache(lat,lng);
                    setPersonalMarkerFromPrefsIntoMap();
                }
            });
        } else {
            notifLiveData.setValue("Please take a picture during submission time");
        }
    }

    private void setPersonalMarkerFromPrefsIntoMap(){
        if (Boolean.TRUE.equals(isPublishingTime.getValue())){

            if (prefs.getCachedPersonalMarkerLng() != (double)0.0 && prefs.getCachedPersonalMarkerLat()!= (double)0.0){
                double lat = prefs.getCachedPersonalMarkerLat();
                double lng = prefs.getCachedPersonalMarkerLng();
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(lat, lng))
                        .title("My Marker")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                personalMarker.setValue(markerOptions);
            } else { //if nothing in prefs,
                DatabaseManager.getInstance().fetchAndHandlePersonalMarker(AuthManager.getInstance().getCurrentUid(), new IPersonalMarkerCallback() {
                    @Override
                    public void onUserMarkerFound(String markerId, Map<String, Object> markerData) {
                        double lat = (double) markerData.get(DatabaseManager.DB_MARKER_LAT);
                        double lng = (double) markerData.get(DatabaseManager.DB_MARKER_LNG);
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(new LatLng(lat, lng))
                                .title("My Marker")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        personalMarker.setValue(markerOptions);


                    }

                    @Override
                    public void onError(String error) {
                        notifLiveData.setValue(error);
                    }

                    @Override
                    public void onNoPersonalMarker() {
                        Log.d("idk","No Personal Marker Found");
                    }


                });

            }
        }
    }



    private void loadMarkersFromDatabase() {
        String uid = AuthManager.getInstance().getCurrentUid();
        DatabaseManager.getInstance().fetchAndHandleMarkerList(uid, new IMarkerListCallback() {
            @Override
            public void onMarkersFetched(Map<String, Map<String, Object>> allMarkers) {
                List<MarkerOptions> markerList = new ArrayList<>();
                for (Map.Entry<String, Map<String, Object>> entry : allMarkers.entrySet()) {
                    String uid = entry.getKey();
                    Map<String, Object> data = entry.getValue();

                    double lat = (Double) data.get(DatabaseManager.DB_MARKER_LAT);
                    double lng = (Double) data.get(DatabaseManager.DB_MARKER_LNG);
                    String username =(String) data.get(DatabaseManager.DB_USERNAME);

                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(new LatLng(lat, lng))
                            .title("User: " + username)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                    markerList.add(markerOptions);

                }
                markers.setValue(markerList);

            }

            @Override
            public void onUserMarkerFound(String uid, Map<String, Object> markerData) {
                double lat = (Double) markerData.get(DatabaseManager.DB_MARKER_LAT);
                double lng = (Double) markerData.get(DatabaseManager.DB_MARKER_LNG);
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(lat, lng))
                        .title("My Marker")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                personalMarker.setValue(markerOptions);

            }

            @Override
            public void onError(String error) {
                notifLiveData.setValue(error);
            }
        });

    }


}
