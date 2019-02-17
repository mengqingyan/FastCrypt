
package com.kxd.fastcrypt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author mengqingyan 2019/2/15
 */
@Target({ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
public @interface Encrypt {
}
