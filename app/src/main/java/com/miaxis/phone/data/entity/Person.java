package com.miaxis.phone.data.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * @author ZJL
 * @date 2022/3/24 14:35
 * @des
 * @updateAuthor
 * @updateDes
 */
@Entity
public class Person {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;
    private String cardnum;
    private String codestatus;

    @Ignore
    public Person() {
    }

    public Person(long id, String name, String cardnum, String codestatus) {
        this.id = id;
        this.name = name;
        this.cardnum = cardnum;
        this.codestatus = codestatus;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCardnum() {
        return cardnum;
    }

    public void setCardnum(String cardnum) {
        this.cardnum = cardnum;
    }

    public String getCodestatus() {
        return codestatus;
    }

    public void setCodestatus(String codestatus) {
        this.codestatus = codestatus;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cardnum='" + cardnum + '\'' +
                ", codestatus='" + codestatus + '\'' +
                '}';
    }
}
