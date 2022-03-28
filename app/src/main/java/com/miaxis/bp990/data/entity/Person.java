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
   private int codestatus;
   @Ignore
   private String fingerprint0;
   @Ignore
   private String fingerprintPosition0;
   @Ignore
   private String fingerprint1;
   @Ignore
   private String fingerprintPosition1;


   public Person() {
   }

   private Person(Builder builder) {
      setId(builder.id);;
      setName(builder.name);
      setCardnum(builder.cardnum);
      setFacepath(builder.facepath);
      setCodestatus(builder.codestatus);
      setFingerprint0(builder.fingerprint0);
      setFingerprintPosition0(builder.fingerprintPosition0);
      setFingerprint1(builder.fingerprint1);
      setFingerprintPosition1(builder.fingerprintPosition1);

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

   public int getCodestatus() {
      return codestatus;
   }

   public void setCodestatus(int codestatus) {
      this.codestatus = codestatus;
   }

   public String getFingerprint0() {
      return fingerprint0;
   }

   public void setFingerprint0(String fingerprint0) {
      this.fingerprint0 = fingerprint0;
   }

   public String getFingerprintPosition0() {
      return fingerprintPosition0;
   }

   public void setFingerprintPosition0(String fingerprintPosition0) {
      this.fingerprintPosition0 = fingerprintPosition0;
   }

   public String getFingerprint1() {
      return fingerprint1;
   }

   public void setFingerprint1(String fingerprint1) {
      this.fingerprint1 = fingerprint1;
   }

   public String getFingerprintPosition1() {
      return fingerprintPosition1;
   }

   public void setFingerprintPosition1(String fingerprintPosition1) {
      this.fingerprintPosition1 = fingerprintPosition1;
   }

   @Override
   public String toString() {
      return "Person{" +
              "id=" + id +
              ", name='" + name + '\'' +
              ", cardnum='" + cardnum + '\'' +
              ", facepath='" + facepath + '\'' +
              ", codestatus=" + codestatus +
              ", fingerprint0='" + fingerprint0 + '\'' +
              ", fingerprintPosition0='" + fingerprintPosition0 + '\'' +
              ", fingerprint1='" + fingerprint1 + '\'' +
              ", fingerprintPosition1='" + fingerprintPosition1 + '\'' +
              '}';
   }

   public static final class Builder {
      private long id;
      private String name;
      private String cardnum;
      private String facepath;
      private int codestatus;
      private String fingerprint0;
      private String fingerprintPosition0;
      private String fingerprint1;
      private String fingerprintPosition1;

      public Builder() {
      }

      public Builder id(Long val) {
         id = val;
         return this;
      }


      public Builder name(String val){
         this.name=val;
         return this;
      }

      public Builder cardnum(String val){
         this.cardnum=val;
         return this;
      }

      public Builder facepath(String val){
         this.facepath=val;
         return this;
      }

      public Builder codestatus(int val){
         this.codestatus=val;
         return this;
      }

      public Builder fingerprint0(String val){
         this.fingerprint0=val;
         return this;
      }

      public Builder fingerprintPosition0(String  val){
         this.fingerprint0=val;
         return this;
      }

      public Builder fingerprint1(String val){
         this.fingerprint1=val;
         return this;
      }

      public Builder fingerprintPosition1(String val){
         this.fingerprintPosition1=val;
         return this;
      }

      public Person build() {
         return new Person(this);
      }
   }
}
