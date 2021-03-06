package com.kxd.fastcrypt.generator;

import com.kxd.fastcrypt.Crypter;

/**
 * @author mengqingyan 2019/2/15
 */
public interface CrypterGenerator {

    /**
     * 对目标类，产生加解密处理类
     * @param targetClazz
     * @return
     */
    Crypter generate(Class targetClazz);

}
