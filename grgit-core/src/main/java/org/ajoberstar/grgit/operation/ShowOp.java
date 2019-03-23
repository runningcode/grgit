package org.ajoberstar.grgit.operation;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.ajoberstar.grgit.Commit;
import org.ajoberstar.grgit.CommitDiff;
import org.ajoberstar.grgit.Repository;
import org.ajoberstar.grgit.service.ResolveService;
import org.ajoberstar.grgit.util.JGitUtil;
import org.ajoberstar.grgit.util.Throwing;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;

/**
 * Show changes made in a commit. Returns changes made in commit in the form of {@link CommitDiff}.
 *
 * @see <a href="http://ajoberstar.org/grgit/grgit-show.html">grgit-show</a>
 * @see <a href="http://git-scm.com/docs/git-show">git-show Manual Page</a>
 * @since 1.2.0
 */
public class ShowOp implements Callable<CommitDiff> {
  private final Repository repo;

  private Object commit;

  public ShowOp(Repository repo) {
    this.repo = repo;
  }

  public CommitDiff call() {
    if (commit == null) {
      throw new IllegalArgumentException("You must specify which commit to show");
    }

    String revString = new ResolveService(repo).toRevisionString(commit);
    RevCommit commitId = (RevCommit) JGitUtil.resolveRevObject(repo, revString);
    RevCommit parentId = JGitUtil.resolveParents(repo, commitId).stream()
        .map(RevCommit.class::cast)
        .findFirst()
        .orElse(null);

    Commit commit = JGitUtil.resolveCommit(repo, commitId);

    TreeWalk walk = new TreeWalk(repo.getJgit().getRepository());
    walk.setRecursive(true);

    try {
      if (parentId != null) {
        walk.addTree(parentId.getTree());
        walk.addTree(commitId.getTree());
        List initialEntries = DiffEntry.scan(walk);
        RenameDetector detector = new RenameDetector(repo.getJgit().getRepository());
        detector.addAll(initialEntries);
        List<DiffEntry> entries = detector.compute();
        Map<DiffEntry.ChangeType, List<DiffEntry>> entriesByType = entries.stream().collect(Collectors.groupingBy(DiffEntry::getChangeType));

        return new CommitDiff(
            commit,
            entriesByType.getOrDefault(DiffEntry.ChangeType.ADD, Collections.emptyList()).stream().map(DiffEntry::getNewPath).collect(Collectors.toSet()),
            entriesByType.getOrDefault(DiffEntry.ChangeType.COPY, Collections.emptyList()).stream().map(DiffEntry::getNewPath).collect(Collectors.toSet()),
            entriesByType.getOrDefault(DiffEntry.ChangeType.MODIFY, Collections.emptyList()).stream().map(DiffEntry::getNewPath).collect(Collectors.toSet()),
            entriesByType.getOrDefault(DiffEntry.ChangeType.DELETE, Collections.emptyList()).stream().map(DiffEntry::getOldPath).collect(Collectors.toSet()),
            entriesByType.getOrDefault(DiffEntry.ChangeType.RENAME, Collections.emptyList()).stream().map(DiffEntry::getNewPath).collect(Collectors.toSet()),
            entriesByType.getOrDefault(DiffEntry.ChangeType.RENAME, Collections.emptyList()).stream().collect(Collectors.toMap(DiffEntry::getOldPath, DiffEntry::getNewPath)));
      } else {
        walk.addTree(commitId.getTree());

        Set<String> added = new LinkedHashSet<>();
        while (walk.next()) {
          added.add(walk.getPathString());
        }
        return new CommitDiff(commit, added, null, null, null, null, null);
      }
    } catch (IOException e) {
      Throwing.sneakyThrow(e);
      return null;
    }
  }

  public Object getCommit() {
    return commit;
  }

  /**
   * The commit to show
   *
   * @see {@link ResolveService#toRevisionString(Object)}
   */
  public void setCommit(Object commit) {
    this.commit = commit;
  }
}
