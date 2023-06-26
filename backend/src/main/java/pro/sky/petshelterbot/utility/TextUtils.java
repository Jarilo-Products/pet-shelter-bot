package pro.sky.petshelterbot.utility;

import java.util.HashMap;
import java.util.Map;

public class TextUtils {

    /**
     * Сообщение о том, что пользователь ввел необрабатываемую команду.
     */
    public static final String UNKNOWN_COMMAND = """
            К сожалению, бот не знает что ответить :(
            Используйте команду /volunteer для вызова волонтера
            """;

    public static final String COMMAND_START = "/start";
    public static final String COMMAND_INFO = "/info";
    public static final String COMMAND_HOW_TO_PET = "/howtopet";
    public static final String COMMAND_SEND_REPORT = "/sendreport";
    public static final String COMMAND_ABOUT = "/about";
    public static final String COMMAND_ADDRESS = "/address";
    public static final String COMMAND_SAFETY = "/safety";
    public static final String COMMAND_GUARD = "/guard";
    public static final String COMMAND_SEND_CONTACTS = "/sendcontacts";
    public static final String COMMAND_VOLUNTEER = "/volunteer";
    public static final String COMMAND_REASONS_FOR_REFUSAL = "/reasons_refusal";
    public static final String COMMAND_PLACE_PREPARATION = "/place_preparation";
    public static final String COMMAND_DOCUMENTS = "/documents";
    public static final String COMMAND_ACQUAINTANCE = "/acquaintance";
    public static final String COMMAND_TRANSPORTATION = "/transportation";
    public static final String COMMAND_CYNOLOGISTADVICE = "/cynologistadvice";
    public static final String COMMAND_LIST_CYNOLOGIST = "/listcynologist";


    public static final String COMMAND_SUCCESSFUL_REPORT = "/successful_report";

    public static final String COMMAND_NO_PHOTO_REPORT = "/successful_no_photo";

    public static final String COMMAND_NO_TEXT_REPORT = "/successful_no_text";

    public static final String COMMAND_LOSING_TIME_REPORT = "/losing_time_report";

    public static final String COMMAND_NO_PET_REPORT = "/no_pet_report";

