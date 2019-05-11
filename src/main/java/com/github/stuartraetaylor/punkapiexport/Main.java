package com.github.stuartraetaylor.punkapiexport;

import java.io.IOException;
import java.util.*;

import com.github.stuartraetaylor.punkapiexport.beerxml.*;
import com.github.stuartraetaylor.punkapiexport.punkapi.*;
import com.github.stuartraetaylor.punkapiexport.yeasts.YeastsDBReader;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.*;

public class Main {

    private static Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        Options options = new Options()
                .addOption(new Option("a", "all", false, "Export all recipes"))
                .addOption(new Option("b", "beer", true, "Export a single recipe"));
                //.addOption(new Option("y", "list-yeasts", true, "Lists all yeasts"))
                //.addOption(new Option("g", "list-grains", true, "List all grains"))
                //.addOption(new Option("h", "list-hops", true, "List all hops"));

        HelpFormatter formatter = new HelpFormatter();

        try {
            Properties versionInfo = loadVersionInfo();
            log.info("PunkAPI Export v{}", versionInfo.getProperty("version"));

            CommandLine cmd = new DefaultParser().parse(options, args);
            PunkReader reader = new PunkDBReader();
            //PunkReader reader = new PunkAPIReader();

            if (cmd.hasOption("all")) {
                exportAll(reader);
            } else if (cmd.hasOption("beer")) {
                exportSingle(cmd.getOptionValue("beer"), reader);
            } else if (cmd.hasOption("list-yeasts")) {
                //listYeasts(reader);
            } else if (cmd.hasOption("list-grains")) {
                //listGrains(reader);
            } else if (cmd.hasOption("list-hops")) {
                //listHops(reader);
            } else {
                exportAll(reader);
            }
        } catch (PunkDBFileException e) {
            log.error(e.getMessage());
            System.exit(1);
        } catch (PunkException e) {
            log.error("Export failed", e);
            System.exit(1);
        } catch (ParseException e) {
            formatter.printHelp("java -jar punkapiexport.jar", options);
            System.exit(2);
        } catch (Exception e) {
            log.error("Unknown error: {}", e.getMessage(), e);
            System.exit(3);
		}
    }

    static void exportSingle(String beerName, PunkReader reader) throws PunkException {
        PunkDocument document = reader.read(beerName);
        log.info("Read beer: {}", document.getName());

        PunkWriter writer = new BeerXMLWriter(new YeastsDBReader());
        writer.write(document);
	}

    static void exportAll(PunkReader reader) throws PunkException {
        List<PunkDocument> documents = reader.readAll();
        log.info("Read: {} docs", documents.size());

        PunkWriter writer = new BeerXMLWriter(new YeastsDBReader());
        writer.write(documents);
    }

    static Properties loadVersionInfo() throws IOException {
        Properties versionInfo = new Properties();
        versionInfo.load(
            Main.class.getResourceAsStream("/version-info.properties")
        );
        return versionInfo;
    }

}
