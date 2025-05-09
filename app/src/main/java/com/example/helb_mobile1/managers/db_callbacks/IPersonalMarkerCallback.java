package com.example.helb_mobile1.managers.db_callbacks;

import java.util.Map;

public interface IPersonalMarkerCallback {
    void onUserMarkerFound(String markerId, Map<String, Object> markerData);
    void onError(String error);
    void onNoPersonalMarker();
}
