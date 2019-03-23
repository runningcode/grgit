package org.ajoberstar.grgit;

import java.time.ZonedDateTime;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * A tag.
 *
 * @since 0.2.0
 */
public final class Tag extends Ref {
  private final Commit commit;
  private final Person tagger;
  private final String fullMessage;
  private final String shortMessage;
  private final ZonedDateTime dateTime;

  public Tag(Commit commit, Person tagger, String fullName, String fullMessage, String shortMessage, ZonedDateTime dateTime) {
    super(fullName);
    this.commit = commit;
    this.tagger = tagger;
    this.fullMessage = fullMessage;
    this.shortMessage = shortMessage;
    this.dateTime = dateTime;
  }

  /**
   * The commit this tag points to.
   */
  public Commit getCommit() {
    return commit;
  }

  /**
   * The person who created the tag.
   */
  public Person getTagger() {
    return tagger;
  }

  /**
   * The full tag message.
   */
  public String getFullMessage() {
    return fullMessage;
  }

  /**
   * The shortened tag message.
   */
  public String getShortMessage() {
    return shortMessage;
  }

  /**
   * The time the commit was created with the time zone of the committer, if available.
   */
  public ZonedDateTime getDateTime() {
    return dateTime;
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
