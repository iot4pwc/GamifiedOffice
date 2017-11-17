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

public class RESTService extends AbstractVerticle {

  private DBHelper dbHelper;

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
      System.out.println("RESTful service running on port " + ConstLib.HTTP_SERVER_PORT);
    });

  }

  @Override
  public void stop() {
    dbHelper.closeDatasource();
  }

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
    }
    else {
      routingContext.response()
      .putHeader("content-type", "application/json; charset=utf-8")
      .setStatusCode(400)
      .end();
    }
  }
  
  private void login(RoutingContext routingContext) {
    JsonObject body = routingContext.getBodyAsJson();
    String username = routingContext.request().getParam(ConstLib.USERNAME_URL_PATTERN);
    String password = body.getString(ConstLib.PASSWORD_FIELD);

    //TODO: Call login method here to check credentials (true for now)
    boolean flag = true;

    if (flag) {
      routingContext.response()
            .setStatusCode(200)
              .end();
    } else {
      routingContext.response()
            .setStatusCode(400)
              .end();
    }
  }

  private void getProfile(RoutingContext routingContext) {
    String username = routingContext.request().getParam(ConstLib.USERNAME_URL_PATTERN);

    //TODO: Get profile call here (hard coded for now)
    JsonObject profile = new JsonObject().put("name", "Bob").put("alias", "shiningBlackHole");

    routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            .setStatusCode(200)
            .end(profile.encodePrettily());
  }

  private void updateProfile(RoutingContext routingContext) {
    String username = routingContext.request().getParam(ConstLib.USERNAME_URL_PATTERN);

    //TODO: Update profile call here (returns a boolean success flag)
    boolean isSuccess = true;

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

  private void getUserStatsInChallenge(RoutingContext routingContext) {
    String challenge = routingContext.request().getParam(ConstLib.CHALLENGE_URL_PATTERN);
    String username = routingContext.request().getParam(ConstLib.USERNAME_URL_PATTERN);

    //Get ranking call here (hardcoded for now)
    JsonObject rank = new JsonObject()
            .put("rank", 4)
            .put("total_score", 87)
            .put("water_intake_score", 56)
            .put("sitting_score", 96);

    routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            .setStatusCode(200)
            .end(rank.encodePrettily());

  }

  private void getChallengeStats(RoutingContext routingContext) {
    String challenge = routingContext.request().getParam(ConstLib.CHALLENGE_URL_PATTERN);

    //Get ranking call here (hardcoded for now)
    JsonArray rankArr = new JsonArray()
            .add(new JsonObject().put("alias", "runningFish").put("rank", 1).put("total_score", 106))
            .add(new JsonObject().put("alias", "changingMask").put("rank", 2).put("total_score", 103));
    JsonObject rank = new JsonObject().put("rank", rankArr);

    routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            .setStatusCode(200)
            .end(rank.encodePrettily());

  }

  private void getUserRecentStats(RoutingContext routingContext) {
    String username = routingContext.request().getParam(ConstLib.USERNAME_URL_PATTERN);

    //Get ranking call here (hardcoded for now)
    JsonObject rank = new JsonObject()
            .put("rank", 4)
            .put("total_score", 87)
            .put("water_intake_score", 56)
            .put("sitting_score", 96);

    routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            .setStatusCode(200)
            .end(rank.encodePrettily());

  }
  
    private void getUserFullStats(RoutingContext routingContext) {
    String username = routingContext.request().getParam(ConstLib.USERNAME_URL_PATTERN);

    //Get ranking call here (hardcoded for now)
    JsonObject rank = new JsonObject()
            .put("rank", 4)
            .put("today", new JsonObject().put("total_score", 34).put("water_intake_score", 54).put("sitting_score", 24))
            .put("yesterday", new JsonObject().put("total_score", 34).put("water_intake_score", 54).put("sitting_score", 24))
            .put("last_week", new JsonObject().put("total_score", 34).put("water_intake_score", 54).put("sitting_score", 24))
            .put("last_month", new JsonObject().put("total_score", 34).put("water_intake_score", 54).put("sitting_score", 24));

    routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            .setStatusCode(200)
            .end(rank.encodePrettily());

  }

}
