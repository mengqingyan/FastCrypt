package com.kxd.fastcrypt;

/**
 * @author mengqingyan 2019/2/14
 */
public class EnBean2 {

    @Encrypt
    private String name;

    private String idType;

    @Encrypt
    private String idNo;
    @Encrypt
    private EnBean3 enBean3;

    public EnBean3 getEnBean3() {
        return enBean3;
    }

    public void setEnBean3(EnBean3 enBean3) {
        this.enBean3 = enBean3;
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
        return "EnBean2{" +
                "name='" + name + '\'' +
                ", idType='" + idType + '\'' +
                ", idNo='" + idNo + '\'' +
                '}';
    }
}
