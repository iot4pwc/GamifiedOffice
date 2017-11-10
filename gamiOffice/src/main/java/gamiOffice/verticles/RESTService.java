package gamiOffice.verticles;

import gamiOffice.components.helper.DBHelper;
import gamiOffice.constants.ConstLib;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class RESTService extends AbstractVerticle{
	private DBHelper dbHelper;
	@Override
	public void start() {
		Router router = Router.router(vertx);
		/**
		 *input the restful endpoint here
		 */
		router.route().handler(BodyHandler.create());
		router.get("/test").handler(this::autoTest);
		//add any addtional routers below

/*		vertx.createHttpServer(
				new HttpServerOptions()
				.setSsl(true)
				.setPemKeyCertOptions(
						new PemKeyCertOptions()
						.setKeyPath(ConstLib.PRIVATE_KEY_PATH)
						.setCertPath(ConstLib.CERTIFICATE_PATH)
						)
				).requestHandler(router::accept).listen(8443);*/
		
		vertx.createHttpServer().requestHandler(router::accept).listen(8080);

		vertx.executeBlocking(future -> {
			dbHelper = DBHelper.getInstance(ConstLib.GAMIFIED_OFFICE);
			future.complete();
		}, response -> {
			System.out.println("RESTful service running on port 8080");
		});

	}

	@Override
	public void stop() {
		//close resources here
	}
	
	private void autoTest(RoutingContext routingContext){
		JsonObject res = new JsonObject();
		res.put("Connected", "okay");
		routingContext.response()
	      .putHeader("content-type", "application/json; charset=utf-8")
	      .setStatusCode(200)
	      .end(res.encodePrettily());
	} 
	
	//add any addtional logic below

}
