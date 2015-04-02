package com.apporiented.spring.override;

import java.util.List;
import java.util.Map;


public class OverrideTestBean {
    private String string;
    private List<String> list;
    private Map<String,String> map;

    public String getString() {
        return string;
    }

    public void setString(String value) {
        this.string = value;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }
}
