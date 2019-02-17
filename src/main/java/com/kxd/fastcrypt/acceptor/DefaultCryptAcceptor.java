package com.kxd.fastcrypt.acceptor;

import com.kxd.fastcrypt.Encrypt;

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
        Class<?> fieldType = field.getType();
        if(fieldType.equals(String.class) && fieldType.isAnnotationPresent(Encrypt.class)) {
            return true;
        }

        if(Iterable.class.isAssignableFrom(fieldType)
                || this.acceptTarget(fieldType)) {
            return true;
        }
        return false;
    }
}
