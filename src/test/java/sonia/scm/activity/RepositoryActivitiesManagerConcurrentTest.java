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

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
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
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("squid:S2699")
// ignore warning of legacy code: "Tests should include assertions"
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
    @SuppressWarnings("squid:S2925")
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
