package pro.sky.petshelterbot.utility;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.util.HashMap;
import java.util.Map;

import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_ABOUT;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_ACQUAINTANCE;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_ADDRESS;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_CYNOLOGIST_ADVICE;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_DOCUMENTS;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_GUARD;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_HOW_TO_PET;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_INFO;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_CYNOLOGIST_LIST;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_MAIN;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_MAIN_CAT;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_MAIN_DOG;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_NO_PET_REPORT;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_PLACE_PREPARATION;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_REASONS_FOR_REFUSAL;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_SAFETY;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_SEND_CONTACTS;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_SEND_REPORT;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_START;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_TRANSPORTATION;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_VOLUNTEER_EMPTY;

public class CallbackUtils {

  public static Map<String, InlineKeyboardMarkup> BUTTONS = new HashMap<>() {{
    put(COMMAND_START, new InlineKeyboardMarkup(
        new InlineKeyboardButton("🐱 Для кошек").callbackData("/main_CAT"),
        new InlineKeyboardButton("🐶 Для собак").callbackData("/main_DOG")));

    put(COMMAND_MAIN, new InlineKeyboardMarkup(
        new InlineKeyboardButton[]{
            new InlineKeyboardButton("\uD83D\uDD0E Получить информацию о приюте").callbackData(COMMAND_INFO)
        },
        new InlineKeyboardButton[]{
            new InlineKeyboardButton("\uD83E\uDD32 Как взять животное из приюта").callbackData(COMMAND_HOW_TO_PET)
        },
        new InlineKeyboardButton[]{
            new InlineKeyboardButton("\uD83D\uDCDD Оставить контактные данные").callbackData(COMMAND_SEND_CONTACTS)
        },
        new InlineKeyboardButton[]{
            new InlineKeyboardButton("\uD83D\uDCE9 Прислать отчет о питомце").callbackData(COMMAND_SEND_REPORT)
        }
    ));
    put(COMMAND_MAIN_CAT, get(COMMAND_MAIN));
    put(COMMAND_MAIN_DOG, get(COMMAND_MAIN));

    put(COMMAND_INFO, new InlineKeyboardMarkup(
        new InlineKeyboardButton[]{
            new InlineKeyboardButton("⏪ Назад ⏪").callbackData(COMMAND_MAIN)
        },
        new InlineKeyboardButton[]{
            new InlineKeyboardButton("😎 О приюте").callbackData(COMMAND_ABOUT),
        },
        new InlineKeyboardButton[]{
            new InlineKeyboardButton("\uD83D\uDDD3 Адрес и расписание работы").callbackData(COMMAND_ADDRESS)
        },
        new InlineKeyboardButton[]{
            new InlineKeyboardButton("👮🏻 Контактные данные охраны").callbackData(COMMAND_GUARD)
        },
        new InlineKeyboardButton[]{
            new InlineKeyboardButton("‼ Техника безопасности").callbackData(COMMAND_SAFETY)
        }
    ));

    put(COMMAND_HOW_TO_PET + "_CAT", new InlineKeyboardMarkup(
        new InlineKeyboardButton[]{
            new InlineKeyboardButton("⏪ Назад ⏪").callbackData(COMMAND_MAIN)
        },
        new InlineKeyboardButton[]{
            new InlineKeyboardButton("☝ Правила знакомства с животным").callbackData(COMMAND_ACQUAINTANCE),
        },
        new InlineKeyboardButton[]{
            new InlineKeyboardButton("\uD83D\uDCC4 Список необходимых документов").callbackData(COMMAND_DOCUMENTS)
        },
        new InlineKeyboardButton[]{
            new InlineKeyboardButton("🚗 О транспортировке").callbackData(COMMAND_TRANSPORTATION)
        },
        new InlineKeyboardButton[]{
            new InlineKeyboardButton("\uD83E\uDDFA Об обустройстве места").callbackData(COMMAND_PLACE_PREPARATION)
        },
        new InlineKeyboardButton[]{
            new InlineKeyboardButton("❌ Причины отказа").callbackData(COMMAND_REASONS_FOR_REFUSAL)
        }
    ));

    put(COMMAND_HOW_TO_PET + "_DOG", new InlineKeyboardMarkup(
        new InlineKeyboardButton[]{
            new InlineKeyboardButton("⏪ Назад ⏪").callbackData(COMMAND_MAIN)
        },
        new InlineKeyboardButton[]{
            new InlineKeyboardButton("☝ Правила знакомства с животным").callbackData(COMMAND_ACQUAINTANCE),
        },
        new InlineKeyboardButton[]{
            new InlineKeyboardButton("\uD83D\uDCC4 Список необходимых документов").callbackData(COMMAND_DOCUMENTS)
        },
        new InlineKeyboardButton[]{
            new InlineKeyboardButton("🚗 О транспортировке").callbackData(COMMAND_TRANSPORTATION)
        },
        new InlineKeyboardButton[]{
            new InlineKeyboardButton("\uD83E\uDDFA Об обустройстве места").callbackData(COMMAND_PLACE_PREPARATION)
        },
        new InlineKeyboardButton[]{
            new InlineKeyboardButton("\uD83E\uDDD0 Советы кинолога").callbackData(COMMAND_CYNOLOGIST_ADVICE)
        },
        new InlineKeyboardButton[]{
            new InlineKeyboardButton("\uD83E\uDD13 Список проверенных кинологов").callbackData(COMMAND_CYNOLOGIST_LIST)
        },
        new InlineKeyboardButton[]{
            new InlineKeyboardButton("❌ Причины отказа").callbackData(COMMAND_REASONS_FOR_REFUSAL)
        }
    ));

    put(COMMAND_ABOUT, new InlineKeyboardMarkup(new InlineKeyboardButton("⏪ Назад ⏪").callbackData(COMMAND_INFO)));
    put(COMMAND_ADDRESS, get(COMMAND_ABOUT));
    put(COMMAND_GUARD, get(COMMAND_ABOUT));
    put(COMMAND_SAFETY, get(COMMAND_ABOUT));

    put(COMMAND_CYNOLOGIST_LIST + "_DOG", new InlineKeyboardMarkup(new InlineKeyboardButton("⏪ Назад ⏪").callbackData(COMMAND_HOW_TO_PET)));

    put(COMMAND_CYNOLOGIST_ADVICE + "_DOG", get(COMMAND_CYNOLOGIST_LIST + "_DOG"));
    put(COMMAND_TRANSPORTATION, get(COMMAND_CYNOLOGIST_LIST + "_DOG"));
    put(COMMAND_ACQUAINTANCE, get(COMMAND_CYNOLOGIST_LIST + "_DOG"));
    put(COMMAND_DOCUMENTS, get(COMMAND_CYNOLOGIST_LIST + "_DOG"));
    put(COMMAND_PLACE_PREPARATION, get(COMMAND_CYNOLOGIST_LIST + "_DOG"));
    put(COMMAND_REASONS_FOR_REFUSAL, get(COMMAND_CYNOLOGIST_LIST + "_DOG"));

    put(COMMAND_SEND_CONTACTS, new InlineKeyboardMarkup(new InlineKeyboardButton("⏪ Назад ⏪").callbackData(COMMAND_MAIN)));

    put(COMMAND_SEND_REPORT, get(COMMAND_SEND_CONTACTS));
    put(COMMAND_NO_PET_REPORT, get(COMMAND_SEND_CONTACTS));
    put(COMMAND_VOLUNTEER_EMPTY, get(COMMAND_SEND_CONTACTS));
  }};

}
