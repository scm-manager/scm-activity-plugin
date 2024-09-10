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


import sonia.scm.activity.Activity;
import sonia.scm.repository.ChangesetPagingResult;
import sonia.scm.repository.Repository;
import sonia.scm.repository.api.RepositoryService;

import java.io.IOException;
import java.util.Set;

/**
 *
 * @author Sebastian Sdorra
 */
public class CombinedBranchCollector extends AbstractChangesetCollector
{


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
  @Override
  protected void collectChangesets(RepositoryService repositoryService,
    Set<Activity> activitySet, Repository repository, int pageSize)
    throws IOException
  {
    ChangesetPagingResult cpr =
      repositoryService.getLogCommand().setPagingLimit(
        pageSize).getChangesets();

    appendChangesets(activitySet, repository, cpr);
  }
}
