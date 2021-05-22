package adapters;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class InfoComparatorDate implements Comparator<Info> {
    @Override
    public int compare(Info o1, Info o2) {
        String d1 = o1.getCreation_date();
        String d2 = o2.getCreation_date();

        String[] parts1 = d1.split("T");
        String d1_simple = parts1[0];

        String[] part2 = d2.split("T");
        String d2_simple = part2[0];

        Date date1=null,date2=null;
        try {
            date1=new SimpleDateFormat("yyyy-mm-dd").parse(d1_simple);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            date2=new SimpleDateFormat("yyyy-mm-dd").parse(d2_simple);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1.compareTo(date2);
    }
}
