package com.kxd.fastcrypt.acceptor;

import java.lang.reflect.Field;

/**
 * @author mengqingyan 2019/2/15
 */
public class DefaultCryptAcceptor implements CryptAcceptor {

    public boolean acceptTarget(Class targetClazz) {
        if(targetClazz.getCanonicalName().startsWith("com.kxd")) {
            return true;
        }
        return false;
    }

    public boolean acceptField(Field field) {
        return false;
    }
}
