package com.kxd.fastcrypt;

import com.kxd.fastcrypt.algorithm.ICryptAlgorithm;

/**
 * @author mengqingyan 2019/2/14
 */
public final class StringCrypter implements Crypter {

    private ICryptAlgorithm cryptAlgorithm;

    public Object encrypt(Object bean) {
        return cryptAlgorithm.encrypt((String) bean);
    }

    public Object decrypt(Object bean) {
        return cryptAlgorithm.decrypt((String) bean);
    }

    public void setCryptAlgorithm(ICryptAlgorithm cryptAlgorithm) {
        this.cryptAlgorithm = cryptAlgorithm;
    }
}
