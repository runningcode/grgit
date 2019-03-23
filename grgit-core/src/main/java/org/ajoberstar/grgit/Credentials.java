package org.ajoberstar.grgit;

import java.util.Optional;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Credentials to use for remote operations.
 * 
 * @since 0.2.0
 */
public final class Credentials {
  private final String username;
  private final String password;

  public Credentials() {
    this.username = null;
    this.password = null;
  }

  public Credentials(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return Optional.ofNullable(username).orElse("");
  }

  public String getPassword() {
    return Optional.ofNullable(password).orElse("");
  }

  public boolean isPopulated() {
    return username != null;
  }

  @Override
  public boolean equals(Object that) {
    return EqualsBuilder.reflectionEquals(this, that);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }
}
