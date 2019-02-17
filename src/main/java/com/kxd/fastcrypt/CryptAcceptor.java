package com.kxd.fastcrypt;

import java.lang.reflect.Field;

/**
 * @author mengqingyan 2019/2/15
 */
public interface CryptAcceptor {

    boolean acceptTarget(Class targetClazz);

    boolean acceptField(Field field);
}
