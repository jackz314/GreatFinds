package com.greatfinds.cs201.tests.entry;

import javax.persistence.*;

@Entity
@NamedQuery(name = "getMsg", query = "SELECT e from Entry e")
public class Entry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String msg;

    public Entry(String msg) {
        this.msg = msg;
    }

    public Entry() {
        //no-op
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "id=" + id +
                ", msg='" + msg + '\'' +
                '}';
    }
}
