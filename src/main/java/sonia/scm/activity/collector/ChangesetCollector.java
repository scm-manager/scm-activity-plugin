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
import sonia.scm.repository.Repository;
import sonia.scm.repository.api.RepositoryServiceFactory;

import java.util.Set;

/**
 *
 * @author Sebastian Sdorra
 */
public interface ChangesetCollector
{

  /**
   * Method description
   *
   *
   *
   * @param repositoryServiceFactory
   * @param activityList
   * @param repository
   * @param pageSize
   */
  public void collectChangesets(
    RepositoryServiceFactory repositoryServiceFactory,
    Set<Activity> activityList, Repository repository, int pageSize);
}
