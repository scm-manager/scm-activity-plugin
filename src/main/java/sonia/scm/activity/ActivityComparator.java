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

package sonia.scm.activity;

//~--- non-JDK imports --------------------------------------------------------

import sonia.scm.repository.Changeset;
import sonia.scm.util.Util;

//~--- JDK imports ------------------------------------------------------------

import java.util.Comparator;
import java.util.Date;

/**
 *
 * @author Sebastian Sdorra
 */
public class ActivityComparator implements Comparator<Activity>
{

  /** Field description */
  public static final ActivityComparator INSTANCE = new ActivityComparator();

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   *
   * @param a1
   * @param a2
   *
   * @return
   */
  @Override
  public int compare(Activity a1, Activity a2)
  {
    return Util.compare(getDate(a2), getDate(a1));
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param a
   *
   * @return
   */
  private Date getDate(Activity a)
  {
    Date d = null;

    if (a != null)
    {
      Changeset c = a.getChangeset();

      if (c != null)
      {
        Long time = c.getDate();

        if (time != null)
        {
          d = new Date(time);
        }
      }
    }

    return d;
  }
}
