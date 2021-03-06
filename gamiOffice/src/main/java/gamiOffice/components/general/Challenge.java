package gamiOffice.components.general;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.*;
import gamiOffice.components.activities.Activity;
import gamiOffice.components.activities.SittingDuration;
import gamiOffice.components.activities.WaterIntake;
import gamiOffice.constants.ConstLib;
import gamiOffice.components.helper.DBHelper;
import io.vertx.core.json.JsonObject;

public class Challenge {
  Map<String, Activity> activities;
  Map<String, Double> weights;
  Map<String, User> users;
  Map<String, HashMap<String, Double>> totalScores;
  Map<String, HashMap<String, Double>> todayScores;

  //	Map<Activity, Double> Weight;
  //	Map<User, Double> EmployeeInvolved;

  String ChallengeId;
  String ChallengeName;
  Date ChallengeStartDate;
  Date ChallengeEndDate;

  public Challenge(String name, String id){
    this.ChallengeName = name;
    //?or can be auto set
    this.ChallengeId = id;

    this.activities = new HashMap<String, Activity>();
    this.weights = new HashMap<String, Double>();
    this.users = new HashMap<String, User>();
    this.totalScores = new HashMap<String, HashMap<String, Double>>();
    this.todayScores = new HashMap<String, HashMap<String, Double>>();

  }

  public void setScore(String userEmail, String activityName, double scoreToAdd){
  	System.out.println(this.getClass().getName()+" setscore() called on " + userEmail + " " + activityName + " " + scoreToAdd);
  	double scoreToday = Double.sum(todayScores.get(userEmail).get(activityName), scoreToAdd);
  	System.out.println(this.getClass().getName()+" old todayScore" + todayScores.get(userEmail).get(activityName) + " new todayScore" + scoreToday);
  	todayScores.get(userEmail).put(activityName,scoreToday);
  	double scoreTotal = Double.sum(totalScores.get(userEmail).get(activityName), scoreToAdd);
  	System.out.println(this.getClass().getName()+" new totalScore" + scoreTotal);
  	totalScores.get(userEmail).put(activityName,scoreTotal);
  	System.out.println(this.getClass().getName()+" score all updated");
  	
  	//update the user score in the database
  	String query = "update participant_component_score set total_score = " + scoreTotal + 
  			", today_score = "+ scoreToday +
  			" where component_code = '"+ activityName +
  			"' and email = '"+ userEmail +"';";
  	boolean res = DBHelper.getInstance("gamified_office").update(query);
  	if(!res){
  		System.out.println("Update Failed on query: " + query);
  	}
  	
  }
  
  public boolean enroll (String user_id, User user) {
    if (!users.containsKey(user_id)) {
      HashMap<String, Double> tempActivitiesScores = new HashMap<String, Double>();
      for (String activity_id: activities.keySet()) {
        tempActivitiesScores.put(activity_id, ConstLib.CHALLENGE_INITIAL_SCORE);
      }
      totalScores.put(user_id, tempActivitiesScores);
      return false; //@TODO discuss w/ Lydia
    }
    return true; //@TODO discuss w/ Lydia
  }

  //this part can be hardcode currently
  public boolean registerActivity(){
    return true;
  }

  public double getScore (String user_id) {
    Map<String, Double> tempActivitiesScores = totalScores.get(user_id);
    Double tempScore = 0.0;

    for (Entry<String, Double> anEntry: tempActivitiesScores.entrySet()) {
      tempScore += anEntry.getValue() * weights.get(anEntry.getKey());
    }

    return tempScore;
  }

  public double getScore (String user_id, String activitiy_id) {
    return totalScores.get(user_id).get(activitiy_id);
  }

