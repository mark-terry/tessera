package com.quorum.tessera.launcher.test;

import com.quorum.tessera.config.AppType;
import com.quorum.tessera.config.ClientMode;
import com.quorum.tessera.config.CommunicationType;
import com.quorum.tessera.config.Config;
import com.quorum.tessera.config.ConfigFactory;
import com.quorum.tessera.config.ServerConfig;
import com.quorum.tessera.context.RuntimeContext;
import com.quorum.tessera.discovery.Discovery;
import com.quorum.tessera.enclave.Enclave;
import com.quorum.tessera.launcher.Launcher;
import com.quorum.tessera.launcher.ScheduledServiceFactory;
import com.quorum.tessera.privacygroup.ResidentGroupHandler;
import com.quorum.tessera.recovery.workflow.BatchResendManager;
import com.quorum.tessera.server.TesseraServer;
import com.quorum.tessera.transaction.EncodedPayloadManager;
import com.quorum.tessera.transaction.TransactionManager;
import java.net.URI;
import java.security.Security;
import java.util.List;
import java.util.Optional;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrionLauncher {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrionLauncher.class);
  private List<TesseraServer> tesseraServers;
  private boolean isRunning;

  public void start() throws Exception {
    Security.addProvider(new BouncyCastleProvider());
    try {
      final Config testConfig = new Config();
      testConfig.setClientMode(ClientMode.ORION);

      final ServerConfig thirdParty = new ServerConfig();
      thirdParty.setApp(AppType.THIRD_PARTY);
      thirdParty.setCommunicationType(CommunicationType.REST);
      thirdParty.setServerAddress("http://localhost:0");

      final ServerConfig p2p = new ServerConfig();
      p2p.setApp(AppType.P2P);
      p2p.setCommunicationType(CommunicationType.REST);
      p2p.setServerAddress("http://localhost:0");

      final ServerConfig q2t = new ServerConfig();
      q2t.setApp(AppType.Q2T);
      q2t.setCommunicationType(CommunicationType.REST);
      q2t.setServerAddress("http://localhost:0");

      final List<ServerConfig> serverList = List.of(thirdParty, p2p, q2t);
      testConfig.setServerConfigs(serverList);

      LOGGER.debug("Storing config {}", testConfig);
      ConfigFactory.create().store(testConfig);
      LOGGER.debug("Stored config {}", testConfig);

      LOGGER.debug("Creating enclave");
      final Enclave enclave = Enclave.create();
      LOGGER.debug("Created enclave {}", enclave);

      LOGGER.debug("Creating RuntimeContext");
      final RuntimeContext runtimeContext = RuntimeContext.getInstance();
      LOGGER.debug("Created RuntimeContext {}", runtimeContext);

      LOGGER.debug("Creating Discovery");
      Discovery discovery = Discovery.create();
      discovery.onCreate();
      LOGGER.debug("Created Discovery {}", discovery);

      if (runtimeContext.isMultiplePrivateStates()) {
        LOGGER.debug("Creating ResidentGroupHandler");
        ResidentGroupHandler residentGroupHandler = ResidentGroupHandler.create();
        residentGroupHandler.onCreate(testConfig);
        LOGGER.debug("Created ResidentGroupHandler {}", residentGroupHandler);
      }

      LOGGER.debug("Creating EncodedPayloadManager");
      EncodedPayloadManager.create();
      LOGGER.debug("Created EncodedPayloadManager");

      LOGGER.debug("Creating BatchResendManager");
      BatchResendManager.create();
      LOGGER.debug("Created BatchResendManager");

      LOGGER.debug("Creating txn manager");
      TransactionManager transactionManager = TransactionManager.create();
      LOGGER.debug("Created txn manager");

      LOGGER.debug("Validating if transaction table exists");
      if (!transactionManager.upcheck()) {
        throw new RuntimeException(
            "The database has not been setup correctly. Please ensure transaction tables "
                + "are present and correct");
      }

      LOGGER.debug("Creating ScheduledServiceFactory");
      ScheduledServiceFactory scheduledServiceFactory =
          ScheduledServiceFactory.fromConfig(testConfig);
      scheduledServiceFactory.build();
      LOGGER.debug("Created ScheduledServiceFactory");

      LOGGER.debug("Creating Launcher");
      tesseraServers = Launcher.create(false).launchServer(testConfig);
      if (tesseraServers.size() == 3) {
        isRunning = true;
      }
    } catch (final Throwable ex) {
      LOGGER.debug(null, ex);
      if (Optional.ofNullable(ex.getMessage()).isPresent()) {
        System.err.println("ERROR: Cause is " + ex.getMessage());
      } else {
        System.err.println("ERROR: In class " + ex.getClass().getSimpleName());
      }

      isRunning = false;
      System.exit(2);
    }
  }

  public boolean isRunning() {
    return isRunning;
  }

  public URI getThirdPartyURL() {
    return tesseraServers.stream()
        .filter(tesseraServer -> tesseraServer.getAppType().equals(AppType.THIRD_PARTY))
        .findFirst()
        .get()
        .getUri();
  }

  public URI getQ2TURL() {
    return tesseraServers.stream()
        .filter(tesseraServer -> tesseraServer.getAppType().equals(AppType.Q2T))
        .findFirst()
        .get()
        .getUri();
  }

  public URI getP2pUrl() {
    return tesseraServers.stream()
        .filter(tesseraServer -> tesseraServer.getAppType().equals(AppType.P2P))
        .findFirst()
        .get()
        .getUri();
  }
}
