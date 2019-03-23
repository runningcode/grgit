package org.ajoberstar.grgit.util;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.ajoberstar.grgit.Branch;
import org.ajoberstar.grgit.Commit;
import org.ajoberstar.grgit.Person;
import org.ajoberstar.grgit.Remote;
import org.ajoberstar.grgit.Repository;
import org.ajoberstar.grgit.Status;
import org.ajoberstar.grgit.Tag;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.lib.BranchConfig;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.RemoteConfig;

/**
 * Utility class to perform operations against JGit objects.
 *
 * @since 0.1.0
 */
public class JGitUtil {
  private JGitUtil() {
    // don't instantiate
  }

  /**
   * Resolves a JGit {@code ObjectId} using the given revision string.
   *
   * @param repo the Grgit repository to resolve the object from
   * @param revstr the revision string to use
   * @return the resolved object
   */
  public static ObjectId resolveObject(Repository repo, String revstr) {
    try {
      return repo.getJgit().getRepository().resolve(revstr);
    } catch (IOException e) {
      Throwing.sneakyThrow(e);
      return null;
    }
  }

  /**
   * Resolves a JGit {@code RevObject} using the given revision string.
   *
   * @param repo the Grgit repository to resolve the object from
   * @param revstr the revision string to use
   * @param peel whether or not to peel the resolved object
   * @return the resolved object
   */
  public static RevObject resolveRevObject(Repository repo, String revstr, boolean peel) {
    try {
      ObjectId id = resolveObject(repo, revstr);
      RevWalk walk = new RevWalk(repo.getJgit().getRepository());
      RevObject rev = walk.parseAny(id);
      return peel ? walk.peel(rev) : rev;
    } catch (IOException e) {
      Throwing.sneakyThrow(e);
      return null;
    }
  }

  /**
   * Resolves a JGit {@code RevObject} using the given revision string.
   *
   * @param repo the Grgit repository to resolve the object from
   * @param revstr the revision string to use
   * @return the resolved object
   */
  public static RevObject resolveRevObject(Repository repo, String revstr) {
    return JGitUtil.resolveRevObject(repo, revstr, false);
  }

  /**
   * Resolves the parents of an object.
   *
   * @param repo the Grgit repository to resolve the parents from
   * @param id the object to get the parents of
   * @return the parents of the commit
   */
  public static Set<ObjectId> resolveParents(Repository repo, ObjectId id) {
    try {
      final RevWalk walk = new RevWalk(repo.getJgit().getRepository());
      RevCommit rev = walk.parseCommit(id);
      return Arrays.stream(rev.getParents())
          .map(Throwing.function(walk::parseCommit))
          .collect(Collectors.toSet());
    } catch (IOException e) {
      Throwing.sneakyThrow(e);
      return null;
    }
  }

  /**
   * Resolves a Grgit {@code Commit} using the given revision string.
   *
   * @param repo the Grgit repository to resolve the commit from
   * @param revstr the revision string to use
   * @return the resolved commit
   */
  public static Commit resolveCommit(Repository repo, String revstr) {
    ObjectId id = resolveObject(repo, revstr);
    return resolveCommit(repo, id);
  }

  /**
   * Resolves a Grgit {@code Commit} using the given object.
   *
   * @param repo the Grgit repository to resolve the commit from
   * @param id the object id of the commit to resolve
   * @return the resolved commit
   */
  public static Commit resolveCommit(Repository repo, ObjectId id) {
    try {
      RevWalk walk = new RevWalk(repo.getJgit().getRepository());
      return convertCommit(repo, walk.parseCommit(id));
    } catch (IOException e) {
      Throwing.sneakyThrow(e);
      return null;
    }
  }

  /**
   * Converts a JGit commit to a Grgit commit.
   *
   * @param rev the JGit commit to convert
   * @return a corresponding Grgit commit
   */
  public static Commit convertCommit(Repository repo, RevCommit rev) {
    try (ObjectReader reader = repo.getJgit().getRepository().newObjectReader()) {
      String id = ObjectId.toString(rev);
      String abbreviatedId = reader.abbreviate(rev).name();
      PersonIdent committerPerson = rev.getCommitterIdent();
      Person committer = new Person(committerPerson.getName(), committerPerson.getEmailAddress());
      PersonIdent authorPerson = rev.getAuthorIdent();
      Person author = new Person(authorPerson.getName(), authorPerson.getEmailAddress());

      Instant instant = Instant.ofEpochSecond(rev.getCommitTime());
      ZoneId zone = Optional.ofNullable(rev.getCommitterIdent().getTimeZone())
          .map(TimeZone::toZoneId)
          .orElse(ZoneOffset.UTC);
      ZonedDateTime dateTime = ZonedDateTime.ofInstant(instant, zone);

      String fullMessage = rev.getFullMessage();
      String shortMessage = rev.getShortMessage();
      List<String> parentIds = Arrays.stream(rev.getParents()).map(oid -> ObjectId.toString(oid)).collect(Collectors.toList());
      return new Commit(id, abbreviatedId, parentIds, author, committer, dateTime, fullMessage, shortMessage);
    } catch (IOException e) {
      Throwing.sneakyThrow(e);
      return null;
    }
  }

