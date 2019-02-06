package sonia.scm.activity.api;


import de.otto.edison.hal.Embedded;
import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Links;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sonia.scm.api.v2.resources.ChangesetDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuppressWarnings("squid:S2160") // No semantics for equals here
public class ActivityDto extends HalRepresentation {

  private String repositoryName;

  private String repositoryNamespace;

  private String repositoryType;

  public ActivityDto(Links links, Embedded embedded) {
    super(links, embedded);
  }

  public ChangesetDto extractChangeset(){
    List<ChangesetDto> changeset = getEmbedded().getItemsBy("changeset", ChangesetDto.class);
    return changeset.get(0);
  }
}
