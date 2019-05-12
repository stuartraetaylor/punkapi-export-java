package com.github.stuartraetaylor.punkapiexport.beerxml;

import static java.util.stream.Collectors.*;

import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.github.stuartraetaylor.punkapiexport.*;
import com.github.stuartraetaylor.punkapiexport.reference.*;
import com.github.stuartraetaylor.punkapiexport.beerxml.model.RECIPES;
import com.github.stuartraetaylor.punkapiexport.beerxml.model.RECIPES.RECIPE;
import com.github.stuartraetaylor.punkapiexport.beerxml.model.RECIPES.RECIPE.*;
import com.github.stuartraetaylor.punkapiexport.beerxml.model.RECIPES.RECIPE.FERMENTABLES.FERMENTABLE;
import com.github.stuartraetaylor.punkapiexport.beerxml.model.RECIPES.RECIPE.HOPS.HOP;
import com.github.stuartraetaylor.punkapiexport.beerxml.model.RECIPES.RECIPE.MASH.MASHSTEPS;
import com.github.stuartraetaylor.punkapiexport.beerxml.model.RECIPES.RECIPE.MASH.MASHSTEPS.MASHSTEP;
import com.github.stuartraetaylor.punkapiexport.beerxml.model.RECIPES.RECIPE.YEASTS.YEAST;
import com.github.stuartraetaylor.punkapiexport.punkapi.model.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BeerXMLWriter implements PunkWriter {

    private static final File baseDir = Paths.get("beerxml").toAbsolutePath().toFile();

    // FIXME check these values.
    static final int defaultBoilTime = 60;
    static final int defaultEfficiency = 70;
    static final int defaultMashTime = 60;
    static final int defaultMashTemp = 65;

    private final Map<String, Yeast> yeastLookup;
    private final Map<String, Hop> hopLookup;

    private final Set<String> warnedRecipeItems = new HashSet<>();

    public BeerXMLWriter(Reader<Yeast> yeastReader, Reader<Hop> hopReader) {
        this.yeastLookup = createLookup(yeastReader);
        this.hopLookup = createLookup(hopReader);

        if (!baseDir.exists())
            baseDir.mkdir();
    }

    @Override
    public void write(Collection<PunkDocument> documents) throws PunkException {
        init();
        for (PunkDocument d : documents) {
            try {
                writeDocument(d);
            } catch (BeerXMLExportException e) {
                log.error("Failed to export recipe: {}", d.getName(), e);
            }
        }

        if (!warnedRecipeItems.isEmpty())
            log.warn("Warned recipe items: {}", warnedRecipeItems.size());
    }

    @Override
    public void write(PunkDocument document) throws PunkException {
        init();
        writeDocument(document);
    }

	public void writeDocument(PunkDocument document) throws PunkException {
        try {
            log.debug("Exporting recipe: {}", document.getName());
            RECIPES beerxml = createRecipes(document.getDocument());

            File beerFile = new File(baseDir, document.getName() + "_beerxml.xml");
            log.debug("Writing BeerXML recipe: {}", beerFile.getAbsolutePath());

            JAXBContext jaxbContext = JAXBContext.newInstance(RECIPES.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(beerxml, beerFile);
        } catch (JAXBException e) {
            throw new PunkException("Failed to export recipe: " + document.getName(), e);
        }
    }

    private RECIPES createRecipes(PunkSchema document) throws BeerXMLExportException {
        RECIPES recipes = new RECIPES();
        RECIPE recipe = createRecipe(document);
        recipes.getRECIPE().add(recipe);
        return recipes;
    }

    private RECIPE createRecipe(PunkSchema document) throws BeerXMLExportException {
        RECIPE recipe = new RECIPE();
        recipe.setNAME(document.getName());
        recipe.setVERSION(1);
        recipe.setTYPE("All Grain");
        recipe.setBOILTIME(defaultBoilTime);
        recipe.setEFFICIENCY(defaultEfficiency);
        recipe.setESTCOLOR(String.valueOf(document.getSrm()));
        recipe.setIBU(String.valueOf(document.getIbu()));
        recipe.setESTOG(String.valueOf(document.getTargetOg()));
        recipe.setESTFG(String.valueOf(document.getTargetFg()));

        recipe.setFERMENTABLES(new FERMENTABLES());
        recipe.setHOPS(new HOPS());
        recipe.setYEASTS(new YEASTS());
        recipe.setMASH(new MASH());
        recipe.setSTYLE(new STYLE());

        createNotes(recipe, document);
        createBatchSize(recipe, document.getVolume());
        createBoilBoilSize(recipe, document.getBoilVolume());
        createPrimaryTemp(recipe, document.getMethod().getFermentation());
        createFermentables(recipe.getFERMENTABLES(), document.getIngredients().getMalt());
        createHops(recipe.getHOPS(), document.getIngredients().getHops());
        createYeasts(recipe.getYEASTS(), document.getIngredients().getYeast());
        createMash(recipe.getMASH(), document.getMethod());
        //createStyle(recipe.getSTYLE(), document);

        return recipe;
    }

    private void createNotes(RECIPE recipe, PunkSchema document) {
        String notes = ""; // FIXME CDATA.
        notes += "Brewdog " + document.getName() + "\n\n";
        notes += "Tagline: " + document.getTagline() + "\n";
        notes += "First brewed: " + document.getFirstBrewed() + "\n";
        notes += "Description:\n" + document.getDescription() + "\n\n";
        notes += "Food pairing:\n" + document.getFoodPairing() + "\n\n";
        notes += "Brewers tips:\n" + document.getBrewersTips() + "\n\n";
        notes += "Contributed by: " + document.getContributedBy() + "\n";
        notes += "PunkAPI id: " + document.getId() + "\n";

        recipe.setNOTES(notes);
    }

    private void createBatchSize(RECIPE recipe, PunkVolume volume) throws BeerXMLExportException {
        if (!volume.getUnit().equals("liters"))
            throw new BeerXMLExportException("Unsupported volume unit: " + volume.getUnit());

        recipe.setBATCHSIZE(volume.getValue());
        recipe.setDISPLAYBATCHSIZE(volume.getValue() + " L");
    }

    private void createBoilBoilSize(RECIPE recipe, PunkBoilVolume volume) throws BeerXMLExportException {
        if (!volume.getUnit().equals("liters"))
            throw new BeerXMLExportException("Unsupported volume unit: " + volume.getUnit());

        recipe.setBOILSIZE(volume.getValue());
        recipe.setDISPLAYBOILSIZE(volume.getValue() + " L");
    }

    private void createPrimaryTemp(RECIPE recipe, PunkFermentation fermentation) throws BeerXMLExportException {
        if (fermentation.getTemp().getValue() == null) {
            log.warn("No fermentation temperature found: {}", recipe.getNAME());
            return;
        }

        int temp = fermentation.getTemp().getValue().intValue();
        String unit = fermentation.getTemp().getUnit();

        if (!unit.equals("celsius"))
            throw new BeerXMLExportException("Unsupported fermentation temp unit: " + unit);

        recipe.setPRIMARYTEMP(temp);
        recipe.setDISPLAYPRIMARYTEMP(temp + " C");
    }

    private void createFermentables(FERMENTABLES fermentables, List<PunkMalt> punkMalts) throws BeerXMLExportException {
        for (PunkMalt malt : punkMalts) {
            FERMENTABLE fermentable = new FERMENTABLE();
            fermentables.getFERMENTABLE().add(fermentable);

            fermentable.setNAME(malt.getName());
            fermentable.setTYPE("Grain");
            createFermentableAmount(fermentable, malt.getAmount());
        }
    }

    private void createFermentableAmount(FERMENTABLE fermentable, PunkAmount amount) throws BeerXMLExportException {
        switch (amount.getUnit()) {
        case "kilograms":
            fermentable.setAMOUNT(amount.getValue().doubleValue());
            break;
        case "grams":
            fermentable.setAMOUNT(gramsToKG(amount.getValue()).doubleValue());
            break;
        default:
            throw new BeerXMLExportException("Malt amount unit not supported: " + amount.getUnit());
        }
    }

    private void createHops(HOPS hops, List<PunkHop> punkHops) throws BeerXMLExportException {
        for (PunkHop punkHop : punkHops) {
            HOP hop = new HOP();
            hops.getHOP().add(hop);
            hop.setFORM("pellet");

            String punkHopName = punkHop.getName();
            Hop hopDesc = lookupHop(punkHopName);
            if (hopDesc != null) {
                hop.setNAME(hopDesc.getName());
                hop.setALPHA(alpha(hopDesc));
            } else {
                hop.setNAME(punkHopName);
            }

            createHopAddition(hop, punkHop.getAdd());
            createAmount(hop, punkHop.getAmount());
        }
    }

    private void createHopAddition(HOP hop, String punkAddition) throws BeerXMLExportException {
        try {
            // Absolute time.
            int time = Integer.parseInt(punkAddition);
            hop.setTIME(time);
            hop.setUSE("Boil");
        } catch (NumberFormatException e) {
            // Description.
            HopUse hopUse = translateHopAdd(punkAddition);
            if (hopUse != null) {
                hop.setUSE(hopUse.getUse());

                if (hopUse.getTime() != null)
                    hop.setTIME(hopUse.getTime());
            }
        }
    }

    private HopUse translateHopAdd(String hopAdd) throws BeerXMLExportException {
        switch (hopAdd.toLowerCase()) {
            case "first wort":
            case "first wort hops":
                return HopUse.FIRST_WORT;
            case "start":
                return HopUse.START;
            case "middle":
            case "kettle":
                return HopUse.MIDDLE;
            case "end":
            case "flame out":
            case "additions":
                return HopUse.END;
            case "whirlpool":
                return HopUse.WHIRLPOOL;
            case "dry hop":
            case "fv":
            case "fv addition":
                return HopUse.DRY_HOP;
            default:
                if (weirdHopException(hopAdd))
                    return null;

                throw new BeerXMLExportException("Unsupported hop addition: " + hopAdd);
        }
    }

    private boolean weirdHopException(String hopAdd) {
        // FIXME these aren't really hop additions.
        switch (hopAdd) {
        case "Wood Ageing":
        case "Mash":
        case "secondary":
        case "maturation":
            return true;
        default:
            return false;
        }
    }

    private void createAmount(HOP hop, PunkAmount__1 amount) {
        if (!(amount.getUnit().equals("grams") || amount.getUnit().equals("kilogram"))) {
            log.warn("Skipping; unsuported hop unit: " + amount.getUnit());
            return;
        }

        BigDecimal kgAmount;
        if (amount.getUnit().equals("grams"))
            kgAmount = gramsToKG(amount.getValue());
        else
            kgAmount = amount.getValue();

        hop.setAMOUNT(kgAmount.doubleValue());
    }

    private void createYeasts(YEASTS yeasts, String punkYeast) {
        YEAST yeast = new YEAST();
        yeast.setAMOUNT(0.1);
        yeasts.setYEAST(yeast);

        Yeast yeastDesc = lookupYeast(punkYeast);
        if (yeastDesc != null) {
            yeast.setNAME(yeastDesc.getName());
            yeast.setFORM(String.valueOf(yeastDesc.getForm()));
            yeast.setPRODUCTID(yeastDesc.getStrain());
            yeast.setLABORATORY(yeastDesc.getLaboratory());
            yeast.setATTENUATION(attenuation(yeastDesc)); // FIXME not an int.
            yeast.setFLOCCULATION(String.valueOf(yeastDesc.getFlocculation()));
            yeast.setMINTEMPERATURE((int) yeastDesc.getTemperatureMin()); // FIXME not an int.
            yeast.setMAXTEMPERATURE(yeastDesc.getTemperatureMax());
        } else {
            yeast.setNAME(punkYeast);
        }
    }

    private int attenuation(Yeast yeast) {
        return (int) (yeast.getAttenuationMax() + yeast.getAttenuationMin()) / 2;
    }

    private double alpha(Hop hop) {
        return BigDecimal.valueOf(hop.getAlphaMin() + hop.getAlphaMax())
                .divide(BigDecimal.valueOf(2))
                .round(new MathContext(2))
                .doubleValue();
    }

    private void createMash(MASH mash, PunkMethod method) {
        mash.setNAME("Mash");
        mash.setMASHSTEPS(new MASHSTEPS());

        for (PunkMashTemp punkStep : method.getMashTemp()) {
            MASHSTEP step = new MASHSTEP();
            mash.getMASHSTEPS().getMASHSTEP().add(step);

            if (punkStep.getTemp().getValue() != null)
                step.setSTEPTEMP(punkStep.getTemp().getValue().intValue());
            else
                step.setSTEPTEMP(defaultMashTemp);

            BigDecimal duration = punkStep.getDuration();
            if (duration != null)
                step.setSTEPTIME(duration.intValue());
            else
                step.setSTEPTIME(defaultMashTime);

            step.setTYPE("Infusion");
        }
    }

    private BigDecimal gramsToKG(BigDecimal value) {
        return value.divide(new BigDecimal(1000.0));
    }

    private Yeast lookupYeast(String yeastName) {
        String strain = YeastParser.parse(yeastName);
        if (strain == null) {
            logRecipeWarning("Could not parse yeast: {}", yeastName);
            return null;
        }

        Yeast yeast = yeastLookup.get(strain);
        if (yeast == null) {
            logRecipeWarning("Unrecognised yeast strain: {}", strain);
            return null;
        }

        return yeast;
    }

    private Hop lookupHop(String hopName) {
        String identifier = Hop.normalise(hopName);
        if (identifier == null) {
            log.warn("Null hop name: {}", hopName);
            return null;
        }

        Hop hop = hopLookup.get(identifier);
        if (hop == null) {
            logRecipeWarning("Unrecognised hop: {}", hopName);
            return null;
        }

        return hop;
    }

    private <T extends ReferenceEntity> Map<String, T> createLookup(Reader<T> reader) {
        try {
            List<T> records = reader.readAll();
            Map<String, T> lookup = records.stream()
                    .collect(toMap(
                            ReferenceEntity::identifier,
                            Function.identity(),
                            (k1, k2) -> k1) // ignore duplicate items.
                        );
            log.debug("Loaded JSON DB: {}", lookup.size());
            return lookup;
        } catch (PunkException e) {
            log.error("Failed to load JSON DB", e);
            return Collections.emptyMap();
        }
    }

    private void init() {
        log.info("Exporting recipe(s) to: {}", baseDir.getAbsolutePath());
        warnedRecipeItems.clear();
    }

    private void logRecipeWarning(String message, String item) {
        if (!log.isWarnEnabled())
            return;

        if (!warnedRecipeItems.contains(item)) {
            log.warn(message, item);
            warnedRecipeItems.add(item);
        }
    }

    private final Logger log = LogManager.getLogger(this.getClass());

}
