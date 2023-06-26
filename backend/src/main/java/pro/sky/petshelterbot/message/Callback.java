package pro.sky.petshelterbot.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Callback {

  private String buttonText;

  private String callbackMessage;

}
