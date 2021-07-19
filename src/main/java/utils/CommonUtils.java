package utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CommonUtils {

    public String checkStringEncoding(String text) {
        String result = "";
        String originalStr = text;
        String[] charSet = {"utf-8", "euc-kr", "ksc5601", "iso-8859-1", "x-windows-949"};

//        for (int i = 0; i < charSet.length; i++) {
//            for (int j = 0; j < charSet.length; j++) {
//                try {
//                    System.out.println("[" + charSet[i] + "," + charSet[j] + "] = " + new String(originalStr.getBytes(charSet[i]), charSet[j]));
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

        return result;
    }

    /**
     * 날짜 더하기
     *
     * @param dFormat yyyyMMdd ..
     * @param strDate 20190612 ..
     * @param dateUnit DATE or HOUR
     * @param addDate 1, -1 ...
     * @return
     */
    public String dateAdd(String dFormat, String strDate, String dateUnit, int addDate) {
        DateFormat dateFormat = null;
        Date date = new Date();

        try {
            dateFormat = new SimpleDateFormat(dFormat);
            date = dateFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if ("DATE".equals(dateUnit)) {
            cal.add(Calendar.DATE, addDate);
        } else if ("HOUR".endsWith(dateUnit)) {
            cal.add(Calendar.HOUR, addDate);
        }
        return dateFormat.format(cal.getTime());
    }

}
