package com.kxd.fastcrypt;

import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.core.Converter;
import net.sf.cglib.core.DebuggingClassWriter;
import org.junit.Test;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mengqingyan 2019/2/15
 */
public class TestAsm {

    @Test
    public void testAsm() {
        // 代理类class文件存入本地磁盘
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "D:\\idea_proj\\java_proj\\FastCrypt\\target\\test-classes");
        Class targetClazz = EnBean.class;

        DefaultCryptAlgorithm cryptAlgorithm = new DefaultCryptAlgorithm();
        CrypterAsmGenerator crypterAsmGenerator = new CrypterAsmGenerator();

        crypterAsmGenerator.setCryptAlgorithm(cryptAlgorithm);
        crypterAsmGenerator.setCryptAcceptor(new DefaultCryptAcceptor());

        Crypter crypter = crypterAsmGenerator.generate(targetClazz);

        EnBean target = new EnBean();
        target.setIdNo("123");

        EnBean2 enBean2 = new EnBean2();
        enBean2.setIdNo("enBean2_123");

        target.setEnBean2(enBean2);

        List<EnBean2> enBean2s = new ArrayList<EnBean2>(2);
        EnBean2 enBean2s1 = new EnBean2();
        enBean2s1.setIdNo("enBean2s1_123");
        EnBean2 enBean2s2 = new EnBean2();
        enBean2s2.setIdNo("enBean2s2_123");
        enBean2s.add(enBean2s1);
        enBean2s.add(enBean2s2);
        target.setEnBean2s(enBean2s);

        crypter.encrypt(target);
        System.out.println(target);
    }
    @Test
    public void testBeanCopy() {
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "D:\\code");
        EnBean target = new EnBean();
        EnBean source = new EnBean();
        source.setIdNo("123");
        BeanCopier copier = BeanCopier.create(EnBean.class, EnBean.class, true);
        // 执行source到target的属性复制
        copier.copy(source, target, new Converter() {
            public Object convert(Object value, Class target, Object context) {
                if (target.equals(Integer.TYPE)) {
                    return new Integer(((Number)value).intValue() + 1);
                }
                return value;
            }
        });
        System.out.println("target Rate: " + target.getIdNo());

    }

    @Test
    public void testPropertyDes() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(EnBean.class, Object.class);
        PropertyDescriptor[] all = info.getPropertyDescriptors();
        for (PropertyDescriptor propertyDescriptor : all) {
            Class<?> propertyType = propertyDescriptor.getPropertyType();
            System.out.println(propertyType);
            System.out.println(propertyDescriptor);
        }
    }
}
