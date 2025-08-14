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
