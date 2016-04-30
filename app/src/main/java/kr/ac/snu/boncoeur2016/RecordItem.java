package kr.ac.snu.boncoeur2016;

import kr.ac.snu.boncoeur2016.utils.Define;

/**
 * Created by hyes on 2016. 4. 27..
 */
public class RecordItem {

    private static final int nRecords = 4;

    private String name, date, email;
    private int age;
    private String[] recordFile = new String[nRecords];
    private String[] pos = new String[nRecords];

    public RecordItem(String name, String date, int age, String email, String recordFile1, String pos1, String recordFile2, String pos2, String recordFile3, String pos3, String recordFile4, String pos4) {
        this.name = name;
        this.date = date;
        this.age = age;
        this.email = email;
        this.recordFile[0] = recordFile1;
        this.pos[0] = pos1;
        this.recordFile[1] = recordFile2;
        this.pos[1] = pos2;
        this.recordFile[2] = recordFile3;
        this.pos[2] = pos3;
        this.recordFile[3] = recordFile4;
        this.pos[3] = pos4;
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
        return recordFile[0];
    }

    public String getRecordFile2() {
        return recordFile[1];
    }

    public String getRecordFile3() {
        return recordFile[2];
    }

    public String getRecordFile4() {
        return recordFile[3];
    }

    public String getRecordFile(String position) {

        for (int i = 0; i < nRecords; i++)
            if (position.equals(Define.POS_TAG[i]))
                return recordFile[i];
        return null;
    }

    public String getPos1() {
        return pos[0];
    }

    public String getPos2() {
        return pos[1];
    }

    public String getPos3() {
        return pos[2];
    }

    public String getPos4() {
        return pos[3];
    }

}
