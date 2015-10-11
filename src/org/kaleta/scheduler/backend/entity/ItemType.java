package org.kaleta.scheduler.backend.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stanislav Kaleta on 10.10.2015.
 */
public class ItemType {
    private String name;
    private List<String> preparedDescriptions;
    private Boolean income;

    public ItemType(){
        name = null;
        preparedDescriptions = new ArrayList<String>();
        income = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPreparedDescriptions() {
        return preparedDescriptions;
    }

    public Boolean getIncome() {
        return income;
    }

    public void setIncome(Boolean income) {
        this.income = income;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemType type = (ItemType) o;

        if (name != null ? !name.equals(type.name) : type.name != null) return false;
        if (preparedDescriptions != null ? !preparedDescriptions.equals(type.preparedDescriptions) : type.preparedDescriptions != null)
            return false;
        return !(income != null ? !income.equals(type.income) : type.income != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (preparedDescriptions != null ? preparedDescriptions.hashCode() : 0);
        result = 31 * result + (income != null ? income.hashCode() : 0);
        return result;
    }
}
