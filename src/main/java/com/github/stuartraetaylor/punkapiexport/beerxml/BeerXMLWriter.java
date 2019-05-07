package com.github.stuartraetaylor.punkapiexport.beerxml;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.github.stuartraetaylor.punkapiexport.PunkDocument;
import com.github.stuartraetaylor.punkapiexport.PunkException;
import com.github.stuartraetaylor.punkapiexport.PunkWriter;
import com.github.stuartraetaylor.punkapiexport.beerxml.model.RECIPES;
import com.github.stuartraetaylor.punkapiexport.beerxml.model.RECIPES.RECIPE;
import com.github.stuartraetaylor.punkapiexport.beerxml.model.RECIPES.RECIPE.FERMENTABLES;
import com.github.stuartraetaylor.punkapiexport.beerxml.model.RECIPES.RECIPE.HOPS;
import com.github.stuartraetaylor.punkapiexport.beerxml.model.RECIPES.RECIPE.MASH;
import com.github.stuartraetaylor.punkapiexport.beerxml.model.RECIPES.RECIPE.YEASTS;
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

	@Override
	public void write(Collection<PunkDocument> documents) throws PunkException {
        for (PunkDocument d : documents)
            write(d);
	}

	@Override
    public void write(PunkDocument document) throws PunkException {
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

    private RECIPES createRecipes(PunkSchema document) {
        RECIPES recipes = new RECIPES();
        RECIPE recipe = createRecipe(document);
        recipes.getRECIPE().add(recipe);
        return recipes;
	}

	private RECIPE createRecipe(PunkSchema document) {
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

        createNotes(recipe, document);
        createBatchSize(recipe, document.getVolume());
        createBoilBoilSize(recipe, document.getBoilVolume());
        createPrimaryTemp(recipe, document.getMethod().getFermentation());
        createFermentables(recipe.getFERMENTABLES(), document.getIngredients().getMalt());
        createHops(recipe.getHOPS(), document.getIngredients().getHops());
        createYeasts(recipe.getYEASTS(), document.getIngredients().getYeast());
        createMash(recipe.getMASH(), document.getMethod());

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

	private void createBatchSize(RECIPE recipe, PunkVolume volume) {
        if (!volume.getUnit().equals("liters"))
            throw new IllegalArgumentException("Unsupported volume unit: " + volume.getUnit());

        recipe.setBATCHSIZE(volume.getValue());
        recipe.setDISPLAYBATCHSIZE(volume.getValue() + " L");
    }

    private void createBoilBoilSize(RECIPE recipe, PunkBoilVolume volume) {
        if (!volume.getUnit().equals("liters"))
            throw new IllegalArgumentException("Unsupported volume unit: " + volume.getUnit());

        recipe.setBOILSIZE(volume.getValue());
        recipe.setDISPLAYBOILSIZE(volume.getValue() + " L");
    }

    private void createPrimaryTemp(RECIPE recipe, PunkFermentation fermentation) {
        if (fermentation.getTemp().getValue() == null) {
            log.warn("No fermentation temperature found: {}", recipe.getNAME());
            return;
        }

        int temp = fermentation.getTemp().getValue().intValue();
        String unit = fermentation.getTemp().getUnit();

        if (!unit.equals("celsius"))
            throw new IllegalArgumentException("Unsupported fermentation temp unit: " + unit);

        recipe.setPRIMARYTEMP(temp);
        recipe.setDISPLAYPRIMARYTEMP(temp + " C");
    }

    private void createFermentables(FERMENTABLES fermentables, List<PunkMalt> punkMalts) {
        for (PunkMalt malt : punkMalts) {
            FERMENTABLE fermentable = new FERMENTABLE();
            fermentables.getFERMENTABLE().add(fermentable);

            fermentable.setNAME(malt.getName());
            fermentable.setTYPE("Grain");
            createFermentableAmount(fermentable, malt.getAmount());
        }
	}

    private void createFermentableAmount(FERMENTABLE fermentable, PunkAmount amount) {
        switch (amount.getUnit()) {
            case "kilograms":
                fermentable.setAMOUNT(amount.getValue().doubleValue());
                break;
            case "grams":
                fermentable.setAMOUNT(gramsToKG(amount.getValue()).doubleValue());
                break;
            default:
                throw new IllegalArgumentException("Malt amount unit not supported: " + amount.getUnit());
        }
	}

    private void createHops(HOPS hops, List<PunkHop> punkHops) {
        for (PunkHop punkHop : punkHops) {
            HOP hop = new HOP();
            hops.getHOP().add(hop);

            hop.setNAME(punkHop.getName());
            hop.setFORM("pellet");

            createHopAddition(hop, punkHop.getAdd());
            createAmount(hop, punkHop.getAmount());
        }
	}

    private void createHopAddition(HOP hop, String punkAddition) {
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

    private HopUse translateHopAdd(String hopAdd) {
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

                throw new IllegalArgumentException("Unsupported hop addition: " + hopAdd);
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
        if (!amount.getUnit().equals("grams")) {
            log.warn("Skipping; unsuported hop unit: " + amount.getUnit());
            return;
        }

        BigDecimal kgAmount = gramsToKG(amount.getValue());
        hop.setAMOUNT(kgAmount.doubleValue());
	}

	private void createYeasts(YEASTS yeasts, String punkYeast) {
        YEAST yeast = new YEAST();
        yeast.setNAME(punkYeast);
        yeasts.setYEAST(yeast);
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

	private final Logger log = LogManager.getLogger(this.getClass());

}
