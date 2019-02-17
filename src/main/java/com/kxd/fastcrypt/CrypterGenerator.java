package com.kxd.fastcrypt;
/**
 * @author mengqingyan 2019/2/15
 */
public interface CrypterGenerator {

    /**
     * 对目标类，产生加解密处理类，目标类中，禁止出现循环引用
     * @param targetClazz
     * @return
     */
    Crypter generate(Class targetClazz);

}
