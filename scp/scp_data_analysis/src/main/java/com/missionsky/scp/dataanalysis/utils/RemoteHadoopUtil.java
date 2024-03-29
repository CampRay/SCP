package com.missionsky.scp.dataanalysis.utils;

import java.io.*;
import java.net.*;
import java.util.jar.*;
import java.util.regex.Matcher;

/**
 * 远程发送任务至JobTracker 
 * 1.实现运行时加载指定文件夹下的配置文件
 * 2.运行时打Jar包
 */
public class RemoteHadoopUtil {
	
    /**
     * 加载配置文件
     */
    public static void setConf(Class<?> clazz, Thread thread, String path) {
        URL url = clazz.getResource(path);
        try {
            File confDir = new File(url.toURI());
            if (!confDir.exists()) {
                return;
            }
            URL key = confDir.getCanonicalFile().toURI().toURL();
            ClassLoader classLoader = thread.getContextClassLoader();
            classLoader = new URLClassLoader(new URL[] { key }, classLoader);
            thread.setContextClassLoader(classLoader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 动态生成Jar包
     */
    public static File createJar(Class<?> clazz) throws Exception {
        String fqn = clazz.getName();
        String base = fqn.substring(0, fqn.lastIndexOf("."));
        base = "/" + base.replaceAll("\\.", Matcher.quoteReplacement("/"));
        URL root = clazz.getResource("");

        JarOutputStream out = null;
        final File jar = File.createTempFile("HadoopRunningJar-", ".jar", new File(System.getProperty("java.io.tmpdir")));
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                jar.delete();
            }
        });
        try {
            File path = new File(root.toURI());
            Manifest manifest = new Manifest();
            manifest.getMainAttributes().putValue("Manifest-Version", "1.0");
            manifest.getMainAttributes().putValue("Created-By", "RemoteHadoopUtil");
            out = new JarOutputStream(new FileOutputStream(jar), manifest);
            writeBaseFile(out, path, base);
        } finally {
            out.flush();
            out.close();
        }
        return jar;
    }

    /**
     * 递归添加.class文件
     */
    private static void writeBaseFile(JarOutputStream out, File file, String base) throws IOException {
        if (file.isDirectory()) {
            File[] fl = file.listFiles();
            if (base.length() > 0) {
                base = base + "/";
            }
            for (int i = 0; i < fl.length; i++) {
                writeBaseFile(out, fl[i], base + fl[i].getName());
            }
        } else {
            out.putNextEntry(new JarEntry(base));
            FileInputStream in = null;
            try {
                in = new FileInputStream(file);
                byte[] buffer = new byte[1024];
                int n = in.read(buffer);
                while (n != -1) {
                    out.write(buffer, 0, n);
                    n = in.read(buffer);
                }
            } finally {
                in.close();
            }  
        }
    }
}
