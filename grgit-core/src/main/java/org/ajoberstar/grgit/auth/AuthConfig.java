package org.ajoberstar.grgit.auth;

import java.util.LinkedHashMap;
import java.util.Map;

import org.ajoberstar.grgit.Credentials;
import org.eclipse.jgit.util.SystemReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stores configuration options for how to authenticate with remote repositories.
 *
 * @see <a href="http://ajoberstar.org/grgit/grgit-authentication.html">grgit-authentication</a>
 * @since 0.2.0
 */
public class AuthConfig {
  private static final Logger logger = LoggerFactory.getLogger(AuthConfig.class);

  private static final String USERNAME_OPTION = "org.ajoberstar.grgit.auth.username";
  private static final String PASSWORD_OPTION = "org.ajoberstar.grgit.auth.password";
  private static final String USERNAME_ENV_VAR = "GRGIT_USER";
  private static final String PASSWORD_ENV_VAR = "GRGIT_PASS";

  private final Map<String, String> props;
  private final Map<String, String> env;

  private AuthConfig(Map<String, String> props, Map<String, String> env) {
    this.props = props;
    this.env = env;

    GrgitSystemReader.install();
    logger.debug("If SSH is used, the following external command (if non-null) will be used instead of JSch: {}", SystemReader.getInstance().getenv("GIT_SSH"));
  }

  /**
   * Constructs and returns a {@link Credentials} instance reflecting the settings in the system
   * properties.
   *
   * @return a credentials instance reflecting the settings in the system properties, or, if the
   *         username isn't set, {@code null}
   */
  public Credentials getHardcodedCreds() {
    String username = props.getOrDefault(USERNAME_OPTION, env.get(USERNAME_ENV_VAR));
    String password = props.getOrDefault(PASSWORD_OPTION, env.get(PASSWORD_ENV_VAR));
    return new Credentials(username, password);
  }

  /**
   * Factory method to construct an authentication configuration from the given properties and
   * environment.
   *
   * @param props the properties to use in this configuration
   * @param env the environment vars to use in this configuration
   * @return the constructed configuration
   * @throws IllegalArgumentException if force is set to an invalid option
   */
  public static AuthConfig fromMap(Map props, Map env) {
    return new AuthConfig(props, env);
  }

  /**
   * Factory method to construct an authentication configuration from the given properties and
   * environment.
   *
   * @param props the properties to use in this configuration
   * @return the constructed configuration
   * @throws IllegalArgumentException if force is set to an invalid option
   */
  public static AuthConfig fromMap(Map props) {
    return AuthConfig.fromMap(props, new LinkedHashMap());
  }

  /**
   * Factory method to construct an authentication configuration from the current system properties
   * and environment variables.
   *
   * @return the constructed configuration
   * @throws IllegalArgumentException if force is set to an invalid option
   */
  public static AuthConfig fromSystem() {
    return fromMap(System.getProperties(), System.getenv());
  }

  public static String getUSERNAME_OPTION() {
    return USERNAME_OPTION;
  }

  public static String getPASSWORD_OPTION() {
    return PASSWORD_OPTION;
  }

  public static String getUSERNAME_ENV_VAR() {
    return USERNAME_ENV_VAR;
  }

  public static String getPASSWORD_ENV_VAR() {
    return PASSWORD_ENV_VAR;
  }
}
