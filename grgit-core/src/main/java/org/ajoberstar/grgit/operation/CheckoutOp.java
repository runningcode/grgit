package org.ajoberstar.grgit.operation;

import java.util.concurrent.Callable;

import org.ajoberstar.grgit.Repository;
import org.ajoberstar.grgit.service.ResolveService;
import org.ajoberstar.grgit.util.Throwing;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * Checks out a branch to the working tree. Does not support checking out specific paths.
 *
 * @see <a href="http://ajoberstar.org/grgit/grgit-checkout.html">grgit-checkout</a>
 * @see <a href="http://git-scm.com/docs/git-checkout">git-checkout Manual Page</a>
 * @since 0.1.0
 */
public class CheckoutOp implements Callable<Void> {
  private final Repository repo;

  private Object branch;
  private boolean createBranch = false;
  private Object startPoint;
  private boolean orphan = false;

  public CheckoutOp(Repository repo) {
    this.repo = repo;
  }

  public Void call() {
    if (startPoint != null && !createBranch && !orphan) {
      throw new IllegalArgumentException("Cannot set a start point if createBranch and orphan are false.");
    } else if ((createBranch || orphan) && branch == null) {
      throw new IllegalArgumentException("Must specify branch name to create.");
    }

    CheckoutCommand cmd = repo.getJgit().checkout();
    ResolveService resolve = new ResolveService(repo);
    if (DefaultGroovyMethods.asBoolean(branch)) {
      cmd.setName(resolve.toBranchName(branch));
    }

    cmd.setCreateBranch(createBranch);
    cmd.setStartPoint(resolve.toRevisionString(startPoint));
    cmd.setOrphan(orphan);
    try {
      cmd.call();
      return null;
    } catch (GitAPIException e) {
      Throwing.sneakyThrow(e);
      return null;
    }
  }

  public Object getBranch() {
    return branch;
  }

  /**
   * The branch or commit to checkout.
   *
   * @see {@link ResolveService#toBranchName(Object)}
   */
  public void setBranch(Object branch) {
    this.branch = branch;
  }

  public boolean isCreateBranch() {
    return createBranch;
  }

  /**
   * {@code true} if the branch does not exist and should be created, {@code false} (the default)
   * otherwise
   */
  public void setCreateBranch(boolean createBranch) {
    this.createBranch = createBranch;
  }

  public Object getStartPoint() {
    return startPoint;
  }

  /**
   * If {@code createBranch} or {@code orphan} is {@code true}, start the new branch at this commit.
   *
   * @see {@link ResolveService#toRevisionString(Object)}
   */
  public void setStartPoint(Object startPoint) {
    this.startPoint = startPoint;
  }

  public boolean isOrphan() {
    return orphan;
  }

  /**
   * {@code true} if the new branch is to be an orphan, {@code false} (the default) otherwise
   */
  public void setOrphan(boolean orphan) {
    this.orphan = orphan;
  }
}
