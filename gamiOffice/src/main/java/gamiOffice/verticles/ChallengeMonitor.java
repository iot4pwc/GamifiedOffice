package gamiOffice.verticles;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import gamiOffice.components.general.SecreteMidNightProcess;
import gamiOffice.components.helper.DBHelper;
import gamiOffice.constants.ConstLib;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChallengeMonitor extends AbstractVerticle{
	DBHelper dbHelper;
	ArrayList<String> challengeIds;
	Logger logger = LogManager.getLogger(ChallengeMonitor.class);

	@Override
	public void start(){
		vertx.executeBlocking(future -> {
			dbHelper = DBHelper.getInstance(ConstLib.GAMIFIED_OFFICE);
			future.complete();
		}, response -> {
			deployTimedTask();
			loadChallenges();
			if(challengeIds.isEmpty()){
				logger.info("No available challenge to deploy");
			}else{
				vertx.executeBlocking(future -> {
					for(String challengeId: challengeIds){
						vertx.deployVerticle(new ChallengeController(challengeId));
						logger.info("Deploying Controler for Challenge [" + challengeId + "]");
					}
					future.complete("All Challenge Controller Deployment complete");
					logger.info("All Challenge Controller Deployment complete");
				}, res -> {
					logger.info(res.result().toString());
				});
			}
		});

	}

	private void deployTimedTask() {
		logger.info("Deploying timer task");
		Timer timer = new Timer();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 30);
		calendar.set(Calendar.SECOND, 00);
		long scheduledtime = calendar.getTimeInMillis();
		long delaytime = scheduledtime - System.currentTimeMillis();
		timer.schedule(new SecreteMidNightProcess(), delaytime, ConstLib.PERIOD_DAY);
	}

	@Override
	public void stop(){

	}

	private void loadChallenges(){
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String query = "select distinct challenge_id from challenge where start_date < '" + 
				timeStamp + 
				"' and end_date > '"+ timeStamp +"';";
		List<JsonObject> result = dbHelper.select(query);
		ArrayList<String> res = new ArrayList<>();
		for (JsonObject aResult : result){
			res.add(aResult.getString("challenge_id"));
		}
		challengeIds = res;
	}

	public ArrayList<String> getOnGoingChallenges(){
		return this.challengeIds;
	}

}
