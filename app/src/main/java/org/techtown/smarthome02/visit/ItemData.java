package org.techtown.smarthome02.visit;

public class ItemData {

    // 파이어 베이스에서 데이터 불러오는데 순서 상관없음음
   public String date;
    public String idx; // primary key
    public String image;
    public String name;

    public ItemData() {}

    public ItemData(String date, String idx, String image, String name) {
        this.date = date;
        this.idx = idx;
        this.image = image;
        this.name = name;
    }
}
