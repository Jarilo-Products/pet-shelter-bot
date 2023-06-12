package pro.sky.petshelterbot.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pro.sky.petshelterbot.model.enums.HealthStatus;
import pro.sky.petshelterbot.model.enums.Sex;
import pro.sky.petshelterbot.model.enums.Status;
import pro.sky.petshelterbot.model.enums.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "pets")
@NoArgsConstructor
@Getter
@Setter
public class Pet {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private Long id;

  @NotNull
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Type type;

  @NotBlank
  @Column(nullable = false)
  private String name;

  @NotNull
  private LocalDate birthdate;

  @NotNull
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Status status;

  @NotNull
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private HealthStatus healthStatus;

  @NotNull
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Sex sex;

}
