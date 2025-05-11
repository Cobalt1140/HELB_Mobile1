# Welcome to Cloud Functions for Firebase for Python!
# To get started, simply uncomment the below code or create your own.
# Deploy with `firebase deploy`

from firebase_functions import scheduler_fn
from firebase_admin import initialize_app, db
import requests
from datetime import datetime
import math
from firebase_functions import https_fn

from pytz import timezone as pytz_timezone

PARIS_TZ = pytz_timezone("Europe/Paris")





# Initialize Firebase Admin SDK
initialize_app()
    
    
def haversine(lat1, lon1, lat2, lon2):
    R = 6371000  # Radius of Earth in meters
    phi1 = math.radians(lat1)
    phi2 = math.radians(lat2)
    dphi = math.radians(lat2 - lat1)
    dlambda = math.radians(lon2 - lon1)
    a = math.sin(dphi/2)**2 + math.cos(phi1)*math.cos(phi2)*math.sin(dlambda/2)**2
    return R * 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))




@scheduler_fn.on_schedule(schedule="every day 08:00", region="europe-west1")
def daily_word_scheduler(event: scheduler_fn.ScheduledEvent) -> None:
    return run_daily_word_logic()


@https_fn.on_request(region="europe-west1")
def test_daily_word_scheduler(req: https_fn.Request) -> https_fn.Response:
    result = run_daily_word_logic()
    return https_fn.Response(f"Triggered manually: {result}")


def run_daily_word_logic():
    
    
    response = requests.get("https://trouve-mot.fr/api/random")
    data = response.json()
    word = data[0]["name"]

    ref = db.reference(f"dailyWord")
    ref.set({
        "word": word,
        "markerList": {}
    })

    print(f"Daily word for today set to '{word}'")
    
    
@https_fn.on_request(region="europe-west1")
def test_award_points_for_closeness(req: https_fn.Request) -> https_fn.Response:
    result = run_award_points_logic()
    return https_fn.Response(f"Triggered Manually: {result}")

    
@scheduler_fn.on_schedule(schedule="every day 18:00", region="europe-west1") 
def award_points_for_closeness(event: scheduler_fn.ScheduledEvent) -> None:
    return run_award_points_logic()



def run_award_points_logic():
    marker_ref = db.reference(f'dailyWord/markerList')
    markers_snapshot = marker_ref.get()

    if not markers_snapshot:
        users_ref = db.reference(f'userProfiles')
        users_snapshot = users_ref.get()
        for uid in users_snapshot.keys():
            user_ref = users_ref.child(uid)
            user_ref.child('dailyPoints').set(0)
        return "No markers found."

    markers = []
    
    for uid, data in markers_snapshot.items():
        if all(k in data for k in ("lat", "lng")): #check if "lat" and "lng" are both in marker/data
            markers.append({
                "uid": uid,
                "lat": data["lat"],
                "lng": data["lng"]
            })    

    #nbr_users = len(markers)
    user_scores = {}

    for i, marker in enumerate(markers):
        nearby = 0
        for j, other in enumerate(markers):
            if i == j:
                continue
            dist = haversine(marker['lat'], marker['lng'], other['lat'], other['lng'])
            if dist < 8:  # define how close in meters you have to be
                nearby += 1
        uid = marker['uid']
        user_scores[uid] = nearby #I could add something to make it less unfair if less players play a given day
        
    users_ref = db.reference(f'userProfiles')
    users_snapshot = users_ref.get()
    
    if not users_snapshot:
        return "No users found."
    

    
    for uid in users_snapshot.keys():
        user_ref = users_ref.child(uid)

        if uid not in user_scores:
            user_ref.child('dailyPoints').set(0)

        
        
    
    

    # Update each user's points
    for uid, points in user_scores.items():
        user_ref = db.reference(f'userProfiles/{uid}')
        
        # Lifetime points
        user_ref.child('pointTotal').transaction(lambda current: (current or 0) + points)
        
        # Daily points
        user_ref.child('dailyPoints').set(points)

    return f"Points awarded to {len(user_scores)} users."

