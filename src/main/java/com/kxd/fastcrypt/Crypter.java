package com.kxd.fastcrypt;

/**
 * @author mengqingyan 2019/2/14
 */
public interface Crypter {
    void encrypt(Object bean);

    void decrypt(Object bean);
}
