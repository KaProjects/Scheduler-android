package org.kaleta.scheduler.backend.service;

import android.content.Context;
import android.util.Log;
import org.kaleta.scheduler.MyActivity;
import org.kaleta.scheduler.backend.entity.Item;
import org.kaleta.scheduler.backend.entity.ItemType;
import org.kaleta.scheduler.backend.entity.Month;
import org.kaleta.scheduler.backend.manager.ConfigManager;
import org.kaleta.scheduler.backend.manager.ManagerException;
import org.kaleta.scheduler.backend.manager.MonthManager;
import org.kaleta.scheduler.frontend.MessageDialog;

import java.io.*;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stanislav Kaleta on 28.12.2015.
 */
public class SocketServerService extends Thread {
    private Context context;
    private BufferedReader input;
    private PrintWriter output;

    public SocketServerService(Context context){
        super("socket-server-thread");
        this.context = context;
    }

    @Override
    public void run() {
        ServerSocket server;
        try{
            server = new ServerSocket(7777);
        } catch  (IOException e) {
            Log.e("IOException", e.getMessage());
            new MessageDialog(context, e.getMessage());
            return;
        }

        while (true){
            try{
                Socket client = server.accept();
                input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                output = new PrintWriter(client.getOutputStream(),true);
                List<ItemType> importedTypes = new ArrayList<ItemType>();
                boolean working = true;
                while (working){
                    String demand = input.readLine();
                    if (demand == null){
                        working = false;
                        continue;
                    }
                    if (demand.equals("sendData")){
                        sendData();
                    }
                    if (demand.startsWith("importedMonth")){
                        String monthName = demand.split("\\$")[1];
                        new Service().markMonthAsExported(monthName);
                    }
                    if (demand.equals("exportingTypesStarted")){
                        importedTypes.clear();
                    }
                    if (demand.startsWith("item")){
                        ItemType type = new ItemType();
                        type.setName(demand.split("\\$")[1]);
                        type.setIncome(Boolean.valueOf(demand.split("\\$")[2]));
                        if (demand.split("\\$").length == 4){
                            for (String description : demand.split("\\$")[3].split("\\&")){
                                type.getPreparedDescriptions().add(description);
                            }
                        }
                        importedTypes.add(type);
                    }
                    if (demand.equals("exportingTypesFinished")){
                        new Service().setItemTypes(importedTypes);
                    }
                    if (demand.equals("thanks")){
                        working = false;
                    }
                }
                client.close();
            } catch (IOException e) {
                Log.e("IOException",e.getMessage());
                new MessageDialog(context, e.getMessage());
            }
        }
    }

    private void sendData(){
        try {
            ConfigManager configManager = new ConfigManager();
            List<Integer> ids = configManager.retrieveConfig().getMonthIds();
            for(Integer id : ids){
                MonthManager monthManager = new MonthManager();
                Month month = monthManager.retrieveMonth(id);
                output.println("month$" + month.getName() +"$"+ month.getItemList().size());
                for(Item item : month.getItemList()){
                    if (!item.getExported()){
                        String sign = (item.getIncome()) ? "+" : "-";
                        String amount = String.valueOf(item.getAmount());
                        Integer day = item.getDay();
                        output.println("item$" + sign +"$"+ amount +"$"+ day +"$"+ item.getType() +"$"+ item.getDescription());
                    }
                }
            }
            output.println("done");
        } catch (ManagerException e) {
            e.printStackTrace();
        }
    }
}
