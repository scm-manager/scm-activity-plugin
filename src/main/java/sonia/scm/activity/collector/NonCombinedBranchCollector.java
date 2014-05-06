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



package sonia.scm.activity.collector;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import sonia.scm.activity.Activity;
import sonia.scm.activity.ActivitySet;
import sonia.scm.cache.Cache;
import sonia.scm.repository.Branch;
import sonia.scm.repository.Branches;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.ChangesetPagingResult;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryException;
import sonia.scm.repository.api.LogCommandBuilder;
import sonia.scm.repository.api.RepositoryService;
import sonia.scm.repository.api.RepositoryServiceFactory;
import sonia.scm.util.Util;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.util.List;
import java.util.Set;

/**
 *
 * @author Sebastian Sdorra
 */
public class NonCombinedBranchCollector extends AbstractChangesetCollector
{

  /**
   * Constructs ...
   *
   *
   * @param activitiesCache
   */
  public NonCombinedBranchCollector(Cache<String, ActivitySet> activitiesCache)
  {
    this.activitiesCache = activitiesCache;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param repositoryServiceFactory
   * @param activitySet
   * @param repository
   * @param pageSize
   */
  @Override
  public void collectChangesets(
    RepositoryServiceFactory repositoryServiceFactory,
    Set<Activity> activitySet, Repository repository, int pageSize)
  {

    ActivitySet activities = activitiesCache.get(repository.getId());

    if (activities == null)
    {
      activities = new ActivitySet(pageSize);
      super.collectChangesets(repositoryServiceFactory, activities, repository,
        pageSize);
      activitiesCache.put(repository.getId(), activities);
    }

    activitySet.addAll(activities);
  }

  /**
   * Method description
   *
   *
   * @param repositoryService
   * @param activitySet
   * @param repository
   * @param pageSize
   *
   * @throws IOException
   * @throws RepositoryException
   */
  @Override
  protected void collectChangesets(RepositoryService repositoryService,
    Set<Activity> activitySet, Repository repository, int pageSize)
    throws IOException, RepositoryException
  {
    Branches branches = repositoryService.getBranchesCommand().getBranches();

    if (branches != null)
    {
      //J-
      LogCommandBuilder log = repositoryService.getLogCommand()
                                               .setDisableCache(true);

      ChangesetPagingResult cpr = log.setPagingStart(0)
                                     .setPagingLimit(pageSize)
                                     .getChangesets();
      //J+

      String defaultBranch = getBranch(cpr);

      appendChangesets(activitySet, repository, cpr);

      for (Branch branch : branches)
      {
        String name = branch.getName();

        if (Strings.isNullOrEmpty(defaultBranch) ||!name.equals(defaultBranch))
        {
          cpr = log.setPagingStart(0).setPagingLimit(pageSize).setBranch(
            name).getChangesets();

          prepareChangesetPagingResult(cpr, name);
          appendChangesets(activitySet, repository, cpr);
        }
      }
    }
  }

  /**
   * Method description
   *
   *
   * @param cpr
   * @param branch
   */
  private void prepareChangesetPagingResult(ChangesetPagingResult cpr,
    String branch)
  {
    if (cpr != null)
    {
      List<String> list = Lists.newArrayList(branch);

      for (Changeset c : cpr)
      {
        c.setBranches(list);
      }
    }
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param cpr
   *
   * @return
   */
  private String getBranch(ChangesetPagingResult cpr)
  {
    String branch = null;
    List<Changeset> changesets = cpr.getChangesets();

    if (Util.isNotEmpty(changesets))
    {
      Changeset c = changesets.get(0);

      if (c != null)
      {
        List<String> defaultBranches = c.getBranches();

        if (Util.isNotEmpty(defaultBranches))
        {
          branch = defaultBranches.get(0);
        }
      }
    }

    return branch;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private final Cache<String, ActivitySet> activitiesCache;
}
