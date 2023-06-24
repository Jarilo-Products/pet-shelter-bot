package pro.sky.petshelterbot.service;

import org.springframework.stereotype.Service;
import pro.sky.petshelterbot.model.Person;
import pro.sky.petshelterbot.model.Report;
import pro.sky.petshelterbot.repository.ReportRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReportService {

  private final ReportRepository reportRepository;

  public ReportService(ReportRepository reportRepository) {
    this.reportRepository = reportRepository;
  }

  public Optional<Report> getReportByPersonAndData(Person person, LocalDate localDate) {
    return reportRepository.getReportByPersonAndDate(person, localDate);
  }

  public List<Report> getReportsByDate(LocalDate date) {
    return reportRepository.getReportsByDate(date);
  }

  public void save(Report report) {
    reportRepository.save(report);
  }

}
