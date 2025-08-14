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

package sonia.scm.activity;

//~--- non-JDK imports --------------------------------------------------------

import sonia.scm.repository.Changeset;
import sonia.scm.util.Util;

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
