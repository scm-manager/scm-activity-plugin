package sonia.scm.activity.api;

import com.google.inject.Inject;
import de.otto.edison.hal.Links;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sonia.scm.activity.Activities;
import sonia.scm.activity.Activity;
import sonia.scm.api.v2.resources.ChangesetToChangesetDtoMapper;
import sonia.scm.api.v2.resources.InstantAttributeMapper;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.repository.api.RepositoryService;
import sonia.scm.repository.api.RepositoryServiceFactory;

import javax.ws.rs.core.UriInfo;

@Mapper
public abstract class ActivityMapper implements InstantAttributeMapper {

  private LinkBuilder linkBuilder;

  @Inject
  private ChangesetToChangesetDtoMapper toChangesetDtoMapper;

  @Inject
  private RepositoryServiceFactory serviceFactory;


  public ActivityDto map(Activity activity){
    if ( activity == null ) {
      return null;
    }

    ActivityDto activityDto = new ActivityDto();

    try(RepositoryService repositoryService = serviceFactory.create(activity.getRepositoryId())){
      activityDto.setChangeset(toChangesetDtoMapper.map(activity.getChangeset(), repositoryService.getRepository()));
    }

    activityDto.setRepositoryId( activity.getRepositoryId() );
    activityDto.setRepositoryName( activity.getRepositoryName() );
    activityDto.setRepositoryNamespace( activity.getRepositoryNamespace() );
    activityDto.setRepositoryType( activity.getRepositoryType() );

    return activityDto;
  }

  @Mapping(target = "attributes", ignore = true)
  public abstract ActivitiesDto map(Activities activities);

  public ActivityMapper using(UriInfo uriInfo) {
    this.linkBuilder = new LinkBuilder(uriInfo::getBaseUri, ActivityResource.class);
    return this;
  }

  @AfterMapping
  void addLinks(@MappingTarget ActivitiesDto dto) {
    Links.Builder links = Links.linkingTo();
    links.self(linkBuilder.method("getLatestActivity").parameters().href());
    dto.add(links.build());
  }

}
