package com.miaxis.bp990.data.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * @author ZJL
 * @date 2022/3/24 14:04
 * @des 
 * @version 
 * @updateAuthor 
 * @updateDes 
 */
@Entity
public class Person   {
   @PrimaryKey(autoGenerate = true)
   private long id;

   private String name;
   private String cardnum;
   private String facepath;
   private String finger1;
   private String finger2;
   private String codestatus;

   @Ignore
   public Person() {
   }

   public Person(String name, String cardnum, String facepath, String finger1, String finger2, String codestatus) {
      this.name = name;
      this.cardnum = cardnum;
      this.facepath = facepath;
      this.finger1 = finger1;
      this.finger2 = finger2;
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

   public String getFacepath() {
      return facepath;
   }

   public void setFacepath(String facepath) {
      this.facepath = facepath;
   }

   public String getFinger1() {
      return finger1;
   }

   public void setFinger1(String finger1) {
      this.finger1 = finger1;
   }

   public String getFinger2() {
      return finger2;
   }

   public void setFinger2(String finger2) {
      this.finger2 = finger2;
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
              ", facepath='" + facepath + '\'' +
              ", finger1='" + finger1 + '\'' +
              ", finger2='" + finger2 + '\'' +
              ", codestatus='" + codestatus + '\'' +
              '}';
   }
}
