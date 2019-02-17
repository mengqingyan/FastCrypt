package com.kxd.fastcrypt.handler;

import com.kxd.fastcrypt.Crypter;
import com.kxd.fastcrypt.generator.CrypterGenerator;
import com.kxd.fastcrypt.generator.CrypterGeneratorAware;

import java.util.Iterator;

/**
 * @author mengqingyan 2019/2/15
 */
public class DefaultIterableCryptHandler implements IterableCryptHandler, CrypterGeneratorAware {

    private CrypterGenerator crypterGenerator;

    public DefaultIterableCryptHandler() {

    }

    public void handleEncrypt(Iterable iterable) {
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            Object next = iterator.next();
            Crypter crypter = crypterGenerator.generate(next.getClass());
            crypter.encrypt(next);
        }
    }

    public void handleDecrypt(Iterable iterable) {
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            Object next = iterator.next();
            Crypter crypter = crypterGenerator.generate(next.getClass());
            crypter.decrypt(next);
        }
    }

    public void setCrypterGenerator(CrypterGenerator crypterGenerator) {
        this.crypterGenerator = crypterGenerator;
    }
}
