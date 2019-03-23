package org.ajoberstar.grgit;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Status of the current working tree and index.
 */
public final class Status {
  private final Changes staged;
  private final Changes unstaged;
  private final Set<String> conflicts;

  public Status(Changes staged, Changes unstaged, Set conflicts) {
    this.staged = staged;
    this.unstaged = unstaged;
    this.conflicts = Optional.ofNullable(conflicts).orElse(Collections.emptySet());
  }

  public Status(Map<String, Object> args) {
    this.staged = (Changes) args.getOrDefault("staged", new Changes(null, null, null));
    this.unstaged = (Changes) args.getOrDefault("unstaged", new Changes(null, null, null));
    this.conflicts = StreamSupport.stream(((Iterable<String>) args.getOrDefault("conflicts", Collections.emptyList())).spliterator(), false).collect(Collectors.toSet());
  }

  public Status() {
    this(new Changes(null, null, null), new Changes(null, null, null), Collections.emptySet());
  }

  public Changes getStaged() {
    return staged;
  }

  public Changes getUnstaged() {
    return unstaged;
  }

  public Set<String> getConflicts() {
    return conflicts;
  }

  /**
   * Whether the repository has any changes or conflicts.
   *
   * @return {@code true} if there are no changes either staged or unstaged or any conflicts,
   *         {@code false} otherwise
   */
  public boolean isClean() {
    return !Stream.of(staged.getAllChanges(), unstaged.getAllChanges(), conflicts).flatMap(Set::stream).findAny().isPresent();
  }

  public static final class Changes {
    private final Set<String> added;
    private final Set<String> modified;
    private final Set<String> removed;

    public Changes(Set<String> added, Set<String> modified, Set<String> removed) {
      this.added = Optional.ofNullable(added).orElse(Collections.emptySet());
      this.modified = Optional.ofNullable(modified).orElse(Collections.emptySet());
      this.removed = Optional.ofNullable(removed).orElse(Collections.emptySet());
    }

    public Changes(Map<String, Iterable<String>> args) {
      this.added = StreamSupport.stream(args.getOrDefault("added", Collections.emptyList()).spliterator(), false).collect(Collectors.toSet());
      this.modified = StreamSupport.stream(args.getOrDefault("modified", Collections.emptyList()).spliterator(), false).collect(Collectors.toSet());
      this.removed = StreamSupport.stream(args.getOrDefault("removed", Collections.emptyList()).spliterator(), false).collect(Collectors.toSet());
    }

    public Set<String> getAdded() {
      return added;
    }

    public Set<String> getModified() {
      return modified;
    }

    public Set<String> getRemoved() {
      return removed;
    }

    /**
     * Gets all changed files.
     *
     * @return all changed files
     */
    public Set<String> getAllChanges() {
      return Stream.of(added, modified, removed).flatMap(Set::stream).collect(Collectors.toSet());
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
