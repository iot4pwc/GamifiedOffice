package gamiOffice.components.general;

import java.util.*;

import gamiOffice.components.activities.Activity;

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
  }

  User(String name, String email, String employeeid, String alias){
    this.Name = name;
    this.Email = email;
    this.EmployeeId = employeeid;
    this.setAlias(alias);
    this.ActivityMap = new HashMap<Activity, List<String>>();
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

}
