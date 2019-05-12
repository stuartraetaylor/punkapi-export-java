package com.github.stuartraetaylor.punkapiexport;

public interface PunkReader extends Reader<PunkDocument> {

    PunkDocument read(String beerName) throws PunkException;

}
