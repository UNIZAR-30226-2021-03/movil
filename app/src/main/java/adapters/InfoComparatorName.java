package adapters;

import java.util.Comparator;

public class InfoComparatorName implements Comparator<Info> {
    @Override
    public int compare(Info o1, Info o2) {
        String n1=o1.getName();
        String n2=o2.getName();
        return  n1.compareTo(n2);
    }
}
