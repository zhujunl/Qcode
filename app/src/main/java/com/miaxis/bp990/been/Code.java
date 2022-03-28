package com.miaxis.bp990.been;

/**
 * @author ZJL
 * @date 2022/3/25 16:13
 * @des
 * @updateAuthor
 * @updateDes
 */
public class Code {

    public String cardNumber;
    public int codeStatus;
    public long id;
    public String name;
    public long timestamp;
    public int codeType;//二维码类型 0 网证，其他 健康码

    public Code() {
    }

    @Override
    public String toString() {
        return "Code{" +
                "cardNumber='" + cardNumber + '\'' +
                ", codeStatus=" + codeStatus +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", timestamp=" + timestamp +
                ", codeType=" + codeType +
                '}';
    }
}
