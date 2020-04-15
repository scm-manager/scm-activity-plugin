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

import com.google.inject.util.Providers;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.ScmPathInfo;
import sonia.scm.api.v2.resources.ScmPathInfoStore;

import java.net.URI;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndexLinkEnricherTest {

  @Mock
  ScmPathInfoStore pathInfoStore;
  @Mock
  ScmPathInfo pathInfo;
  @Mock
  HalAppender appender;
  @Mock
  Subject subject;

  @BeforeEach
  void bindSubject() {
    ThreadContext.bind(subject);
  }

  @AfterEach
  void unbindSubject() {
    ThreadContext.unbindSubject();
  }

  @BeforeEach
  void mockPathInfo() {
    lenient().when(pathInfoStore.get()).thenReturn(pathInfo);
    lenient().when(pathInfo.getApiRestUri()).thenReturn(URI.create("/scm"));
  }

  @Test
  void shouldAppendLinkForAuthenticatedUserWithoutObstacle() {
    IndexLinkEnricher enricher = new IndexLinkEnricher(Providers.of(pathInfoStore), emptySet());
    when(subject.isAuthenticated()).thenReturn(true);

    enricher.enrich(null, appender);

    Mockito.verify(appender).appendLink("activity", "/v2/plugins/activity");
  }

  @Test
  void shouldNotAppendLinkForUnauthenticatedUserWithoutObstacle() {
    IndexLinkEnricher enricher = new IndexLinkEnricher(Providers.of(pathInfoStore), emptySet());
    when(subject.isAuthenticated()).thenReturn(false);

    enricher.enrich(null, appender);

    Mockito.verify(appender, never()).appendLink(any(), any());
  }

  @Test
  void shouldNotAppendLinkForAuthenticatedUserWithObstacle() {
    IndexLinkEnricher enricher = new IndexLinkEnricher(Providers.of(pathInfoStore), singleton(new Obstacle() {}));

    enricher.enrich(null, appender);

    Mockito.verify(appender, never()).appendLink(any(), any());
  }
}
