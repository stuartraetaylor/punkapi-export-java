package com.github.stuartraetaylor.punkapiexport.beerxml;

import com.github.stuartraetaylor.punkapiexport.PunkException;

public class BeerXMLExportException extends PunkException {

    public BeerXMLExportException(String message) {
        super(message);
    }

    public BeerXMLExportException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeerXMLExportException(Throwable cause) {
        super(cause);
    }

}
