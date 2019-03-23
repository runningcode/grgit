package org.ajoberstar.grgit.operation;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import org.ajoberstar.grgit.Repository;
import org.ajoberstar.grgit.util.Throwing;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * Adds files to the index.
 *
 * @see <a href="http://ajoberstar.org/grgit/grgit-add.html">grgit-add</a>
 * @see <a href="http://git-scm.com/docs/git-add">git-add Manual Page</a>
 * @since 0.1.0
 */
public class AddOp implements Callable<Void> {
  private final Repository repo;
  /**
   * Patterns of files to add to the index.
   */
  private Set<String> patterns = new LinkedHashSet<>();
  /**
   * {@code true} if changes to all currently tracked files should be added to the index,
   * {@code false} otherwise.
   */
  private boolean update = false;

  public AddOp(Repository repo) {
    this.repo = repo;
  }

  public Void call() {
    try {
      AddCommand cmd = repo.getJgit().add();
      patterns.forEach(cmd::addFilepattern);
      cmd.setUpdate(update);
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

  public void setPatterns(Set<String> patterns) {
    this.patterns = patterns;
  }

  public boolean isUpdate() {
    return update;
  }

  public void setUpdate(boolean update) {
    this.update = update;
  }
}
