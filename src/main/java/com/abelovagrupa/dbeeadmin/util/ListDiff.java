package com.abelovagrupa.dbeeadmin.util;

import com.abelovagrupa.dbeeadmin.model.column.Column;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

public class ListDiff {
    public static <T> DiffResult<T> compareLists(List<T> oldList, List<T> newList, BiFunction<T, T, HashMap<String, Object[]>> attributeComparator, Class<T> compareClass){
        DiffResult<T> result = new DiffResult<>();
//        Set<T> oldSet = new HashSet<>(oldList);
//        Set<T> newSet = new HashSet<>(newList);

        if(compareClass == Column.class){
            List<Column> newColumnList = (List<Column>) newList;
            List<Column> oldColumnList = (List<Column>) oldList;

            for(T oldItem : oldList) {
                if(!Column.containsByAttributes(newColumnList, (Column) oldItem)){
                    result.removed.add(oldItem);
                }
            }

            for(T newItem : newList) {
                if(!Column.containsByAttributes(oldColumnList, (Column) newItem)){
                    result.added.add(newItem);
                }
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

    public static <T> boolean noDiff(DiffResult<T> listDifference){
        return listDifference.changedAttributes.isEmpty() && listDifference.added.isEmpty() && listDifference.removed.isEmpty();
    }
}
