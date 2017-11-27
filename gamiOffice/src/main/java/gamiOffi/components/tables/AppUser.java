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
public class AppUser extends Queriable {

  public static final String tableName = "app_user";
  public static final String email = "email";
  public static final String name = "name";
  public static final String profilePic = "profile_pic";
  public static final String alias = "alias";
  public static final String regDate = "reg_date";
  public static final String age = "age";
  public static final String shareFlag = "share_flag";
  public static final String competeFlag = "compete_flag";
  
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
            case age:
              pstmt.setLong(counter++, recordObject.getLong(attributeName));
              break;
            case email:
            case profilePic:
            case name:
            case alias:
            case regDate:
            case shareFlag:
            case competeFlag:
              pstmt.setString(counter++, recordObject.getString(attributeName));
              break;
        }
      }
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }
  
}
