package com.miaxis.bp990.been;

import com.miaxis.bp990.bridge.Status;
import com.miaxis.bp990.data.entity.Person;

/**
 * @author ZJL
 * @date 2022/3/26 12:14
 * @des
 * @updateAuthor
 * @updateDes
 */
public class ResultSearch {
    private Status code;
    private Person person;
    private String message;

    public ResultSearch(Status code, Person person, String message) {
        this.code = code;
        this.person = person;
        this.message = message;
    }

    public Status getCode() {
        return code;
    }

    public void setCode(Status code) {
        this.code = code;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ResultPerson{" +
                "code=" + code +
                ", person=" + person +
                ", message='" + message + '\'' +
                '}';
    }
}
