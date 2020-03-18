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
package sonia.scm.activity.api;

import com.google.inject.Inject;
import de.otto.edison.hal.Embedded;
import de.otto.edison.hal.Links;
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

import javax.ws.rs.core.UriInfo;

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
