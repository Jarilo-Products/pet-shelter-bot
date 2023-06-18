package pro.sky.petshelterbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.petshelterbot.model.LastCommand;

import java.util.Optional;

public interface LastCommandRepository extends JpaRepository<LastCommand, Long> {

  Optional<LastCommand> findByChatId(Long chatId);

}
