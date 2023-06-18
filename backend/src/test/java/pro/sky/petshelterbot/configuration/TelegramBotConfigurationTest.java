package pro.sky.petshelterbot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TelegramBotConfigurationTest {

  private final TelegramBotConfiguration out = new TelegramBotConfiguration();

  @Test
  void telegramBotTest() {
    String token = "abc123";
    TelegramBot telegramBot = out.telegramBot(token);
    assertEquals(token, telegramBot.getToken());
  }
}