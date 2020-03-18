/**
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

import com.google.inject.Inject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.activity.api.ActivityResource;
import sonia.scm.plugin.Extension;
import sonia.scm.web.security.AdministrationContext;
import sonia.scm.web.security.PrivilegedAction;

//~--- JDK imports ------------------------------------------------------------

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

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
