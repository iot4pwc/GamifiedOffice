package gamiOffice.components.general;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import gamiOffice.components.helper.DBHelper;
import gamiOffice.constants.ConstLib;
import io.vertx.core.json.JsonObject;

public class SecreteMidNightProcess extends TimerTask{

	
	@Override
	public void run() {
		System.out.println(this.getClass().getName() + " Midnight process start...");
		if(resetSittingStatus()){
			System.out.println(this.getClass().getName() + " sitting_status reset done!");
		}
		if(insertNewRecord()){
			System.out.println(this.getClass().getName() + " today score reset done!");
		}
		System.out.println(this.getClass().getName() + " Midnight process end...");
	}

	private boolean insertNewRecord() {
		//get the next date
		Date myday = new Date();               
		SimpleDateFormat formattedDate = new SimpleDateFormat("yyyy-MM-dd");            
		Calendar c = Calendar.getInstance();        
		c.add(Calendar.DATE, 1);  // number of days to add   
		String today = (String)(formattedDate.format(myday.getTime()));
		String tomorrow = (String)(formattedDate.format(c.getTime()));
		//System.out.println("Tomorrows date is " + tomorrow);
		List<JsonObject> result = DBHelper.getInstance(ConstLib.GAMIFIED_OFFICE).select(""
				+ "select * from participant_component_score where score_date = '"+today+"'");
		StringBuilder query = new StringBuilder();
		query.append("insert into participant_component_score (component_id, component_code, participant_id, score_date, score, email) values ");
		for(JsonObject aResult : result){
			String row = "(" + Integer.valueOf(aResult.getString("component_id")) + ", '"+ aResult.getString("component_code") + 
					"', " + Integer.valueOf(aResult.getString("participant_id")) + ", '"+ tomorrow + 
					"', 0.0, '"+ aResult.getString("email") +"'),";
			query.append(row);
		}
		query.setLength(query.length() - 1);
		query.append(";");
		//System.out.println(query.toString());
		return DBHelper.getInstance(ConstLib.GAMIFIED_OFFICE).update(query.toString());
	}
	
	private boolean resetSittingStatus(){
		//get tomorrow time
		SimpleDateFormat formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");            
		Calendar c = Calendar.getInstance();        
		c.add(Calendar.DATE, 1);  // number of days to add  
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 00);
		c.set(Calendar.SECOND, 00);
		String tomorrow = (String)(formattedDate.format(c.getTime()));
		return DBHelper.getInstance(ConstLib.GAMIFIED_OFFICE).update("update sitting_status set duration = 0, start_time = '"+ tomorrow +"', status = '0';");
	}

}
