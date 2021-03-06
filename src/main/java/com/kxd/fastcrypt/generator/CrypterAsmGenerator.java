package com.kxd.fastcrypt.generator;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import com.kxd.fastcrypt.Crypter;
import com.kxd.fastcrypt.NopCrypter;
import com.kxd.fastcrypt.StringCrypter;
import com.kxd.fastcrypt.acceptor.CryptAcceptor;
import com.kxd.fastcrypt.acceptor.DefaultCryptAcceptor;
import com.kxd.fastcrypt.algorithm.ICryptAlgorithm;
import com.kxd.fastcrypt.handler.DefaultIterableCryptHandler;
import com.kxd.fastcrypt.handler.IterableCryptHandler;

import net.sf.cglib.core.*;

/**
 * @author mengqingyan 2019/2/15
 */
public class CrypterAsmGenerator implements CrypterGenerator{

    private static final Logger logger = LoggerFactory.getLogger(CrypterAsmGenerator.class);

    private static final CrypterKey KEY_FACTORY = (CrypterKey) KeyFactory.create(CrypterKey.class);

    private static final Type CRYPTER = TypeUtils.parseType(Crypter.class.getCanonicalName());

    private static final Type ICRYPTALGORITHM = TypeUtils.parseType(ICryptAlgorithm.class.getCanonicalName());
    private static final Type ITERABLECRYPTHANDLER = TypeUtils.parseType(IterableCryptHandler.class.getCanonicalName());

    private static final Type EX = TypeUtils.parseType("java.lang.Exception");

    private static final Signature ENCRYPT = new Signature("encrypt", Constants.TYPE_OBJECT,
            new Type[]{Constants.TYPE_OBJECT});

    private static final Signature DECRYPT = new Signature("decrypt", Constants.TYPE_OBJECT,
            new Type[]{Constants.TYPE_OBJECT});

    private static final Signature encryptM = TypeUtils.parseSignature("String encrypt(String)");

    private static final Signature decryptM = TypeUtils.parseSignature("String decrypt(String)");
    private static final Signature handleEncrypt = TypeUtils.parseSignature("Object handleEncrypt(java.lang.Iterable)");
    private static final Signature handleDecrypt = TypeUtils.parseSignature("Object handleDecrypt(java.lang.Iterable)");

    public static final Signature constructor2 = TypeUtils.parseConstructor(IterableCryptHandler.class.getCanonicalName() + "," + ICryptAlgorithm.class.getCanonicalName());



    interface CrypterKey {
        Object newInstance(String target);
    }

    private final NopCrypter nopCrypter = new NopCrypter();

    private IterableCryptHandler iterableCryptHandler = new DefaultIterableCryptHandler(this);

    private final StringCrypter stringCrypter = new StringCrypter();

    private ICryptAlgorithm cryptAlgorithm;

    private CryptAcceptor cryptAcceptor = new DefaultCryptAcceptor();

    public Crypter generate(Class targetClazz) {
        if(!cryptAcceptor.acceptTarget(targetClazz)) {
            return nopCrypter;
        }
        if(targetClazz.equals(String.class)) {
            return stringCrypter;
        }

        Generator gen = new Generator();
        gen.setTarget(targetClazz);

        gen.setIterableCryptHandler(iterableCryptHandler);
        gen.setCryptAlgorithm(cryptAlgorithm);
        gen.setCryptAcceptor(cryptAcceptor);
        return gen.create();
    }

    public void setIterableCryptHandler(IterableCryptHandler iterableCryptHandler) {
        this.iterableCryptHandler = iterableCryptHandler;
        if(this.iterableCryptHandler instanceof CrypterGeneratorAware) {
            CrypterGeneratorAware crypterGeneratorAware = (CrypterGeneratorAware) iterableCryptHandler;
            crypterGeneratorAware.setCrypterGenerator(this);
        }
    }

    public void setCryptAlgorithm(ICryptAlgorithm cryptAlgorithm) {
        this.cryptAlgorithm = cryptAlgorithm;
        this.stringCrypter.setCryptAlgorithm(cryptAlgorithm);
    }

