package com.github.stuartraetaylor.punkapiexport.punkapi;

import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;

import java.io.PrintStream;
import java.util.*;

import com.github.stuartraetaylor.punkapiexport.*;
import com.github.stuartraetaylor.punkapiexport.punkapi.model.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PunkDBStats implements PunkStats {

    private final PrintStream out;

    public PunkDBStats(PrintStream out) {
        this.out = out;
    }

	@Override
    public void yeasts(Collection<PunkDocument> documents) {
        Map<String, ItemCount> yeastCounts = new HashMap<>();

        for (PunkDocument d : documents) {
            PunkSchema schema = d.getDocument();
            String yeastName = schema.getIngredients().getYeast();
            count(yeastName, yeastCounts);
        }

        log.info("Printing yeast counts:");
        print(yeastCounts);
	}

	@Override
	public void malts(Collection<PunkDocument> documents) {
        Map<String, ItemCount> maltCounts = new HashMap<>();

        for (PunkDocument d : documents) {
            PunkSchema schema = d.getDocument();
            List<PunkMalt> malts = schema.getIngredients().getMalt();
            for (PunkMalt malt : malts) {
                count(malt.getName(), maltCounts);
            }
        }

        log.info("Printing malt counts:");
        print(maltCounts);
	}

	@Override
    public void hops(Collection<PunkDocument> documents) {
        Map<String, ItemCount> hopCounts = new HashMap<>();

        for (PunkDocument d : documents) {
            PunkSchema schema = d.getDocument();
            List<PunkHop> hops = schema.getIngredients().getHops();
            for (PunkHop hop : hops) {
                count(hop.getName(), hopCounts);
            }
        }

        log.info("Printing hop counts:");
        print(hopCounts);
    }

    private void count(String item, Map<String, ItemCount> counts) {
        if (item == null) {
            log.debug("Skipping null item");
            return;
        }

        ItemCount count;
        if ((count = counts.get(item)) == null)
            counts.put(item, count=new ItemCount(item));

        count.increment();
	}

    private void print(Map<String, ItemCount> counts) {
        List<ItemCount> sorted = counts.values().stream()
            .sorted(
                comparing(ItemCount::getCount)
                    .reversed()
                    .thenComparing(ItemCount::getName)
            ).collect(toList());

        for (ItemCount item : sorted) {
            out.printf("%3d   %s%n", item.getCount(), item.getName());
        }

        out.printf("%nUnique items: %s%n", sorted.size());
    }

    static class ItemCount {
        private final String name;
        private int count = 0;

        public ItemCount(String name) {
            this.name = name;
        }

        public int increment() {
            return ++count;
        }

        public String getName() { return name; }
        public int getCount() { return count; }
    }

    private final Logger log = LogManager.getLogger(this.getClass());

}
