package org.ajoberstar.grgit.operation;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import org.ajoberstar.grgit.Repository;
import org.ajoberstar.grgit.service.ResolveService;
import org.ajoberstar.grgit.util.Throwing;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * Reset changes in the repository.
 *
 * @see <a href="http://ajoberstar.org/grgit/grgit-reset.html">grgit-reset</a>
 * @see <a href="http://git-scm.com/docs/git-reset">git-reset Manual Page</a>
 * @since 0.1.0
 */
public class ResetOp implements Callable<Void> {
  private final Repository repo;

  private Set<String> paths = new LinkedHashSet<String>();
  private Object commit;
  private Mode mode = Mode.MIXED;

  public ResetOp(Repository repo) {
    this.repo = repo;
  }

  public Void call() {
    if (!paths.isEmpty() && !mode.equals(Mode.MIXED)) {
      throw new IllegalStateException("Cannot set mode when resetting paths.");
    }

    ResetCommand cmd = repo.getJgit().reset();
    paths.forEach(cmd::addPath);

    if (commit != null) {
      cmd.setRef(new ResolveService(repo).toRevisionString(commit));
    }

    if (paths.isEmpty()) {
      cmd.setMode(mode.jgit);
    }


    try {
      cmd.call();
      return null;
    } catch (GitAPIException e) {
      Throwing.sneakyThrow(e);
      return null;
    }
  }

  public Set<String> getPaths() {
    return paths;
  }

  /**
   * The paths to reset.
   */
  public void setPaths(Set<String> paths) {
    this.paths = paths;
  }

  public Object getCommit() {
    return commit;
  }

  /**
   * The commit to reset back to. Defaults to HEAD.
   *
   * @see {@link ResolveService#toRevisionString(Object)}
   */
  public void setCommit(Object commit) {
    this.commit = commit;
  }

  public Mode getMode() {
    return mode;
  }

  /**
   * The mode to use when resetting.
   */
  public void setMode(String mode) {
    this.mode = Mode.valueOf(mode.toUpperCase());
  }

  public static enum Mode {
    HARD(ResetCommand.ResetType.HARD),
    /**
     * Reset the index, but not the working tree.
     */
    MIXED(ResetCommand.ResetType.MIXED),
    /**
     * Only reset the HEAD. Leave the index and working tree as-is.
     */
    SOFT(ResetCommand.ResetType.SOFT);

    Mode(ResetCommand.ResetType jgit) {
      this.jgit = jgit;
    }

    private final ResetCommand.ResetType jgit;
  }
}
