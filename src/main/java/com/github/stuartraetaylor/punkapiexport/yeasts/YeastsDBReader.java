package com.github.stuartraetaylor.punkapiexport.yeasts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.stuartraetaylor.punkapiexport.PunkException;
import com.github.stuartraetaylor.punkapiexport.Yeast;
import com.github.stuartraetaylor.punkapiexport.YeastReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YeastsDBReader implements YeastReader {

    private static final File baseDir = Paths.get("submodules/brewerwall-yeasts/formats").toAbsolutePath().toFile();

	@Override
    public List<Yeast> readAll() throws PunkException {
        File file = new File(baseDir, "yeasts.json");
        try {
            log.debug("Reading yeasts DB file: {}", file.getAbsolutePath());
            return new ObjectMapper().readValue(
                    new FileInputStream(file),
                    new TypeReference<List<Yeast>>() {});
        } catch (IOException e) {
            throw new PunkException("Unable to read yeast data: " + file.getAbsolutePath(), e);
        }

    }

    private final Logger log = LogManager.getLogger(this.getClass());

}