    /**
     * Содержит в себе статический текст, используемый в боте, в виде соответствия [команда -> ответ]
     */
    public static final Map<String, String> ANSWERS = new HashMap<>() {{
        put(COMMAND_START, """
                Привет!
                      
                Наши приюты для собак и кошек в городе Астана насчитывают очень много питомцев
                      
                Пожалуйста, выберите нужный приют (введите цифру)
                  1. приют для кошек
                  2. приют для собак
                """);
        put(COMMAND_START + "_registered", """
                Пожалуйста, выберите нужный приют (введите цифру)
                  1. приют для кошек
                  2. приют для собак
                """);

        put("chosen_CAT", """
                Вы выбрали приют для кошек! Выберите пункт меню из списка:
                  /info Узнать информацию о приюте
                  /howtopet Как взять животное из приюта
                  /sendreport Прислать отчет о питомце
                """);
        put("chosen_DOG", """
                Вы выбрали приют для собак! Выберите пункт меню из списка:
                  /info Узнать информацию о приюте
                  /howtopet Как взять животное из приюта
                  /sendreport Прислать отчет о питомце
                """);

        put(COMMAND_INFO + "_CAT", """
                Привет! На связи приют для кошек города Астаны. Чем я могу вам помочь?
                /about Рассказать о приюте
                /address Выдать расписание работы приюта, адрес и схему проезда
                /guard Выдать контактные данные охраны для оформления пропуска на машину
                /safety Выдать общие рекомендации о технике безопасности на территории приюта
                /sendcontacts Записать контактные данные для связи
                /volunteer Позвать волонтера
                """);
        put(COMMAND_INFO + "_DOG", """
                Привет! На связи приют для собак города Астаны. Чем я могу вам помочь?
                /about Рассказать о приюте
                /address Выдать расписание работы приюта, адрес и схему проезда
                /guard Выдать контактные данные охраны для оформления пропуска на машину
                /safety Выдать  общие рекомендации о технике безопасности на территории приюта
                /sendcontacts Записать контактные данные для связи
                /volunteer Позвать волонтера
                """);

        put(COMMAND_HOW_TO_PET + "_CAT", """
                На этом этапе я предоставлю Вам необходимую информацию.  Что бы Вы хотели узнать?
                /acquaintance Правила знакомства с животным до отъезда домой
                /documents Список документов для оформления
                /transportation Рекомендации по транспортировке
                /place_preparation Рекомендации по обустройству места
                /reasons_refusal Список причин, почему могут отказать
                /sendcontacts Записать контактные данные для связи
                /volunteer Позвать волонтера
                """);

        put(COMMAND_HOW_TO_PET + "_DOG", """
                На этом этапе я предоставлю Вам необходимую информацию.  Что бы Вы хотели узнать?
                /acquaintance Правила знакомства с животным до отъезда домой
                /documents Список документов для оформления
                /transportation Рекомендации по транспортировке
                /place_preparation Рекомендации по обустройству места
                /cynologistadvice Советы кинолога при первом общении с собакой
                /listcynologist Списко проверенных кинологов
                /reasons_refusal Список причин, почему могут отказать
                /sedcontacts Записать контактные данные для связи
                /volunteer Позвать волонтера
                """);

        put(COMMAND_LIST_CYNOLOGIST + "_DOG", """
                У нас есть список проверенных кинологов и мы с радостью готовы им поделиться!
                1. Иван Долгих (ООО"LOVE&DOGS");
                2. Константин Кримонов (ООО"Традиции");
                3. Елена Пиманова ("Animals&people");
                4. Леонид Мовсесян ("Добрые руки").
                /info Нужна ещё информация?
                """);

        put(COMMAND_CYNOLOGISTADVICE + "_DOG", """
                Попав в незнакомую обстановку, многие собаки испытывают стресс, страх новизны, их пугают незнакомые
                предметы, обстановка, явления. Например, подъезд, улица, незнакомые люди, машины и многое другое. 
                Для облегчения этого этапа социализации, кинолог Константин Кримонов составил 5 основных рекомендаций, 
                которые стоит соблюдать ответственным хозяевам животных из приюта.
                            
                1. Не навязывайте собаке своё общество. Разумеется, необходимо следить за животным и находиться рядом с ним, 
                но нельзя забывать, что излишнее внимание и активное взаимодействие с питомцем принесёт ему лишний стресс,
                которого так важно избегать на первых порах. В идеале стоит дождаться момента, когда собака сама начнёт 
                проявлять к вам интерес, сделает первый шаг. Нужно поощрить это действие поглаживанием или лакомством.
                            
                2. Не мешайте животному самостоятельно исследовать новое окружение. Пусть собака обойдёт весь дом, понюхает
                 каждый предмет и угол, убедится в том, что жилище для неё безопасно и не представляет угрозы.
                            
                3. Не устраивайте “смотрины” или какие-либо сборы гостей в первые недели жизни с животным. Собака может
                запаниковать и начать проявлять агрессию из-за внезапного появления шумных компаний незнакомых людей, а это
                явно не пойдёт на пользу процессу “одомашнивания”.
                            
                4. Не торопитесь ухаживать за собакой - кормить её, поить водой, гладить или затаскивать в ванну. 
                Перед осуществлением этих действий нужно установить доверительные, непринуждённые отношения с животным. 
                Советую продемонстрировать питомцу миски для корма и воды и просто оставить их на своём месте.
                            
                5. Если вы столкнулись со страхами собаки, действуйте спокойно и ласково, не напрягая своего питомца. 
                К примеру, в случае с боязнью лифтов я рекомендую дать животному некоторое время на наблюдение за лифтом, 
                постепенно увеличивая длительность таких “сеансов”. Если собака начинает вести себя спокойно, поощряйте это
                поведение едой и поглаживаниями. Следующий шаг - “обнаружение” в лифте знакомого человека, готового поощрить
                собаку лакомством за уверенное поведение.
                /info Нужна ещё информация?
                """);

        put(COMMAND_TRANSPORTATION + "_DOG", """
                Следующий шаг - после знакомства с питомцем, это адаптация к переезду.
                Позвольте животному познакомиться с контейнером для перевозки и автомобилем, в котором пройдёт дорога
                домой. Заранее приучите собаку к наморднику - для этого, посещая приют, вкладывайте лакомства в этот
                аксессуар, используйте его как миску. Через некоторое время можно начать надевать намордник на собаку
                перед началом прогулки, и снимать через некоторое время.
                /info Нужна ещё информация?            
                """);
        put(COMMAND_TRANSPORTATION + "_CAT", """
                 Следующий шаг - после знакомства с питомцем, это адаптация к переезду.
                 Позвольте животному познакомиться с контейнером для перевозки и автомобилем, в котором пройдёт дорога
                 домой. Заранее приучите кота к контейнеру - для этого, посещая приют, вкладывайте лакомства внутрь, тем 
                 самым давая  понять животному о безопасности нового места.
                /info Нужна ещё информация?
                 """);
        put(COMMAND_ACQUAINTANCE + "_CAT", """
                Рекомендации по процессу знакомства и общения с будущим подопечным.
                После того, как вы сделали выбор, начните навещать животное в приюте, строить с ним доверительные отношения.
                Приносить коту лакомства, гладить, аккуратно брать на руки. Когда животное начнёт вас узнавать, 
                тереться о ноги, мяукать при встрече, можно устроить пару гостевых посещений, приведя кота в дом.
                Это поможет будущему питомицу, в дальнейшем к более легкому знакомству с незнакомой обстановкой
                 и привыканию к новому дому.
                /info Нужна ещё информация?                   
                """);

        put(COMMAND_ACQUAINTANCE + "_DOG", """
                Рекомендации по процессу знакомства и общения с будущим подопечным.
                После того, как вы сделали выбор, начните навещать животное в приюте, строить с ним доверительные отношения.
                Приносить собаке лакомства, начать выводить её на прогулки, аккуратно гладить. Это должно происходить 
                спокойно и ненавязчиво, без какого-либо давления с вашей стороны. 
                Когда животное начнёт вас узнавать, вилять хвостом при встрече, и позволит с ним играть, 
                можно устроить пару гостевых посещений, приведя собаку в дом. Это поможет собаке в дальнейшем более 
                легкому знакомству с незнакомой обстановкой и привыканию к новому дому.
                /info Нужна ещё информация?
                """);

        put(COMMAND_DOCUMENTS, """
                Список необходимых документов:
                - ксерокопия паспорта усыновителя + оригинал(для сверки);
                - завление на усыновление;
                - заполненная анкета.
                /info Нужна ещё информация?
                """);

        put(COMMAND_PLACE_PREPARATION, """
                Скоро у Вас появиться новый член семьи и пора подумать об обустройстве уютного местечка,
                где он сможет отдыхать и чувствовать себя хозяином. Для этого нужно:
                1. Выбрать укромное место в помещении и оснастить его всем необходимым для сна.
                Это может быть мягкая подушка/коробка или специальная кроватка по размеру животного.
                2. Приобрести  одну миску для корма и вторую для воды, чтобы всегда была возможность утолить жажду.
                3. Обустроить место для справления нужд. Для этих целей отлично подойдет лоток с любым наполнителем.
                4. Заранее подготовить специальные средства для купания.
                /info Нужна ещё информация?           
                 """);

        put(COMMAND_REASONS_FOR_REFUSAL, """
                Работники и волонтеры стараются сделать все, чтобы животные не оказались на улице повторно, 
                поэтому отдают питомцев приюта  только в надежные руки. 
                Существует пять причин, по которым чаще всего отказывают желающим «усыновить» домашнего любимца.
                1 Большое количество животных дома
                2 Нестабильные отношения в семье
                3 Наличие маленьких детей
                4 Съемное жилье
                5 Животное в подарок или для работы
                /info Нужна ещё информация?
                """);

        put(COMMAND_SEND_REPORT, """
                В течение одного месяца Вам необходимо присылать информацию о том, как Ваш питомец чувствует себя на новом месте.
                В ежедневный отчет входит следующая информация:
                 - Фото животного;
                 - Рацион животного;
                 - Общее самочувствие и привыкание к новому месту;
                 - Изменение в поведении: отказ от старых привычек, приобретение новых.
                 Отчет нужно присылать каждый день!
                """);

        put(COMMAND_SUCCESSFUL_REPORT, """
                Уважаемый хозяин,
                Мы с радостью сообщаем Вам, что ваш ежедневный отчет о питомце из приюта был успешно получен!
                        
                Ваш отчет поможет нам следить за его прогрессом и удостовериться, что он получает все необходимое для своего благополучия.
                Мы ценим вашу инициативу и старания в составлении ежедневного отчета.

                Благодарим вас за ваше содействие в нашей работе и заботу о питомце.
                Мы признательны вам за то, что вы выбрали быть его опекуном.
                """);

        put(COMMAND_NO_TEXT_REPORT, """
                Уважаемый хозяин,
                Мы хотим сообщить Вам, что полученный ежедневный отчет о питомце из приюта был не полным.
                Вы предоставили фото вашего любимца, за что мы вам благодарны, однако, в отчете отсутствует текст, содержащий следующую информацию:                                                                                                                                        
                  - Рацион животного;
                  - Общее самочувствие и привыкание к новому месту;
                  - Изменение в поведении: отказ от старых привычек, приобретение новых.
                Эта информация крайне важна для нас, поскольку мы хотим, чтобы у вашего питомца были обеспечены оптимальные условия и удовлетворены его потребности наилучшим образом.

                Пожалуйста, просим вас дополнить отчет, предоставив текст, в котором вы расскажете о перечисленных выше пунктах.

                Благодарим вас за ваше содействие в нашей работе и заботу о питомце.
                Мы признательны вам за то, что вы выбрали быть его опекуном.
                """);

        put(COMMAND_NO_PHOTO_REPORT, """
                Уважаемый хозяин,
                Мы хотим сообщить вам, что полученный ежедневный отчет о питомце из приюта был не полным.
                Вы предоставили текст, в котором рассказываете о:
                  - Рацион животного;
                  - Общее самочувствие и привыкание к новому месту;
                  - Изменение в поведении: отказ от старых привычек, приобретение новых.
                Однако, в отчете отсутствует фото вашего питомца, которое помогло бы нам визуально оценить его состояние.
                Мы очень ценим вашу заботу о питомце, и фотографии играют важную роль в нашей работе.
                Они позволяют нам следить за физическим развитием питомца, его внешним состоянием и эмоциональным благополучием.
                    
                Пожалуйста, просим вас дополнить отчет, предоставив фото вашего питомца.
                Это поможет нам получить полную картину о благополучии вашего питомца.

                Благодарим вас за ваше содействие в нашей работе и заботу о питомце.
                Мы признательны вам за то, что вы выбрали быть его опекуном.
                """);

        put(COMMAND_LOSING_TIME_REPORT, """
                Уважаемый хозяин,
                Мы с радостью сообщаем Вам, что ваш ежедневный отчет о питомце из приюта был успешно получен!
                Однако, хочу уведомить вас, что отчет был прислан после 21:00, поэтому он будет засчитан за дату следующего дня.
                Пожалуйста, имейте это в виду при составлении будущих отчетов, чтобы мы могли правильно отслеживать прогресс вашего питомца.
                Мы благодарны вам за ваше внимание и заботу о питомце.

                Благодарим вас за ваше содействие в нашей работе и заботу о питомце.
                Ваша забота и участие в создании комфортных условий для него является важным фактором для его благополучия и адаптации в новом окружении.
                Мы признательны вам за то, что вы выбрали быть его опекуном.
                """);

        put(COMMAND_NO_PET_REPORT, """
                Уважаемый пользователь,
                Мы сообщаем Вам, что вы не можете отправить отчет о питомце, поскольку вы еще не стали его опекуном.
                        
                Мы полностью понимаем ваше желание заботиться о животных, однако, для того чтобы отправлять отчеты, требуется быть зарегистрированным опекуном.
                Для того, чтобы вы стали опекуном нам необходимо удостовериться в вашей готовности и возможности заботиться о питомце, а также обеспечить взаимодействие и поддержку со стороны нашего персонала.
                        
                Если вы все же заинтересованы в становлении опекуном питомца из приюта, рекомендуем обратиться к нашему персоналу или посетить наш приют для получения дополнительной информации.
                Или можете оставить контактные данные для связи с вами. 
                Чтобы сделать это вы можете нажать на команду /sendcontacts

                Благодарим вас за вашу заботу о животных и ваш интерес к нашему приюту.
                """);

        put(COMMAND_ABOUT + "_CAT", """
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
        put(COMMAND_ABOUT + "_DOG", """
                Наш приют называется "Chance".
                "Chance" - это место, где бездомные и брошенные собаки находят временное убежище и заботу, пока им не удастся найти новый дом.
                В приюте для собак есть специальные помещения, где животные могут спать, есть и играть, а также зона для прогулок и физических упражнений.
                В приюте работают сотрудники, которые заботятся о животных и обеспечивают им необходимые условия для жизни.
                Это включает в себя кормление, уход за шерстью и здоровьем, а также социализацию с животными и людьми. Наш приюты также предоставляет ветеринарные услуги и помогает с усыновлением животных.
                Приют для собак играет важную роль в борьбе с бездомностью животных. Он помогает спасти сотни жизней и облегчает работу местных властей по контролю за бездомными животными.
                Кроме того, приют предоставляет возможность людям, которые не могут позволить себе покупку собаки у заводчика, найти верного друга и дать ему новый дом.
                            
                /info Если необходима ещё информация
                """);
        put(COMMAND_ADDRESS + "_CAT", """
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
        put(COMMAND_ADDRESS + "_DOG", """
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
        put(COMMAND_GUARD + "_DOG", """
                Контактные данные охраны для оформления пропуска на машину:
                Телефон: +7 (999) 000-22-22
                E-mail: ohrana@сhance.com
                Адрес: Казахстан, город Астана, ул. Объездная, дом 10.
                    
                /info Необходима ещё информация?
                """);
        put(COMMAND_GUARD + "_CAT", """
                Контактные данные охраны для оформления пропуска на машину:
                 Телефон: +7 (999) 000-33-33
                 E-mail: ohrana@newlife.com
                 Адрес: Казахстан, город Астана, улица Мира, дом 24.
                             
                 /info Необходима ещё информация?
                 """);
        put(COMMAND_SAFETY + "_DOG", """
                Общие рекомендации о технике безопасности на территории приюта для собак:
                1. Не пытайтесь подходить к собакам без разрешения сотрудников приюта. Они могут быть нервными или агрессивными, особенно если они только что прибыли в приют.
                2. Если вы заметили, что собака ведет себя агрессивно или нервно, не пытайтесь приблизиться к ней. Обратитесь к сотрудникам приюта для получения помощи.
                3. Если вы пришли к приюту со своей собакой, держите ее на поводке и не позволяйте ей подходить к другим собакам без разрешения.
                4. Не оставляйте детей без присмотра на территории приюта. Собаки могут быть непредсказуемыми, и даже самая дружелюбная собака может напугать или укусить ребенка.
                5. Если вы заметили, что собака находится в опасной ситуации, не пытайтесь ее спасти самостоятельно. Обратитесь к сотрудникам приюта для получения помощи.
                6. Соблюдайте гигиенические меры и не трогайте собак без перчаток или защитных очков.

                /info Нужна ещё информация?
                """);
        put(COMMAND_SAFETY + "_CAT", """   
                Общие рекомендации о технике безопасности на территории приюта для кошек:
                1. Не кормите кошек, если не получили разрешение от персонала приюта. Кормление неправильным пищей может повредить здоровье животных.
                2. Следите за своими детьми и не позволяйте им бегать по территории приюта без вашего присутствия. Кошки могут иметь непредсказуемые реакции на детей, которые могут быть опасными.
                3. Если вы хотите погладить кошку, сначала обратитесь к персоналу приюта и узнайте, какие животные можно брать на руки. Некоторые кошки могут быть агрессивными или бояться людей.
                4. Никогда не пытайтесь забрать кошку из приюта без разрешения персонала. Это не только незаконно, но и может быть опасно для животного.
                5. Соблюдайте гигиену и мойте руки после контакта с животными. Кошки могут быть носителями инфекций и болезней, которые могут передаваться человеку.
                6. Если вы заметили какую-либо проблему на территории приюта (например, разбитое стекло или поврежденный забор), сообщите об этом персоналу приюта. Это поможет избежать потенциальных опасностей для животных и посетителей.
                            
                /info Нужна ещё информация?
                """);

        put(COMMAND_SEND_CONTACTS, """
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
        put(COMMAND_SEND_CONTACTS + "_notenough", "Количество строк должно соответствовать примеру. Попробуйте ещё раз!");
        put(COMMAND_SEND_CONTACTS + "_invalid_name", "Ошибка ввода ФИО!");
        put(COMMAND_SEND_CONTACTS + "_invalid_bithdate", "Ошибка ввода даты рождения!");
        put(COMMAND_SEND_CONTACTS + "_invalid_email", "Ошибка ввода email!");
        put(COMMAND_SEND_CONTACTS + "_invalid", "Пожалуйста, посмотрите пример корректного ввода и попробуйте ещё раз.");
        put("acceptedcontacts", """
                Контактные данные успешно сохранены!
                              
                Если Вам требуется ещё какая-либо информация, то можете перейти на вкладку /info
                """);

        put(COMMAND_VOLUNTEER + "_empty", "К сожалению, мы пока не набрали волонтеров :(");
        put(COMMAND_VOLUNTEER, "На все Ваши вопросы постарается ответить наш волонтер [name]");
        put(COMMAND_VOLUNTEER + "_memo", """
                У Вас открылся чат [user_chat_id]
                Памятка волонтерам по работе с пользователями
                        
                Сообщения от пользователей волонтерам приходят в следующем виде:
                    [user_chat_id] Сообщение пользователя
                Чтобы ответить в нужный чат, необходимо в начале сообщения указать номер чата в таком же формате:
                    [user_chat_id] Ответ волонтера
                Чтобы закрыть чат с пользователем, необходимо после номера чата написать слово "end":
                    [user_chat_id] end
                """);
        put(COMMAND_VOLUNTEER + "_end", "Чат [user_chat_id] успешно закрыт!");
        put(COMMAND_VOLUNTEER + "_end_user", "Чат с волонтером закрыт. Спасибо за обращение!");
        put(COMMAND_VOLUNTEER + "_no_chat", "Чат с таким номером отсутствует в базе данных. Проверьте правильность номера.");
        put(COMMAND_VOLUNTEER + "_chat_not_opened", "На данный момент пользователю с указанным номером чата не требуется помощь. " +
                "Проверьте правильность номера.");
    }};

}
