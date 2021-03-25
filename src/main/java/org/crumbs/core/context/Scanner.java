package org.crumbs.core.context;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Scanner {

    public static List<Class<?>> getClassesInPackage(String packageName) throws Exception {
        Enumeration<URL> enumeration =
                Thread.currentThread().getContextClassLoader()
                        .getResources(packageName.replace(".", "/"));

        List<URL> allResourceUrls = new ArrayList<>();

        List<Class<?>> allClasses = new ArrayList<>();

        while (enumeration.hasMoreElements()) {
            allResourceUrls.add(enumeration.nextElement());
        }

        for (URL url: allResourceUrls) {
            String path = url.getPath();
            if(!isJar(path)) {
                allClasses.addAll(findClassesInDir(new File(url.getFile()), packageName));
            } else {
                String jarPath = path.substring(6, path.lastIndexOf("!"));
                if(!isWindows()){
                    jarPath = "/" + jarPath;
                }
                allClasses.addAll(findClassesInJar(jarPath));
            }
        }

        return allClasses;
    }

    private static boolean isJar(String path) {
        return path.startsWith("file:/") && path.contains("!");
    }

    private static List<Class<?>> findClassesInDir(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClassesInDir(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

    public static List<Class<?>> findClassesInJar(String givenFile) throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        try (JarFile jarFile = new JarFile(givenFile)) {
            Enumeration<JarEntry> e = jarFile.entries();
            while (e.hasMoreElements()) {
                JarEntry jarEntry = e.nextElement();
                if (jarEntry.getName().endsWith(".class")) {
                    String className = jarEntry.getName()
                            .replace("/", ".")
                            .replace(".class", "");
                    classes.add(Class.forName(className));
                }
            }
            return classes;
        }
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }
}