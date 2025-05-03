package com.example.helb_mobile1;

import com.google.firebase.database.DatabaseError;

import java.util.Map;

public interface IMarkerListCallback {
    void onMarkersFetched(Map<String, Map<String, Object>> allMarkers);
    void onUserMarkerFound(String markerId, Map<String, Object> markerData);
    void onError(DatabaseError error);
}
