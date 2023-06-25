package pro.sky.petshelterbot.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TelegramMessage {

  private long chatId;

  private String text;

  private String fileId;

  public TelegramMessage(long chatId) {
    this.chatId = chatId;
  }

  public TelegramMessage(long chatId, String text) {
    this.chatId = chatId;
    this.text = text;
  }

}
