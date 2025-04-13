package com.abelovagrupa.dbeeadmin.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class DiffResult<T> {
    public List<T> added = new LinkedList<>();
    public List<T> removed = new LinkedList<>();
    public HashMap<T,HashMap<String,Object[]>> changedAttributes = new HashMap<>();
}
