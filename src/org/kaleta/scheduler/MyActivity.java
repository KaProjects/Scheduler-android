package org.kaleta.scheduler;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import org.kaleta.scheduler.backend.entity.Config;
import org.kaleta.scheduler.backend.entity.Item;
import org.kaleta.scheduler.backend.entity.ItemType;
import org.kaleta.scheduler.backend.manager.ConfigManager;
import org.kaleta.scheduler.backend.manager.ManagerException;
import org.kaleta.scheduler.backend.service.Service;
import org.kaleta.scheduler.backend.service.SocketServerService;
import org.kaleta.scheduler.frontend.AddItemDialog;
import org.kaleta.scheduler.frontend.InputDialog;
import org.kaleta.scheduler.frontend.MessageDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stanislav Kaleta on 10.10.2015.
 */
public class MyActivity extends Activity {
    public static final String SCHEDULER_DIRECTORY = "SCHEDULER";

    private Service service;
    private List<String> monthNames;

    public MyActivity(){
        service = new Service();
        monthNames = new ArrayList<String>();
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        service.checkConfigData();

        monthNames.addAll(service.getMonthNames());

        final Spinner spinnerMonth = (Spinner) findViewById(R.id.spinnerMonth);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item,monthNames);
        spinnerMonth.setAdapter(adapter);

        Button buttonAddMonth = (Button) findViewById(R.id.buttonAddMonth);
        buttonAddMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new InputDialog(v.getContext(), R.string.dialog_create_month_title, R.string.hint_set_month_name) {
                    @Override
                    public void onPositiveClick(Context context, String insertedValue) {
                        if (insertedValue.trim().isEmpty()) {
                            new MessageDialog(context, "Month name can't be empty!").show();
                            return;
                        }
                        if (monthNames.contains(insertedValue)) {
                            new MessageDialog(context, "Month with name \"" + insertedValue + "\" already exists!").show();
                            return;
                        }

                        service.createMonth(insertedValue);
                        monthNames.add(insertedValue);
                        if (monthNames.size() == 1){
                            spinnerMonth.setAdapter(new ArrayAdapter<String>(context, R.layout.spinner_item,monthNames));
                        }
                        spinnerMonth.setSelection(monthNames.indexOf(insertedValue));

                    }
                }.show();
            }
        });

        Button buttonAddItem = (Button) findViewById(R.id.buttonAddItem);
        buttonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (monthNames.contains(spinnerMonth.getSelectedItem())) {
                    new AddItemDialog(v.getContext(), service.getItemTypes()) {
                        @Override
                        public void performAddItem(Item item) {
                            service.addItem(item, (String) spinnerMonth.getSelectedItem());
                        }
                    }.show();
                }
            }
        });


        Button buttonPreview = (Button) findViewById(R.id.buttonPreview);
        buttonPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int ip = wifiInfo.getIpAddress();

                String ipString = String.format(
                        "%d.%d.%d.%d",
                        (ip & 0xff),
                        (ip >> 8 & 0xff),
                        (ip >> 16 & 0xff),
                        (ip >> 24 & 0xff));
                new MessageDialog(v.getContext(),ipString).show();
            }
        });

        Button buttonStats = (Button) findViewById(R.id.buttonStats);

        new SocketServerService(this).start();
    }
}
