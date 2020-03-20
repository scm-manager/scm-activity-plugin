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

import com.google.inject.Inject;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;
import sonia.scm.activity.api.ActivitiesDto;
import sonia.scm.activity.api.ActivityDto;
import sonia.scm.api.v2.resources.ChangesetDto;
import sonia.scm.config.ScmConfiguration;
import sonia.scm.plugin.PluginLoader;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//~--- JDK imports ------------------------------------------------------------

/**
 *
 * @author Sebastian Sdorra
 */
@Provider
@Produces({MediaType.APPLICATION_ATOM_XML, "application/rss+xml"})
public class FeedMessageBodyWriter implements MessageBodyWriter<ActivitiesDto> {

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

  public static final String CHANGESET_UI_LINK_TEMPLATE = "{0}/repo/{1}/{2}/changeset/{3}";

  //~--- constructors ---------------------------------------------------------
    private PluginLoader pluginLoader;

  /**
   * Constructs ...
   *
   *
   *
   * @param pluginLoader
   * @param configuration
   */
  @Inject
  public FeedMessageBodyWriter(PluginLoader pluginLoader, ScmConfiguration configuration) {
    this.pluginLoader = pluginLoader;
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
  public void writeTo(ActivitiesDto activities, Class<?> type, Type genericType,
                      Annotation[] annotations, MediaType mediaType,
                      MultivaluedMap<String, Object> httpHeaders,
                      OutputStream entityStream)
    throws IOException {
    ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
    try {
      Thread.currentThread().setContextClassLoader(pluginLoader.getUberClassLoader());
      SyndFeed feed = new SyndFeedImpl();

      if (mediaType.equals(MediaType.APPLICATION_ATOM_XML_TYPE)) {
        feed.setFeedType(TYPE_ATOM);
      } else if (mediaType.toString().equals(MEDIA_TYPE_RSS)) {
        feed.setFeedType(TYPE_RSS);
      } else {
        throw new WebApplicationException(Status.BAD_REQUEST);
      }

      feed.setTitle(TITLE);
      feed.setLink(configuration.getBaseUrl());
      feed.setDescription(DESCRIPTION.concat(configuration.getBaseUrl()));

      List<SyndEntry> entries = new ArrayList<>();
      List<ActivityDto> activityList = activities.getActivities();

      for (ActivityDto activity : activityList) {
        SyndEntry entry = new SyndEntryImpl();
        ChangesetDto changeset = activity.extractChangeset();
        Instant changesetDate = changeset.getDate();

        entry.setAuthor(changeset.getAuthor() == null? "": changeset.getAuthor().getName());
        entry.setTitle(changeset.getDescription());
        Date date = Date.from(changesetDate);
        entry.setPublishedDate(date);
        entry.setUpdatedDate(date);
        entry.setLink(createLink(activity));
        entry.setDescription(createDescription(activity));
        entries.add(entry);
      }

      feed.setEntries(entries);

      SyndFeedOutput output = new SyndFeedOutput();

      try {
        output.output(feed, new OutputStreamWriter(entityStream));
      } catch (FeedException ex) {
        throw new WebApplicationException(ex);
      }

    } finally {
      Thread.currentThread().setContextClassLoader(contextClassLoader);
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
  public long getSize(ActivitiesDto activities, Class<?> type, Type genericType,
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
    return ActivitiesDto.class.isAssignableFrom(type);
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
  private SyndContent createDescription(ActivityDto activity)
  {
    SyndContent content = new SyndContentImpl();

    content.setType(MEDIA_TYPE_HTML);
    content.setValue(MessageFormat.format(DESCRIPTION_TEMPLATE,
            activity.getRepositoryName(), activity.getRepositoryType(),
            activity.extractChangeset().getDescription()));

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
  private String createLink(ActivityDto activity) {
    return MessageFormat.format(CHANGESET_UI_LINK_TEMPLATE,
      configuration.getBaseUrl(),
      activity.getRepositoryNamespace(),
      activity.getRepositoryName(),
      activity.extractChangeset().getId());
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private ScmConfiguration configuration;
}
