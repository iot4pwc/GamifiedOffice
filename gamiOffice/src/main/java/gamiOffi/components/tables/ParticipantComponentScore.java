/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamiOffi.components.tables;

import io.vertx.core.json.JsonObject;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Tarun
 */
public class ParticipantComponentScore extends Queriable {

  public static final String tableName = "participant_component_score";
  public static final String partCompId = "participant_id";
  public static final String componentId = "challenge_id";
  public static final String email = "email";
  public static final String totalScore = "total_score";
  public static final String todayScore = "today_score";
  public static final String yesterdayScore = "yesterday_score";
  public static final String lastWeekScore = "last_week_score";
  public static final String lastMonthScore = "last_month_score";
  
  private static Challenge tableInstance;
  
  public static Challenge getInstance() {
    if (tableInstance == null) {
      return new Challenge();
    } else {
      return tableInstance;
    }
  }
  
  @Override
  public String getTableName() {
    return this.tableName;
  }

  @Override
  public void configureInsertPstmt(PreparedStatement pstmt, JsonObject recordObject, List<String> attributeNames) {
    int counter = 1;
    try {
      for (String attributeName : attributeNames) {
          switch (attributeName) {
            case partCompId:
            case componentId:
            case totalScore:
            case todayScore:
            case yesterdayScore:
            case lastWeekScore:
            case lastMonthScore:
              pstmt.setDouble(counter++, recordObject.getLong(attributeName));
              break;
            case email:
              pstmt.setString(counter++, recordObject.getString(attributeName));
              break;
        }
      }
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }
  
}
