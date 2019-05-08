package com.github.stuartraetaylor.punkapiexport;

import java.util.List;

import com.github.stuartraetaylor.punkapiexport.beerxml.*;
import com.github.stuartraetaylor.punkapiexport.punkapi.*;
import com.github.stuartraetaylor.punkapiexport.yeasts.YeastsDBReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    private static Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            log.info("PunkAPI Export");

            //exportOne("punk10");
            exportAll();

        } catch (PunkException e) {
            log.error("Fail", e);
        }
    }

    static void exportOne(String beerName) throws PunkException {
        //PunkReader reader = new PunkAPIReader();
        PunkReader reader = new PunkDBReader();
        PunkDocument document = reader.read(beerName);

        PunkWriter writer = new BeerXMLWriter(new YeastsDBReader());
        writer.write(document);
	}

    static void exportAll() throws PunkException {
        PunkReader reader = new PunkDBReader();
        List<PunkDocument> documents = reader.readAll();

        log.info("Read: {} docs", documents.size());

        PunkWriter writer = new BeerXMLWriter(new YeastsDBReader());
        writer.write(documents);
	}

}
