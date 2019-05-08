package com.github.stuartraetaylor.punkapiexport;

import java.util.List;

public interface YeastReader {

    List<Yeast> readAll() throws PunkException;

}
