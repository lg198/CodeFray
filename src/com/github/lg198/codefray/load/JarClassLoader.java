package com.github.lg198.codefray.load;

import java.net.URL;
import java.net.URLClassLoader;

public class JarClassLoader extends URLClassLoader {

    private URL url;

    public JarClassLoader(URL url) {
        super(new URL[] { url });
        this.url = url;
    }
}
