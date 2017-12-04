package gamiOffice.verticles;
import java.util.LinkedList;
import java.util.List;

import gamiOffice.components.general.Challenge;
import gamiOffice.components.helper.DBHelper;
import gamiOffice.components.activities.Activity;
import gamiOffice.constants.ConstLib;
import io.vertx.core.AbstractVerticle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChallengeController extends AbstractVerticle{
	Challenge challenge;
	DBHelper dbHelper;
	String challengeId;
	Logger logger = LogManager.getLogger(ChallengeController.class);
	
	public ChallengeController(String challengeId){
		this.challengeId = challengeId;
	}
	
	@Override
	public void start(){
		//get DB connections
		vertx.executeBlocking(future -> {
			dbHelper = DBHelper.getInstance(ConstLib.GAMIFIED_OFFICE);
			future.complete();
		}, response -> {
			//initialize the challenge
			challenge = loadChallenge(challengeId);
			List<String> activities = new LinkedList<>(challenge.getActivities());
			//deploy activity controller for each of the activity
			vertx.executeBlocking(future -> {
	            // first, retrieve token
				for(String activity: activities){
					vertx.deployVerticle(new ActivityController(challenge, activity));
					logger.info("Deploying Controler for activity [" + activity + "]");
				}
				future.complete("All Activity Controller Deployment complete");
				logger.info("All Activity Controller Deployment complete");
			}, res -> {
				logger.info(res.result().toString());
	        });
		});
	}
	
	@Override
	public void stop(){
		
	}
	
	private Challenge loadChallenge(String Id){
		//retrieve the information based on challengeId
		Challenge c = new Challenge("some challenge", Id);
		c.setWeight();
		c.setEmployeeInvolved();
		return c;
	}

}
