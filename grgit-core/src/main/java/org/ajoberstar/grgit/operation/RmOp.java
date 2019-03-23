package org.ajoberstar.grgit.operation;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import org.ajoberstar.grgit.Repository;
import org.ajoberstar.grgit.util.Throwing;
import org.eclipse.jgit.api.RmCommand;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * Remove files from the index and (optionally) delete them from the working tree. Note that
 * wildcards are not supported.
 *
 * @see <a href="http://ajoberstar.org/grgit/grgit-remove.html">grgit-remove</a>
 * @see <a href="http://git-scm.com/docs/git-rm">git-rm Manual Page</a>
 * @since 0.1.0
 */
public class RmOp implements Callable<Void> {
  private final Repository repo;

  private Set<String> patterns = new LinkedHashSet<String>();
  private boolean cached = false;

  public RmOp(Repository repo) {
    this.repo = repo;
  }

  public Void call() {
    final RmCommand cmd = repo.getJgit().rm();
    patterns.forEach(cmd::addFilepattern);
    cmd.setCached(cached);
    try {
      cmd.call();
      return null;
    } catch (GitAPIException e) {
      Throwing.sneakyThrow(e);
      return null;
    }
  }

  public Set<String> getPatterns() {
    return patterns;
  }

  /**
   * The file patterns to remove.
   */
  public void setPatterns(Set<String> patterns) {
    this.patterns = patterns;
  }

  public boolean isCached() {
    return cached;
  }

  /**
   * {@code true} if files should only be removed from the index, {@code false} (the default)
   * otherwise.
   */
  public void setCached(boolean cached) {
    this.cached = cached;
  }
}
