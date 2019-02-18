package com.kxd.fastcrypt;
/**
 * @author mengqingyan 2019/2/14
 */
public final class NopCrypter implements Crypter {
    public Object encrypt(Object bean) {
        return bean;
    }

    public Object decrypt(Object bean) {
        return bean;
    }
}
