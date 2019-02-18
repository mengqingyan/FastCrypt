package com.kxd.fastcrypt.acceptor;

import java.lang.reflect.Field;

import com.kxd.fastcrypt.Encrypt;

/**
 * @author mengqingyan 2019/2/15
 */
public class DefaultCryptAcceptor implements CryptAcceptor {

    public boolean acceptTarget(Class targetClazz) {
        if(targetClazz.equals(String.class)
                || targetClazz.getCanonicalName().startsWith("com.kxd")) {
            return true;
        }
        return false;
    }

    public boolean acceptField(Field field) {
        if(field.isAnnotationPresent(Encrypt.class)) {
            return true;
        }

        return false;
    }
}
