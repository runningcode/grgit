package org.ajoberstar.grgit.operation;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.ajoberstar.grgit.Branch;
import org.ajoberstar.grgit.Repository;
import org.ajoberstar.grgit.service.ResolveService;
import org.ajoberstar.grgit.util.JGitUtil;
import org.ajoberstar.grgit.util.Throwing;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * Lists branches in the repository. Returns a list of {@link Branch}.
 *
 * @see <a href="http://ajoberstar.org/grgit/grgit-branch.html">grgit-branch</a>
 * @see <a href="http://git-scm.com/docs/git-branch">git-branch Manual Page</a>
 * @since 0.2.0
 */
public class BranchListOp implements Callable<List<Branch>> {
  private final Repository repo;

  private Mode mode = Mode.LOCAL;
  private Object contains = null;

  public BranchListOp(Repository repo) {
    this.repo = repo;
  }

  public List<Branch> call() {
    ListBranchCommand cmd = repo.getJgit().branchList();
    cmd.setListMode(mode.jgit);
    if (contains != null) {
      cmd.setContains(new ResolveService(repo).toRevisionString(contains));
    }

    try {
      return cmd.call().stream()
          .map(ref -> JGitUtil.resolveBranch(repo, ref.getName()))
          .collect(Collectors.toList());
    } catch (GitAPIException e) {
      Throwing.sneakyThrow(e);
      return null;
    }
  }

  public Mode getMode() {
    return mode;
  }

  /**
   * Which branches to return.
   */
  public void setMode(Mode mode) {
    this.mode = mode;
  }

  public Object getContains() {
    return contains;
  }

  /**
   * Commit ref branches must contains
   */
  public void setContains(Object contains) {
    this.contains = contains;
  }

  public static enum Mode {
    ALL(ListBranchCommand.ListMode.ALL), REMOTE(ListBranchCommand.ListMode.REMOTE), LOCAL(null);

    private Mode(ListBranchCommand.ListMode jgit) {
      this.jgit = jgit;
    }

    private final ListBranchCommand.ListMode jgit;
  }
}
