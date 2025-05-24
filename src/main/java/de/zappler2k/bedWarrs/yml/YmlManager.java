package de.zappler2k.bedWarrs.yml;

import java.io.File;

public class YmlManager {

    public void copyFile(String resourceFile, File finalPath) {
        if(!finalPath.getParentFile().exists()) {
            finalPath.getParentFile().mkdirs();
        }

    }
}
