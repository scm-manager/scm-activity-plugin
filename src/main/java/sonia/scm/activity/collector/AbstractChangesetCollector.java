/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
