package org.ajoberstar.grgit.operation;

import java.util.concurrent.Callable;

import org.ajoberstar.grgit.Repository;
import org.ajoberstar.grgit.Status;
import org.ajoberstar.grgit.util.JGitUtil;
import org.ajoberstar.grgit.util.Throwing;
import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * Gets the current status of the repository. Returns an {@link Status}.
 *
 * @see <a href="http://ajoberstar.org/grgit/grgit-status.html">grgit-status</a>
 * @see <a href="http://git-scm.com/docs/git-status">git-status Manual Page</a>
 * @since 0.1.0
 */
public class StatusOp implements Callable<Status> {
  private final Repository repo;

  public StatusOp(Repository repo) {
    this.repo = repo;
  }

  public Status call() {
    StatusCommand cmd = repo.getJgit().status();
    try {
      return JGitUtil.convertStatus(cmd.call());
    } catch (GitAPIException e) {
      Throwing.sneakyThrow(e);
      return null;
    }
  }
}
