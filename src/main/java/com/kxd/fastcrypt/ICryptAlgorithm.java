package com.kxd.fastcrypt;
/**
 * @author mengqingyan 2019/2/15
 */
public interface ICryptAlgorithm {

    String encrypt(String arg);

    String decrypt(String arg);
}
