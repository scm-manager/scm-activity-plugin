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
import sonia.scm.repository.api.LogCommandBuilder;
import sonia.scm.repository.api.RepositoryService;
import sonia.scm.repository.api.RepositoryServiceFactory;
import sonia.scm.util.Util;

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
   */
  @Override
  protected void collectChangesets(RepositoryService repositoryService,
    Set<Activity> activitySet, Repository repository, int pageSize)
    throws IOException
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
