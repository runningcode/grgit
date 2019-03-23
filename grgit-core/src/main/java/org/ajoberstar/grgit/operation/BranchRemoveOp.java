package org.ajoberstar.grgit.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.ajoberstar.grgit.Repository;
import org.ajoberstar.grgit.service.ResolveService;
import org.ajoberstar.grgit.util.Throwing;
import org.eclipse.jgit.api.DeleteBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * Removes one or more branches from the repository. Returns a list of the fully qualified branch
 * names that were removed.
 *
 * @see <a href="http://ajoberstar.org/grgit/grgit-branch.html">grgit-branch</a>
 * @see <a href="http://git-scm.com/docs/git-branch">git-branch Manual Page</a>
 * @since 0.2.0
 */
public class BranchRemoveOp implements Callable<List<String>> {
  private final Repository repo;

  private List<Object> names = new ArrayList<>();
  private boolean force = false;

  public BranchRemoveOp(Repository repo) {
    this.repo = repo;
  }

  public List<String> call() {
    DeleteBranchCommand cmd = repo.getJgit().branchDelete();
    String[] branchNames = names.stream()
        .map(name -> new ResolveService(repo).toBranchName(name))
        .toArray(String[]::new);
    cmd.setBranchNames(branchNames);
    cmd.setForce(force);

    try {
      return cmd.call();
    } catch (GitAPIException e) {
      Throwing.sneakyThrow(e);
      return null;
    }
  }

  public List getNames() {
    return names;
  }

  /**
   * List of all branche names to remove.
   *
   * @see {@link ResolveService#toBranchName(Object)}
   */
  public void setNames(List names) {
    this.names = names;
  }

  public boolean isForce() {
    return force;
  }

  /**
   * If {@code false} (the default), only remove branches that are merged into another branch. If
   * {@code true} will delete regardless.
   */
  public void setForce(boolean force) {
    this.force = force;
  }
}
