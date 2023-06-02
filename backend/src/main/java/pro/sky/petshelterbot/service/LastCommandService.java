package pro.sky.petshelterbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.petshelterbot.model.LastCommand;
import pro.sky.petshelterbot.repository.LastCommandRepository;

import java.util.Optional;

@Service
public class LastCommandService {

  private final LastCommandRepository lastCommandRepository;

  @Autowired
  public LastCommandService(LastCommandRepository lastCommandRepository) {
    this.lastCommandRepository = lastCommandRepository;
  }

  public Optional<LastCommand> getByChatId(Long chatId) {
    LastCommand lastCommand = lastCommandRepository.findByChatId(chatId);

    return Optional.ofNullable(lastCommand);
  }

  public void save(LastCommand lastCommand) {
    lastCommandRepository.save(lastCommand);
  }

}
