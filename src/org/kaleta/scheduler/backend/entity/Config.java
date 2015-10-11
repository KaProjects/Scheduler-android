package org.kaleta.scheduler.backend.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stanislav Kaleta on 10.10.2015.
 */
public class Config {
    private List<Integer> monthIds;
    private List<UserType> types;

    public Config(){
        monthIds = new ArrayList<Integer>();
        types = new ArrayList<UserType>();
    }

    public List<Integer> getMonthIds() {
        return monthIds;
    }

    public List<UserType> getTypes() {
        return types;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Config config = (Config) o;

        if (monthIds != null ? !monthIds.equals(config.monthIds) : config.monthIds != null) return false;
        return !(types != null ? !types.equals(config.types) : config.types != null);

    }

    @Override
    public int hashCode() {
        int result = monthIds != null ? monthIds.hashCode() : 0;
        result = 31 * result + (types != null ? types.hashCode() : 0);
        return result;
    }
}
