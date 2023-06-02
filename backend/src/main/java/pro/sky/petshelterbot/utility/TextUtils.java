package pro.sky.petshelterbot.utility;

import java.util.HashMap;
import java.util.Map;

public class TextUtils {

  public static final Map<String, String> ANSWERS = new HashMap<>() {{
    put("start", """
        Привет!
              
        Наши приюты для собак и кошек в городе Астана насчитывают очень много питомцев
              
        Пожалуйста, выберите нужный приют (введите цифру)
          1. приют для кошек
          2. приют для собак
        """);
    put("start_registered", """
        Пожалуйста, выберите нужный приют (введите цифру)
          1. приют для кошек
          2. приют для собак
        """);

    put("chosen_cat", """
        Вы выбрали приют для кошек! Выберите пункт меню из списка:
          /info Узнать информацию о приюте
          /howtopet Как взять животное из приюта
          /sendreport Прислать отчет о питомце
        """);
    put("chosen_dog", """
        Вы выбрали приют для собак! Выберите пункт меню из списка:
          /info Узнать информацию о приюте
          /howtopet Как взять животное из приюта
          /sendreport Прислать отчет о питомце
        """);

    // Лера
    put("info_cat", """
        
        """);
    put("info_dog", """
        
        """);

    // Оля
    put("howtopet_cat", """
        
        """);
    put("howtopet_dog", """
        
        """);

    // Женя
    put("sendreport_cat", """
        
        """);
    put("sendreport_dog", """
        
        """);
  }};

}