  /**
   * Resolves a Grgit tag from a name.
   *
   * @param repo the Grgit repository to resolve from
   * @param name the name of the tag to resolve
   * @return the resolved tag
   */
  public static Tag resolveTag(Repository repo, String name) {
    try {
      Ref ref = repo.getJgit().getRepository().findRef(name);
      return resolveTag(repo, ref);
    } catch (IOException e) {
      Throwing.sneakyThrow(e);
      return null;
    }
  }

  /**
   * Resolves a Grgit Tag from a JGit ref.
   *
   * @param repo the Grgit repository to resolve from
   * @param ref the JGit ref to resolve
   * @return the resolved tag
   */
  public static Tag resolveTag(Repository repo, Ref ref) {
    String fullName = ref.getName();
    try {
      RevWalk walk = new RevWalk(repo.getJgit().getRepository());
      RevTag rev = walk.parseTag(ref.getObjectId());
      RevCommit target = (RevCommit) walk.peel(rev);
      walk.parseBody(rev.getObject());
      Commit commit = convertCommit(repo, target);
      PersonIdent taggerPerson = rev.getTaggerIdent();
      Person tagger = new Person(taggerPerson.getName(), taggerPerson.getEmailAddress());
      String fullMessage = rev.getFullMessage();
      String shortMessage = rev.getShortMessage();

      Instant instant = rev.getTaggerIdent().getWhen().toInstant();
      ZoneId zone = Optional.ofNullable(rev.getTaggerIdent().getTimeZone())
          .map(TimeZone::toZoneId)
          .orElse(ZoneOffset.UTC);
      ZonedDateTime dateTime = ZonedDateTime.ofInstant(instant, zone);

      return new Tag(commit, tagger, fullName, fullMessage, shortMessage, dateTime);
    } catch (IncorrectObjectTypeException e) {
      Commit commit = resolveCommit(repo, ref.getObjectId());
      return new Tag(commit, null, fullName, null, null, null);
    } catch (IOException e) {
      Throwing.sneakyThrow(e);
      return null;
    }
  }

  /**
   * Resolves a Grgit branch from a name.
   *
   * @param repo the Grgit repository to resolve from
   * @param name the name of the branch to resolve
   * @return the resolved branch
   */
  public static Branch resolveBranch(Repository repo, String name) {
    try {
      Ref ref = repo.getJgit().getRepository().findRef(name);
      return resolveBranch(repo, ref);
    } catch (IOException e) {
      Throwing.sneakyThrow(e);
      return null;
    }
  }

  /**
   * Resolves a Grgit branch from a JGit ref.
   *
   * @param repo the Grgit repository to resolve from
   * @param ref the JGit ref to resolve
   * @return the resolved branch or {@code null} if the {@code ref} is {@code null}
   */
  public static Branch resolveBranch(Repository repo, Ref ref) {
    if (ref == null) {
      return null;
    }

    String fullName = ref.getName();
    String shortName = org.eclipse.jgit.lib.Repository.shortenRefName(fullName);
    Config config = repo.getJgit().getRepository().getConfig();
    BranchConfig branchConfig = new BranchConfig(config, shortName);

    Branch trackingBranch = Optional.ofNullable(branchConfig.getTrackingBranch())
        .map(tracking -> resolveBranch(repo, tracking))
        .orElse(null);

    return new Branch(fullName, trackingBranch);
  }

  /**
   * Converts a JGit status to a Grgit status.
   *
   * @param jgitStatus the status to convert
   * @return the converted status
   */
  public static Status convertStatus(org.eclipse.jgit.api.Status jgitStatus) {
    Status.Changes staged = new Status.Changes(jgitStatus.getAdded(), jgitStatus.getChanged(), jgitStatus.getRemoved());
    Status.Changes unstaged = new Status.Changes(jgitStatus.getUntracked(), jgitStatus.getModified(), jgitStatus.getMissing());
    Set<String> conflicts = jgitStatus.getConflicting();
    return new Status(staged, unstaged, conflicts);
  }

  /**
   * Converts a JGit remote to a Grgit remote.
   *
   * @param rc the remote config to convert
   * @return the converted remote
   */
  public static Remote convertRemote(RemoteConfig rc) {
    String name = rc.getName();
    String url = rc.getURIs().stream().map(Object::toString).findFirst().orElse(null);
    String pushUrl = rc.getPushURIs().stream().map(Object::toString).findFirst().orElse(null);
    List<String> fetchRefSpecs = rc.getFetchRefSpecs().stream().map(Object::toString).collect(Collectors.toList());
    List<String> pushRefSpecs = rc.getPushRefSpecs().stream().map(Object::toString).collect(Collectors.toList());
    boolean mirror = rc.isMirror();
    return new Remote(name, url, pushUrl, fetchRefSpecs, pushRefSpecs, mirror);
  }

  /**
   * Checks if {@code base} is an ancestor of {@code tip}.
   *
   * @param repo the repository to look in
   * @param base the version that might be an ancestor
   * @param tip the tip version
   * @since 0.2.2
   */
  public static boolean isAncestorOf(Repository repo, Commit base, Commit tip) {
    try {
      org.eclipse.jgit.lib.Repository jgit = repo.getJgit().getRepository();
      RevWalk revWalk = new RevWalk(jgit);
      RevCommit baseCommit = revWalk.lookupCommit(jgit.resolve(base.getId()));
      RevCommit tipCommit = revWalk.lookupCommit(jgit.resolve(tip.getId()));
      return revWalk.isMergedInto(baseCommit, tipCommit);
    } catch (IOException e) {
      Throwing.sneakyThrow(e);
      return false;
    }
  }
}
