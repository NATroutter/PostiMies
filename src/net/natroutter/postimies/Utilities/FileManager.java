package net.natroutter.postimies.Utilities;

import net.natroutter.postimies.Postimies;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileManager {

    private String ConfigFile = null;
    private String ConfigPath;

    private String fileName;
    private String folder;

    public FileManager(String folder, String fileName) {
        this.fileName = fileName;
        this.folder = folder;
        initialize();
        load();
    }

    private void initialize() {
        try {
            ConfigPath = new File(Postimies.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getPath().replace('\\', '/');
        } catch (Exception e) {
            Logger.Error("FileManager Initializing Failed! (0x0)");
            return;
        }

        try {
            String ConfDir = ConfigPath + "/" + folder + "/";
            File Dir = new File(ConfDir);
            if(Dir.exists() && !Dir.isDirectory()) {

            } else {
                Dir.mkdirs();
            }
            File CfgFile = new File(ConfDir + fileName);
            if (!CfgFile.exists()) {
                if (!exportConfFile()) {
                    Logger.Error("FileManager Initializing Failed! (0x1)");
                    return;
                }
            }
            Logger.Info("FileManager Initializes! (" + fileName + ")");
        } catch (Exception e) {
            Logger.Error("FileManager Initializing Failed! (0x2)");
            System.exit(0);
        }

    }

    public void load() {
        try {
            ConfigFile = readFile();
            Logger.Info(fileName + " Loaded!");
        } catch (Exception e) {
            Logger.Error("Can't Read " + fileName + "\n" + e.getMessage());
            return;
        }
    }

    public String getFile() { return ConfigFile; }

    public void Save(String json) {
        writeFile(json, false);
    }

    private String exportResource(String resourceName) throws Exception {
        InputStream stream =  Postimies.class.getResourceAsStream("/" + resourceName);
        if(stream == null) {
            throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
        }

        int readBytes;
        byte[] buffer = new byte[4096];

        System.out.println("TEST: " + ConfigPath + "/" + folder + "/" + resourceName);

        OutputStream resStreamOut = new FileOutputStream(ConfigPath + "/" + folder + "/" + resourceName);

        while ((readBytes = stream.read(buffer)) > 0) {
            resStreamOut.write(buffer, 0, readBytes);
        }

        stream.close();
        resStreamOut.close();

        return ConfigPath + "/" + folder + "/" + resourceName;
    }

    private boolean exportConfFile() {
        try {
            exportResource(fileName);
            return true;
        } catch (Exception e) {
            Logger.Error("Can't export file ("+fileName+")");
            e.printStackTrace();
        }
        return false;
    }

    private String readFile() throws IOException {

        String ConfFile = ConfigPath + "/" + folder + "/" + fileName;

        BufferedReader br = new BufferedReader(new FileReader(ConfFile));
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();

        while (line != null) {
            sb.append(line);
            sb.append(System.lineSeparator());
            line = br.readLine();
        }

        String Content = sb.toString();
        br.close();
        return Content;
    }

    private boolean writeFile(String Content, Boolean KeepOld) {
        String path = ConfigPath + "/" + folder + "/" + fileName;

        File file = new File(path);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);

            if (KeepOld) {
                String Old = readFile();
                bw.write(Old + Content);
            } else {
                bw.write(Content);
            }
            bw.close();
            return true;
        } catch (Exception e) {}
        return false;
    }


}
