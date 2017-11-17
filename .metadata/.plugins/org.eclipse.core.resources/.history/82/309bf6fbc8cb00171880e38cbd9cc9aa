package gamiOffice.components.general;

import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.*;

import gamiOffice.components.activities.Activity;

public class Challenge {
	private static final double INITIAL_SCORE = 0.0;
	Map<Activity, Double> Weight;
	Map<User, Double> EmployeeInvolved;
	
	String ChallengeId;
	String ChallengeName;
	Date ChallengeStartDate;
	Date ChallengeEndDate;
	
	Challenge(String name, String id){
		this.ChallengeName = name;
		//?or can be auto set
		this.ChallengeId = id;
		this.Weight = new HashMap<Activity, Double>();
		this.EmployeeInvolved = new HashMap<User, Double>();
	}
	
	public boolean enroll(User user){
		for(Activity act : Weight.keySet()){
			if(!user.ActivityMap.containsKey(act)){
				EmployeeInvolved.put(user, INITIAL_SCORE);
				return false;
			}
		}
		return true;
	}
	
	//this part can be hardcode currently
	public boolean registerActivity(){
		return true;
	}
	
	public double getScore(User user){
		return EmployeeInvolved.get(user);
	}
	
	public List<Entry<User, Double>> getRank(){
		List<Entry<User, Double>> records = new LinkedList<Entry<User, Double>>(EmployeeInvolved.entrySet());
		Collections.sort(records, new Comparator<Entry<User, Double>>(){

			@Override
			public int compare(Entry<User, Double> e1, Entry<User, Double> e2) {
				return (int) (e2.getValue() - e1.getValue());
			}
			
		});
		
		return records;
	}
	
	public void setWeight(){
		//read from a static file in directory when called and populate the map
		
	}
}
