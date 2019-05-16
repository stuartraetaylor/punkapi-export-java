package com.github.stuartraetaylor.punkapiexport.reference;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.github.stuartraetaylor.punkapiexport.*;

public class HopDBReader implements Reader<Hop> {

    private static final File baseDir = Paths.get("").toAbsolutePath().toFile();

    @Override
    public List<Hop> readAll() throws PunkException {
        Reader<Hop> baseDb = new JsonDBReader<Hop>(baseDir, "submodules/hops-json/hops-min.json", Hop.class) { };
        Reader<Hop> extrasDb = new JsonDBReader<Hop>(baseDir, "data/hops-extra.json", Hop.class) { };

        return union(
            baseDb.readAll(),
            extrasDb.readAll()
        );
    }

    private static <T> List<T> union(List<T> l1, List<T> l2) {
        List<T> union = new ArrayList<>(l1.size() + l2.size());
        union.addAll(l1);
        union.addAll(l2);
        return union;
    }

}
