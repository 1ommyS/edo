package org.example;/*
package org.example;

import lombok.val;
import org.example.files.FileSearcher;
import org.example.files.FileSearcherOS;

import java.io.File;

*/

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * @author 1ommy
 * @version 29.10.2023
 */

public class Main {
    public static void main(String[] args) throws Exception {
        // TODO: 1) система ролей
        //  2) старшие пользователи могут назначить задачи младшим и младшие могут просматривать
        //  свои задачи новые,старые, помечать задачу выполненной

//        LocalDate
//            LocalDateTime
//                Date
        Calendar mydate = new GregorianCalendar();
        String mystring = "January 2, 2010";
        Date thedate = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(mystring);
        mydate.setTime(thedate);
    }
}
