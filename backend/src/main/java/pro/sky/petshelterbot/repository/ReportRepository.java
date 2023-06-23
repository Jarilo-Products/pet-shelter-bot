package pro.sky.petshelterbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.petshelterbot.model.Person;
import pro.sky.petshelterbot.model.Report;

import java.time.LocalDate;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {

  Optional<Report> getReportByPersonAndDate(Person person, LocalDate date);
}
