package org.ajoberstar.grgit;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import groovy.lang.IntRange;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.codehaus.groovy.runtime.StringGroovyMethods;

/**
 * A commit.
 *
 * @since 0.1.0
 */
public final class Commit {
  private final String id;
  private final String abbreviatedId;
  private final List<String> parentIds;
  private final Person author;
  private final Person committer;
  private final ZonedDateTime dateTime;
  private final String fullMessage;
  private final String shortMessage;

  public Commit(String id, String abbreviatedId, List<String> parentIds, Person author, Person committer, ZonedDateTime dateTime, String fullMessage, String shortMessage) {
    this.id = id;
    this.abbreviatedId = abbreviatedId;
    this.parentIds = parentIds;
    this.author = author;
    this.committer = committer;
    this.dateTime = dateTime;
    this.fullMessage = fullMessage;
    this.shortMessage = shortMessage;
  }

  /**
   * The full hash of the commit.
   */
  public String getId() {
    return id;
  }

  /**
   * The abbreviated hash of the commit.
   */
  public String getAbbreviatedId() {
    return abbreviatedId;
  }

  /**
   * Hashes of any parent commits.
   */
  public List<String> getParentIds() {
    return parentIds;
  }

  /**
   * The author of the changes in the commit.
   */
  public Person getAuthor() {
    return author;
  }

  /**
   * The committer of the changes in the commit.
   */
  public Person getCommitter() {
    return committer;
  }

  /**
   * The time the commit was created with the time zone of the committer, if available.
   */
  public ZonedDateTime getDateTime() {
    return dateTime;
  }

  /**
   * The full commit message.
   */
  public String getFullMessage() {
    return fullMessage;
  }

  /**
   * The shortened commit message.
   */
  public String getShortMessage() {
    return shortMessage;
  }


  /**
   * The time the commit was created in seconds since "the epoch".
   *
   * @return the time
   * @deprecated use Commit#dateTime
   */
  @Deprecated
  public long getTime() {
    return dateTime.toEpochSecond();
  }

  /**
   * The time the commit was created.
   *
   * @return the date
   * @deprecated use Commit#dateTime
   */
  @Deprecated
  public Date getDate() {
    return Date.from(dateTime.toInstant());
  }

  /**
   * The first {@code length} characters of the commit hash.
   *
   * @param length the number of characters to abbreviate the hash.
   */
  @Deprecated
  public String getAbbreviatedId(int length) {
    return ((String) (StringGroovyMethods.getAt(id, new IntRange(0, (length - 1)))));
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
