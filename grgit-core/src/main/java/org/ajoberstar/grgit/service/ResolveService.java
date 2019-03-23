package org.ajoberstar.grgit.service;

import java.io.IOException;

import groovy.lang.GString;
import org.ajoberstar.grgit.Branch;
import org.ajoberstar.grgit.Commit;
import org.ajoberstar.grgit.Ref;
import org.ajoberstar.grgit.Repository;
import org.ajoberstar.grgit.Tag;
import org.ajoberstar.grgit.util.JGitUtil;
import org.ajoberstar.grgit.util.Throwing;
import org.eclipse.jgit.lib.ObjectId;

/**
 * Convenience methods to resolve various objects.
 *
 * @see <a href="http://ajoberstar.org/grgit/grgit-resolve.html">grgit-resolve</a>
 * @since 0.2.2
 */
public class ResolveService {
  public ResolveService(Repository repository) {
    this.repository = repository;
  }

  /**
   * Resolves an object ID from the given object. Can handle any of the following types:
   *
   * <ul>
   * <li>{@link Commit}</li>
   * <li>{@link Tag}</li>
   * <li>{@link Branch}</li>
   * <li>{@link Ref}</li>
   * </ul>
   *
   * @param object the object to resolve
   * @return the corresponding object id
   */
  public String toObjectId(Object object) {
    if (object == null) {
      return null;
    } else if (object instanceof Commit) {
      return ((Commit) object).getId();
    } else if (object instanceof Ref) {
      try {
        return ObjectId.toString(repository.getJgit().getRepository().exactRef(((Ref) object).getFullName()).getObjectId());
      } catch (IOException e) {
        Throwing.sneakyThrow(e);
        return null;
      }
    } else {
      throwIllegalArgument(object);
    }

    return null;
  }

  /**
   * Resolves a commit from the given object. Can handle any of the following types:
   *
   * <ul>
   * <li>{@link Commit}</li>
   * <li>{@link Tag}</li>
   * <li>{@link Branch}</li>
   * <li>{@link String}</li>
   * <li>{@link GString}</li>
   * </ul>
   *
   * <p>
   * String arguments can be in the format of any
   * <a href="http://git-scm.com/docs/gitrevisions.html">Git revision string</a>.
   * </p>
   *
   * @param object the object to resolve
   * @return the corresponding commit
   */
  public Commit toCommit(Object object) {
    if (object == null) {
      return ((Commit) (object));
    } else if (object instanceof Commit) {
      return ((Commit) (object));
    } else if (object instanceof Tag) {
      return ((Tag) object).getCommit();
    } else if (object instanceof Branch) {
      return JGitUtil.resolveCommit(repository, ((Branch) object).getFullName());
    } else if (object instanceof String || object instanceof GString) {
      return JGitUtil.resolveCommit(repository, object.toString());
    } else {
      throwIllegalArgument(object);
    }

    return null;
  }

  /**
   * Resolves a branch from the given object. Can handle any of the following types:
   * <ul>
   * <li>{@link Branch}</li>
   * <li>{@link String}</li>
   * <li>{@link GString}</li>
   * </ul>
   *
   * @param object the object to resolve
   * @return the corresponding commit
   */
  public Branch toBranch(Object object) {
    if (object == null) {
      return ((Branch) (object));
    } else if (object instanceof Branch) {
      return ((Branch) (object));
    } else if (object instanceof String || object instanceof GString) {
      return JGitUtil.resolveBranch(repository, object.toString());
    } else {
      throwIllegalArgument(object);
    }

    return null;
  }

  /**
   * Resolves a branch name from the given object. Can handle any of the following types:
   * <ul>
   * <li>{@link String}</li>
   * <li>{@link GString}</li>
   * <li>{@link Branch}</li>
   * </ul>
   *
   * @param object the object to resolve
   * @return the corresponding branch name
   */
  public String toBranchName(Object object) {
    if (object == null) {
      return null;
    } else if (object instanceof String || object instanceof GString) {
      return object.toString();
    } else if (object instanceof Branch) {
      return ((Branch) object).getFullName();
    } else {
      throwIllegalArgument(object);
    }

    return null;
  }

  /**
   * Resolves a tag from the given object. Can handle any of the following types:
   * <ul>
   * <li>{@link Tag}</li>
   * <li>{@link String}</li>
   * <li>{@link GString}</li>
   * </ul>
   *
   * @param object the object to resolve
   * @return the corresponding commit
   */
  public Tag toTag(Object object) {
    if (object == null) {
      return ((Tag) (object));
    } else if (object instanceof Tag) {
      return ((Tag) (object));
    } else if (object instanceof String || object instanceof GString) {
      return JGitUtil.resolveTag(repository, object.toString());
    } else {
      throwIllegalArgument(object);
    }

    return null;
  }

  /**
   * Resolves a tag name from the given object. Can handle any of the following types:
   * <ul>
   * <li>{@link String}</li>
   * <li>{@link GString}</li>
   * <li>{@link Tag}</li>
   * </ul>
   *
   * @param object the object to resolve
   * @return the corresponding tag name
   */
  public String toTagName(Object object) {
    if (object == null) {
      return null;
    } else if (object instanceof String || object instanceof GString) {
      return object.toString();
    } else if (object instanceof Tag) {
      return ((Tag) object).getFullName();
    } else {
      throwIllegalArgument(object);
    }

    return null;
  }

  /**
   * Resolves a revision string that corresponds to the given object. Can handle any of the following
   * types:
   * <ul>
   * <li>{@link Commit}</li>
   * <li>{@link Tag}</li>
   * <li>{@link Branch}</li>
   * <li>{@link String}</li>
   * <li>{@link GString}</li>
   * </ul>
   *
   * @param object the object to resolve
   * @return the corresponding commit
   */
  public String toRevisionString(Object object) {
    if (object == null) {
      return null;
    } else if (object instanceof Commit) {
      return ((Commit) object).getId();
    } else if (object instanceof Tag) {
      return ((Tag) object).getFullName();
    } else if (object instanceof Branch) {
      return ((Branch) object).getFullName();
    } else if (object instanceof String || object instanceof GString) {
      return object.toString();
    } else {
      throwIllegalArgument(object);
    }

    return null;
  }

  private void throwIllegalArgument(final Object object) {
    throw new IllegalArgumentException("Can\'t handle the following object (" + String.valueOf(object) + ") of class (" + String.valueOf(object.getClass()) + ")");
  }

  private final Repository repository;
}
