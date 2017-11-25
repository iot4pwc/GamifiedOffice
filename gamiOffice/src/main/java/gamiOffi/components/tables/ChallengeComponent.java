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
public class ChallengeComponent extends Queriable {

  public static final String tableName = "challenge_component";
  public static final String componentId = "component_id";
  public static final String challengeId = "challenge_id";
  public static final String componentCode = "component_code";
  public static final String componentWeight = "component_weight";
  public static final String componentDesc = "component_desc";
  
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
            case componentId:
            case challengeId:
            case componentWeight:
              pstmt.setDouble(counter++, recordObject.getLong(attributeName));
              break;
            case componentCode:
            case componentDesc:
              pstmt.setString(counter++, recordObject.getString(attributeName));
              break;
        }
      }
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }
  
}
