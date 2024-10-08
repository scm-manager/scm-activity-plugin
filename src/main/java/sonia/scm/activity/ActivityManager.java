/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package sonia.scm.activity;

//~--- non-JDK imports --------------------------------------------------------

import com.github.legman.Subscribe;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Striped;
import com.google.inject.Inject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.EagerSingleton;
import sonia.scm.SCMContext;
import sonia.scm.activity.collector.ChangesetCollector;
import sonia.scm.activity.collector.ChangesetCollectorFactory;
import sonia.scm.cache.Cache;
import sonia.scm.cache.CacheManager;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.PostReceiveRepositoryHookEvent;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryEvent;
import sonia.scm.repository.RepositoryManager;
import sonia.scm.repository.api.RepositoryServiceFactory;
import sonia.scm.security.Role;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;

//~--- JDK imports ------------------------------------------------------------

/**
 *
 * @author Sebastian Sdorra
 */
@Extension
@EagerSingleton
public class ActivityManager
{

  /** Field description */
  public static final String CACHE_REPOSITORY =
    "sonia.cache.activity.repository";

  /** Field description */
  public static final String CACHE_USER = "sonia.cache.activity.user";

  /** the logger for ActivityManager */
  private static final Logger logger =
    LoggerFactory.getLogger(ActivityManager.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   *
   * @param cacheManager
   * @param repositoryServiceFactory
   * @param repositoryManager
   */
  @Inject
  public ActivityManager(CacheManager cacheManager,
    RepositoryServiceFactory repositoryServiceFactory,
    RepositoryManager repositoryManager)
  {
    this.userCache = cacheManager.getCache(CACHE_USER);
    this.repositoryCache = cacheManager.getCache(CACHE_REPOSITORY);
    this.repositoryServiceFactory = repositoryServiceFactory;
    this.repositoryManager = repositoryManager;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param event
   */
  @Subscribe
  public void onEvent(PostReceiveRepositoryHookEvent event)
  {
    this.clearCaches(event.getRepository());
  }

  /**
   * Method description
   *
   *
   * @param event
   */
  @Subscribe
  public void onEvent(RepositoryEvent event)
  {
    if (event.getEventType().isPost())
    {
      this.clearCaches(event.getItem());
    }
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param pageSize
   *
   * @return
   */
  public Activities getLatestActivity(int pageSize)
  {
    Subject subject = SecurityUtils.getSubject();
    String name;

    if (subject.hasRole(Role.USER))
    {
      name = (String) subject.getPrincipal();
    }
    else
    {
      name = SCMContext.USER_ANONYMOUS;
    }

    Activities activities = userCache.get(name);

    if (activities == null)
    {
      activities = getActivities(pageSize);
      userCache.put(name, activities);
    }

    return activities;
  }

  /**
   * Method description
   *
   *
   * @param pageSize
   *
   * @return
   */
  @VisibleForTesting
  Activities getActivities(int pageSize)
  {
    Set<Activity> activitySet = Sets.newHashSet();
    Collection<Repository> repositories = repositoryManager.getAll();

    for (Repository r : repositories)
    {
      appendActivities(activitySet, r, pageSize);
    }

    List<Activity> activityList =
      Ordering.from(ActivityComparator.INSTANCE).sortedCopy(activitySet);

    if (activitySet.size() > pageSize)
    {
      activityList = ImmutableList.copyOf(activityList.subList(0, pageSize));
    }

    return new Activities(activityList);
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param repository
   *
   * @return
   */
  @VisibleForTesting
  protected ChangesetCollector createCollector(Repository repository)
  {
    return ChangesetCollectorFactory.createCollector(repository,
      repositoryCache);
  }

  /**
   * Method description
   *
   *
   *
   * @param activityList
   * @param repository
   * @param pageSize
   */
  private void appendActivities(Set<Activity> activityList,
    Repository repository, int pageSize)
  {
    Lock lock = locks.get(repository.getId());

    ChangesetCollector collector = createCollector(repository);

    try
    {
      lock.lock();

      if (logger.isDebugEnabled())
      {
        logger.debug("collect changesets for repository {} with collector {}",
          repository.getName(), collector.getClass());
      }

      collector.collectChangesets(repositoryServiceFactory, activityList,
        repository, pageSize);
    }
    finally
    {
      lock.unlock();
    }
  }

  /**
   * Method description
   *
   *
   * @param repository
   */
  private void clearCaches(Repository repository)
  {
    logger.debug("clear caches beacause repository {} has changed",
      repository.getId());
    userCache.clear();
    repositoryCache.remove(repository.getId());
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private final Striped<Lock> locks = Striped.lazyWeakLock(10);

  /** Field description */
  private final Cache<String, ActivitySet> repositoryCache;

  /** Field description */
  private final RepositoryManager repositoryManager;

  /** Field description */
  private final RepositoryServiceFactory repositoryServiceFactory;

  /** Field description */
  private final Cache<String, Activities> userCache;
}