    public void setCryptAcceptor(CryptAcceptor cryptAcceptor) {
        this.cryptAcceptor = cryptAcceptor;
    }

    public static class Generator extends AbstractClassGenerator {
        private static final Source SOURCE = new Source(Crypter.class.getName());

        private static final Map<Class, Object> singletonMap = new HashMap<Class, Object>();

        private final Set<Class> visitedClassSet = new HashSet<Class>();

        private Class target;
        private IterableCryptHandler iterableCryptHandler;
        private ICryptAlgorithm cryptAlgorithm;
        private CryptAcceptor cryptAcceptor;

        private boolean useSingleton = true;

        public Generator() {
            super(SOURCE);
        }

        public void setTarget(Class target) {
            setNamePrefix(target.getName());
            this.target = target;
        }

        public void setUseSingleton(boolean useSingleton) {
            this.useSingleton = useSingleton;
        }

        public void setIterableCryptHandler(IterableCryptHandler iterableCryptHandler) {
            this.iterableCryptHandler = iterableCryptHandler;
        }

        public void setCryptAlgorithm(ICryptAlgorithm cryptAlgorithm) {
            this.cryptAlgorithm = cryptAlgorithm;
        }

        public void setCryptAcceptor(CryptAcceptor cryptAcceptor) {
            this.cryptAcceptor = cryptAcceptor;
        }

        protected ClassLoader getDefaultClassLoader() {
            return target.getClassLoader();
        }

        protected ProtectionDomain getProtectionDomain() {
            return ReflectUtils.getProtectionDomain(target);
        }

        public Crypter create() {
            Object key = KEY_FACTORY.newInstance(target.getName());
            return (Crypter) super.create(key);
        }

        public void generateClass(ClassVisitor v) throws Exception {
            Type targetType = Type.getType(target);
            ClassEmitter ce = new ClassEmitter(v);
            ce.begin_class(Constants.V1_6, Constants.ACC_PUBLIC, getClassName(), null, new Type[]{CRYPTER},
                    Constants.SOURCE_FILE);

            EmitUtils.null_constructor(ce);


            ce.declare_field(Constants.ACC_PRIVATE, "iterableCryptHandler",
                    ITERABLECRYPTHANDLER, null);
            ce.declare_field(Constants.ACC_PRIVATE, "cryptAlgorithm",
                    ICRYPTALGORITHM, null);

            //有参构造方法
            CodeEmitter construct = ce.begin_method(Constants.ACC_PUBLIC,
                    constructor2, null);
            construct.load_this();
            construct.dup();
            construct.super_invoke_constructor();
            construct.load_arg(0);
            construct.putfield("iterableCryptHandler");

            construct.load_this();
            construct.load_arg(1);
            construct.putfield("cryptAlgorithm");


            construct.return_value();
            construct.end_method();

            CodeEmitter e = ce.begin_method(Constants.ACC_PUBLIC, ENCRYPT, null);
            generateMethod(targetType, e, encryptM);
            e.load_arg(0);

            e.return_value();
            e.end_method();

            CodeEmitter de = ce.begin_method(Constants.ACC_PUBLIC, DECRYPT, null);
            generateMethod(targetType, de, decryptM);
            de.load_arg(0);
            de.return_value();
            de.end_method();

            ce.end_class();

        }

        private void generateMethod(Type targetType, CodeEmitter e, Signature encryptM) {
            e.load_arg(0);
            Label lable = e.make_label();
            e.ifnonnull(lable);
            e.load_arg(0);
            e.return_value();
            e.mark(lable);

            Local targetLocal = e.make_local();
            e.load_arg(0);
            e.checkcast(targetType);
            e.store_local(targetLocal);

            try {
                visitTarget(e, targetLocal, target, encryptM);
            } catch (Exception e1) {
                throw new RuntimeException(e1);
            }
        }