  public List<Entry<User, Double>> getRank() { 
    Map<User, Double> tempUserScores = new HashMap<User, Double>();

    for (String user_id: totalScores.keySet()) {
      tempUserScores.put(users.get(user_id), getScore(user_id));
    }

    List<Map.Entry<User, Double>> records = new ArrayList<Entry<User, Double>>(tempUserScores.entrySet());
    Collections.sort(records, (o1, o2) -> o1.getValue().compareTo(o2.getValue()));

    // Testing
    System.out.println(records.toString());

    return records;
  }

  public void setWeight() {
    //read from a JsonString in directory when called and populate the map
    List<JsonObject> result = DBHelper.getInstance("gamified_office").select(
        "SELECT component_code, component_weight " +
            "FROM challenge " +
            "JOIN challenge_component USING (challenge_id) " +
            "WHERE challenge_id = '" + this.ChallengeId + "';");

    for (JsonObject aResult : result) {
      this.weights.put(aResult.getString("component_code"), Double.valueOf(aResult.getString("component_weight")));

      switch (aResult.getString("component_code")) {
        case SittingDuration.COMPONENT_CODE:
          activities.put(aResult.getString("component_code"), new SittingDuration());
          break;
        case WaterIntake.COMPONENT_CODE:
          activities.put(aResult.getString("component_code"), new WaterIntake());
          break;
        default:
          System.out.println("Activity not found, no corresponding component_code");
      }
    }
  }

  public Map<String, Activity> getActivities(){
  	return this.activities;
  }
  //  public void setWeight(String filename) {
  //    //read from a static file in directory when called and populate the map 
  //  }

  public Map<String, User> getUserList(){
  	return this.users;
  }
  public void setEmployeeInvolved() {

    // Get user information first.
    List<JsonObject> result = DBHelper.getInstance("gamified_office").select(
        "SELECT app_user.email, app_user.name, app_user.alias, challenge_component.component_weight, challenge_component.component_code, participant.total_score, participant.today_score "
            + "FROM app_user "
            + "JOIN participant USING (email) "
            + "JOIN challenge_component USING (challenge_id) "
            + "JOIN participant_component_score USING (component_id) "
            + "WHERE challenge_id = '" + this.ChallengeId + "';");



    // Set today's scores 
    for (JsonObject aResult: result) {
    	System.out.println(aResult.toString());
      if (todayScores.containsKey(aResult.getString("email"))) {
        this.todayScores.get(aResult.getString("email")).put(aResult.getString("component_code"), Double.valueOf(aResult.getString("today_score")));
      } else {
        this.todayScores.put(aResult.getString("email"), new HashMap<>());
        this.todayScores.get(aResult.getString("email")).put(aResult.getString("component_code"), Double.valueOf(aResult.getString("today_score")));
      }

      // Set total scores
      if (totalScores.containsKey(aResult.getString("email"))) {
        this.totalScores.get(aResult.getString("email")).put(aResult.getString("component_code"), Double.valueOf(aResult.getString("today_score")));
      } else {
        this.totalScores.put(aResult.getString("email"), new HashMap<>());
        this.totalScores.get(aResult.getString("email")).put(aResult.getString("component_code"), Double.valueOf(aResult.getString("today_score")));
      }

      // Set user profile
      if (!users.containsKey(aResult.getString("email"))) {
        this.users.put(aResult.getString("email"), 
            new User(
                aResult.getString("name"), 
                aResult.getString("email"), 
                aResult.getString("email"), 
                aResult.getString("alias")));
      }
    }

    
    for (JsonObject aResult: result) {
    	//System.out.println(aResult.toString());
      this.weights.put(aResult.getString("component_code"), Double.valueOf(aResult.getString("component_weight")));

      switch (aResult.getString("component_code")) {
        case SittingDuration.COMPONENT_CODE:
          activities.put(aResult.getString("component_code"), new SittingDuration());
          break;
        case WaterIntake.COMPONENT_CODE:
          activities.put(aResult.getString("component_code"), new WaterIntake());
          break;
        default:
          System.out.println("Activity not found, no corresponding component_code");
      }
    }
  }
}
