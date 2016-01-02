package org.kaleta.scheduler.backend.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stanislav Kaleta on 10.10.2015.
 */
public class Month {
    private Integer id;
    private String name;
    private List<Item> itemList;

    public Month(){
        name = null;
        itemList = new ArrayList<Item>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Month month = (Month) o;

        if (id != null ? !id.equals(month.id) : month.id != null) return false;
        if (name != null ? !name.equals(month.name) : month.name != null) return false;
        return !(itemList != null ? !itemList.equals(month.itemList) : month.itemList != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (itemList != null ? itemList.hashCode() : 0);
        return result;
    }
}
