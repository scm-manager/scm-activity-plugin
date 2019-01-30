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

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import sonia.scm.activity.collector.ChangesetCollector;
import sonia.scm.cache.Cache;
import sonia.scm.cache.CacheManager;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryManager;
import sonia.scm.repository.api.RepositoryServiceFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

//~--- JDK imports ------------------------------------------------------------

/**
 *
 * @author Sebastian Sdorra
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(RepositoryServiceFactory.class)
public class RepositoryActivitiesManagerConcurrentTest
{

  /**
   * Method description
   *
   *
   *
   * @throws ExecutionException
   * @throws InterruptedException
   */
  @Test
  public void testConcurrentAccess()
    throws InterruptedException, ExecutionException
  {
    CacheManager cacheManager = mock(CacheManager.class);
    Cache cache = mock(Cache.class);

    when(cacheManager.getCache(any())).thenReturn(cache);
    when(cache.get(any())).thenReturn(null);

    RepositoryServiceFactory rsf = mock(RepositoryServiceFactory.class);
    RepositoryManager rm = mock(RepositoryManager.class);
    List<Repository> repositories = Lists.newArrayList();

    repositories.add(new Repository("1", "git","1", "1"));
    repositories.add(new Repository("2", "git","2", "2"));
    repositories.add(new Repository("3", "git","3", "3"));

    final Map<String, ChangesetCollector> collectors = Maps.newHashMap();

    collectors.put("1", new ConcurrentChangesetCollector());
    collectors.put("2", new ConcurrentChangesetCollector());
    collectors.put("3", new ConcurrentChangesetCollector());

    when(rm.getAll()).thenReturn(repositories);

    final ActivityManager activityManager = new ActivityManager(cacheManager,
                                              rsf, rm)
    {

      @Override
      protected ChangesetCollector createCollector(Repository repository)
      {
        return collectors.get(repository.getId());
      }
    };

    ExecutorService exec = Executors.newFixedThreadPool(10);

    List<Future<?>> futures = Lists.newArrayList();

    try
    {

      for (int i = 0; i < 100; i++)
      {
        Future<?> f = exec.submit(new Runnable()
        {

          @Override
          public void run()
          {
            activityManager.getActivities(12);
          }
        });

        futures.add(f);
      }

    }
    finally
    {
      exec.shutdown();
    }

    exec.awaitTermination(2, TimeUnit.MINUTES);

    for (Future<?> f : futures)
    {
      try
      {
        f.get();
      }
      catch (ExecutionException ex)
      {
        Throwables.propagateIfInstanceOf(ex.getCause(), AssertionError.class);

        throw ex;
      }
    }
  }

  //~--- inner classes --------------------------------------------------------

  /**
   * Class description
   *
   *
   * @version        Enter version here..., 14/05/06
   * @author         Enter your name here...
   */
  private static class ConcurrentChangesetCollector
    implements ChangesetCollector
  {

    /**
     * Method description
     *
     *
     * @param repositoryServiceFactory
     * @param activityList
     * @param repository
     * @param pageSize
     */
    @Override
    public void collectChangesets(
      RepositoryServiceFactory repositoryServiceFactory,
      Set<Activity> activityList, Repository repository, int pageSize)
    {
      ++counter;

      long nc = counter;

      try
      {
        Thread.sleep(50l);
      }
      catch (InterruptedException ex) {}

      assertEquals(nc, counter);
    }

    //~--- fields -------------------------------------------------------------

    /** Field description */
    private long counter = 0;
  }
}
