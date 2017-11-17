import gamiOffice.constants.ConstLib;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

public class Main {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

          DeploymentOptions deploymentOptions = new DeploymentOptions().setInstances(ConstLib.RESTFUL_SERVICE_NUMBER);
          vertx.deployVerticle("gamiOffice.verticles.RESTService", deploymentOptions);
          deploymentOptions = new DeploymentOptions().setInstances(ConstLib.CHALLENGE_CONTROLLER_NUMBER);
          vertx.deployVerticle("gamiOffice.verticles.ChallengeController", deploymentOptions);

    }

}
