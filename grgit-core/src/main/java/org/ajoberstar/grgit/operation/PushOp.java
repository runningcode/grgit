package org.ajoberstar.grgit.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.ajoberstar.grgit.PushException;
import org.ajoberstar.grgit.Repository;
import org.ajoberstar.grgit.auth.TransportOpUtil;
import org.ajoberstar.grgit.util.Throwing;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.RemoteRefUpdate;

/**
 * Push changes to a remote repository.
 *
 * @see <a href="http://ajoberstar.org/grgit/grgit-push.html">grgit-push</a>
 * @see <a href="http://git-scm.com/docs/git-push">git-push Manual Page</a>
 * @since 0.1.0
 */
public class PushOp implements Callable<Void> {
  private final Repository repo;

  private String remote;
  private List<String> refsOrSpecs = new ArrayList();
  private boolean all = false;
  private boolean tags = false;
  private boolean force = false;
  private boolean dryRun = false;

  public PushOp(Repository repo) {
    this.repo = repo;
  }

  public Void call() {
    PushCommand cmd = repo.getJgit().push();
    TransportOpUtil.configure(cmd, repo.getCredentials());
    if (remote != null) {
      cmd.setRemote(remote);
    }

    refsOrSpecs.forEach(cmd::add);

    if (all) {
      cmd.setPushAll();
    }

    if (tags) {
      cmd.setPushTags();
    }

    cmd.setForce(force);
    cmd.setDryRun(dryRun);

    try {
      String errorMessage = StreamSupport.stream(cmd.call().spliterator(), false)
          .flatMap(result -> {
            return result.getRemoteUpdates().stream()
                .filter(update -> !(update.getStatus().equals(RemoteRefUpdate.Status.OK) || update.getStatus().equals(RemoteRefUpdate.Status.UP_TO_DATE)))
                .map(update -> String.format("%s to %s (%s)", update.getSrcRef(), update.getRemoteName(), update.getMessage()));
          }).collect(Collectors.joining(", "));
      if (!errorMessage.isEmpty()) {
        throw new PushException("Failed to push: " + errorMessage);
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
   * The remote to push to.
   */
  public void setRemote(String remote) {
    this.remote = remote;
  }

  public List getRefsOrSpecs() {
    return refsOrSpecs;
  }

  /**
   * The refs or refspecs to use when pushing. If {@code null} and {@code all} is {@code false} only
   * push the current branch.
   */
  public void setRefsOrSpecs(List refsOrSpecs) {
    this.refsOrSpecs = refsOrSpecs;
  }

  public boolean isAll() {
    return all;
  }

  /**
   * {@code true} to push all branches, {@code false} (the default) to only push the current one.
   */
  public void setAll(boolean all) {
    this.all = all;
  }

  public boolean isTags() {
    return tags;
  }

  /**
   * {@code true} to push tags, {@code false} (the default) otherwise.
   */
  public void setTags(boolean tags) {
    this.tags = tags;
  }

  public boolean isForce() {
    return force;
  }

  /**
   * {@code true} if branches should be pushed even if they aren't a fast-forward, {@code false} (the
   * default) if it should fail.
   */
  public void setForce(boolean force) {
    this.force = force;
  }

  public boolean isDryRun() {
    return dryRun;
  }

  /**
   * {@code true} if result of this operation should be just estimation of real operation result, no
   * real push is performed. {@code false} (the default) if real push to remote repo should be
   * performed.
   *
   * @since 0.4.1
   */
  public void setDryRun(boolean dryRun) {
    this.dryRun = dryRun;
  }
}
