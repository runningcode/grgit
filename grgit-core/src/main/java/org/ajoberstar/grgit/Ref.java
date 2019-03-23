package org.ajoberstar.grgit;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.eclipse.jgit.lib.Repository;

/**
 * A ref.
 *
 * @since 2.0.0
 */
public class Ref {
  private final String fullName;

  public Ref(String fullName) {
    this.fullName = fullName;
  }

  /**
   * The fully qualified name of this ref.
   */
  public String getFullName() {
    return fullName;
  }

  /**
   * The simple name of the ref.
   *
   * @return the simple name
   */
  public String getName() {
    return Repository.shortenRefName(fullName);
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
