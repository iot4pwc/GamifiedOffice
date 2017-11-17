package gamiOffice.components.activities;

import java.util.List;

import gamiOffice.components.general.User;

public abstract class Activity {
	//store general info of current activity
	String ActivityName;
	String GainPerUnit;
	List<String> topicSet; 

	//?
	int UnitOfMeasure;
	
	//for individual activity
	public abstract double calculateScore(User u);
	
	//for group activity
	public abstract double calculateScore();

}
