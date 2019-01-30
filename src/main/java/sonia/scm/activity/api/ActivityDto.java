package sonia.scm.activity.api;


import de.otto.edison.hal.HalRepresentation;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sonia.scm.api.v2.resources.ChangesetDto;

@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
public class ActivityDto extends HalRepresentation {

  private ChangesetDto changeset;

  private String repositoryId;

  private String repositoryName;

  private String repositoryNamespace;

  private String repositoryType;

}
