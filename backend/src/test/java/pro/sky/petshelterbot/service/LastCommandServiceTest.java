package pro.sky.petshelterbot.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.petshelterbot.model.LastCommand;
import pro.sky.petshelterbot.repository.LastCommandRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LastCommandServiceTest {

  @Mock
  private LastCommandRepository lastCommandRepository;

  @InjectMocks
  private LastCommandService lastCommandService;

  @Test
  public void getByChatIdTest() {
    LastCommand lastCommand = new LastCommand();
    when(lastCommandRepository.findByChatId(1L)).thenReturn(Optional.of(lastCommand));
    when(lastCommandRepository.findByChatId(2L)).thenReturn(Optional.empty());

    assertTrue(lastCommandService.getByChatId(1L).isPresent());
    assertTrue(lastCommandService.getByChatId(2L).isEmpty());
  }

  @Test
  public void saveTest() {
    LastCommand lastCommand = new LastCommand();
    lastCommandService.save(lastCommand);

    verify(lastCommandRepository, times(1)).save(lastCommand);
  }

}
