package kr.ac.snu.boncoeur2016;

/**
 * Created by hyes on 2016. 4. 27..
 */
public class RecordItem {
    private String name, date, email;
    private int age;

    private String recordFile1, recordFile2, recordFile3, recordFile4;
    private String pos1, pos2, pos3, pos4;

    public RecordItem(String name, String date, int age, String email, String recordFile1, String pos1, String recordFile2, String pos2, String recordFile3, String pos3, String recordFile4, String pos4) {
        this.name = name;
        this.date = date;
        this.age = age;
        this.email = email;
        this.recordFile1 = recordFile1;
        this.pos1 = pos1;
        this.recordFile2 = recordFile2;
        this.pos2 = pos2;
        this.recordFile3 = recordFile3;
        this.pos3 = pos3;
        this.recordFile4 = recordFile4;
        this.pos4 = pos4;


    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public int getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public String getRecordFile1() {
        return recordFile1;
    }

    public String getRecordFile2() {
        return recordFile2;
    }

    public String getRecordFile3() {
        return recordFile3;
    }

    public String getRecordFile4() {
        return recordFile4;
    }

    public String getPos1() {
        return pos1;
    }

    public String getPos2() {
        return pos2;
    }

    public String getPos3() {
        return pos3;
    }

    public String getPos4() {
        return pos4;
    }

}
