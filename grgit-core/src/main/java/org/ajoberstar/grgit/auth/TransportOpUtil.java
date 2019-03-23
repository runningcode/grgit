package org.ajoberstar.grgit.auth;

import java.util.Optional;

import org.ajoberstar.grgit.Credentials;
import org.eclipse.jgit.api.TransportCommand;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class that allows a JGit {@code TransportCommand} to be configured to use additional
 * authentication options.
 */
public final class TransportOpUtil {
  private static final Logger logger = LoggerFactory.getLogger(TransportOpUtil.class);

  private TransportOpUtil() {
    // don't instantiate
  }

  /**
   * Configures the given transport command with the given credentials.
   *
   * @param cmd the command to configure
   * @param credentials the hardcoded credentials to use, if not {@code null}
   */
  public static void configure(TransportCommand cmd, Credentials credentials) {
    AuthConfig config = AuthConfig.fromSystem();
    cmd.setCredentialsProvider(determineCredentialsProvider(config, credentials));
  }

  private static CredentialsProvider determineCredentialsProvider(AuthConfig config, Credentials credentials) {
    Credentials systemCreds = config.getHardcodedCreds();
    if (Optional.ofNullable(credentials).filter(Credentials::isPopulated).isPresent()) {
      logger.info("using hardcoded credentials provided programmatically");
      return new UsernamePasswordCredentialsProvider(credentials.getUsername(), credentials.getPassword());
    } else if (Optional.ofNullable(systemCreds).filter(Credentials::isPopulated).isPresent()) {
      logger.info("using hardcoded credentials from system properties");
      return new UsernamePasswordCredentialsProvider(systemCreds.getUsername(), systemCreds.getPassword());
    } else {
      return null;
    }

  }
}
