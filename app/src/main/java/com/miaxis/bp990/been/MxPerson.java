package com.miaxis.bp990.been;

/**
 * @author ZJL
 * @date 2022/3/24 14:35
 * @des
 * @updateAuthor
 * @updateDes
 */
public class MxPerson {

    public long id;
    public String name;//姓名
    public String cardNumber;//身份证号码
    public int codeStatus;//健康码状态 0绿码  1黄码  2红码
    public long timestamp;

    public MxPerson(String name, String cardNumber, int codeStatus) {
        this.name = name;
        this.cardNumber = cardNumber;
        this.codeStatus = codeStatus;
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", codeStatus='" + codeStatus + '\'' +
                '}';
    }
}
