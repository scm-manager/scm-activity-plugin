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

import com.google.common.base.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Sebastian Sdorra
 */
@XmlRootElement(name = "activities")
@XmlAccessorType(XmlAccessType.FIELD)
public class Activities implements Serializable
{

  /** Field description */
  private static final long serialVersionUID = -5004740584154846070L;

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   */
  public Activities() {}

  /**
   * Constructs ...
   *
   *
   * @param activities
   */
  public Activities(List<Activity> activities)
  {
    this.activities = activities;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param obj
   *
   * @return
   */
  @Override
  public boolean equals(Object obj)
  {
    if (obj == null)
    {
      return false;
    }

    if (getClass() != obj.getClass())
    {
      return false;
    }

    final Activities other = (Activities) obj;

    return Objects.equal(activities, other.activities);
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public int hashCode()
  {
    return Objects.hashCode(activities);
  }

  /**
   * Method description
   *
   *
   * @return
   */


  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  public List<Activity> getActivities()
  {
    return activities;
  }

  //~--- set methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param activities
   */
  public void setActivities(List<Activity> activities)
  {
    this.activities = activities;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  @XmlElement(name = "activitiy")
  @XmlElementWrapper(name = "activities")
  private List<Activity> activities;
}
