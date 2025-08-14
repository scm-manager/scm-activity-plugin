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

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.apache.shiro.SecurityUtils;
import sonia.scm.activity.api.ActivityResource;
import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.HalEnricher;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.api.v2.resources.Index;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.plugin.Extension;

import java.util.Set;

@Extension
@Enrich(Index.class)
public class IndexLinkEnricher implements HalEnricher {

  private final Provider<ScmPathInfoStore> scmPathInfoStoreProvider;
  private final Set<Obstacle> obstacles;

  @Inject
  public IndexLinkEnricher(Provider<ScmPathInfoStore> scmPathInfoStoreProvider, Set<Obstacle> obstacles) {
    this.scmPathInfoStoreProvider = scmPathInfoStoreProvider;
    this.obstacles = obstacles;
  }

  @Override
  public void enrich(HalEnricherContext context, HalAppender appender) {
    if (obstacles.isEmpty() && SecurityUtils.getSubject().isAuthenticated()) {
      LinkBuilder linkBuilder = new LinkBuilder(scmPathInfoStoreProvider.get().get(), ActivityResource.class);
      String href = linkBuilder.method("getLatestActivity").parameters().href();
      appender.appendLink("activity", href);
    }
  }
}
