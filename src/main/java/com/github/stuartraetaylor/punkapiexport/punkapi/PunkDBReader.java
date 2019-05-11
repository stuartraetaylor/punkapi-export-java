package com.github.stuartraetaylor.punkapiexport.punkapi;

import static com.github.stuartraetaylor.punkapiexport.punkapi.PunkAPIUtil.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.stuartraetaylor.punkapiexport.*;
import com.github.stuartraetaylor.punkapiexport.punkapi.model.PunkSchema;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PunkDBReader implements PunkReader {

    private static final File baseDir = Paths.get("submodules/punkapi-db/data").toAbsolutePath().toFile();

	@Override
    public PunkDocument read(String beerName) throws PunkException {
        String formattedName = formatBeerName(beerName);
        File beerFile = new File(baseDir, formattedName + ".json");

        if (!beerFile.exists())
            throw new PunkDBFileException("File not found: " + beerFile.getAbsolutePath());

        PunkSchema document = readFile(beerFile);
        return new PunkDocument(formattedName, document);
    }

    @Override
    public List<PunkDocument> readAll() throws PunkException {
        File[] beerFiles = baseDir.listFiles();
        if (beerFiles.length == 0)
            throw new PunkException("No beer JSON found: " + baseDir);

        List<PunkDocument> documents = new ArrayList<>(beerFiles.length);
        for (File file : beerFiles) {
            String name = removeExtension(file.getName());
            PunkSchema document = readFile(file);
            documents.add(new PunkDocument(name, document));
        }

        return documents;
    }

    private PunkSchema readFile(File file) throws PunkException {
        try {
            log.debug("Reading Punk API DB file: {}", file.getAbsolutePath());
            return new ObjectMapper().readValue(
                    new FileInputStream(file),
                    new TypeReference<PunkSchema>() {});
        } catch (IOException e) {
            throw new PunkException("Unable to read PunkAPI data: " + file.getAbsolutePath(), e);
        }
    }

    private final Logger log = LogManager.getLogger(this.getClass());

}
