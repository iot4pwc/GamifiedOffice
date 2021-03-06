package gamiOffice.constants;
import org.apache.logging.log4j.Level;

public class ConstLib {
	public static final String MYSQL_CONNECTION_STRING = "jdbc:mysql://%s/%s?autoReconnect=true&useSSL=false";
	public static final String HIKARI_POOL_NAME = "DBHelper connection pool";
	public static final int HIKARI_MAX_POOL_SIZE = 4;
	public static final boolean HIKARI_CACHE_PSTMT = true;
	public static final int HIKARI_PSTMT_CACHE_SIZE = 256;
	public static final boolean HIKARI_USE_SERVER_PSTMT = true;

	public static final String GAMIFIED_OFFICE = "gamified_office";
	public static final String PRIVATE_KEY_PATH = "";
	public static final String CERTIFICATE_PATH = "";

	public static final int RESTFUL_SERVICE_NUMBER = 1;
	public static final int CHALLENGE_CONTROLLER_NUMBER = 8;
	public static final int CHALLENGE_MONITOR_NUMER = 1;

	public static final boolean MQTT_TLS_ENABLED = false;
	public static final String MQTT_BROKER_STRING = "tcp://%s:1883";
	public static final String MQTT_BROKER_TLS_STRING = "ssl://%s:8883";
	public static final String MQTT_CA_FILE = "/home/ubuntu/ca.crt";
	public static final String MQTT_TLS_VERSION = "TLSv1.2";
	public static final String MQTT_CLIENT_ID = "iot4pwc";
	public static final int MQTT_QUALITY_OF_SERVICE = 2;
	//topics
	public static final String TOPIC_WATERINTAKE = "/gamified_office/rfid";
	public static final String TOPIC_LIGHT = "/gamified_office/light";
	public static final String TOPIC_SITTING = "/gamified_office/sitting";

	public static final int HTTP_SERVER_PORT= 8090;
	public static final String PASSWORD_FIELD = "password";
	public static final String USERNAME_URL_PATTERN = "user";
	public static final String CHALLENGE_URL_PATTERN = "challenge";

	public static final String REQUIRED_HEADER_KEY = "secretKey";
	public static final String REQUIRED_HEADER_VALUE = "secretValue";
	public static final Double CHALLENGE_INITIAL_SCORE = 0.0;
	
	// logger settings
	public static final Level LOGGING_LEVEL = Level.INFO;
	public static final String LOGGING_CONFIG = "log4j2.xml";
}
