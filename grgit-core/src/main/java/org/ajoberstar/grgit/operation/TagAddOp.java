package org.ajoberstar.grgit.operation;

import java.util.concurrent.Callable;

import org.ajoberstar.grgit.Person;
import org.ajoberstar.grgit.Repository;
import org.ajoberstar.grgit.Tag;
import org.ajoberstar.grgit.service.ResolveService;
import org.ajoberstar.grgit.util.JGitUtil;
import org.ajoberstar.grgit.util.Throwing;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.eclipse.jgit.api.TagCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;

/**
 * Adds a tag to the repository. Returns the newly created {@link Tag}.
 *
 * @see <a href="http://ajoberstar.org/grgit/grgit-tag.html">grgit-tag</a>
 * @see <a href="http://git-scm.com/docs/git-tag">git-tag Manual Page</a>
 * @since 0.2.0
 */
public class TagAddOp implements Callable<Tag> {
  private final Repository repo;

  private String name;
  private String message;
  private Person tagger;
  private boolean annotate = true;
  private boolean force = false;
  private Object pointsTo;

  public TagAddOp(Repository repo) {
    this.repo = repo;
  }

  public Tag call() {
    TagCommand cmd = repo.getJgit().tag();
    cmd.setName(name);
    cmd.setMessage(message);
    if (DefaultGroovyMethods.asBoolean(tagger)) {
      cmd.setTagger(new PersonIdent(tagger.getName(), tagger.getEmail()));
    }

    cmd.setAnnotated(annotate);
    cmd.setForceUpdate(force);
    if (DefaultGroovyMethods.asBoolean(pointsTo)) {
      String revstr = new ResolveService(repo).toRevisionString(pointsTo);
      cmd.setObjectId(JGitUtil.resolveRevObject(repo, revstr));
    }

    try {
      Ref ref = cmd.call();
      return JGitUtil.resolveTag(repo, ref);
    } catch (GitAPIException e) {
      Throwing.sneakyThrow(e);
      return null;
    }
  }

  public String getName() {
    return name;
  }

  /**
   * The name of the tag to create.
   */
  public void setName(String name) {
    this.name = name;
  }

  public String getMessage() {
    return message;
  }

  /**
   * The message to put on the tag.
   */
  public void setMessage(String message) {
    this.message = message;
  }

  public Person getTagger() {
    return tagger;
  }

  /**
   * The person who created the tag.
   */
  public void setTagger(Person tagger) {
    this.tagger = tagger;
  }

  public boolean isAnnotate() {
    return annotate;
  }

  /**
   * {@code true} (the default) if an annotated tag should be created, {@code false} otherwise.
   */
  public void setAnnotate(boolean annotate) {
    this.annotate = annotate;
  }

  public boolean isForce() {
    return force;
  }

  /**
   * {@code true} to overwrite an existing tag, {@code false} (the default) otherwise
   */
  public void setForce(boolean force) {
    this.force = force;
  }

  public Object getPointsTo() {
    return pointsTo;
  }

  /**
   * The commit the tag should point to.
   *
   * @see {@link ResolveService#toRevisionString(Object)}
   */
  public void setPointsTo(Object pointsTo) {
    this.pointsTo = pointsTo;
  }
}
