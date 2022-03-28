package com.miaxis.phone.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import kotlin.jvm.Transient;

/**
 * @author ZJL
 * @date 2022/3/24 14:35
 * @des
 * @updateAuthor
 * @updateDes
 */
@Entity
public class MxPerson {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public String name;//姓名
    public String cardNumber;//身份证号码
    public int codeStatus;//健康码状态 0绿码  1黄码  2红码
    public long timestamp;
    @Transient
    public int codeType;//二维码类型 1 网证，2 健康码

    public MxPerson(String name, String cardNumber, int codeStatus) {
        this.name = name;
        this.cardNumber = cardNumber;
        this.codeStatus = codeStatus;
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "MxPerson{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", codeStatus=" + codeStatus +
                ", timestamp=" + timestamp +
                ", codeType=" + codeType +
                '}';
    }
}
