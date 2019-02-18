package com.kxd.fastcrypt;

/**
 * @author mengqingyan 2019/2/14
 */
public interface Crypter {
    Object encrypt(Object bean);

    Object decrypt(Object bean);
}
