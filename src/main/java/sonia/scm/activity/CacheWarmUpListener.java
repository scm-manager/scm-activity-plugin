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

import com.google.inject.Inject;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.activity.api.ActivityResource;
import sonia.scm.plugin.Extension;
import sonia.scm.web.security.AdministrationContext;
import sonia.scm.web.security.PrivilegedAction;

/**
 *
 * @author Sebastian Sdorra
 */
@Extension
public class CacheWarmUpListener implements ServletContextListener
{

  /** Field description */
  private static final String PROPERTY_DISABLE =
    "sonia.scm.activity.disable-warmup";

  /** Field description */
  private static final String THREADNAME = "ActivityCacheWarmUp";

  /**
   * the logger for CacheWarmUpListener
   */
  private static final Logger logger =
    LoggerFactory.getLogger(CacheWarmUpListener.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param adminContext
   */
  @Inject
  public CacheWarmUpListener(AdministrationContext adminContext)
  {
    this.adminContext = adminContext;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param sce
   */
  @Override
  public void contextDestroyed(ServletContextEvent sce)
  {

    // do nothing
  }

  /**
   * Method description
   *
   *
   * @param sce
   */
  @Override
  public void contextInitialized(ServletContextEvent sce)
  {
    if (Boolean.getBoolean(PROPERTY_DISABLE))
    {
      logger.warn("skip cache warm up, because the property {} is set", PROPERTY_DISABLE);
    }
    else
    {
      logger.info("start cache warm up");
      adminContext.runAsAdmin(CacheWarmUpAction.class);
    }
  }

  //~--- inner classes --------------------------------------------------------

  /**
   * Class description
   *
   *
   * @version        Enter version here..., 14/05/07
   * @author         Enter your name here...
   */
  private static class CacheWarmUpAction implements PrivilegedAction
  {

    /**
     * Constructs ...
     *
     *
     * @param activityManager
     */
    @Inject
    public CacheWarmUpAction(ActivityManager activityManager)
    {
      this.activityManager = activityManager;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     */
    @Override
    public void run()
    {
      Subject subject = SecurityUtils.getSubject();
      Runnable runnable = subject.associateWith(new Runnable()
      {

        @Override
        public void run()
        {
          activityManager.getLatestActivity(ActivityResource.PAGE_SIZE);
        }
      });

      new Thread(runnable, THREADNAME).start();
    }

    //~--- fields -------------------------------------------------------------

    /** Field description */
    private final ActivityManager activityManager;
  }


  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private final AdministrationContext adminContext;
}
