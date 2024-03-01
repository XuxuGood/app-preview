package com.netease.cloud.extension.transform;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * 热更新扩展动态字节码增强
 *
 * @Author xiaoxuxuy
 * @Date 2024年03月01日
 * @Version: 1.0
 */
public class HotSwapExtTransformer implements ClassFileTransformer {

    public HotSwapExtTransformer() {
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        System.out.println(11111);
        return new byte[0];
    }

}
