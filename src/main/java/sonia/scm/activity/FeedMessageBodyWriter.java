/**
 * Copyright (c) 2010, Sebastian Sdorra
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */



package sonia.scm.activity;

//~--- non-JDK imports --------------------------------------------------------

import com.google.inject.Inject;

import sonia.scm.config.ScmConfiguration;
import sonia.scm.repository.Changeset;

//~--- JDK imports ------------------------------------------------------------

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author Sebastian Sdorra
 */
@Provider
@Produces({ MediaType.APPLICATION_ATOM_XML, "application/rss+xml" })
public class FeedMessageBodyWriter implements MessageBodyWriter<Activities>
{

  /** Field description */
  public static final String DESCRIPTION = "Activities of ";

  /** Field description */
  public static final String DESCRIPTION_TEMPLATE =
    "<strong>{0} ({1})</strong>: {2}";

  /** Field description */
  public static final String MEDIA_TYPE_HTML = "text/html";

  /** Field description */
  public static final String MEDIA_TYPE_RSS = "application/rss+xml";

  /** Field description */
  public static final String TITLE = "Activities";

  /** Field description */
  public static final String TYPE_ATOM = "atom_1.0";

  /** Field description */
  public static final String TYPE_RSS = "rss_2.0";

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   *
   * @param configuration
   */
  @Inject
  public FeedMessageBodyWriter(ScmConfiguration configuration)
  {
    this.configuration = configuration;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param activities
   * @param type
   * @param genericType
   * @param annotations
   * @param mediaType
   * @param httpHeaders
   * @param entityStream
   *
   * @throws IOException
   * @throws WebApplicationException
   */
  @Override
  public void writeTo(Activities activities, Class<?> type, Type genericType,
                      Annotation[] annotations, MediaType mediaType,
                      MultivaluedMap<String, Object> httpHeaders,
                      OutputStream entityStream)
          throws IOException, WebApplicationException
  {
    SyndFeed feed = new SyndFeedImpl();

    if (mediaType.equals(MediaType.APPLICATION_ATOM_XML_TYPE))
    {
      feed.setFeedType(TYPE_ATOM);
    }
    else if (mediaType.toString().equals(MEDIA_TYPE_RSS))
    {
      feed.setFeedType(TYPE_RSS);
    }
    else
    {
      throw new WebApplicationException(Status.BAD_REQUEST);
    }

    feed.setTitle(TITLE);
    feed.setLink(configuration.getBaseUrl());
    feed.setDescription(DESCRIPTION.concat(configuration.getBaseUrl()));

    List<SyndEntry> entries = new ArrayList<SyndEntry>();
    List<Activity> activityList = activities.getActivities();

    for (Activity activity : activityList)
    {
      SyndEntry entry = new SyndEntryImpl();
      Changeset changeset = activity.getChangeset();
      Date date = new Date(changeset.getDate());

      entry.setAuthor(changeset.getAuthor().getName());
      entry.setTitle(changeset.getDescription());
      entry.setPublishedDate(date);
      entry.setUpdatedDate(date);
      entry.setLink(createLink(activity));
      entry.setDescription(createDescription(activity));
      entries.add(entry);
    }

    feed.setEntries(entries);

    SyndFeedOutput output = new SyndFeedOutput();

    try
    {
      output.output(feed, new OutputStreamWriter(entityStream));
    }
    catch (FeedException ex)
    {
      throw new WebApplicationException(ex);
    }
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   *
   * @param activities
   * @param type
   * @param genericType
   * @param annotations
   * @param mediaType
   *
   * @return
   */
  @Override
  public long getSize(Activities activities, Class<?> type, Type genericType,
                      Annotation[] annotations, MediaType mediaType)
  {
    return -1;
  }

  /**
   * Method description
   *
   *
   * @param type
   * @param genericType
   * @param annotations
   * @param mediaType
   *
   * @return
   */
  @Override
  public boolean isWriteable(Class<?> type, Type genericType,
                             Annotation[] annotations, MediaType mediaType)
  {
    return Activities.class.isAssignableFrom(type);
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param activity
   *
   * @return
   */
  private SyndContent createDescription(Activity activity)
  {
    SyndContent content = new SyndContentImpl();

    content.setType(MEDIA_TYPE_HTML);
    content.setValue(MessageFormat.format(DESCRIPTION_TEMPLATE,
            activity.getRepositoryName(), activity.getRepositoryType(),
            activity.getChangeset().getDescription()));

    return content;
  }

  /**
   * Method description
   *
   *
   * @param activity
   *
   * @return
   */
  private String createLink(Activity activity)
  {
//    RepositoryUrlProvider repoProvider =
//      UrlProviderFactory.createUrlProvider(configuration.getBaseUrl(),
//        UrlProviderFactory.TYPE_WUI).getRepositoryUrlProvider();
//
//    return repoProvider.getDiffUrl(activity.getRepositoryId(),
//                                   activity.getChangeset().getId());
    // TODO replace with DTO
    return null;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private ScmConfiguration configuration;
}
