package com.miaxis.bp990.been;

import android.graphics.Bitmap;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class IDCardRecord {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    /* 注释说明：二代证 / 港澳台 / 外国人永久居留证 */
    /**
     * 卡片类型 空值=二代证，J=港澳台，I=外国人永久居留证
     **/
    private String cardType;
    /**
     * 物理编号
     **/
    private String cardId;
    /**
     * 姓名
     **/
    private String name;
    /**
     * 身份证号码 / 身份证号 / 永久居留证号码
     **/
    private String cardNumber;

    private Bitmap cardBitmap;

    /**
     * 指纹0
     **/

    private String fingerprint0;
    /**
     * 指纹0指位
     **/

    private String fingerprintPosition0;
    /**
     * 指纹1
     **/

    private String fingerprint1;
    /**
     * 指纹1指位
     **/

    private String fingerprintPosition1;

    public IDCardRecord() {
    }

    private IDCardRecord(Builder builder) {
        setId(builder.id);
        setCardType(builder.cardType);
        setCardId(builder.cardId);
        setName(builder.name);
        setCardNumber(builder.cardNumber);
        setCardBitmap(builder.cardBitmap);
        setFingerprint0(builder.fingerprint0);
        setFingerprintPosition0(builder.fingerprintPosition0);
        setFingerprint1(builder.fingerprint1);
        setFingerprintPosition1(builder.fingerprintPosition1);
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Bitmap getCardBitmap() {
        return cardBitmap;
    }

    public void setCardBitmap(Bitmap cardBitmap) {
        this.cardBitmap = cardBitmap;
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

    public static final class Builder {
        private Long id;
        private String cardType;
        private String cardId;
        private String name;
        private String cardNumber;
        private Bitmap cardBitmap;
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

        public Builder cardType(String val) {
            cardType = val;
            return this;
        }

        public Builder cardId(String val) {
            cardId = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }
        public Builder cardNumber(String val) {
            cardNumber = val;
            return this;
        }
        public Builder cardBitmap(Bitmap val) {
            cardBitmap = val;
            return this;
        }
        public Builder fingerprint0(String val) {
            fingerprint0 = val;
            return this;
        }
        public Builder fingerprintPosition0(String  val) {
            fingerprintPosition0 = val;
            return this;
        }
        public Builder fingerprint1(String  val) {
            fingerprint1 = val;
            return this;
        }
        public Builder fingerprintPosition1(String  val) {
            fingerprintPosition1 = val;
            return this;
        }
        public IDCardRecord build() {
            return new IDCardRecord(this);
        }
    }

    @Override
    public String toString() {
        return "IDCardRecord{" +
                "id=" + id +
                ", cardType='" + cardType + '\'' +
                ", cardId='" + cardId + '\'' +
                ", name='" + name + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                '}';
    }
}
