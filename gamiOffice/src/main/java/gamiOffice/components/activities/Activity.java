package gamiOffice.components.activities;

import gamiOffice.components.general.User;

public abstract class Activity {
	//store general info of current activity
	String ActivityName;
	String GainPerUnit;
	//String topic 

	//?
	int UnitOfMeasure;
	
	//for individual activity
	public abstract double calculateScore(User u);
	
	//for group activity
	public abstract double calculateScore();

}
