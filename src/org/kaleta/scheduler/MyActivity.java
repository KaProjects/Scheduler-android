package org.kaleta.scheduler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import org.kaleta.scheduler.backend.service.Service;
import org.kaleta.scheduler.frontend.InputDialog;
import org.kaleta.scheduler.frontend.MessageDialog;

import java.util.ArrayList;
import java.util.List;

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

        final Spinner spinnerNotes = (Spinner) findViewById(R.id.mainSpinnerMonth);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,monthNames);
        spinnerNotes.setAdapter(adapter);

        Button buttonAddMonth = (Button) findViewById(R.id.mainButtonMonth);
        buttonAddMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputDialog dialog = new InputDialog(v.getContext(), R.string.creating_month, R.string.set_month_name) {
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
                        spinnerNotes.setSelection(monthNames.indexOf(insertedValue));
                    }
                };
                dialog.show();
            }
        });

        Button buttonAddItem = (Button) findViewById(R.id.mainButtonItem);

        Button buttonPreview = (Button) findViewById(R.id.mainButtonPreview);

        Button buttonStats = (Button) findViewById(R.id.mainButtonStats);



    }
}
