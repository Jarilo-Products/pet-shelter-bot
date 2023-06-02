package pro.sky.petshelterbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.petshelterbot.model.LastCommand;

public interface LastCommandRepository extends JpaRepository<LastCommand, Long> {

  LastCommand findByChatId(Long chatId);

}
