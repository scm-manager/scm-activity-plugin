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
        for (Changeset c : changesetList)
        {
          activitySet.add(new Activity(repository, c));
        }
      }
    }
  }
}
