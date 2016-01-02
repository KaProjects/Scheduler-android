package org.kaleta.scheduler.service;

import org.kaleta.scheduler.backend.entity.Config;
import org.kaleta.scheduler.backend.entity.Item;
import org.kaleta.scheduler.backend.entity.ItemType;
import org.kaleta.scheduler.backend.entity.Month;
import org.kaleta.scheduler.backend.manager.ConfigManager;
import org.kaleta.scheduler.backend.manager.ManagerException;
import org.kaleta.scheduler.backend.manager.MonthManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stanislav Kaleta on 10.10.2015.
 */
public class Service {

    /**
     * Checks if data directory and config file are created and will create them if not.
     */
    public void checkConfigData(){
        ConfigManager manager = new ConfigManager();
        try {
            manager.createConfig();
        } catch (ManagerException e) {
            throw new ServiceFailureException(e);
        }
    }

    /**
     * Creates new month with specified name.
     * @param name - name of new month
     */
    public void createMonth(String name){
        for (String existing : getMonthNames()){
            if (existing.equals(name)){
                throw new IllegalArgumentException("Month already exists!");
            }
        }
        try {
            Month newMonth = new Month();
            newMonth.setName(name);

            ConfigManager configManager = new ConfigManager();
            Config config = configManager.retrieveConfig();
            int highestId = 0;
            for (Integer existingId : config.getMonthIds()){
                if (existingId > highestId){
                    highestId = existingId;
                }
            }
            newMonth.setId(highestId + 1);

            MonthManager monthManager = new MonthManager();
            monthManager.createMonth(newMonth);

            config.getMonthIds().add(newMonth.getId());
            configManager.updateConfig(config);
        } catch (ManagerException e){
            throw new ServiceFailureException(e);
        }
    }

    /**
     * Retrieves names of all months in data source.
     */
    public List<String> getMonthNames(){
        try {
            List<String> monthNames = new ArrayList<String>();
            ConfigManager configManager = new ConfigManager();
            MonthManager monthManager = new MonthManager();
            for (Integer id : configManager.retrieveConfig().getMonthIds()){
                monthNames.add(monthManager.retrieveMonth(id).getName());
            }
            return monthNames;
        } catch (ManagerException e){
            throw new ServiceFailureException(e);
        }
    }

    /**
     * Retrieves all registered item types from data source.
     */
    public List<ItemType> getItemTypes(){
        try {
            ConfigManager configManager = new ConfigManager();
            Config config = configManager.retrieveConfig();
            return config.getTypes();
        } catch (ManagerException e) {
            throw new ServiceFailureException(e);
        }
    }

    /**
     * Saves item types to data source.
     */
    public void setItemTypes(List<ItemType> typeList){
        try {
            ConfigManager configManager = new ConfigManager();
            Config config = configManager.retrieveConfig();
            config.getTypes().clear();
            config.getTypes().addAll(typeList);
            configManager.updateConfig(config);
        } catch (ManagerException e) {
            throw new ServiceFailureException(e);
        }
    }

    /**
     * Adds new item to data source.
     * @param newItem - item which has to be added.
     * @param monthName - name of related month
     */
    public void addItem(Item newItem, String monthName){
        try {
            ConfigManager configManager = new ConfigManager();
            MonthManager monthManager = new MonthManager();

            for (Integer id : configManager.retrieveConfig().getMonthIds()){
                Month month = monthManager.retrieveMonth(id);
                if (monthName.equals(month.getName())){
                    Integer newId = 0;
                    for (Item i : month.getItemList()){
                        if (i.getId() > newId){
                            newId = i.getId();
                        }
                    }
                    newId++;
                    newItem.setId(newId);
                    newItem.setExported(false);
                    month.getItemList().add(newItem);
                    monthManager.updateMonth(month);
                }
            }
        } catch (ManagerException e) {
            throw new ServiceFailureException(e);
        }
    }

    /**
     * Marks month as exported in data source.
     */
    public void markMonthAsExported(String monthName){
        try {
            ConfigManager configManager = new ConfigManager();
            MonthManager monthManager = new MonthManager();

            for (Integer id : configManager.retrieveConfig().getMonthIds()){
                Month month = monthManager.retrieveMonth(id);
                if (monthName.equals(month.getName())){
                    for (Item item : month.getItemList()){
                        item.setExported(true);
                    }
                    monthManager.updateMonth(month);
                }
            }
        } catch (ManagerException e) {
            throw new ServiceFailureException(e);
        }
    }
}
