package com.kxd.fastcrypt.handler;

import java.util.*;

import com.kxd.fastcrypt.Crypter;
import com.kxd.fastcrypt.generator.CrypterGenerator;

/**
 * @author mengqingyan 2019/2/15
 */
public class DefaultIterableCryptHandler implements IterableCryptHandler {

    private final CrypterGenerator crypterGenerator;

    public DefaultIterableCryptHandler(CrypterGenerator crypterGenerator) {
        this.crypterGenerator = crypterGenerator;
    }

    public Object handleEncrypt(Iterable iterable) {
        Iterator iterator = iterable.iterator();

        List res = new ArrayList();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            Crypter crypter = crypterGenerator.generate(next.getClass());
            Object encrypt = crypter.encrypt(next);
            res.add(encrypt);
        }

        return returnRes(iterable, res);
    }

    public Object handleDecrypt(Iterable iterable) {
        Iterator iterator = iterable.iterator();

        List res = new ArrayList();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            Crypter crypter = crypterGenerator.generate(next.getClass());
            Object encrypt = crypter.decrypt(next);
            res.add(encrypt);
        }

        return returnRes(iterable, res);
    }

    private Object returnRes(Iterable iterable, List res) {

        Class<? extends Iterable> iterableClass = iterable.getClass();
        String iterableClassName = iterableClass.getName();
        if("java.util.Arrays$ArrayList".equals(iterableClassName)) {
            return Arrays.asList(res);
        }

        if(iterable instanceof Collection) {
            Collection collection = null;
            try {
                collection = (Collection) iterable.getClass().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            collection.addAll(res);
            return collection;
        }
        return iterable;
    }

}
