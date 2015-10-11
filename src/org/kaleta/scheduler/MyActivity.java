package org.kaleta.scheduler;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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
import org.kaleta.scheduler.frontend.AddItemDialog;
import org.kaleta.scheduler.frontend.InputDialog;
import org.kaleta.scheduler.frontend.MessageDialog;

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

        final Spinner spinnerMonth = (Spinner) findViewById(R.id.mainSpinnerMonth);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,monthNames);
        spinnerMonth.setAdapter(adapter);

        Button buttonAddMonth = (Button) findViewById(R.id.mainButtonMonth);
        buttonAddMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new InputDialog(v.getContext(), R.string.creating_month, R.string.set_month_name_hint) {
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
                        spinnerMonth.setSelection(monthNames.indexOf(insertedValue));
                    }
                }.show();
            }
        });

        Button buttonAddItem = (Button) findViewById(R.id.mainButtonItem);
        buttonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddItemDialog(v.getContext(), service.getItemTypes()) {
                    @Override
                    public void performAddItem(Item item) {
                        // TODO service.performAddItem() <- month name, item
                    }
                }.show();
            }
        });


        Button buttonPreview = (Button) findViewById(R.id.mainButtonPreview);

        Button buttonStats = (Button) findViewById(R.id.mainButtonStats);



    }
}
