package pro.sky.petshelterbot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.DeleteMyCommands;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class TelegramBotConfiguration {

  @Bean
  public TelegramBot telegramBot(@Value("${telegram.bot.token}") String token) {
    TelegramBot bot = new TelegramBot(token);
    bot.execute(new DeleteMyCommands());
    return bot;
  }

  @Bean
  public Clock clock() {
    return Clock.systemDefaultZone();
  }

}
