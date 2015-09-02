package com.github.lg198.codefray.load;

import com.github.lg198.codefray.api.golem.ControllerDef;
import com.github.lg198.codefray.api.golem.GolemController;
import com.github.lg198.codefray.game.golem.CFGolemController;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ControllerLoader {


    public CFGolemController load(File f) {
        if (!f.getName().endsWith(".jar")) {
            throw new LoadException("File supplied is not a jarfile!");
        }


        JarClassLoader classLoader;
        try {
            classLoader = new JarClassLoader(f.toURI().toURL());
        } catch (MalformedURLException e) {
            throw new LoadException("Jar supplied could not be loaded!");
        }

        try {
            Class<GolemController> controller = findController(new ZipFile(f.getAbsolutePath()), classLoader);
            if (controller == null) {
                throw new LoadException("Controller not found!");
            }

            return wrapController(controller.newInstance());
        } catch (IOException e) {
            throw new LoadException("Unable to locate controller!");
        } catch (InstantiationException e) {
            throw new LoadException("Unable to instantiate controller! Make sure its constructor has no parameters!");
        } catch (IllegalAccessException e) {
            throw new LoadException("Unable to access controller constructor! Make sure it is public!");
}
}

    private Class<GolemController> findController(ZipFile zf, JarClassLoader classLoader) {
        try {
            Enumeration<? extends ZipEntry> zee = zf.entries();
            while (zee.hasMoreElements()) {
                ZipEntry ze = zee.nextElement();
                if (ze.getName().endsWith(".class") && !ze.getName().contains("$")) {
                    String searchName = ze.getName().replaceAll("[/\\\\]", "\\.").replace(".class", "");
                    Class<?> c = classLoader.loadClass(searchName);
                    for (Class<?> i : c.getInterfaces()) {
                        if (i.getName().equals(GolemController.class.getName())) {
                            return (Class<GolemController>) c;
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
        }

        return null;
    }

    public static CFGolemController wrapController(GolemController c) {
        ControllerDef def = c.getClass().getAnnotation(ControllerDef.class);
        return new CFGolemController(c, def.id(), def.name(), def.version(), def.devId());
    }
}
