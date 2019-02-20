package com.kxd.fastcrypt;

import java.util.List;
import java.util.Set;

/**
 * @author mengqingyan 2019/2/14
 */
public class EnBean {

    @Encrypt
    private EnBean enBean;

    @Encrypt
    private String name;

    private String idType;

    @Encrypt
    private String idNo;

    @Encrypt
    private EnBean2 enBean2;

    @Encrypt
    private EnBean2 enBean22;

    @Encrypt
    private EnBean3 enBean3;

    private Object obj;

    @Encrypt
    private List<EnBean2> enBean2s;
    @Encrypt
    private List<String> subNameList;
    @Encrypt
    private Set<String> subNameSet;
    @Encrypt
    private List otherList;

    public EnBean3 getEnBean3() {
        return enBean3;
    }

    public void setEnBean3(EnBean3 enBean3) {
        this.enBean3 = enBean3;
    }

    public EnBean2 getEnBean22() {
        return enBean22;
    }

    public void setEnBean22(EnBean2 enBean22) {
        this.enBean22 = enBean22;
    }

    public EnBean getEnBean() {
        return enBean;
    }

    public void setEnBean(EnBean enBean) {
        this.enBean = enBean;
    }

    public List getOtherList() {
        return otherList;
    }

    public void setOtherList(List otherList) {
        this.otherList = otherList;
    }

    public List<String> getSubNameList() {
        return subNameList;
    }

    public void setSubNameList(List<String> subNameList) {
        this.subNameList = subNameList;
    }

    public Set<String> getSubNameSet() {
        return subNameSet;
    }

    public void setSubNameSet(Set<String> subNameSet) {
        this.subNameSet = subNameSet;
    }

    public List<EnBean2> getEnBean2s() {
        return enBean2s;
    }

    public void setEnBean2s(List<EnBean2> enBean2s) {
        this.enBean2s = enBean2s;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public EnBean2 getEnBean2() {
        return enBean2;
    }

    public void setEnBean2(EnBean2 enBean2) {
        this.enBean2 = enBean2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name == null) {
            System.out.println("no null");
            System.out.println("no null2");
//            return;

        }
        this.name = name;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    @Override
    public String toString() {
        return "EnBean{" +
                "name='" + name + '\'' +
                ", idType='" + idType + '\'' +
                ", idNo='" + idNo + '\'' +
                ", enBean2=" + enBean2 +
                ", obj=" + obj +
                ", enBean2s=" + enBean2s +
                ", subNameList=" + subNameList +
                ", subNameSet=" + subNameSet +
                ", otherList=" + otherList +
                '}';
    }
}
