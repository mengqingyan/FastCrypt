package com.kxd.fastcrypt;

/**
 * @author mengqingyan 2019/2/14
 */
public class EnBean3 {

    @Encrypt
    private String otherName;

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }
}
