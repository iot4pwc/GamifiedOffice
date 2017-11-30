import gamiOffice.constants.ConstLib;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import java.util.Properties;

public class Main {

  public static void main(String[] args) {
    // set system properties
    Properties props = System.getProperties();
    props.setProperty("java.util.logging.config.file", ConstLib.LOGGING_CONFIG);
    props.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.Log4j2LogDelegateFactory");
    // Modify logging configuration here
    Configurator.setRootLevel(ConstLib.LOGGING_LEVEL);
    Logger logger = LogManager.getLogger(Main.class);
    	
    Vertx vertx = Vertx.vertx();
        
    DeploymentOptions deploymentOptions = new DeploymentOptions().setInstances(ConstLib.RESTFUL_SERVICE_NUMBER);
    vertx.deployVerticle("gamiOffice.verticles.RESTService", deploymentOptions);
    deploymentOptions = new DeploymentOptions().setInstances(ConstLib.CHALLENGE_MONITOR_NUMER);
    vertx.deployVerticle("gamiOffice.verticles.ChallengeMonitor", deploymentOptions);

    logger.info("Start Gamified Office.");
  }

}
