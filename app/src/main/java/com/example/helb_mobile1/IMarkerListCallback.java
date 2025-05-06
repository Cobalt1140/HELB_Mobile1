package com.example.helb_mobile1;

import java.util.Map;

public interface IMarkerListCallback {
    void onMarkersFetched(Map<String, Map<String, Object>> allMarkers);
    void onUserMarkerFound(String markerId, Map<String, Object> markerData);
    void onError(String error);
}
