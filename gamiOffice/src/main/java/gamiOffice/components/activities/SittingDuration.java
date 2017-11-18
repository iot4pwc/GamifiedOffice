package gamiOffice.components.activities;

import gamiOffice.components.general.Challenge;
import gamiOffice.components.general.User;
import io.vertx.core.json.JsonObject;

public class SittingDuration extends Activity{

	public static final String COMPONENT_CODE = "SittingDuration";
	
	public SittingDuration(){
		this.ActivityName = COMPONENT_CODE;
	}

	@Override
	public void updateScore(Challenge challenge, JsonObject payload) {
		// TODO Auto-generated method stub
		
	}
	
}
