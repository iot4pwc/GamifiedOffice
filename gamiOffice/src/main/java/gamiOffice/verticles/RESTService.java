package gamiOffice.verticles;

import gamiOffice.components.helper.DBHelper;
import gamiOffice.constants.ConstLib;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RESTService extends AbstractVerticle {

  private DBHelper dbHelper;
  Logger logger = LogManager.getLogger(RESTService.class);

  /**
   * Perform the start routine for this verticle:
   * 1. Initialize the dbHelper instance
   * 2. Create restful endpoints, and link to corresponding handlers.
   * 3. Begin listening to HTTP_SERVER_PORT.
   */
  @Override
  public void start() {
    Router router = Router.router(vertx);

    vertx.executeBlocking(future -> {
      dbHelper = DBHelper.getInstance(ConstLib.GAMIFIED_OFFICE);
      future.complete();
    }, response -> {
      /**
       * input the restful endpoint here
       */
      //Filter
      router.route("/*").handler(this::filter);

      router.route().handler(BodyHandler.create());
      router.post("/:" + ConstLib.USERNAME_URL_PATTERN + "/login").handler(this::login);
      router.get("/:" + ConstLib.USERNAME_URL_PATTERN + "/profile").handler(this::getProfile);
      router.post("/:" + ConstLib.USERNAME_URL_PATTERN + "/profile").handler(this::updateProfile);
      router.get("/:" + ConstLib.CHALLENGE_URL_PATTERN + "/:" + ConstLib.USERNAME_URL_PATTERN + "/getRanking").handler(this::getUserStatsInChallenge);
      router.get("/:" + ConstLib.CHALLENGE_URL_PATTERN + "/getRanking").handler(this::getChallengeStats);
      router.get("/:" + ConstLib.USERNAME_URL_PATTERN + "/getRecentStats").handler(this::getUserRecentStats);
      router.get("/:" + ConstLib.USERNAME_URL_PATTERN + "/getFullStats").handler(this::getUserFullStats);

      //add any addtional routers below
      vertx.createHttpServer().requestHandler(router::accept).listen(ConstLib.HTTP_SERVER_PORT);
      logger.info("RESTful service running on port " + ConstLib.HTTP_SERVER_PORT);
    });

  }

  /**
   * Close the dbHelper resources.
   */
  @Override
  public void stop() {
    dbHelper.closeDatasource();
  }

  /**
   * Filter all the incoming requests which do not contain the secret header key-value pair.
   * Pass the request if valid header is found. Otherwise, send a 400 response code.
   * @param routingContext 
   */
  private void filter(RoutingContext routingContext) {
    MultiMap headers = routingContext.request().headers();
    if (headers.contains(ConstLib.REQUIRED_HEADER_KEY)) {
      if (headers.get(ConstLib.REQUIRED_HEADER_KEY).equals(ConstLib.REQUIRED_HEADER_VALUE)) {
        routingContext.next();
      } else {
        routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .setStatusCode(400)
                .end();
      }
    } else {
      routingContext.response()
              .putHeader("content-type", "application/json; charset=utf-8")
              .setStatusCode(400)
              .end();
    }
  }

  /**
   * Handler to process the login request.
   * Must contain username in the url, and password in the request body.
   * If valid credentials, send 200 OK along with all the current challenges which the user has been enrolled into. 
   * Otherwise, 400 Bad Request.
   * @param routingContext 
   */
  private void login(RoutingContext routingContext) {
    JsonObject body = routingContext.getBodyAsJson();
    String username = routingContext.request().getParam(ConstLib.USERNAME_URL_PATTERN);
    String password = body.getString(ConstLib.PASSWORD_FIELD);

    boolean flag = checkCredentials(username, password);

    if (flag) {
      //TODO: Get all related challanges for the given user.
      JsonObject allChallenges = new JsonObject();
      JsonArray allIndividualChallenges = new JsonArray();
      List<JsonObject> challenges = getAllChallenges(username);
      for (JsonObject challenge : challenges) {
        allIndividualChallenges.add(challenge);
      }
      allChallenges.put("all_challenges", allIndividualChallenges);

      routingContext.response()
              .setStatusCode(200)
              .end(allChallenges.encodePrettily());

    } else {
      routingContext.response()
              .setStatusCode(400)
              .end();
    }
  }

  /**
   * Handler to get the profile information for a user.
   * Query the database and send the retrieved information in the response body.
   * If the user is not found, send back 404 Not Found.
   * @param routingContext 
   */
  private void getProfile(RoutingContext routingContext) {
    String username = routingContext.request().getParam(ConstLib.USERNAME_URL_PATTERN);

    JsonObject profile = getUserProfile(username);

    if (profile != null) {
      profile.put("profileImage", profile.getString("profile_pic"));
      profile.remove("profile_pic");
      logger.info("################################");
      logger.info(profile);
      logger.info("################################");

      routingContext.response()
              .putHeader("content-type", "application/json; charset=utf-8")
              .setStatusCode(200)
              .end(profile.encodePrettily());
    } else {
      routingContext.response()
              .setStatusCode(404)
              .end();
    }
  }

  /**
   * Handler for the POST request to update a user profile.
   * The request body must contain all the information, whether or not it has been changed.
   * If update is successful, send back 200 OK. Otherwise, send back 400 Bad Request.
   * @param routingContext 
   */
  private void updateProfile(RoutingContext routingContext) {
    String username = routingContext.request().getParam(ConstLib.USERNAME_URL_PATTERN);
    JsonObject body = routingContext.getBodyAsJson();

    boolean isSuccess = updateProfile(username, body);

    if (isSuccess) {
      routingContext.response()
              .setStatusCode(200)
              .end();
    } else {
      routingContext.response()
              .setStatusCode(400)
              .end();
    }
  }

  /**
   * Handler to get a user's statistics for a particular challenge.
   * URL must contain both the user email and the challenge id.
   * Sends back the user's rank, total score, and score by components for the particular challenge.
   * @param routingContext 
   */
  private void getUserStatsInChallenge(RoutingContext routingContext) {
    String challenge = routingContext.request().getParam(ConstLib.CHALLENGE_URL_PATTERN);
    String username = routingContext.request().getParam(ConstLib.USERNAME_URL_PATTERN);

    JsonObject rank = getUserStatsInChallenge(challenge, username);

    if (rank != null) {
      routingContext.response()
              .putHeader("content-type", "application/json; charset=utf-8")
              .setStatusCode(200)
              .end(rank.encodePrettily());
    } else {
      routingContext.response()
              .setStatusCode(404)
              .end();
    }
  }

  /**
   * Handler to get the challenge statistics.
   * URL must contain challenge_id.
   * Sends back a JSON with details about all the participants of the challenge.
   * The details include their rank in the challenge, and their total and component-wise score in the challenge.
   * @param routingContext 
   */
  private void getChallengeStats(RoutingContext routingContext) {
    String challenge = routingContext.request().getParam(ConstLib.CHALLENGE_URL_PATTERN);

    JsonObject rank = getChallengeStats(challenge);
    if (rank != null) {
      routingContext.response()
              .putHeader("content-type", "application/json; charset=utf-8")
              .setStatusCode(200)
              .end(rank.encodePrettily());
    } else {
      routingContext.response()
              .setStatusCode(404)
              .end();
    }

  }

  /**
   * Handler to get a user's recent statistics.
   * URL must contain the user's email.
   * Sends back the user's rank and today's score, both total and component-wise. 
   * @param routingContext 
   */
  private void getUserRecentStats(RoutingContext routingContext) {
    String username = routingContext.request().getParam(ConstLib.USERNAME_URL_PATTERN);

    JsonObject rank = getUserRecentStats(username);
    if (rank != null) {
      routingContext.response()
              .putHeader("content-type", "application/json; charset=utf-8")
              .setStatusCode(200)
              .end(rank.encodePrettily());
    } else {
      routingContext.response()
              .setStatusCode(404)
              .end();
    }

  }

   /**
   * Handler to get a user's full statistics.
   * URL must contain the user's email.
   * Sends back the user's rank and his various aggregated scores.
   * Aggregated scores contain today's, yesterday's, last week's, and last month's scores.
   * Both total and component-wise scores are sent back for each level of aggregation.
   * @param routingContext 
   */
  private void getUserFullStats(RoutingContext routingContext) {
    String username = routingContext.request().getParam(ConstLib.USERNAME_URL_PATTERN);

    JsonObject rank = getUserFullStats(username);
    if (rank != null) {
      routingContext.response()
              .putHeader("content-type", "application/json; charset=utf-8")
              .setStatusCode(200)
              .end(rank.encodePrettily());
    } else {
      routingContext.response()
              .setStatusCode(404)
              .end();
    }
  }

  private boolean checkCredentials(String username, String password) {
    String query = "SELECT * FROM app_user WHERE email='"+username+"'";
    List<JsonObject> result = dbHelper.select(query);
    if (result.size() > 0 && "password".equals(password)) {
      return true;
    }
    return false;
  }

  private List<JsonObject> getAllChallenges(String username) {
    String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    String query = "SELECT DISTINCT challenge_id, challenge_name "
            + "FROM participant "
            + "JOIN challenge USING (challenge_id) "
            + "WHERE email = '" + username + "' "
            + "AND start_date < '" + timeStamp + "' "
            + "AND end_date > '" + timeStamp + "' ";
    List<JsonObject> result = dbHelper.select(query);
    return result;
  }

  private JsonObject getUserProfile(String username) {
    String query = "SELECT name, email, profile_pic, reg_date, alias, age, compete_flag, share_flag "
            + "FROM app_user "
            + "WHERE email = '" + username + "'";
    List<JsonObject> result = dbHelper.select(query);
    if (result.size() > 0) {
      return result.get(0);
    }
    return null;
  }

  private boolean updateProfile(String username, JsonObject body) {
    String name = body.getString("name");
    String alias = body.getString("alias");
    String profile_pic = body.getString("profileImage");
    String ageString = body.getString("age");
    int age = ageString.equals("") ? 0 : Integer.parseInt(ageString);
    String query = "UPDATE app_user "
            + "SET age=" + age + ", "
            + "alias='" + alias + "', "
            + "name='" + name + "', "
            + "profile_pic='" + profile_pic + "' "
            + "WHERE email = '" + username + "'";
    boolean flag = dbHelper.update(query);
    return flag;
  }

  private JsonObject getUserStatsInChallenge(String challenge, String username) {
    String query = "SELECT "
            + "p.total_score as total_score, "
            + "pc.component_code as comp, "
            + "pc.total_score as comp_score, "
            + "(SELECT p3.rank FROM (SELECT @curRank := @curRank + 1 AS rank, p2.email from participant_view p2 where p2.challenge_id = '" + challenge + "' ORDER BY p2.total_score DESC) p3 WHERE p3.email='"+username+"') as total_rank "
            + "FROM participant_view p "
            + "JOIN participant_component_view pc USING(participant_id) "
            + ", ( SELECT @curRank := 0 ) q "
            + "WHERE p.challenge_id = '" + challenge + "' AND p.email = '" + username + "'";
    List<JsonObject> res = dbHelper.select(query);
    if (res.size() > 0) {
      JsonObject stats = new JsonObject();
      stats.put("rank", res.get(0).getString("total_rank"))
              .put("total_score", res.get(0).getString("total_score"));
      for (JsonObject row : res) {
        stats.put(row.getString("comp"), row.getString("comp_score"));
      }
      return stats;
    }
    return null;
  }

  private JsonObject getChallengeStats(String challenge) {
    String query = "SELECT "
            + "@curRank := @curRank + 1 as rank, "
            + "au.alias as alias, "
            + "p.total_score as total_score "
            + "FROM participant_view p "
            + "JOIN app_user au USING(email) "
            + ", ( SELECT @curRank := 0 ) q "
            + "WHERE p.challenge_id = '" + challenge + "' "
            + "ORDER BY p.total_score DESC";
    List<JsonObject> res = dbHelper.select(query);
    if (res.size() > 0) {
      JsonObject stats = new JsonObject();
      JsonArray arr = new JsonArray();
      for (JsonObject row : res) {
        arr.add(new JsonObject()
                .put("rank", row.getString("rank"))
                .put("alias", row.getString("alias"))
                .put("total_score", row.getString("total_score")));
      }
      stats.put("rank", arr);
      return stats;
    }
    return null;
  }

  private JsonObject getUserRecentStats(String username) {
    String query = "SELECT "
            + "p.today_score as today_score, "
            + "p.challenge_id, "
            + "pc.component_code as comp, "
            + "pc.today_score as comp_score, "
            + "(SELECT p3.rank FROM (SELECT @curRank := @curRank + 1 AS rank, p2.email from participant_view p2 where p2.challenge_id = challenge_id ORDER BY p2.today_score DESC) p3 WHERE p3.email='"+username+"') as total_rank "
            + "FROM participant_view p "
            + "JOIN participant_component_view pc USING(email) "
            + ", ( SELECT @curRank := 0 ) q "
            + "WHERE p.email = '" + username + "'";
    List<JsonObject> res = dbHelper.select(query);
    if (res.size() > 0) {
      JsonObject stats = new JsonObject();
      for (JsonObject row : res) {
        if (stats.containsKey(row.getString("challenge_id"))) {
          JsonObject rank = stats.getJsonObject(row.getString("challenge_id"));
          rank.put(row.getString("comp"), row.getString("comp_score"));
          stats.put(row.getString("challenge_id"), rank);
        } else {
          JsonObject rank = new JsonObject()
                  .put("rank", row.getString("total_rank"))
                  .put("total_score", row.getString("today_score"))
                  .put(row.getString("comp"), row.getString("comp_score"));
          stats.put(row.getString("challenge_id"), rank);
        }
      }
      return stats;
    }
    return null;
  }

  private JsonObject getUserFullStats(String username) {
    String query = "SELECT "
            + "p.today_score as today_score, "
            + "p.yesterday_score as yesterday_score, "
            + "p.last_week_score as last_week_score, "
            + "p.last_month_Score as last_month_score, "
            + "p.challenge_id, "
            + "pc.component_code as comp, "
            + "pc.today_score as comp_today_score, "
            + "pc.yesterday_score as comp_yesterday_score, "
            + "pc.last_week_score as comp_last_week_score, "
            + "pc.last_month_score as comp_last_month_score, "
            + "(SELECT p3.rank FROM (SELECT @curRank := @curRank + 1 AS rank, p2.email from participant_view p2 where p2.challenge_id = challenge_id ORDER BY p2.today_score DESC) p3 WHERE p3.email='"+username+"') as total_rank "
            + "FROM participant_view p "
            + "JOIN participant_component_view pc USING(email) "
            + ", ( SELECT @curRank := 0 ) q "
            + "WHERE p.email = '" + username + "';";
    List<JsonObject> res = dbHelper.select(query);
    if (res.size() > 0) {
      JsonObject stats = new JsonObject();
      for (JsonObject row : res) {
        if (stats.containsKey(row.getString("challenge_id"))) {
          JsonObject rank = stats.getJsonObject(row.getString("challenge_id"));
          JsonObject today = rank.getJsonObject("today")
                  .put(row.getString("comp"), row.getString("comp_today_score"));
          JsonObject yesterday = rank.getJsonObject("yesterday")
                  .put(row.getString("comp"), row.getString("comp_yesterday_score"));
          JsonObject lastWeek = rank.getJsonObject("last_week")
                  .put(row.getString("comp"), row.getString("comp_last_week_score"));
          JsonObject lastMonth = rank.getJsonObject("last_month")
                  .put(row.getString("comp"), row.getString("comp_last_month_score"));
          rank.put("today", today)
                  .put("yesterday", yesterday)
                  .put("last_week", lastWeek)
                  .put("last_month", lastMonth);
          stats.put(row.getString("challenge_id"), rank);
        } else {
          JsonObject today = new JsonObject()
                  .put("total_score", row.getString("today_score"))
                  .put(row.getString("comp"), row.getString("comp_today_score"));
          JsonObject yesterday = new JsonObject()
                  .put("total_score", row.getString("yesterday_score"))
                  .put(row.getString("comp"), row.getString("comp_yesterday_score"));
          JsonObject lastWeek = new JsonObject()
                  .put("total_score", row.getString("last_week_score"))
                  .put(row.getString("comp"), row.getString("comp_last_Week_score"));
          JsonObject lastMonth = new JsonObject()
                  .put("total_score", row.getString("last_month_score"))
                  .put(row.getString("comp"), row.getString("comp_last_month_score"));
          JsonObject rank = new JsonObject()
                  .put("rank", row.getString("total_rank"))
                  .put("today", today)
                  .put("yesterday", yesterday)
                  .put("last_week", lastWeek)
                  .put("last_month", lastMonth);
          stats.put(row.getString("challenge_id"), rank);
        }
      }
      return stats;
    }
    return null;
  }

}
