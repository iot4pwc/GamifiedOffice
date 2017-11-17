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
public class Challenge extends Queriable {

  public static final String tableName = "challenge";
  public static final String challengeId = "challenge_id";
  public static final String startDate = "start_date";
  public static final String endDate = "end_date";
  
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
            case challengeId:
              pstmt.setLong(counter++, recordObject.getLong(attributeName));
              break;
            case startDate:
            case endDate:
              pstmt.setString(counter++, recordObject.getString(attributeName));
              break;
        }
      }
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }
}
