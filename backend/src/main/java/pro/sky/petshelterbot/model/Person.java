package pro.sky.petshelterbot.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "persons")
@NoArgsConstructor
@Getter
@Setter
public class Person {
  @Id
  @Column(nullable = false)
  private Long chatId;

  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String lastName;

  private String middleName;

  @Column(nullable = false)
  private LocalDate birthdate;

  private String phone;

  private String email;

  private String address;

  @ManyToOne
  @JoinColumn(name = "pet_id")
  private Pet pet;

  private LocalDate probationEnd;

  private LocalDate lastReportDate;

  @Column(nullable = false)
  private Boolean isVolunteer;

  public Person(Long chatId,
                String firstName,
                String lastName,
                String middleName,
                LocalDate birthdate,
                String phone,
                String email,
                String address) {
    this.chatId = chatId;
    this.firstName = firstName;
    this.lastName = lastName;
    this.middleName = middleName;
    this.birthdate = birthdate;
    this.phone = phone;
    this.email = email;
    this.address = address;
    this.isVolunteer = false;
  }
}
