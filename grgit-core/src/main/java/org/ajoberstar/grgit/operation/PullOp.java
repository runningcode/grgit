package org.ajoberstar.grgit.operation;

import java.util.concurrent.Callable;

import org.ajoberstar.grgit.Repository;
import org.ajoberstar.grgit.auth.TransportOpUtil;
import org.ajoberstar.grgit.util.Throwing;
import org.codehaus.groovy.runtime.StringGroovyMethods;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * Pulls changes from the remote on the current branch. If the changes conflict, the pull will fail,
 * any conflicts can be retrieved with {@code grgit.status()}, and throwing an exception.
 *
 * @see <a href="http://ajoberstar.org/grgit/grgit-pull.html">grgit-pull</a>
 * @see <a href="http://git-scm.com/docs/git-pull">git-pull Manual Page</a>
 * @since 0.2.0
 */
public class PullOp implements Callable<Void> {
  private final Repository repo;

  private String remote;
  private String branch;
  private boolean rebase = false;

  public PullOp(Repository repo) {
    this.repo = repo;
  }

  public Void call() {
    PullCommand cmd = repo.getJgit().pull();
    if (StringGroovyMethods.asBoolean(remote)) {
      cmd.setRemote(remote);
    }

    if (StringGroovyMethods.asBoolean(branch)) {
      cmd.setRemoteBranchName(branch);
    }

    cmd.setRebase(rebase);
    TransportOpUtil.configure(cmd, repo.getCredentials());

    try {
      PullResult result = cmd.call();
      if (!result.isSuccessful()) {
        throw new IllegalStateException("Could not pull: " + String.valueOf(result));
      }
      return null;
    } catch (GitAPIException e) {
      Throwing.sneakyThrow(e);
      return null;
    }
  }

  public String getRemote() {
    return remote;
  }

  /**
   * The name of the remote to pull. If not set, the current branch's configuration will be used.
   */
  public void setRemote(String remote) {
    this.remote = remote;
  }

  public String getBranch() {
    return branch;
  }

  /**
   * The name of the remote branch to pull. If not set, the current branch's configuration will be
   * used.
   */
  public void setBranch(String branch) {
    this.branch = branch;
  }

  public boolean isRebase() {
    return rebase;
  }

  /**
   * Rebase on top of the changes when they are pulled in, if {@code true}. {@code false} (the
   * default) otherwise.
   */
  public void setRebase(boolean rebase) {
    this.rebase = rebase;
  }
}
