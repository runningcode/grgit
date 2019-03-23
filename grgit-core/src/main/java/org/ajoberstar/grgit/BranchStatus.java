package org.ajoberstar.grgit;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * The tracking status of a branch.
 *
 * @since 0.2.0
 */
public final class BranchStatus {
  private final Branch branch;
  private final int aheadCount;
  private final int behindCount;

  public BranchStatus(Branch branch, int aheadCount, int behindCount) {
    this.branch = branch;
    this.aheadCount = aheadCount;
    this.behindCount = behindCount;
  }

  /**
   * The branch this object is for.
   */
  public Branch getBranch() {
    return branch;
  }

  /**
   * The number of commits this branch is ahead of its upstream.
   */
  public int getAheadCount() {
    return aheadCount;
  }

  /**
   * The number of commits this branch is behind its upstream.
   */
  public int getBehindCount() {
    return behindCount;
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
