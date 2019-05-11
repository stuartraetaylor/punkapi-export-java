package com.github.stuartraetaylor.punkapiexport;

import java.util.Collection;

public interface PunkStats {

    void yeasts(Collection<PunkDocument> documents);
    void malts(Collection<PunkDocument> documents);
    void hops(Collection<PunkDocument> documents);

}
