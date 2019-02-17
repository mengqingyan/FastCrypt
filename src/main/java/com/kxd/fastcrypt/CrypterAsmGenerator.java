package com.kxd.fastcrypt;

import net.sf.cglib.core.*;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

import java.beans.PropertyDescriptor;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mengqingyan 2019/2/15
 */
public class CrypterAsmGenerator implements CrypterGenerator{
    private static final CrypterKey KEY_FACTORY = (CrypterKey) KeyFactory.create(CrypterKey.class);

    private static final Type CRYPTER = TypeUtils.parseType(Crypter.class.getCanonicalName());

    private static final Type ICRYPTALGORITHM = TypeUtils.parseType(ICryptAlgorithm.class.getCanonicalName());
    private static final Type ITERABLECRYPTHANDLER = TypeUtils.parseType(IterableCryptHandler.class.getCanonicalName());

    private static final Type EX = TypeUtils.parseType("java.lang.Exception");

    private static final Signature ENCRYPT = new Signature("encrypt", Type.VOID_TYPE,
            new Type[]{Constants.TYPE_OBJECT});

    private static final Signature DECRYPT = new Signature("decrypt", Type.VOID_TYPE,
            new Type[]{Constants.TYPE_OBJECT});

    private static final Signature encryptM = TypeUtils.parseSignature("String encrypt(String)");

    private static final Signature decryptM = TypeUtils.parseSignature("String decrypt(String)");
    private static final Signature handleEncrypt = TypeUtils.parseSignature("void handleEncrypt(java.lang.Iterable)");
    private static final Signature handleDecrypt = TypeUtils.parseSignature("void handleDecrypt(java.lang.Iterable)");

    public static final Signature constructor2 = TypeUtils.parseConstructor(IterableCryptHandler.class.getCanonicalName() + "," + ICryptAlgorithm.class.getCanonicalName());



    interface CrypterKey {
        Object newInstance(String target);
    }

    private final NopCrypter nopCrypter = new NopCrypter();
    private IterableCryptHandler iterableCryptHandler = new DefaultIterableCryptHandler();

    private ICryptAlgorithm cryptAlgorithm;

    private CryptAcceptor cryptAcceptor;

    public Crypter generate(Class targetClazz) {
        if(!cryptAcceptor.acceptTarget(targetClazz)) {
            return nopCrypter;
        }
        Generator gen = new Generator();
        gen.setTarget(targetClazz);

        if(iterableCryptHandler instanceof CrypterGeneratorAware) {
            CrypterGeneratorAware crypterGeneratorAware = (CrypterGeneratorAware) iterableCryptHandler;
            crypterGeneratorAware.setCrypterGenerator(this);
        }
        gen.setIterableCryptHandler(iterableCryptHandler);
        gen.setCryptAlgorithm(cryptAlgorithm);
        gen.setCryptAcceptor(cryptAcceptor);
        return gen.create();
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

    public static class Generator extends AbstractClassGenerator {
        private static final Source SOURCE = new Source(Crypter.class.getName());

        private static final Map<Class, Object> singletonMap = new HashMap<Class, Object>();

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
            e.return_value();
            e.end_method();

            CodeEmitter de = ce.begin_method(Constants.ACC_PUBLIC, DECRYPT, null);
            generateMethod(targetType, de, decryptM);
            de.return_value();
            de.end_method();

            ce.end_class();

        }

        private void generateMethod(Type targetType, CodeEmitter e, Signature encryptM) {
            e.load_arg(0);
            Label lable = e.make_label();
            e.ifnonnull(lable);

            e.return_value();
            e.mark(lable);

            Local targetLocal = e.make_local();
            e.load_arg(0);
            e.checkcast(targetType);
            e.store_local(targetLocal);
//            e.load_local(targetLocal);

            visitTarget(e, targetLocal, target, encryptM);
        }

        private void visitTarget(CodeEmitter e, Local targetLocal, Class target, Signature encryptM) {
            PropertyDescriptor[] setters = ReflectUtils.getBeanSetters(target);
            Map names = new HashMap();
            for (int i = 0; i < setters.length; i++) {
                names.put(setters[i].getName(), setters[i]);
            }
            for (int i = 0; i < setters.length; i++) {
                PropertyDescriptor setter = setters[i];
                PropertyDescriptor getter = (PropertyDescriptor) names.get(setter.getName());
                if (getter != null) {


                    Class<?> aClass = setter.getPropertyType();
                    boolean acceptTarget = this.cryptAcceptor.acceptTarget(aClass);
                    if (!aClass.equals(String.class)
                            && !Iterable.class.isAssignableFrom(aClass)
                            && !acceptTarget) {
                        continue;
                    }
                    MethodInfo read = ReflectUtils.getMethodInfo(getter.getReadMethod());
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
                        e.load_this();
                        e.getfield("iterableCryptHandler");

                        e.load_local(readLocal);
                        e.box(read.getSignature().getReturnType());

                        if(encryptM == CrypterAsmGenerator.encryptM) {
                            e.invoke_interface(ITERABLECRYPTHANDLER, handleEncrypt);
                        } else {
                            e.invoke_interface(ITERABLECRYPTHANDLER, handleDecrypt);
                        }

                    } else if (aClass.equals(String.class)) {
                        e.load_local(targetLocal);
                        e.load_this();
                        e.getfield("cryptAlgorithm");

                        e.load_local(readLocal);
                        e.box(read.getSignature().getReturnType());

                        e.invoke_interface(ICRYPTALGORITHM, encryptM);
                        e.unbox_or_zero(setterType);
                        e.invoke(write);
                    } else if (acceptTarget) {
                        visitTarget(e, readLocal, aClass, encryptM);
                    }
                    e.mark(lablex);
                }
            }
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
