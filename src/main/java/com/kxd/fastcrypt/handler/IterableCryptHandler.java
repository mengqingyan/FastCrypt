package com.kxd.fastcrypt.handler;
/**
 * @author mengqingyan 2019/2/15
 */
public interface IterableCryptHandler {
    Object handleEncrypt(Iterable iterable);
    Object handleDecrypt(Iterable iterable);
}
