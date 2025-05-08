package com.example.helb_mobile1.managers;

import androidx.annotation.NonNull;

import com.example.helb_mobile1.managers.db_callbacks.ICurrentTimeCallback;
import com.example.helb_mobile1.managers.db_callbacks.IDailyWordCallback;
import com.example.helb_mobile1.managers.db_callbacks.ILeaderboardCallback;
import com.example.helb_mobile1.managers.db_callbacks.IMarkerListCallback;
import com.example.helb_mobile1.managers.db_callbacks.ISubmitMarkerCallback;
import com.example.helb_mobile1.managers.db_callbacks.IUserDataCallback;
import com.example.helb_mobile1.managers.db_callbacks.IUsernameCallback;
import com.example.helb_mobile1.models.UserScore;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.time.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseManager {

    public static final String DB_USERNAME = "username";
    public static final String DB_MARKER_LAT = "lat";
    public static final String DB_MARKER_LNG = "lng";


    private static final String DB_DAILY_WORD = "dailyWord";
    private static final String DB_WORD = "word";
    private static final String DB_MARKER_LIST = "markerList";
    private static final String DB_USER_PROFILE = "userProfiles";
    private static final String DB_POINT_TOTAL = "pointTotal";
    private static final String DB_POINT_DAILY = "dailyPoints";
    private static final String DB_MARKER_TIMESTAMP = "timestamp";
    private static final double CENTER_POINT_BOUNDARY_LAT = 50.81973292056924; // Example: Brussels
    private static final double CENTER_POINT_BOUNDARY_LNG = 4.399286094121766;
    private static final double BOUNDARY_MAX_DISTANCE = 350.0; // Allowed range
    private static final String DB_URL = "https://helbmobile1-default-rtdb.europe-west1.firebasedatabase.app/";


    private final FirebaseDatabase db;
    private static DatabaseManager instance;
    /*
    DB Structure
    - userProfiles
        - <uid>
            - username <String>
            - pointTotal <int>
            - dailyPoints <int>


    - dailyWord
        - word <String>
        - markerList
            - <uid>
                - username
                - lat
                - lng
                - timestamp
     */


    private DatabaseManager(){
        db = FirebaseDatabase.getInstance(DB_URL);
    }

    public static synchronized DatabaseManager getInstance(){
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public void createUserProfile(String uid, String username) {
        DatabaseReference userRef = db.getReference(DB_USER_PROFILE).child(uid);

        //TODO make this into a model? debatable
        Map<String, Object> userData = new HashMap<>();
        userData.put(DB_USERNAME, username);
        userData.put(DB_POINT_TOTAL, 0);
        userRef.setValue(userData);
    }
    //TODO add a delete UserProfile function

    public void handleIsUsernameTaken(String usernameToCheck, ValueEventListener listener) {
        DatabaseReference usersRef = db.getReference(DB_USER_PROFILE);
        usersRef.orderByChild(DB_USERNAME)
                .equalTo(usernameToCheck)
                .addListenerForSingleValueEvent(listener);
    }


    public void submitMarker(String uid, double lat, double lng, ISubmitMarkerCallback callback) {
        handleIfWithinTimeWindow(TimeConfig.NEW_WORD_TIME_HOUR, TimeConfig.PUBLISH_TIME_HOUR, new ICurrentTimeCallback() {
            @Override
            public void onTimeCheckFailed(String message) {
                callback.onError("Problem checking time on server: "+message);
            }

            @Override
            public void onWithinTimeWindow() {
                double distance = haversineDistance(lat, lng, CENTER_POINT_BOUNDARY_LAT, CENTER_POINT_BOUNDARY_LNG);
                if (distance > BOUNDARY_MAX_DISTANCE) {
                    callback.onError("You are outside the allowed submission area, please go closer to the Plaine Campus.\nDistance from center of campus: " + (int) distance + "m");
                    return;
                }
                fetchAndHandleUsernameWithUid(uid, new IUsernameCallback() {
                    @Override
                    public void onSuccess(String username) {

                        DatabaseReference markerRef = db.getReference(DB_DAILY_WORD)
                                .child(DB_MARKER_LIST)
                                .child(uid); // Use UID as the key

                        Map<String, Object> markerData = new HashMap<>();
                        markerData.put(DB_MARKER_LAT, lat);
                        markerData.put(DB_MARKER_LNG, lng);
                        markerData.put(DB_USERNAME, username);
                        markerData.put(DB_MARKER_TIMESTAMP, System.currentTimeMillis());

                        markerRef.setValue(markerData)
                                .addOnSuccessListener(task -> callback.onSuccess())
                                .addOnFailureListener(e -> callback.onError(e.getMessage()));
                    }

                    @Override
                    public void onError(String error) {
                        callback.onError("Failed to get username: " + error);
                    }
                });
            }

            @Override
            public void onOutsideTimeWindow(int time) {
                callback.onError("Outside of time window (8h-18h): "+time);
            }
        });

    }


    public void fetchAndHandleAccountData(String uid, IUserDataCallback callback) {
        DatabaseReference userRef = db.getReference(DB_USER_PROFILE).child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.child(DB_USERNAME).getValue(String.class);
                long points = snapshot.child(DB_POINT_TOTAL).getValue(Long.class);
                callback.onUserDataReceived(username, points);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    public void fetchAndHandleLeaderboard(Boolean isTrueDailyFalseGlobal, ILeaderboardCallback callback){
        DatabaseReference usersRef = db.getReference(DB_USER_PROFILE);

        String dailyOrGlobal;
        if (isTrueDailyFalseGlobal){ //true for Daily
            dailyOrGlobal = DB_POINT_DAILY;
        } else { // false for Global
            dailyOrGlobal = DB_POINT_TOTAL;
        }
        usersRef.orderByChild(dailyOrGlobal)
                .limitToLast(25)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<UserScore> userScores = new ArrayList<>();

                        for (DataSnapshot userSnap : snapshot.getChildren()) {
                            String username = userSnap.child(DB_USERNAME).getValue(String.class);
                            Long score = userSnap.child(dailyOrGlobal).getValue(Long.class);

                            if (username != null) {
                                userScores.add(new UserScore(username, score != null ? score : 0));
                            }
                        }
                        Collections.reverse(userScores);
                        callback.onSuccess(userScores);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                        callback.onError(error.getMessage());
                    }
                });
    }

    public void fetchAndHandleMarkerList(String targetUid, IMarkerListCallback callback){
        handleIfWithinTimeWindow(TimeConfig.PUBLISH_TIME_HOUR, TimeConfig.NEW_WORD_TIME_HOUR, new ICurrentTimeCallback() {
            @Override
            public void onTimeCheckFailed(String message) {
                callback.onError("Couldn't verify current server time: "+message);
            }

            @Override
            public void onWithinTimeWindow() {
                DatabaseReference markersRef = db.getReference(DB_DAILY_WORD).child(DB_MARKER_LIST);
                markersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) { //snapshot is list of all markers
                        //TODO make this into a model
                        Map<String, Map<String, Object>> allMarkers = new HashMap<>();
                        for (DataSnapshot markerSnap : snapshot.getChildren()) { //foreach marker
                            String uid = markerSnap.getKey();
                            Map<String, Object> markerData = new HashMap<>();
                            markerData.put(DB_MARKER_LAT, markerSnap.child(DB_MARKER_LAT).getValue(Double.class));
                            markerData.put(DB_MARKER_LNG, markerSnap.child(DB_MARKER_LNG).getValue(Double.class));
                            markerData.put(DB_MARKER_TIMESTAMP, markerSnap.child(DB_MARKER_TIMESTAMP).getValue(Long.class));
                            markerData.put(DB_USERNAME, markerSnap.child(DB_USERNAME).getValue(String.class));
                            if (uid != null && uid.equals(targetUid)) {
                                callback.onUserMarkerFound(uid, markerData);
                            } else {
                                allMarkers.put(uid, markerData);
                            }
                        }
                        callback.onMarkersFetched(allMarkers);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError("Couldn't access database correctly: "+error.getMessage());
                    }
                });
            }

            @Override
            public void onOutsideTimeWindow(int time) {
                callback.onError("Outside time window ("+TimeConfig.PUBLISH_TIME_HOUR+"h-"+
                        TimeConfig.NEW_WORD_TIME_HOUR+"h)\nCurrent hour: "+time+"h");
            }
        });

    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // Radius of the Earth in meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in meters
    }

    private void fetchAndHandleUsernameWithUid(String uid, IUsernameCallback callback){
        DatabaseReference userRef = db.getReference(DB_USER_PROFILE).child(uid);
        userRef.child(DB_USERNAME).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.getValue(String.class);
                if (username != null) {
                    callback.onSuccess(username);
                } else {
                    callback.onError("Username not found for UID: " + uid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError("Couldn't access database correctly: "+error.getMessage());
            }
        });
    }

    private void handleIfWithinTimeWindow(int startHour, int endHour, ICurrentTimeCallback callback) {
        DatabaseReference serverTimeRef = db.getReference("serverTime");

        serverTimeRef.setValue(ServerValue.TIMESTAMP);
        serverTimeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long utcMillis = snapshot.getValue(Long.class);
                if (utcMillis == null) {
                    callback.onTimeCheckFailed("Server time fetch failed");
                    return;
                }

                ZonedDateTime belgianTime = Instant.ofEpochMilli(utcMillis)
                        .atZone(ZoneId.of(TimeConfig.SERVER_TIMEZONE));

                int currentHour = belgianTime.getHour();

                boolean inWindow;
                if (startHour < endHour) {
                    inWindow = currentHour >= startHour && currentHour < endHour;
                } else {
                    inWindow = currentHour >= startHour || currentHour < endHour;
                }

                if (inWindow) {
                    callback.onWithinTimeWindow();
                } else {
                    callback.onOutsideTimeWindow(currentHour);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onTimeCheckFailed(error.getMessage());
            }
        });
    }


    public void fetchAndHandleDailyWord(IDailyWordCallback callback){
        DatabaseReference wordRef = db.getReference(DB_DAILY_WORD);
        wordRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String dailyWord = snapshot.child(DB_WORD).getValue(String.class);
                callback.onDailyWordFound(dailyWord);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });

    }

}
