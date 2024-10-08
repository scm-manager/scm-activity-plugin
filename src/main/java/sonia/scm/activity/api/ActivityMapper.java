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

package sonia.scm.activity.api;

import com.google.inject.Inject;
import de.otto.edison.hal.Embedded;
import de.otto.edison.hal.Links;
import jakarta.ws.rs.core.UriInfo;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ObjectFactory;
import sonia.scm.activity.Activities;
import sonia.scm.activity.Activity;
import sonia.scm.api.v2.resources.ChangesetToChangesetDtoMapper;
import sonia.scm.api.v2.resources.HalAppenderMapper;
import sonia.scm.api.v2.resources.InstantAttributeMapper;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.repository.api.RepositoryService;
import sonia.scm.repository.api.RepositoryServiceFactory;

@Mapper
public abstract class ActivityMapper extends HalAppenderMapper implements InstantAttributeMapper {

  private LinkBuilder linkBuilder;

  @Inject
  private ChangesetToChangesetDtoMapper toChangesetDtoMapper;

  @Inject
  private RepositoryServiceFactory serviceFactory;

  @Mapping(target = "attributes", ignore = true)
  public abstract ActivityDto map(Activity activity);

  @Mapping(target = "attributes", ignore = true)
  public abstract ActivitiesDto map(Activities activities);

  public ActivityMapper using(UriInfo uriInfo) {
    this.linkBuilder = new LinkBuilder(uriInfo::getBaseUri, ActivityResource.class);
    return this;
  }

  @ObjectFactory
  ActivityDto createDto( Activity activity) {

    Embedded.Builder embeddedBuilder = Embedded.embeddedBuilder();
    try(RepositoryService repositoryService = serviceFactory.create(activity.getRepositoryId())){
      embeddedBuilder.with("changeset", toChangesetDtoMapper.map(activity.getChangeset(), repositoryService.getRepository()));
    }
    return new ActivityDto(null, embeddedBuilder.build());
  }

  @AfterMapping
  void addLinks(@MappingTarget ActivitiesDto dto) {
    Links.Builder links = Links.linkingTo();
    links.self(linkBuilder.method("getLatestActivity").parameters().href());
    dto.add(links.build());
  }

}
