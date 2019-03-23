package org.ajoberstar.grgit;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * A branch.
 *
 * @since 0.2.0
 */
public final class Branch extends Ref {
  private final Branch trackingBranch;

  public Branch(String fullName, Branch trackingBranch) {
    super(fullName);
    this.trackingBranch = trackingBranch;
  }

  /**
   * This branch's upstream branch. {@code null} if this branch isn't tracking an upstream.
   */
  public Branch getTrackingBranch() {
    return trackingBranch;
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
