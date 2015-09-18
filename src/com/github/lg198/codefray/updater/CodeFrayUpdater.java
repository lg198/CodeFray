package com.github.lg198.codefray.updater;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class CodeFrayUpdater {

    public static final String GH_LATEST = "https://github.com/lg198/CodeFray/releases/latest";

    public static String VERSION = "";

    public static void checkForUpdate() throws IOException {
        URL url = new URL(GH_LATEST);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoInput(true);
        con.setRequestMethod("GET");
        con.getResponseCode();

        String latestVersion = con.getURL().toString().replaceAll(".*/", "");

        InputStream is = CodeFrayUpdater.class.getResourceAsStream("/version");
        String ourVersion = "Unknown";
        if (is != null) {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            ourVersion = br.readLine().trim();
            br.close();
        }


        if (!ourVersion.equals(latestVersion)) {
            update(latestVersion);
        }

        VERSION = ourVersion;
    }

    private static void update(String version) throws IOException {
        Alert a = new Alert(AlertType.INFORMATION);
        a.setTitle("CodeFray");
        a.setHeaderText("CodeFray Updater");
        a.setContentText("A CodeFray update is available! Version " + version + " will be installed! You will now specify where you want the new version to be saved.");
        a.showAndWait();

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setInitialDirectory(null);
        chooser.setTitle("Where do you want to save the updated jar?");
        File chosen = chooser.showDialog(null);
        if (chosen == null) {
            return;
        }
        chosen = new File(chosen, "CodeFray.jar");

        System.out.println(chosen.getAbsolutePath());

        String fileUrl = "https://github.com/lg198/CodeFray/releases/download/" + version + "/CodeFray.jar";

        HttpURLConnection con1 = (HttpURLConnection) new URL(fileUrl).openConnection();
        con1.setDoInput(true);
        con1.setRequestMethod("GET");
        System.out.println(con1.getResponseCode());

        File temp = new File(chosen.getAbsolutePath() + ".temp");
        FileOutputStream fw = new FileOutputStream(temp);
        byte[] buff = new byte[1024*64];
        for (int bread; (bread = con1.getInputStream().read(buff)) > -1;) {
            fw.write(buff, 0, bread);
        }
        fw.close();
        con1.getInputStream().close();

        ZipFile zfile = new ZipFile(temp);


        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(chosen));

        zfile.stream().forEach(entry -> {
            try {
                zos.putNextEntry(entry);
                InputStream is = zfile.getInputStream(entry);
                for (int bread; (bread = is.read(buff)) > -1; ) {
                    zos.write(buff, 0, bread);
                }
                is.close();
                zos.closeEntry();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        zos.putNextEntry(new ZipEntry("version"));
        zos.write(version.getBytes(Charset.forName("UTF-8")));
        zos.closeEntry();
        zos.close();

        temp.delete();
        temp.deleteOnExit();

        Alert b = new Alert(AlertType.INFORMATION);
        b.setTitle("Update");
        b.setHeaderText("CodeFray was updated successfully!");
        b.setContentText("Replace the current jar file with the new one at '" + chosen.getAbsolutePath() + "'!");
        b.showAndWait();
        Platform.exit();
    }
}
