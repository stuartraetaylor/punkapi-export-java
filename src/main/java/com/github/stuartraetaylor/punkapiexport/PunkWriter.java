package com.github.stuartraetaylor.punkapiexport;

import java.util.Collection;

public interface PunkWriter {

    void write(PunkDocument document) throws PunkException;
    void write(Collection<PunkDocument> documents) throws PunkException;

}
