package com.kxd.fastcrypt;

import com.kxd.fastcrypt.algorithm.DefaultCryptAlgorithm;
import com.kxd.fastcrypt.generator.CrypterAsmGenerator;
import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.core.Converter;
import net.sf.cglib.core.DebuggingClassWriter;
import org.junit.Test;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author mengqingyan 2019/2/15
 */
public class TestAsm {

    @Test
    public void testAsm() {
        // 代理类class文件存入本地磁盘
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "target\\test-classes");
        Class targetClazz = EnBean.class;

        DefaultCryptAlgorithm cryptAlgorithm = new DefaultCryptAlgorithm();
        CrypterAsmGenerator crypterAsmGenerator = new CrypterAsmGenerator();

        crypterAsmGenerator.setCryptAlgorithm(cryptAlgorithm);

        Crypter crypter = crypterAsmGenerator.generate(targetClazz);

        EnBean target = new EnBean();
        target.setName("Test target");
        target.setIdNo("123");
        target.setIdType("000");

        List otherList = new ArrayList(2);
        otherList.add("TestOther1");
        EnBean2 enBean2Other = new EnBean2();
        enBean2Other.setName("Test enBean2Other");
        enBean2Other.setIdNo("enBean2Other_123");
        enBean2Other.setIdType("000");
        otherList.add(enBean2Other);
        target.setOtherList(otherList);


//        List<String> subNameList = new ArrayList<String>(2);
//        subNameList.add("subNameList1");
//        subNameList.add("subNameList2");
//        target.setSubNameList(subNameList);

        target.setSubNameList(Arrays.asList("subNameList1","subNameList2"));

        Set<String> subNameSet = new HashSet<String>(2);
        subNameSet.add("subNameSet1");
        subNameSet.add("subNameSet2");
        target.setSubNameSet(subNameSet);


        EnBean2 enBean2 = new EnBean2();
        enBean2.setName("Test enBean2");
        enBean2.setIdNo("enBean2_123");
        enBean2.setIdType("000");

        target.setEnBean2(enBean2);

        List<EnBean2> enBean2s = new ArrayList<EnBean2>(2);
        EnBean2 enBean2s1 = new EnBean2();
        enBean2s1.setName("Test enBean2s1");
        enBean2s1.setIdNo("enBean2s1_123");
        enBean2s1.setIdType("000");
        EnBean2 enBean2s2 = new EnBean2();
        enBean2s2.setName("Test enBean2s2");
        enBean2s2.setIdNo("enBean2s2_123");
        enBean2s2.setIdType("000");
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
            System.out.println(propertyDescriptor.getName());
        }
    }

    @Test
    public void testField() throws Exception {
//        Field idNo = EnBean3.class.getDeclaredField("idNo");
//        System.out.println(idNo);
        Field[] fields = EnBean.class.getDeclaredFields();
        for (Field field : fields) {
//            System.out.println(field);
//            System.out.println(field.getGenericType());
//            System.out.println(field.isAnnotationPresent(Encrypt.class));

            Type genericType = field.getGenericType();
            if(genericType == null) continue;
            // 如果是泛型参数的类型
            if(genericType instanceof ParameterizedType){
                ParameterizedType pt = (ParameterizedType) genericType;
                //得到泛型里的class类型对象
                Class<?> genericClazz = (Class<?>)pt.getActualTypeArguments()[0];
                System.out.println("field: " + field + ", genericClazz: " + genericClazz);
            }
        }
    }


}
