package org.ajoberstar.grgit.operation;

import java.util.Set;
import java.util.concurrent.Callable;

import org.ajoberstar.grgit.Repository;
import org.ajoberstar.grgit.util.Throwing;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.eclipse.jgit.api.CleanCommand;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * Remove untracked files from the working tree. Returns the list of file paths deleted.
 *
 * @see <a href="http://ajoberstar.org/grgit/grgit-clean.html">grgit-clean</a>
 * @see <a href="http://git-scm.com/docs/git-clean">git-clean Manual Page</a>
 * @since 0.2.0
 */
public class CleanOp implements Callable<Set<String>> {
  private final Repository repo;

  private Set<String> paths;
  private boolean directories = false;
  private boolean dryRun = false;
  private boolean ignore = true;

  public CleanOp(Repository repo) {
    this.repo = repo;
  }

  public Set<String> call() {
    CleanCommand cmd = repo.getJgit().clean();
    if (DefaultGroovyMethods.asBoolean(paths)) {
      cmd.setPaths(paths);
    }

    cmd.setCleanDirectories(directories);
    cmd.setDryRun(dryRun);
    cmd.setIgnore(ignore);

    try {
      return cmd.call();
    } catch (GitAPIException e) {
      Throwing.sneakyThrow(e);
      return null;
    }
  }

  public Set<String> getPaths() {
    return paths;
  }

  /**
   * The paths to clean. {@code null} if all paths should be included.
   */
  public void setPaths(Set<String> paths) {
    this.paths = paths;
  }

  public boolean getDirectories() {
    return directories;
  }

  public boolean isDirectories() {
    return directories;
  }

  /**
   * {@code true} if untracked directories should also be deleted, {@code false} (the default)
   * otherwise
   */
  public void setDirectories(boolean directories) {
    this.directories = directories;
  }

  public boolean isDryRun() {
    return dryRun;
  }

  /**
   * {@code true} if the files should be returned, but not deleted, {@code false} (the default)
   * otherwise
   */
  public void setDryRun(boolean dryRun) {
    this.dryRun = dryRun;
  }

  public boolean isIgnore() {
    return ignore;
  }

  /**
   * {@code false} if files ignored by {@code .gitignore} should also be deleted, {@code true} (the
   * default) otherwise
   */
  public void setIgnore(boolean ignore) {
    this.ignore = ignore;
  }
}
