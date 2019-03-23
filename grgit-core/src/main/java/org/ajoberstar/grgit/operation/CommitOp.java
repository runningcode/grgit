package org.ajoberstar.grgit.operation;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import org.ajoberstar.grgit.Commit;
import org.ajoberstar.grgit.Person;
import org.ajoberstar.grgit.Repository;
import org.ajoberstar.grgit.util.JGitUtil;
import org.ajoberstar.grgit.util.Throwing;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 * Commits staged changes to the repository. Returns the new {@code Commit}.
 *
 * @see <a href="http://ajoberstar.org/grgit/grgit-commit.html">grgit-commit</a>
 * @see <a href="http://git-scm.com/docs/git-commit">git-commit Manual Reference.</a>
 * @since 0.1.0
 */
public class CommitOp implements Callable<Commit> {
  private final Repository repo;

  private String message;
  private String reflogComment;
  private Person committer;
  private Person author;
  private Set<String> paths = new LinkedHashSet<String>();
  private boolean all = false;
  private boolean amend = false;

  public CommitOp(Repository repo) {
    this.repo = repo;
  }

  public Commit call() {
    final CommitCommand cmd = repo.getJgit().commit();
    cmd.setMessage(message);
    cmd.setReflogComment(reflogComment);
    if (committer != null) {
      cmd.setCommitter(new PersonIdent(committer.getName(), committer.getEmail()));
    }

    if (author != null) {
      cmd.setAuthor(new PersonIdent(author.getName(), author.getEmail()));
    }

    paths.forEach(cmd::setOnly);
    if (all) {
      cmd.setAll(all);
    }

    cmd.setAmend(amend);

    try {
      RevCommit commit = cmd.call();
      return JGitUtil.convertCommit(repo, commit);
    } catch (GitAPIException e) {
      Throwing.sneakyThrow(e);
      return null;
    }
  }

  public String getMessage() {
    return message;
  }

  /**
   * Commit message.
   */
  public void setMessage(String message) {
    this.message = message;
  }

  public String getReflogComment() {
    return reflogComment;
  }

  /**
   * Comment to put in the reflog.
   */
  public void setReflogComment(String reflogComment) {
    this.reflogComment = reflogComment;
  }

  public Person getCommitter() {
    return committer;
  }

  /**
   * The person who committed the changes. Uses the git-config setting, if {@code null}.
   */
  public void setCommitter(Person committer) {
    this.committer = committer;
  }

  public Person getAuthor() {
    return author;
  }

  /**
   * The person who authored the changes. Uses the git-config setting, if {@code null}.
   */
  public void setAuthor(Person author) {
    this.author = author;
  }

  public Set<String> getPaths() {
    return paths;
  }

  /**
   * Only include these paths when committing. {@code null} to include all staged changes.
   */
  public void setPaths(Set<String> paths) {
    this.paths = paths;
  }

  public boolean isAll() {
    return all;
  }

  /**
   * Commit changes to all previously tracked files, even if they aren't staged, if {@code true}.
   */
  public void setAll(boolean all) {
    this.all = all;
  }

  public boolean isAmend() {
    return amend;
  }

  /**
   * {@code true} if the previous commit should be amended with these changes.
   */
  public void setAmend(boolean amend) {
    this.amend = amend;
  }
}
