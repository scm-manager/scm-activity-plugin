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

import com.google.common.collect.ImmutableSet;
import sonia.scm.activity.ActivitySet;
import sonia.scm.cache.Cache;
import sonia.scm.repository.Repository;

import java.util.Set;

/**
 *
 * @author Sebastian Sdorra
 */
public class ChangesetCollectorFactory
{

  /** Field description */
  private static final Set<String> NON_COMBINED_BRANCH = ImmutableSet.of("git");

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param repository
   * @param cache
   *
   * @return
   */
  public static ChangesetCollector createCollector(Repository repository,
    Cache<String, ActivitySet> cache)
  {
    ChangesetCollector collector;

    if (NON_COMBINED_BRANCH.contains(repository.getType()))
    {
      collector = new NonCombinedBranchCollector(cache);
    }
    else
    {
      collector = new CombinedBranchCollector();
    }

    return collector;
  }
}
