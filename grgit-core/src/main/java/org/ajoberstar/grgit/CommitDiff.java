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

public final class CommitDiff {
  private final Commit commit;
  private final Set<String> added;
  private final Set<String> copied;
  private final Set<String> modified;
  private final Set<String> removed;
  private final Set<String> renamed;
  private final Map<String, String> renamings;

  public CommitDiff(Commit commit, Set<String> added, Set<String> copied, Set<String> modified, Set<String> removed, Set<String> renamed, Map<String, String> renamings) {
    this.commit = commit;
    this.added = Optional.ofNullable(added).orElse(Collections.emptySet());
    this.copied = Optional.ofNullable(copied).orElse(Collections.emptySet());
    this.modified = Optional.ofNullable(modified).orElse(Collections.emptySet());
    this.removed = Optional.ofNullable(removed).orElse(Collections.emptySet());
    this.renamed = Optional.ofNullable(renamed).orElse(Collections.emptySet());
    this.renamings = Optional.ofNullable(renamings).orElse(Collections.emptyMap());
  }

  public CommitDiff(Map<String, Object> args) {
    this.commit = (Commit) args.get("commit");
    this.added = StreamSupport.stream(((Iterable<String>) args.getOrDefault("added", Collections.emptyList())).spliterator(), false).collect(Collectors.toSet());
    this.copied = StreamSupport.stream(((Iterable<String>) args.getOrDefault("copied", Collections.emptyList())).spliterator(), false).collect(Collectors.toSet());
    this.modified = StreamSupport.stream(((Iterable<String>) args.getOrDefault("modified", Collections.emptyList())).spliterator(), false).collect(Collectors.toSet());
    this.removed = StreamSupport.stream(((Iterable<String>) args.getOrDefault("removed", Collections.emptyList())).spliterator(), false).collect(Collectors.toSet());
    this.renamed = StreamSupport.stream(((Iterable<String>) args.getOrDefault("renamed", Collections.emptyList())).spliterator(), false).collect(Collectors.toSet());
    this.renamings = (Map<String, String>) args.getOrDefault("renamings", Collections.emptyMap());
  }

  public Commit getCommit() {
    return commit;
  }

  public Set<String> getAdded() {
    return added;
  }

  public Set<String> getCopied() {
    return copied;
  }

  public Set<String> getModified() {
    return modified;
  }

  public Set<String> getRemoved() {
    return removed;
  }

  public Set<String> getRenamed() {
    return renamed;
  }

  public Map<String, String> getRenamings() {
    return renamings;
  }

  /**
   * Gets all changed files.
   *
   * @return all changed files
   */
  public Set<String> getAllChanges() {
    return Stream.of(added, copied, modified, removed, renamed).flatMap(Set::stream).collect(Collectors.toSet());
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
