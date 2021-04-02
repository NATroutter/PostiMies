package net.natroutter.postimies.Utilities;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    public static void Info(String info) {
        Add(info, "[INFO]");
    }
    public static void Error(String info) {
        Add(info, "[ERROR]");
    }
    public static void Warn(String info) {
        Add(info, "[WARN]");
    }

    private static void Add(String log, String tag) {
        Date date = new Date();
        SimpleDateFormat Formater = new SimpleDateFormat("HH:mm:ss");
        String DateTag = "[" + Formater.format(date) + "]";

        String Final = DateTag + tag + " " + log;
        System.out.println(Final);

    }

}
