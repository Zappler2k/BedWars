package de.zappler2k.bedWars.json;

import java.io.File;

public interface JsonModule {

    File getFile();
    String toJson();
    JsonModule fromJson();
    String getDefaultConfig();

}
