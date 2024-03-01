package com.netease.cloud.extension.util;

import org.hotswap.agent.javassist.bytecode.AnnotationsAttribute;
import org.hotswap.agent.javassist.bytecode.ClassFile;
import org.hotswap.agent.javassist.bytecode.annotation.Annotation;
import org.hotswap.agent.logging.AgentLogger;
import org.hotswap.agent.util.scanner.Scanner;
import org.hotswap.agent.util.scanner.ScannerVisitor;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author xiaoxuxuy
 * @Date 2024年03月01日
 * @Version: 1.0
 */
public class ClassPathAnnotationScanner {

    private static AgentLogger LOGGER = AgentLogger.getLogger(ClassPathAnnotationScanner.class);

    // Annotation name to search for
    String annotation;

    // scanner to search path
    Scanner scanner;

    /**
     * Create scanner for the annotation.
     */
    public ClassPathAnnotationScanner(String annotation, Scanner scanner) {
        this.annotation = annotation;
        this.scanner = scanner;
    }

    /**
     * Run the scan - search path for files containing annotation.
     *
     * @param classLoader classloader to resolve path
     * @param path        path to scan {@link org.hotswap.agent.util.scanner.Scanner#scan(ClassLoader, String, ScannerVisitor)}
     * @return list of class names containing the annotation
     * @throws IOException scan exception.
     */
    public List<String> scanExtTransforms(ClassLoader classLoader, String path) throws IOException {
        final List<String> files = new LinkedList<>();
        scanner.scan(classLoader, path, file -> {
            ClassFile cf;
            try {
                DataInputStream dstream = new DataInputStream(file);
                cf = new ClassFile(dstream);
            } catch (IOException e) {
                throw new IOException("Stream not a valid classFile", e);
            }

            if (hasAnnotation(cf))
                files.add(cf.getName());
        });
        return files;
    }

    /**
     * Check if the file contains annotation.
     */
    protected boolean hasAnnotation(ClassFile cf) throws IOException {
        AnnotationsAttribute visible = (AnnotationsAttribute) cf.getAttribute(AnnotationsAttribute.visibleTag);
        if (visible != null) {
            for (Annotation ann : visible.getAnnotations()) {
                if (annotation.equals(ann.getTypeName())) {
                    return true;
                }
            }
        }
        return false;
    }

}
