
package com.kxd.fastcrypt;

import java.lang.annotation.*;

/**
 * @author mengqingyan 2019/2/15
 */
@Documented
@Target({ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Encrypt {
}
