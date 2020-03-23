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

import com.google.common.collect.ImmutableSet;

import sonia.scm.cache.Cache;
import sonia.scm.repository.Repository;

//~--- JDK imports ------------------------------------------------------------

import java.util.Set;
import sonia.scm.activity.ActivitySet;

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
