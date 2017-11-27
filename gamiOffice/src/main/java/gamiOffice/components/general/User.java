package gamiOffice.components.general;

import java.util.*;
import java.util.Map.Entry;

import gamiOffice.components.activities.Activity;
import gamiOffice.components.activities.SittingDuration;
import gamiOffice.components.activities.WaterIntake;
import gamiOffice.components.helper.DBHelper;
import io.vertx.core.json.JsonObject;

public class User {
  //Employee basic information
  private String EmployeeId;
  private String Name;
  private String Email;
  private String Alias;

  //Map storing the activity the employee is enrolled and the corresponding sensor id
  Map<Activity, List<String>> ActivityMap;
  /**
   * @param String name - name of employee
   * @param String email - email of employee
   * @param String employeeid - id of employee
   * 
   * The name, email and id can only be set when initialize*/
  User(String name, String email, String employeeid){
    this.Name = name;
    this.Email = email;
    this.EmployeeId = employeeid;
    this.setAlias("Not Specified");
    this.ActivityMap = new HashMap<Activity, List<String>>();
    populateActivity();
  }

  User(String name, String email, String employeeid, String alias){
    this.Name = name;
    this.Email = email;
    this.EmployeeId = employeeid;
    this.setAlias(alias);
    this.ActivityMap = new HashMap<Activity, List<String>>();
    populateActivity();
  }

  /**
   * Getter method for all parameters
   * */
  public String getEmploeeId() {
    return EmployeeId;
  }

  public String getName() {
    return Name;
  }

  public String getEmail() {
    return Email;
  }

  public String getAlias() {
    return Alias;
  }

  /**
   * Setter method for Alias
   * */
  public void setAlias(String alias) {
    Alias = alias;
  }

  public List<String> getActivitySensor(String activityName){
    for(Entry<Activity, List<String>> e : ActivityMap.entrySet()){
      if(e.getKey().getName().equals(activityName)){
        return e.getValue();
      }
    }

    return new ArrayList<String>();
  }

  public void populateActivity(){
    ArrayList<String> sensors = new ArrayList<>();


    // Get user information first.
    List<JsonObject> result = DBHelper.getInstance("gamified_office").select(
        "SELECT challenge_component.component_code, user_component_sensor.sensor_value "
            + "FROM  challenge_component "
            + "JOIN user_component_sensor "
            + "USING (component_code) "
            + "WHERE user_component_sensor.email = '" + this.Email + "';");
    
    System.out.println(result);

    for (JsonObject aResult: result) {
      Activity anActivity = null;
      switch (aResult.getString("component_code")) {
        case WaterIntake.COMPONENT_CODE:
          anActivity = new WaterIntake();
          break;
        case SittingDuration.COMPONENT_CODE:
          anActivity = new SittingDuration();
          break;
        default:
          System.out.println("No matching activity found.");
          break;
      }

      if (anActivity != null) {
        List<String> mappedSensors;
        if (!ActivityMap.containsKey(anActivity)) {
          mappedSensors = new LinkedList<String>();
        } else {
          mappedSensors = ActivityMap.get(anActivity);
        }
        mappedSensors.add(aResult.getString("sensor_value"));
        this.ActivityMap.put(anActivity, mappedSensors);
      }
    }
    System.out.println(ActivityMap.keySet().toString());
    System.out.println(ActivityMap.values().toString());
  }
}
