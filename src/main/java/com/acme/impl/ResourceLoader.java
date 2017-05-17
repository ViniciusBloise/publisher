package com.acme.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Created by viniciusbloise on 16/05/17.
 */
public class ResourceLoader {

    public static String[] load(String path){
        ClassLoader classLoader = ResourceLoader.class.getClassLoader();
        File file = new File(classLoader.getResource(path).getFile());
        List<String> lines = null;
        try {
            lines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  lines.toArray(new String[lines.size()]);
    }
}
