package org.ajoberstar.grgit.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.ajoberstar.grgit.Commit;
import org.ajoberstar.grgit.Repository;
import org.ajoberstar.grgit.util.JGitUtil;
import org.ajoberstar.grgit.util.Throwing;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.eclipse.jgit.api.RevertCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 * Revert one or more commits. Returns the new HEAD {@link Commit}.
 *
 * @see <a href="http://ajoberstar.org/grgit/grgit-revert.html">grgit-revert</a>
 * @see <a href="http://git-scm.com/docs/git-revert">git-revert Manual Page</a>
 * @since 0.1.0
 */
public class RevertOp implements Callable<Commit> {
  private final Repository repo;

  private List<Object> commits = new ArrayList<Object>();

  public RevertOp(Repository repo) {
    this.repo = repo;
  }

  public Commit call() {
    final RevertCommand cmd = repo.getJgit().revert();
    commits.stream()
        .map(revstr -> JGitUtil.resolveObject(repo, revstr.toString()))
        .forEach(cmd::include);

    try {
      RevCommit commit = cmd.call();
      if (DefaultGroovyMethods.asBoolean(cmd.getFailingResult())) {
        throw new IllegalStateException("Could not merge reverted commits (conflicting files can be retrieved with a call to grgit.status()): " + String.valueOf(cmd.getFailingResult()));
      }
      return JGitUtil.convertCommit(repo, commit);
    } catch (GitAPIException e) {
      Throwing.sneakyThrow(e);
      return null;
    }
  }

  public List<Object> getCommits() {
    return commits;
  }

  /**
   * List of commits to revert.
   *
   * @see {@link ResolveService#toRevisionString(Object)}
   */
  public void setCommits(List<Object> commits) {
    this.commits = commits;
  }
}
