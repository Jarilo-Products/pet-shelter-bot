package pro.sky.petshelterbot.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "reports")
@NoArgsConstructor
@Getter
@Setter
public class Report {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "pet_id", nullable = false)
  private Pet pet;

  @ManyToOne
  @JoinColumn(name = "chat_id", nullable = false)
  private Person person;

  private String fileId;

  @Column(nullable = false)
  private String text;

  @Column(nullable = false)
  private LocalDate date;

}
