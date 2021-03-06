package gamiOffice.components.helper;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Set;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.paho.client.mqttv3.*;

import gamiOffice.constants.ConstLib;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class MqttHelper {
  MqttClient client;
  boolean isTLSEnabled;
  static int clientSuffix = 0;

  public MqttHelper(boolean isTLSEnabled) {
    this.isTLSEnabled = isTLSEnabled;
    client = getAliveClient();
  }

  public void subscribe(Set<String> topics) {
    for (String topic : topics) {
      try {
        client = getAliveClient();
        client.subscribe(topic);
      } catch (MqttException me) {
        me.printStackTrace();
      }
    }
  }

  public void closeConnection() {
    try {
      client.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // need a mechanism to create clientID
  private MqttClient getMqttClient() {
    MqttClient client = null;
    try {
      String broker = String.format(ConstLib.MQTT_BROKER_STRING, System.getenv("MQTT_URL"));
      client = new MqttClient (broker, ConstLib.MQTT_CLIENT_ID + Integer.toString(clientSuffix++));
      MqttConnectOptions connOpts = new MqttConnectOptions();
      connOpts.setCleanSession(true);
      client.connect(connOpts);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      return client;
    }
  }

  private MqttClient getMqttTLSClient() {
    MqttClient client = null;
    try {
      String broker = String.format(ConstLib.MQTT_BROKER_TLS_STRING, System.getenv("MQTT_URL"));
      client = new MqttClient (broker, ConstLib.MQTT_CLIENT_ID + Integer.toString(clientSuffix++));
      MqttConnectOptions connOpts = new MqttConnectOptions();
      connOpts.setCleanSession(true);
      connOpts.setSocketFactory(getSocketFactory(ConstLib.MQTT_CA_FILE));
      client.connect(connOpts);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      return client;
    }
  }

  public MqttClient getAliveClient() {
    if (client == null || !client.isConnected()) {
      client = this.isTLSEnabled ? getMqttTLSClient() : getMqttClient();
    }
    return client;
  }

  private SSLSocketFactory getSocketFactory(final String caCrtFile) throws Exception {
    Security.addProvider(new BouncyCastleProvider());

    X509Certificate caCert = null;

    FileInputStream fis = new FileInputStream(caCrtFile);
    BufferedInputStream bis = new BufferedInputStream(fis);
    CertificateFactory cf = CertificateFactory.getInstance("X.509");

    while (bis.available() > 0) {
      caCert = (X509Certificate) cf.generateCertificate(bis);
    }

    KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType());
    caKs.load(null, null);
    caKs.setCertificateEntry("ca-certificate", caCert);
    TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
    tmf.init(caKs);

    SSLContext context = SSLContext.getInstance(ConstLib.MQTT_TLS_VERSION);
    context.init(null, tmf.getTrustManagers(), null);

    return context.getSocketFactory();
  }
}
