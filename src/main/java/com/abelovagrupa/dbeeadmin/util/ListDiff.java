package com.abelovagrupa.dbeeadmin.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

public class ListDiff {
    public static <T> DiffResult<T> compareLists(List<T> oldList, List<T> newList, BiFunction<T, T, HashMap<String, Object[]>> attributeComparator){
        DiffResult<T> result = new DiffResult<>();
        Set<T> oldSet = new HashSet<>(oldList);
        Set<T> newSet = new HashSet<>(newList);

        for(T oldItem : oldList) {
            if(!newSet.contains(oldItem)){
                result.removed.add(oldItem);
            }
        }

        for(T newItem : newList) {
            if(!oldSet.contains(newItem)){
                result.added.add(newItem);
            }
        }

        for(int i = 0; i < Math.min(oldList.size(),newList.size()); i++){
            T oldItem = oldList.get(i);
            T newItem = newList.get(i);

            HashMap<String,Object[]> diffs = attributeComparator.apply(oldItem,newItem);
            if(!diffs.isEmpty()){
                result.changedAttributes.put(newItem,diffs);
            }

        }
        return result;
    }
}
