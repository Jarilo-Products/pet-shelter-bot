package pro.sky.petshelterbot.model;

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
import java.time.LocalDate;

@Entity
@Table(name = "pets")
@NoArgsConstructor
@Getter
@Setter
public class Pet {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Type type;

  @Column(nullable = false)
  private String name;

  private LocalDate birthdate;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Status status;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private HealthStatus healthStatus;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Sex sex;

}
