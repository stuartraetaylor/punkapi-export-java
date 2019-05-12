package com.github.stuartraetaylor.punkapiexport.reference;

import java.io.File;
import java.nio.file.Paths;

import com.github.stuartraetaylor.punkapiexport.JsonDBReader;

public class YeastDBReader extends JsonDBReader<Yeast> {

    private static final File baseDir = Paths.get("submodules/brewerwall-yeasts/formats").toAbsolutePath().toFile();

    public YeastDBReader() {
        super(baseDir, "yeasts.json", Yeast.class);
    }

}
