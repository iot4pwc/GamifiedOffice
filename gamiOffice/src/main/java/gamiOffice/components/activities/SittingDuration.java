package gamiOffice.components.activities;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import gamiOffice.components.general.Challenge;
import gamiOffice.components.general.User;
import gamiOffice.components.helper.DBHelper;
import gamiOffice.constants.ConstLib;
import io.vertx.core.json.JsonObject;

public class SittingDuration extends Activity{
	
	private static SittingDuration sittingDuration;
	public static final String COMPONENT_CODE = "SITTING_DURATION";
	//key should always be user email
	public Map<String, Integer> durations;
	public Map<String, Long> startTime;
	public Map<String, String> status;

	public SittingDuration(){
		this.ActivityName = COMPONENT_CODE;
		this.topicSet.add(ConstLib.TOPIC_SITTING);
		this.topicSet.add(ConstLib.TOPIC_LIGHT);
		this.GainPerUnit = -0.1;
		loadDurations();
	}
	
	//this will do some more stuff
	private void loadDurations(){
		durations = new HashMap<String, Integer>();
		startTime = new HashMap<String, Long>();
		status = new HashMap<String, String>();
		String query = "select * from sitting_status;";
		List<JsonObject> result = DBHelper.getInstance("gamified_office").select(query);
		for (JsonObject aResult: result) {
			System.out.println(aResult.toString());
			String email = aResult.getString("email");
			Timestamp ts = Timestamp.valueOf(aResult.getString("start_time"));
			long tsTime = ts.getTime();
			durations.put(email, Integer.parseInt(aResult.getString("duration")));
			startTime.put(email, tsTime);
			status.put(email, aResult.getString("status"));
		}
	}

	@Override
	public void updateScore(Challenge challenge, JsonObject payload) {
		// TODO Auto-generated method stub
		//{“timestamp”:1511430000000,“sensor_pk_id”:“59efd0f33a8fd80d3372a7dd”,“value_key”:“value”,“value_content”:“0”}
		if(payload.getString("value_key").equals("value")){
			//locate the corresponding user
			String targetUser = "";
			String sensor = payload.getString("sensor_pk_id");
			System.out.println(this.getClass().getName() + " processing sitting sensor: " + sensor);
			Set<Entry<String, User>> users= challenge.getUserList().entrySet();
			for(Entry<String, User> user : users){
				List<String> sensors = user.getValue().getActivitySensor(this.getName());
				if(sensors.size() > 0 && sensors.contains(sensor)){
					targetUser =  user.getKey();
					break;
				}
			}
			System.out.println("targetUser: " + targetUser);
			if(!targetUser.equals("")){
				startTime.putIfAbsent(targetUser, 0l);
				durations.putIfAbsent(targetUser, 0);
				status.putIfAbsent(targetUser, "0");
				//check if the status is different;
				String currStatus = payload.getString("value_content").equals("0") ? "0" : "1";
				System.out.println(this.getClass().getName() + " current status is : " + currStatus);
				String lastStatus = status.get(targetUser);
				if(!currStatus.equals(lastStatus)){
					long timeStamp = payload.getLong("timestamp");
					//if changed, calculate incremental score and update start time / durations of the day
					//1 --> 0 current duration ends
					//the default value for sitting of the day is 1
					if(currStatus.equals("0")){
						int durationInMinutes = (int) ((timeStamp - startTime.get(targetUser)) / 6000);
						durations.put(targetUser, durations.get(targetUser)+durationInMinutes);
						String query = "update sitting_status set duration = "+ durations.get(targetUser) +" where email = '"+ targetUser +"';";
						DBHelper.getInstance(ConstLib.GAMIFIED_OFFICE).update(query);
						System.out.println(this.getClass().getName()+ " new duration saved to database");
						//calculate the score
						System.out.println(this.getClass().getName() + "new period of sitting detected on user: " + targetUser + " for " + durationInMinutes + " minutes");
						challenge.setScore(targetUser, this.getName(), this.GainPerUnit*durationInMinutes);
						System.out.println("incremental score: " + this.GainPerUnit*durationInMinutes);
					}else{
						//0 --> 1 sitting starts
						startTime.put(targetUser, timeStamp);
						String query = "update sitting_status set start_time = "+ timeStamp +" where email = '"+ targetUser +"';";
						DBHelper.getInstance(ConstLib.GAMIFIED_OFFICE).update(query);
						System.out.println(this.getClass().getName()+ " new startTime saved to database");
					}
					//update the latest status
					status.put(targetUser, currStatus);
					String query = "update sitting_status set status = '"+ currStatus +"' where email = '"+ targetUser +"';";
					DBHelper.getInstance(ConstLib.GAMIFIED_OFFICE).update(query);
					System.out.println(this.getClass().getName()+ " new status saved to database");
				}
			}
		}
	}

	public static Activity getInstance() {
		if(SittingDuration.sittingDuration == null){
			SittingDuration.sittingDuration = new SittingDuration();
		}
		return SittingDuration.sittingDuration;
	}
	
}
