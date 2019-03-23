package org.ajoberstar.grgit;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Remote repository.
 *
 * @since 0.4.0
 */
public final class Remote {
  private final String name;
  private final String url;
  private final String pushUrl;
  private final List<String> fetchRefSpecs;
  private final List<String> pushRefSpecs;
  private final boolean mirror;

  public Remote(String name, String url, String pushUrl, List<String> fetchRefSpecs, List<String> pushRefSpecs, boolean mirror) {
    this.name = name;
    this.url = url;
    this.pushUrl = pushUrl;
    this.fetchRefSpecs = fetchRefSpecs;
    this.pushRefSpecs = pushRefSpecs;
    this.mirror = mirror;
  }

  /**
   * Name of the remote.
   */
  public String getName() {
    return name;
  }

  /**
   * URL to fetch from.
   */
  public String getUrl() {
    return url;
  }

  /**
   * URL to push to.
   */
  public String getPushUrl() {
    return pushUrl;
  }

  /**
   * Specs to fetch from the remote.
   */
  public List<String> getFetchRefSpecs() {
    return fetchRefSpecs;
  }

  /**
   * Specs to push to the remote.
   */
  public List<String> getPushRefSpecs() {
    return pushRefSpecs;
  }

  /**
   * Whether or not pushes will mirror the repository.
   */
  public boolean isMirror() {
    return mirror;
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
