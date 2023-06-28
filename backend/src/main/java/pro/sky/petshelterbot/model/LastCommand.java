package pro.sky.petshelterbot.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pro.sky.petshelterbot.model.enums.Type;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "last_commands")
@NoArgsConstructor
@Getter
@Setter
public class LastCommand {

  @Id
  private Long chatId;

  private String lastCommand;

  private Boolean isClosed;

  @Enumerated(EnumType.STRING)
  private Type activeType;

  private Integer lastMessageId;

}
