package org.ajoberstar.grgit.operation;

import java.util.concurrent.Callable;

import org.ajoberstar.grgit.Branch;
import org.ajoberstar.grgit.BranchStatus;
import org.ajoberstar.grgit.Repository;
import org.ajoberstar.grgit.service.ResolveService;
import org.ajoberstar.grgit.util.Throwing;
import org.eclipse.jgit.lib.BranchTrackingStatus;

/**
 * Gets the tracking status of a branch. Returns a {@link BranchStatus}.
 *
 * <pre>
 * def status = grgit.branch.status(name: 'the-branch')
 * </pre>
 *
 * @since 0.2.0
 */
public class BranchStatusOp implements Callable<BranchStatus> {
  private final Repository repo;

  private Object name;

  public BranchStatusOp(Repository repo) {
    this.repo = repo;
  }

  public BranchStatus call() {
    try {
      Branch realBranch = new ResolveService(repo).toBranch(name);
      if (realBranch.getTrackingBranch() != null) {
        BranchTrackingStatus status = BranchTrackingStatus.of(repo.getJgit().getRepository(), realBranch.getFullName());
        if (status != null) {
          return new BranchStatus(realBranch, status.getAheadCount(), status.getBehindCount());
        } else {
          throw new IllegalStateException("Could not retrieve status for " + String.valueOf(getName()));
        }
      } else {
        throw new IllegalStateException(String.valueOf(getName()) + " is not set to track another branch");
      }
    } catch (Exception e) {
      Throwing.sneakyThrow(e);
      return null;
    }
  }

  public Object getName() {
    return name;
  }

  /**
   * The branch to get the status of.
   *
   * @see {@link ResolveService#toBranch(Object)}
   */
  public void setName(Object name) {
    this.name = name;
  }
}
