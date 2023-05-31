package pro.sky.petshelterbot.processor;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Component
public class TextMessageProcessor {

  // необходимые сервисы нужно будет указать

  private static final Map<String, Consumer<Long>> METHODS = new HashMap<>() {{
    put("/start", TextMessageProcessor::processStartCommand);
  }};


  public TextMessageProcessor() {}

  public void processTextMessage(long chatId, String text) {

  }

  private static void processStartCommand(long chatId) {

  }

}
