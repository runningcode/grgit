package org.ajoberstar.grgit.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.ajoberstar.grgit.Commit;
import org.ajoberstar.grgit.Repository;
import org.ajoberstar.grgit.service.ResolveService;
import org.ajoberstar.grgit.util.JGitUtil;
import org.ajoberstar.grgit.util.Throwing;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;

/**
 * Gets a log of commits in the repository. Returns a list of {@link Commit}s. Since a Git history
 * is not necessarilly a line, these commits may not be in a strict order.
 *
 * @see <a href="http://ajoberstar.org/grgit/grgit-log.html">grgit-log</a>
 * @see <a href="http://git-scm.com/docs/git-log">git-log Manual Page</a>
 * @since 0.1.0
 */
public class LogOp implements Callable<List<Commit>> {
  private final Repository repo;

  private List includes = new ArrayList();
  private List excludes = new ArrayList();
  private List<String> paths = new ArrayList();
  private int skipCommits = -1;
  private int maxCommits = -1;

  public LogOp(Repository repo) {
    this.repo = repo;
  }



  public List<Commit> call() {
    LogCommand cmd = repo.getJgit().log();
    ResolveService resolve = new ResolveService(repo);
    Function<Object, ObjectId> toObjectId = rev -> {
      String revstr = resolve.toRevisionString(rev);
      return JGitUtil.resolveRevObject(repo, revstr, true).getId();
    };

    includes.stream().map(toObjectId).forEach(Throwing.consumer(cmd::add));
    excludes.stream().map(toObjectId).forEach(Throwing.consumer(cmd::not));
    paths.forEach(cmd::addPath);
    cmd.setSkip(skipCommits);
    cmd.setMaxCount(maxCommits);

    try {
      return StreamSupport.stream(cmd.call().spliterator(), false)
          .map(commit -> JGitUtil.convertCommit(repo, commit))
          .collect(Collectors.toList());
    } catch (GitAPIException e) {
      Throwing.sneakyThrow(e);
      return null;
    }
  }

  public List getIncludes() {
    return includes;
  }

  /**
   * @see {@link ResolveService#toRevisionString(Object)}
   */
  public void setIncludes(List includes) {
    this.includes = includes;
  }

  public List getExcludes() {
    return excludes;
  }

  /**
   * @see {@link ResolveService#toRevisionString(Object)}
   */
  public void setExcludes(List excludes) {
    this.excludes = excludes;
  }

  public List getPaths() {
    return paths;
  }

  public void setPaths(List paths) {
    this.paths = paths;
  }

  public int getSkipCommits() {
    return skipCommits;
  }

  public void setSkipCommits(int skipCommits) {
    this.skipCommits = skipCommits;
  }

  public int getMaxCommits() {
    return maxCommits;
  }

  public void setMaxCommits(int maxCommits) {
    this.maxCommits = maxCommits;
  }

  public void range(Object since, Object until) {
    excludes.add(since);
    includes.add(until);
  }
}
