package pro.sky.petshelterbot.utility;

import java.util.HashMap;
import java.util.Map;

public class TextUtils {

  /**
   * Содержит в себе статический текст, используемый в боте, в виде соответствия [команда -> ответ]
   */
  public static final Map<String, String> ANSWERS = new HashMap<>() {{
    put("/start", """
        Привет!
              
        Наши приюты для собак и кошек в городе Астана насчитывают очень много питомцев
              
        Пожалуйста, выберите нужный приют (введите цифру)
          1. приют для кошек
          2. приют для собак
        """);
    put("/start_registered", """
        Пожалуйста, выберите нужный приют (введите цифру)
          1. приют для кошек
          2. приют для собак
        """);

    put("/chosen_CAT", """
        Вы выбрали приют для кошек! Выберите пункт меню из списка:
          /info Узнать информацию о приюте
          /howtopet Как взять животное из приюта
          /sendreport Прислать отчет о питомце
        """);
    put("/chosen_DOG", """
        Вы выбрали приют для собак! Выберите пункт меню из списка:
          /info Узнать информацию о приюте
          /howtopet Как взять животное из приюта
          /sendreport Прислать отчет о питомце
        """);

    // Лера
    put("/info_CAT", """
        Привет! На связи приют для кошек города Астаны. Чем я могу вам помочь?
        /about Рассказать о приюте
        /address Выдать расписание работы приюта, адрес и схему проезда
        /guard Выдать контактные данные охраны для оформления пропуска на машину
        /safety Выдать общие рекомендации о технике безопасности на территории приюта
        /sendcontacts Записать контактные данные для связи
        /volunteer Позвать волонтера
        """);
    put("/info_DOG", """
        Привет! На связи приют для собак города Астаны. Чем я могу вам помочь?
        /about Рассказать о приюте
        /address Выдать расписание работы приюта, адрес и схему проезда
        /guard Выдать контактные данные охраны для оформления пропуска на машину
        /safety Выдать  общие рекомендации о технике безопасности на территории приюта
        /sendcontacts Записать контактные данные для связи
        /volunteer Позвать волонтера
        """);

    // Оля
    put("/howtopet_CAT", """
        На этом этапе я предоставлю Вам необходимую информацию.  Что бы Вы хотели узнать?
        1 - Правила знакомства с животным до отъезда домой
        2 - Список документов для оформления
        3 - Рекомендации по транспортировке
        4 - Рекомендации по обустройству места
        5 - Список причин, почему могут отказать
        6 - Записать контактные данные для связи
        7- Позвать волонтера
        """);
    put("/howtopet_DOG", """
        На этом этапе я предоставлю Вам необходимую информацию.  Что бы Вы хотели узнать?
            1 - Правила знакомства с животным до отъезда домой
            2 - Список документов для оформления
            3 - Рекомендации по транспортировке
            4 - Рекомендации по обустройству места
            5 - Советы кинолога при первом общении с собакой
            6 - Списко проверенных кинологов
            7 - Список причин, почему могут отказать
            8 - Записать контактные данные для связи
            9 - Позвать волонтера
        """);

    put("/sendreport_CAT", """
        В течение одного месяца Вам необходимо присылать информацию о том, как Ваш кот чувствует себя на новом месте.
        В ежедневный отчет входит следующая информация:
         - Фото животного;
         - Рацион животного;
         - Общее самочувствие и привыкание к новому месту;
         - Изменение в поведении: отказ от старых привычек, приобретение новых.
         Отчет нужно присылать каждый день!
        """);
    put("/sendreport_DOG", """
        В течение одного месяца Вам необходимо присылать информацию о том, как Ваша собака чувствует себя на новом месте.
        В ежедневный отчет входит следующая информация:
         - Фото животного;
         - Рацион животного;
         - Общее самочувствие и привыкание к новому месту;
         - Изменение в поведении: отказ от старых привычек, приобретение новых.
        Отчет нужно присылать каждый день!
        """);

    put("/about_CAT", """
            Наш приют называется "New Life".
            New Life - это место, где бездомные или брошенные кошки могут жить, пока их не найдут новые хозяева.
            В нашем приюте есть специальные помещения, где животные могут спать, играть и есть.
            Работники приюта заботятся о здоровье кошек, обеспечивая им медицинское обслуживание и регулярный уход за шерстью и гигиеной.
            Одна из главных задач приюта - найти новый дом для кошек.
            Работники знакомятся с характером каждого животного, чтобы найти ему наиболее подходящего хозяина.
            Они также проводят программы адоптации, чтобы помочь животным адаптироваться к новой среде и найти свое место в новой семье.
            Приюты для кошек - это важная часть работы по борьбе с бездомными животными.
            
            /info Если необходима ещё информация
            """);
    put("/about_DOG", """
            Наш приют называется "Chance".
            "Chance" - это место, где бездомные и брошенные собаки находят временное убежище и заботу, пока им не удастся найти новый дом.
            В приюте для собак есть специальные помещения, где животные могут спать, есть и играть, а также зона для прогулок и физических упражнений.
            В приюте работают сотрудники, которые заботятся о животных и обеспечивают им необходимые условия для жизни.
            Это включает в себя кормление, уход за шерстью и здоровьем, а также социализацию с животными и людьми. Наш приюты также предоставляет ветеринарные услуги и помогает с усыновлением животных.
            Приют для собак играет важную роль в борьбе с бездомностью животных. Он помогает спасти сотни жизней и облегчает работу местных властей по контролю за бездомными животными.
            Кроме того, приют предоставляет возможность людям, которые не могут позволить себе покупку собаки у заводчика, найти верного друга и дать ему новый дом.  
            
            /info Если необходима ещё информация    
            """);
    put("/address_CAT", """
            Расписание работы приюта:
                        
            Понедельник-пятница: с 9:00 до 18:00
            Суббота: с 10:00 до 16:00
            Воскресенье: выходной
                        
            Адрес приюта: улица Мира, дом 24, город Астана, Казахстан
                        
            Схема проезда:
            1. На автомобиле - съезд с трассы М-51 на улицу Мира, дом 24.
            2. На общественном транспорте - автобус №25, остановка "Улица Мира".
            
            /info Требуется ещё информация?
            """);
    put("/address_DOG", """
            Расписание работы приюта:
            Понедельник-пятница: с 9:00 до 18:00
            Суббота-воскресенье: с 10:00 до 16:00
                        
            Адрес приюта: ул. Объездная, дом 10, город Астана, Казахстан
                        
            Схема проезда:
            1. Остановка "Улица Объездная" автобусы № 7, 15; маршрутки № 4, 6.
            2. Остановка "Площадь Первая" автобусы № 2, 5.
            3. На автомобиле - съезд с трассы М-51 на улицу Объездная, дом 10. 
                        
            /info Требуется ещё информация?
            """);
    put("/guard_DOG", """
            Контактные данные охраны для оформления пропуска на машину:
            Телефон: +7 (999) 000-22-22
            E-mail: ohrana@сhance.com
            Адрес: Казахстан, город Астана, ул. Объездная, дом 10.
                
            /info Необходима ещё информация?
            """);
    put("/guard_CAT", """
           Контактные данные охраны для оформления пропуска на машину:
            Телефон: +7 (999) 000-33-33
            E-mail: ohrana@newlife.com
            Адрес: Казахстан, город Астана, улица Мира, дом 24.
                        
            /info Необходима ещё информация?
            """);
    put("/safety_DOG", """
            Общие рекомендации о технике безопасности на территории приюта для собак:
            1. Не пытайтесь подходить к собакам без разрешения сотрудников приюта. Они могут быть нервными или агрессивными, особенно если они только что прибыли в приют.        
            2. Если вы заметили, что собака ведет себя агрессивно или нервно, не пытайтесь приблизиться к ней. Обратитесь к сотрудникам приюта для получения помощи.        
            3. Если вы пришли к приюту со своей собакой, держите ее на поводке и не позволяйте ей подходить к другим собакам без разрешения.       
            4. Не оставляйте детей без присмотра на территории приюта. Собаки могут быть непредсказуемыми, и даже самая дружелюбная собака может напугать или укусить ребенка.        
            5. Если вы заметили, что собака находится в опасной ситуации, не пытайтесь ее спасти самостоятельно. Обратитесь к сотрудникам приюта для получения помощи.         
            6. Соблюдайте гигиенические меры и не трогайте собак без перчаток или защитных очков.

            /info Нужна ещё информация?
            """);
    put("/safety_CAT", """   
            Общие рекомендации о технике безопасности на территории приюта для кошек: 
            1. Не кормите кошек, если не получили разрешение от персонала приюта. Кормление неправильным пищей может повредить здоровье животных.       
            2. Следите за своими детьми и не позволяйте им бегать по территории приюта без вашего присутствия. Кошки могут иметь непредсказуемые реакции на детей, которые могут быть опасными.       
            3. Если вы хотите погладить кошку, сначала обратитесь к персоналу приюта и узнайте, какие животные можно брать на руки. Некоторые кошки могут быть агрессивными или бояться людей.   
            4. Никогда не пытайтесь забрать кошку из приюта без разрешения персонала. Это не только незаконно, но и может быть опасно для животного.       
            5. Соблюдайте гигиену и мойте руки после контакта с животными. Кошки могут быть носителями инфекций и болезней, которые могут передаваться человеку.        
            6. Если вы заметили какую-либо проблему на территории приюта (например, разбитое стекло или поврежденный забор), сообщите об этом персоналу приюта. Это поможет избежать потенциальных опасностей для животных и посетителей.
                        
            /info Нужна ещё информация?
            """);
      put("/sendcontacts", """
              Для того, чтобы мы могли связаться с Вами, оставьте контактные данные.
              
              ВАЖНО! Заполните всю информацию одним сообщением!
              Необходимо указать: ФИО, дата рождения, номер телефона, почта, адрес.
              
              Ниже указан пример правильного заполнения:
              Петров Петр Петрович
              01.01.2000
              89001002030
              email@mail.ru
              г.Астана, ул. Коллективная, д.200, кв.354
              """);
      put("acceptedcontacts", """
              Контактные данные успешно сохранены!
              
              Если Вам требуется ещё какая-либо информация, то можете перейти на вкладку /info""");
      put("/volunteer_empty", "К сожалению, мы пока не набрали волонтеров :(");
      put("/volunteer", "На все Ваши вопросы постарается ответить наш волонтер [name]");
      put("/volunteer_memo", """
        У Вас открылся чат [user_chat_id]
        Памятка волонтерам по работе с пользователями
                
        Сообщения от пользователей волонтерам приходят в следующем виде:
            [user_chat_id] Сообщение пользователя
        Чтобы ответить в нужный чат, необходимо в начале сообщения указать номер чата в таком же формате:
            [user_chat_id] Ответ волонтера
        Чтобы закрыть чат с пользователем, необходимо после номера чата написать слово "end":
            [user_chat_id] end
        """);
      put("/volunteer_end", "Чат [user_chat_id] успешно закрыт!");
      put("/volunteer_end_user", "Чат с волонтером закрыт. Спасибо за обращение!");
      put("/volunteer_no_chat", "Чат с таким номером отсутствует в базе данных. Проверьте правильность номера.");
      put("/volunteer_chat_not_opened", "На данный момент пользователю с указанным номером чата не требуется помощь. Проверьте правильность номера.");
  }};
}
