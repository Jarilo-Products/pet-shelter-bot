package pro.sky.petshelterbot.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.petshelterbot.model.Person;
import pro.sky.petshelterbot.model.Report;
import pro.sky.petshelterbot.repository.ReportRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

  @Mock
  private ReportRepository reportRepository;

  @InjectMocks
  private ReportService out;

  @Test
  public void getReportByPersonAndDateTest() {
    Person personWithReport = new Person();
    personWithReport.setChatId(1L);
    Person personWithoutReport = new Person();
    personWithoutReport.setChatId(2L);

    Report report = new Report();
    report.setId(1L);
    report.setPerson(personWithReport);

    when(reportRepository.getReportByPersonAndDate(personWithReport, LocalDate.now()))
        .thenReturn(Optional.of(report));
    when(reportRepository.getReportByPersonAndDate(personWithoutReport, LocalDate.now()))
        .thenReturn(Optional.empty());

    Optional<Report> existingReportOptional = out.getReportByPersonAndDate(personWithReport, LocalDate.now());
    assertTrue(existingReportOptional.isPresent());
    Report existingReport = existingReportOptional.get();
    assertEquals(existingReport.getPerson().getChatId(), 1L);
    assertEquals(existingReport.getId(), 1L);

    assertTrue(out.getReportByPersonAndDate(personWithoutReport, LocalDate.now()).isEmpty());
  }

  @Test
  public void getReportsByDateTest() {
    Report report = new Report();
    report.setId(1L);

    when(reportRepository.getReportsByDate(any()))
        .thenReturn(Collections.singletonList(report));

    List<Report> reports = out.getReportsByDate(LocalDate.now());

    assertEquals(reports.size(), 1);
    assertEquals(reports.get(0).getId(), 1L);
  }

  @Test
  public void saveTest() {
    Report report = new Report();
    report.setId(1L);

    out.save(report);

    verify(reportRepository).save(report);
  }

}
