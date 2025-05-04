package com.abelovagrupa.dbeeadmin.util;

import com.abelovagrupa.dbeeadmin.model.column.Column;
import com.abelovagrupa.dbeeadmin.model.foreignkey.ForeignKey;
import com.abelovagrupa.dbeeadmin.model.index.Index;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

public class ListDiff {
    public static <T> DiffResult<T> compareLists(List<T> oldList, List<T> newList, BiFunction<T, T, HashMap<String, Object[]>> attributeComparator, Class<T> compareClass){
        DiffResult<T> result = new DiffResult<>();
//        Set<T> oldSet = new HashSet<>(oldList);
//        Set<T> newSet = new HashSet<>(newList);

        for(int i = 0; i < Math.min(oldList.size(),newList.size()); i++){
            T oldItem = oldList.get(i);
            T newItem = newList.get(i);

            HashMap<String,Object[]> diffs = attributeComparator.apply(oldItem,newItem);
            if(!diffs.isEmpty()){
                result.changedAttributes.put(newItem,diffs);
            }

        }

        if(compareClass == Column.class){
            List<Column> newColumnList = (List<Column>) newList;
            List<Column> oldColumnList = (List<Column>) oldList;

            for(T oldItem : oldList) {
                if(!Column.containsByAttributes(newColumnList, (Column) oldItem) && !ListDiff.hasChangedAttributes(oldItem,result)){
                    result.removed.add(oldItem);
                }
            }

            for(T newItem : newList) {
                if(!Column.containsByAttributes(oldColumnList, (Column) newItem) && !ListDiff.hasChangedAttributes(newItem,result)){
                    result.added.add(newItem);
                }
            }
        }

        if(compareClass == Index.class){
            List<Index> newIndexList = (List<Index>) newList;
            List<Index> oldIndexList = (List<Index>) oldList;

            for(T oldItem : oldList) {
                if(!Index.containsByAttributes(newIndexList, (Index) oldItem)  && !hasChangedAttributes(oldItem,result)){
                    result.removed.add(oldItem);
                }
            }

            for(T newItem : newList) {
                if(!Index.containsByAttributes(oldIndexList, (Index) newItem) && !hasChangedAttributes(newItem,result)){
                    result.added.add(newItem);
                }
            }

        }

        if(compareClass == ForeignKey.class){
            List<ForeignKey> newFKList = (List<ForeignKey>) newList;
            List<ForeignKey> oldFkList = (List<ForeignKey>) oldList;

            for(T oldItem : oldList) {
                if(!ForeignKey.containsByAttributes(newFKList, (ForeignKey) oldItem) && !hasChangedAttributes(oldItem,result)){
                    result.removed.add(oldItem);
                }
            }

            for(T newItem : newList) {
                if(!ForeignKey.containsByAttributes(oldFkList, (ForeignKey) newItem) && !hasChangedAttributes(newItem,result)){
                    result.added.add(newItem);
                }
            }

        }


        return result;
    }

    private static <T> boolean hasChangedAttributes(T oldItem, DiffResult<T> result) {
        if(oldItem instanceof Column){
            Column column = (Column) oldItem;
            DiffResult<Column> columnResult = (DiffResult<Column>) result;
            for(Column col : columnResult.changedAttributes.keySet()){
                if(col.getName().equals(column.getName()) ||
                columnResult.changedAttributes.get(col).get("name")[0].equals(column.getName())) return true;
            }
            return false;
        }
        if(oldItem instanceof Index){
            Index index = (Index) oldItem;
            DiffResult<Index> columnResult = (DiffResult<Index>) result;
            for(Index ix : columnResult.changedAttributes.keySet()){
                if(ix.getName().equals(index.getName()) ||
                        columnResult.changedAttributes.get(ix).get("name")[0].equals(index.getName())) return true;
            }
            return false;
        }
        if(oldItem instanceof ForeignKey){
            ForeignKey foreignKey = (ForeignKey) oldItem;
            DiffResult<ForeignKey> columnResult = (DiffResult<ForeignKey>) result;
            for(ForeignKey fk : columnResult.changedAttributes.keySet()){
                if(fk.getName().equals(foreignKey.getName()) ||
                        columnResult.changedAttributes.get(fk).get("name")[0].equals(foreignKey.getName())) return true;
            }
            return false;
        }

        return false;
    }

    public static <T> boolean noDiff(DiffResult<T> listDifference){
        return listDifference.changedAttributes.isEmpty() && listDifference.added.isEmpty() && listDifference.removed.isEmpty();
    }
}
