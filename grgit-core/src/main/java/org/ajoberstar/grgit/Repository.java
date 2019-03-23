package org.ajoberstar.grgit;

import java.io.File;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.eclipse.jgit.api.Git;

/**
 * A repository.
 *
 * @since 0.1.0
 */
public final class Repository {
  private final File rootDir;
  private final Git jgit;
  private final Credentials credentials;

  public Repository(File rootDir, Git jgit, Credentials credentials) {
    this.rootDir = rootDir;
    this.jgit = jgit;
    this.credentials = credentials;
  }

  /**
   * The directory the repository is contained in.
   */
  public File getRootDir() {
    return rootDir;
  }

  /**
   * The JGit instance opened for this repository.
   */
  public Git getJgit() {
    return jgit;
  }

  /**
   * The credentials used when talking to remote repositories.
   */
  public Credentials getCredentials() {
    return credentials;
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
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
        .append("rootDir", rootDir)
        .build();
  }
}
