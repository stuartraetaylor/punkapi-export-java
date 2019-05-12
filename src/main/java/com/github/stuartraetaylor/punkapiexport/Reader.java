package com.github.stuartraetaylor.punkapiexport;

import java.util.List;

public interface Reader<T> {

    List<T> readAll() throws PunkException;

}
