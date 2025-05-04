package com.example.helb_mobile1;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class DatabaseManager {
    private static DatabaseManager instance;


    public static final String DB_DAILY_WORDS = "dailyWords";
    public static final String DB_WORD = "word";
    public static final String DB_MARKER_LIST = "markerList";
    public static final String DB_USER_PROFILE = "userProfiles";
    public static final String DB_USERNAME = "username";
    public static final String DB_POINT_TOTAL = "pointTotal";
    public static final String DB_MARKER_LAT = "lat";
    public static final String DB_MARKER_LNG = "lng";
    public static final String DB_MARKER_TIMESTAMP = "timestamp";
    private final FirebaseDatabase db;
    /*
    DB Structure
    - userProfiles
        - <uid>
            - username <String>
            - pointTotal <int>
            - dailyPoints
                - <date> : <int>

    - dailyWords
        - <date>
            - word <String
            - markerList
                - <uid>
                    - lat
                    - lng
                    - timestamp
     */


    private DatabaseManager(){
        db = FirebaseDatabase.getInstance("https://helbmobile1-default-rtdb.europe-west1.firebasedatabase.app/");
    }

    public static synchronized DatabaseManager getInstance(){
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public void createUserProfile(String uid, String username) {
        //TODO check if user already exists and if username is already taken
        //also figure out how to link this and AuthManager
        DatabaseReference userRef = db.getReference(DB_USER_PROFILE).child(uid);

        Map<String, Object> userData = new HashMap<>();
        userData.put(DB_USERNAME, username);
        userData.put(DB_POINT_TOTAL, 0);
        userRef.setValue(userData);
    }

    public void handleIsUsernameTaken(String usernameToCheck, ValueEventListener listener) {
        DatabaseReference usersRef = db.getReference(DB_USER_PROFILE);
        usersRef.orderByChild(DB_USERNAME)
                .equalTo(usernameToCheck)
                .addListenerForSingleValueEvent(listener);
    }



    /*
    So this code should be on the server side but if I can't do that then you know I guess it's here
    public void storeDailyWord(String date, String word) {
        DatabaseReference wordRef = db.getReference(DB_DAILY_WORDS).child(date);
        wordRef.child(DB_WORD).setValue(word);
    }
     */

    public void submitMarker(String date, String uid, double lat, double lng) {
        DatabaseReference markerRef = db.getReference(DB_DAILY_WORDS)
                .child(date)
                .child(DB_MARKER_LIST)
                .child(uid); // Use UID as the key

        Map<String, Object> markerData = new HashMap<>();
        markerData.put(DB_MARKER_LAT, lat);
        markerData.put(DB_MARKER_LNG, lng);
        markerData.put(DB_MARKER_TIMESTAMP, System.currentTimeMillis());

        markerRef.setValue(markerData);
    }


    public void fetchAndHandleUserData(String uid, IUserDataCallback callback) {
        DatabaseReference userRef = db.getReference(DB_USER_PROFILE).child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.child(DB_USERNAME).getValue(String.class);
                Long points = snapshot.child(DB_POINT_TOTAL).getValue(Long.class);
                callback.onUserDataReceived(username, points != null ? points : 0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error);
            }
        });
    }

    public void fetchAndHandleMarkerList(String date, String targetUid, IMarkerListCallback callback){
        DatabaseReference markersRef = db.getReference(DB_DAILY_WORDS).child(date)
                .child(DB_MARKER_LIST);
        markersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) { //snapshot is list of all markers
                Map<String, Map<String, Object>> allMarkers = new HashMap<>();
                for (DataSnapshot markerSnap : snapshot.getChildren()) { //foreach marker
                    String uid = markerSnap.getKey();
                    Map<String, Object> markerData = new HashMap<>();
                    markerData.put(DB_MARKER_LAT, markerSnap.child(DB_MARKER_LAT).getValue(Double.class));
                    markerData.put(DB_MARKER_LNG, markerSnap.child(DB_MARKER_LNG).getValue(Double.class));
                    markerData.put(DB_MARKER_TIMESTAMP, markerSnap.child(DB_MARKER_TIMESTAMP).getValue(Long.class));
                    allMarkers.put(uid, markerData);

                    if (uid != null && uid.equals(targetUid)) {
                        callback.onUserMarkerFound(uid, markerData);
                    }
                }
                //TODO depending on how I'm gonna manage the callbacks, I could move this method to be called for every marker instead of for all markers simultaneously
                callback.onMarkersFetched(allMarkers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error);
            }
        });
    }

    public void fetchAndHandleDailyWord(String date, IDailyWordCallback callback){
        //TODO update the way to fetch the daily word (from directly from the API to fetching it from the server)
        DatabaseReference wordRef = db.getReference(DB_DAILY_WORDS).child(date);
        wordRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String dailyWord = snapshot.child(DB_WORD).getValue(String.class);
                callback.onDailyWordFound(dailyWord);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error);
            }
        });

    }

}
