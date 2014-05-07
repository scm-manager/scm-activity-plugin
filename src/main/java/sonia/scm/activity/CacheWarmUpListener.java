/**
 * Copyright (c) 2010, Sebastian Sdorra All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of SCM-Manager;
 * nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */



package sonia.scm.activity;

//~--- non-JDK imports --------------------------------------------------------

import com.google.inject.Inject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.plugin.ext.Extension;
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
      logger.warn("skip cache warm up, because the property {} is set");
    }
    else
    {
      logger.info("start cache warmp up");
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
