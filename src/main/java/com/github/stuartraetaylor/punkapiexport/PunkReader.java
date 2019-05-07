package com.github.stuartraetaylor.punkapiexport;

import java.util.List;

public interface PunkReader {

    List<PunkDocument> readAll() throws PunkException;
    PunkDocument read(String beerName) throws PunkException;

}
