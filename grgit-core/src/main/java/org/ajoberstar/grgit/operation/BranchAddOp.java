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
 * Adds a branch to the repository. Returns the newly created {@link Branch}.
 *
 * @see <a href="http://ajoberstar.org/grgit/grgit-branch.html">grgit-branch</a>
 * @see <a href="http://git-scm.com/docs/git-branch">git-branch Manual Page</a>
 * @since 0.2.0
 */
public class BranchAddOp implements Callable<Branch> {
  private final Repository repo;
  private String name;
  private Object startPoint;
  private Mode mode;

  public BranchAddOp(Repository repo) {
    this.repo = repo;
  }

  public Branch call() {
    if (mode != null && startPoint == null) {
      throw new IllegalStateException("Cannot set mode if no start point.");
    }

    CreateBranchCommand cmd = repo.getJgit().branchCreate();
    cmd.setName(name);
    cmd.setForce(false);
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
   * The name of the branch to add.
   */
  public void setName(String name) {
    this.name = name;
  }

  public Object getStartPoint() {
    return startPoint;
  }

  /**
   * The commit the branch should start at. If this is a remote branch it will be automatically
   * tracked.
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
   * The tracking mode to use. If {@code null}, will use the default behavior.
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
