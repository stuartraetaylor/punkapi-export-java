package com.github.stuartraetaylor.punkapiexport;

import com.github.stuartraetaylor.punkapiexport.punkapi.model.PunkSchema;

public class PunkDocument {

    private final String name;
    private final PunkSchema document;

    public PunkDocument(String name, PunkSchema document) {
        this.name = name;
        this.document = document;
    }

    public String getName() {
        return name;
    }

    public PunkSchema getDocument() {
        return document;
    }

    @Override
    public String toString() {
        return name;
    }

}
