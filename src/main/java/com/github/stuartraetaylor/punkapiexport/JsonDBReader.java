package com.github.stuartraetaylor.punkapiexport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class JsonDBReader<T> implements Reader<T> {

    private final File jsonFile;
    private final Class<T> itemClass;

    public JsonDBReader(File baseDir, String fileName, Class<T> itemClass) {
        this.jsonFile = new File(baseDir, fileName);
        this.itemClass = itemClass;
    }

	@Override
    public List<T> readAll() throws PunkException {
        try {
            log.debug("Reading JSON DB file: {}", jsonFile.getAbsolutePath());
            ObjectMapper mapper = new ObjectMapper();
            JavaType type = mapper.getTypeFactory().
                    constructCollectionType(List.class, itemClass);
            return mapper.readValue(
                    new FileInputStream(jsonFile), type);
        } catch (IOException e) {
            throw new PunkException("Unable to read JSON data: " + jsonFile.getAbsolutePath(), e);
        }

    }

    private final Logger log = LogManager.getLogger(this.getClass());

}
