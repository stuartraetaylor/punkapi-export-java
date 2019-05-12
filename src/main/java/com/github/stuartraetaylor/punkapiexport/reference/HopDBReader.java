package com.github.stuartraetaylor.punkapiexport.reference;

import java.io.File;
import java.nio.file.Paths;

import com.github.stuartraetaylor.punkapiexport.JsonDBReader;

public class HopDBReader extends JsonDBReader<Hop> {

    private static final File baseDir = Paths.get("submodules/hops-json").toAbsolutePath().toFile();

    public HopDBReader() {
        super(baseDir, "hops.json", Hop.class);
    }

}
