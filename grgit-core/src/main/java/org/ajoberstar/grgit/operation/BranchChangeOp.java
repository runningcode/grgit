package org.ajoberstar.grgit.operation;

import java.util.concurrent.Callable;

import org.ajoberstar.grgit.Branch;
import org.ajoberstar.grgit.Repository;
import org.ajoberstar.grgit.service.ResolveService;
import org.ajoberstar.grgit.util.JGitUtil;
import org.ajoberstar.grgit.util.Throwing;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;

/**
 * Changes a branch's start point and/or upstream branch. Returns the changed {@link Branch}.
 *
 * @see <a href="http://ajoberstar.org/grgit/grgit-branch.html">grgit-branch</a>
 * @see <a href="http://git-scm.com/docs/git-branch">git-branch Manual Page</a>
 * @since 0.2.0
 */
public class BranchChangeOp implements Callable<Branch> {
  private final Repository repo;

  private String name;
  private Object startPoint;
  private Mode mode;

  public BranchChangeOp(Repository repo) {
    this.repo = repo;
  }

  public Branch call() {
    if (JGitUtil.resolveBranch(repo, name) == null) {
      throw new IllegalStateException("Branch does not exist: " + getName());
    }

    if (startPoint == null) {
      throw new IllegalArgumentException("Must set new startPoint.");
    }

    CreateBranchCommand cmd = repo.getJgit().branchCreate();
    cmd.setName(name);
    cmd.setForce(true);
    if (startPoint != null) {
      String rev = new ResolveService(repo).toRevisionString(startPoint);
      cmd.setStartPoint(rev);
    }

    if (mode != null) {
      cmd.setUpstreamMode(mode.jgit);
    }


    try {
      Ref ref = cmd.call();
      return JGitUtil.resolveBranch(repo, ref);
    } catch (GitAPIException e) {
      Throwing.sneakyThrow(e);
      return null;
    }
  }

  public String getName() {
    return name;
  }

  /**
   * The name of the branch to change.
   */
  public void setName(String name) {
    this.name = name;
  }

  public Object getStartPoint() {
    return startPoint;
  }

  /**
   * The commit the branch should now start at.
   *
   * @see {@link ResolveService#toRevisionString(Object)}
   */
  public void setStartPoint(Object startPoint) {
    this.startPoint = startPoint;
  }

  public Mode getMode() {
    return mode;
  }

  /**
   * The tracking mode to use.
   */
  public void setMode(Mode mode) {
    this.mode = mode;
  }

  public static enum Mode {
    TRACK(CreateBranchCommand.SetupUpstreamMode.TRACK), NO_TRACK(CreateBranchCommand.SetupUpstreamMode.NOTRACK);

    Mode(CreateBranchCommand.SetupUpstreamMode jgit) {
      this.jgit = jgit;
    }

    private final CreateBranchCommand.SetupUpstreamMode jgit;
  }
}
