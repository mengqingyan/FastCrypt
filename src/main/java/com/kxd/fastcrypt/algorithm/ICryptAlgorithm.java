package com.kxd.fastcrypt.algorithm;
/**
 * @author mengqingyan 2019/2/15
 */
public interface ICryptAlgorithm {

    String encrypt(String arg);

    String decrypt(String arg);
}
