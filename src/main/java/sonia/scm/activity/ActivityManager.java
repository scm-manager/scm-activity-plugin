/**
 * Copyright (c) 2010, Sebastian Sdorra
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */



package sonia.scm.activity;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.io.Closeables;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.HandlerEvent;
import sonia.scm.cache.Cache;
import sonia.scm.cache.CacheManager;
import sonia.scm.repository.CacheClearHook;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.ChangesetPagingResult;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryListener;
import sonia.scm.repository.RepositoryManager;
import sonia.scm.repository.api.RepositoryService;
import sonia.scm.repository.api.RepositoryServiceFactory;
import sonia.scm.user.User;
import sonia.scm.util.AssertUtil;
import sonia.scm.util.SecurityUtil;
import sonia.scm.web.security.WebSecurityContext;

//~--- JDK imports ------------------------------------------------------------

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Sebastian Sdorra
 */
@Singleton
public class ActivityManager extends CacheClearHook
  implements RepositoryListener
{

  /** Field description */
  public static final String CACHE_NAME = "sonia.cache.activity";

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
   * @param changesetViewerUtil
   * @param securityContextProvider
   */
  @Inject
  public ActivityManager(CacheManager cacheManager,
    RepositoryServiceFactory repositoryServiceFactory,
    RepositoryManager repositoryManager,
    Provider<WebSecurityContext> securityContextProvider)
  {
    this.activityCache = cacheManager.getCache(String.class, Activities.class,
      CACHE_NAME);
    this.repositoryServiceFactory = repositoryServiceFactory;
    this.repositoryManager = repositoryManager;
    this.securityContextProvider = securityContextProvider;
    init(repositoryManager, activityCache);
    repositoryManager.addListener(this);
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param repository
   * @param event
   */
  @Override
  public void onEvent(Repository repository, HandlerEvent event)
  {
    clearCache();
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
    User user = SecurityUtil.getCurrentUser(securityContextProvider);

    AssertUtil.assertIsNotNull(user);

    Activities activities = activityCache.get(user.getName());

    if (activities == null)
    {
      activities = getActivities(pageSize);
      activityCache.put(user.getName(), activities);
    }

    return activities;
  }

  //~--- methods --------------------------------------------------------------

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
    RepositoryService repositoryService = null;

    try
    {
      repositoryService = repositoryServiceFactory.create(repository);

      ChangesetPagingResult cpr =
        repositoryService.getLogCommand().setPagingLimit(
          pageSize).getChangesets();

      if (cpr != null)
      {
        List<Changeset> changesetList = cpr.getChangesets();

        if (changesetList != null)
        {
          for (Changeset c : changesetList)
          {
            activityList.add(new Activity(repository, c));
          }
        }
      }
    }
    catch (Exception ex)
    {
      logger.error(
        "could retrieve changesets for repository ".concat(
          repository.getName()), ex);
    }
    finally
    {
      Closeables.closeQuietly(repositoryService);
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
  private Activities getActivities(int pageSize)
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
      activityList = activityList.subList(0, pageSize);
    }

    return new Activities(activityList);
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private Cache<String, Activities> activityCache;

  /** Field description */
  private RepositoryManager repositoryManager;

  /** Field description */
  private RepositoryServiceFactory repositoryServiceFactory;

  /** Field description */
  private Provider<WebSecurityContext> securityContextProvider;
}
