package pro.sky.petshelterbot.service;

import org.springframework.stereotype.Service;
import pro.sky.petshelterbot.model.LastCommand;
import pro.sky.petshelterbot.repository.LastCommandRepository;

import java.util.Optional;

@Service
public class LastCommandService {

  private final LastCommandRepository lastCommandRepository;

  public LastCommandService(LastCommandRepository lastCommandRepository) {
    this.lastCommandRepository = lastCommandRepository;
  }

  public Optional<LastCommand> getByChatId(Long chatId) {
    return lastCommandRepository.findByChatId(chatId);
  }

  public void save(LastCommand lastCommand) {
    lastCommandRepository.save(lastCommand);
  }

}
