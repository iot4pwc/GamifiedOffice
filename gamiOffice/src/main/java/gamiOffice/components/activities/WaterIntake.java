package gamiOffice.components.activities;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import gamiOffice.components.general.Challenge;
import gamiOffice.components.general.User;
import io.vertx.core.json.JsonObject;

public class WaterIntake extends Activity{

	public static final String COMPONENT_CODE = "WATER_INTAKE";
	
	public WaterIntake(){
		this.ActivityName = COMPONENT_CODE;
		this.GainPerUnit = 1.0;
	}

	@Override
	public void updateScore(Challenge challenge, JsonObject payload) {
		// TODO Auto-generated method stub
		//payload format: {"timestamp":1511025600000,"sensor_pk_id":"59f11b9e3a8fd80d33e14e7c","value_key":"Tag","value_content":"9025298"}
		
		//step1: finding the user corresponding the payload
		String targetUser = "";
		String id = payload.getString("value_content");
		System.out.println(this.getClass().getName() + "processing rfid: " + id);
		Set<Entry<String, User>> users= challenge.getUserList().entrySet();
		for(Entry<String, User> user : users){
			List<String> sensors = user.getValue().getActivitySensor(this.getName());
			if(sensors.size() > 0 && sensors.contains(id)){
				targetUser =  user.getKey();
				break;
			}
		}
		
		//step2: calculate and update the score
		System.out.println(this.getClass().getName() + "found corresponding user: " + targetUser);
		if(!targetUser.equals("")){
			challenge.setScore(targetUser, this.getName(), this.GainPerUnit*1);
		}
	}
	

}
