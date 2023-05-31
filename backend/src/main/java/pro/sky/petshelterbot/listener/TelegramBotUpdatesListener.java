package pro.sky.petshelterbot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.processor.TextMessageProcessor;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class TelegramBotUpdatesListener implements UpdatesListener {

  private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

  private final TelegramBot telegramBot;
  private final TextMessageProcessor textMessageProcessor;

  @Autowired
  public TelegramBotUpdatesListener(TelegramBot telegramBot,
                                    TextMessageProcessor textMessageProcessor) {
    this.telegramBot = telegramBot;
    this.textMessageProcessor = textMessageProcessor;
  }

  @PostConstruct
  public void init() {
    telegramBot.setUpdatesListener(this);
  }

  @Override
  public int process(List<Update> updates) {
    try {
      updates.stream()
          .filter(update -> update.message() != null)
          .forEach(update -> {
            logger.info("Processing update: {}", update);
            Message message = update.message();
            long chatId = message.chat().id();
            String text = message.text();
            textMessageProcessor.processTextMessage(chatId, text);
          });
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
    return UpdatesListener.CONFIRMED_UPDATES_ALL;
  }

}