        private void visitTarget(CodeEmitter e, Local targetLocal, Class target, Signature encryptM) {
            visitedClassSet.add(target);
            PropertyDescriptor[] setters = ReflectUtils.getBeanSetters(target);
            for (int i = 0; i < setters.length; i++) {
                PropertyDescriptor setter = setters[i];

                    Field propField = getPropField(setter, target);
                    if(propField == null) {
                        continue;
                    }

                    if (!this.cryptAcceptor.acceptField(propField)) {
                        continue;
                    }
                    Class<?> aClass = propField.getType();

                    if(isCircle(aClass, visitedClassSet)) {
                        continue;
                    }

                    MethodInfo read = ReflectUtils.getMethodInfo(setter.getReadMethod());
                    MethodInfo write = ReflectUtils.getMethodInfo(setter.getWriteMethod());

                    Type setterType = write.getSignature().getArgumentTypes()[0];
                    e.load_local(targetLocal);

                    e.invoke(read);

                    e.dup();
                    Label lablex = e.make_label();

                    Local readLocal = e.make_local();
                    e.store_local(readLocal);

                    e.ifnull(lablex);


                    if (Iterable.class.isAssignableFrom(aClass)) {
                        e.load_local(targetLocal);
                        e.load_this();
                        e.getfield("iterableCryptHandler");

                        e.load_local(readLocal);
                        e.box(read.getSignature().getReturnType());

                        if(encryptM == CrypterAsmGenerator.encryptM) {
                            e.invoke_interface(ITERABLECRYPTHANDLER, handleEncrypt);
                        } else {
                            e.invoke_interface(ITERABLECRYPTHANDLER, handleDecrypt);
                        }
                        e.unbox_or_zero(setterType);
                        e.invoke(write);

                    } else if (aClass.equals(String.class)) {
                        e.load_local(targetLocal);
                        e.load_this();
                        e.getfield("cryptAlgorithm");

                        e.load_local(readLocal);
                        e.box(read.getSignature().getReturnType());

                        e.invoke_interface(ICRYPTALGORITHM, encryptM);
                        e.unbox_or_zero(setterType);
                        e.invoke(write);
                    }
                    else  {
                        visitTarget(e, readLocal, aClass, encryptM);
                    }
                    e.mark(lablex);
            }
        }

        private boolean isCircle(final Class<?> aClass, final Set<Class> visitedClassSet) {
            final Set<Class> containedFieldTypeSet = new HashSet<Class>();
            ReflectionUtils.doWithFields(aClass, new ReflectionUtils.FieldCallback() {

                public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                    Class<?> type = field.getType();
                    if(visitedClassSet.contains(type)) {
                        containedFieldTypeSet.add(type);
                    }
                }
            });
            if(containedFieldTypeSet.isEmpty()) {
                return false;
            }
            final boolean[] find = new boolean[1];
            for (final Class containedFieldType : containedFieldTypeSet) {
                ReflectionUtils.doWithFields(containedFieldType, new ReflectionUtils.FieldCallback() {

                    public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                        Class<?> type = field.getType();
                        if(aClass.equals(type)) {
                            logger.warn("find circle when generateClass: {}, detail: [{}] <--> [{}]",
                                    new Object[]{target, aClass, containedFieldType});
                            find[0] = true;
                        }
                    }
                });
                if(find[0] == true) {
                    break;
                }
            }

            return find[0];
        }

        private Field getPropField(PropertyDescriptor setter, Class target) {
            return ReflectionUtils.findField(target, setter.getName());
        }

        protected Object firstInstance(Class type) {
            if(useSingleton) {
                Object o = singletonMap.get(type);
                if(o == null) {
                    o = ReflectUtils.newInstance(type, new Class[]{IterableCryptHandler.class, ICryptAlgorithm.class},
                            new Object[]{this.iterableCryptHandler,this.cryptAlgorithm});
                    singletonMap.put(type, o);
                }
                return o;
            }

            return ReflectUtils.newInstance(type, new Class[]{IterableCryptHandler.class, ICryptAlgorithm.class},
                    new Object[]{this.iterableCryptHandler,this.cryptAlgorithm});
        }

        protected Object nextInstance(Object instance) {
            return instance;
        }
    }
}
