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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.activity.Activity;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.ChangesetPagingResult;
import sonia.scm.repository.Repository;
import sonia.scm.repository.api.RepositoryService;
import sonia.scm.repository.api.RepositoryServiceFactory;

import java.io.IOException;
import java.util.List;
import java.util.Set;

//~--- JDK imports ------------------------------------------------------------

/**
 *
 * @author Sebastian Sdorra
 */
public abstract class AbstractChangesetCollector implements ChangesetCollector
{

  /**
   * the logger for AbstractChangesetCollector
   */
  private static final Logger logger =
    LoggerFactory.getLogger(AbstractChangesetCollector.class);

  //~--- methods --------------------------------------------------------------

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
  protected abstract void collectChangesets(
    RepositoryService repositoryService, Set<Activity> activitySet,
    Repository repository, int pageSize)
    throws IOException;

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
    try(RepositoryService repositoryService = repositoryServiceFactory.create(repository))
    {
      collectChangesets(repositoryService, activitySet, repository, pageSize);
    }
    catch (Exception ex)
    {
      logger.error(
        "could not retrieve changesets for repository ".concat(
          repository.getName()), ex);
    }
  }

  /**
   * Method description
   *
   *
   * @param activitySet
   * @param repository
   * @param cpr
   */
  protected void appendChangesets(Set<Activity> activitySet,
    Repository repository, ChangesetPagingResult cpr)
  {
    if (cpr != null)
    {
      List<Changeset> changesetList = cpr.getChangesets();

      if (changesetList != null)
      {
        for (Changeset changeset : changesetList)
        {
          Activity activity = new Activity();
          activity.setChangeset(changeset);
          activity.setRepositoryId(repository.getId());
          activity.setRepositoryName(repository.getName());
          activity.setRepositoryNamespace(repository.getNamespace());
          activity.setRepositoryType(repository.getType());
          activitySet.add(activity);
        }
      }
    }
  }
}
