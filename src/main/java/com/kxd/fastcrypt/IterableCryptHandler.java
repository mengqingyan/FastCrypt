package com.kxd.fastcrypt;
/**
 * @author mengqingyan 2019/2/15
 */
public interface IterableCryptHandler {
    void handleEncrypt(Iterable iterable);
    void handleDecrypt(Iterable iterable);
}
