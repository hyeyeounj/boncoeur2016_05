package kr.ac.snu.boncoeur2016;

/**
 * Created by hyes on 2016. 4. 25..
 */
public class NameItem {
    private int id;
    private String Name;

    public NameItem(int id, String name) {
        this.id = id;
        Name = name;
    }

    public String getName() {
        return Name;
    }

    public int getId() {
        return id;
    }


}