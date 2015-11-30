package org.kaleta.scheduler.frontend;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import org.kaleta.scheduler.R;
import org.kaleta.scheduler.backend.entity.Item;
import org.kaleta.scheduler.backend.entity.ItemType;
import org.kaleta.scheduler.backend.service.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Stanislav Kaleta on 11.10.2015.
 */
public abstract class AddItemDialog extends AlertDialog.Builder {
    private List<ItemType> types;

    private List<String> textTypeList;
    private List<String> spinnerDescList;

    private ToggleButton toggleButtonIncome;
    private AutoCompleteTextView textType;

    public AddItemDialog(final Context context, List<ItemType> types) {
        super(context);
        this.types = types;
        textTypeList = new ArrayList<String>();
        spinnerDescList = new ArrayList<String>();

        this.setTitle(R.string.adding_item);

        View newItemView = View.inflate(context, R.layout.add_item, null);

        final EditText textDay = (EditText) newItemView.findViewById(R.id.itemTextDay);
        textDay.setHint(R.string.set_day_hint);

        Button buttonToday = (Button) newItemView.findViewById(R.id.itemButtonToday);
        buttonToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                Integer today = calendar.get(Calendar.DAY_OF_MONTH);
                textDay.setText(String.valueOf(today));
            }
        });

        toggleButtonIncome = (ToggleButton) newItemView.findViewById(R.id.itemToggleIncome);
        toggleButtonIncome.setTextOn(context.getResources().getString(R.string.income));
        toggleButtonIncome.setTextOff(context.getResources().getString(R.string.expense));
        toggleButtonIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpTypes(((ToggleButton) v).isChecked());
            }
        });

        textType = (AutoCompleteTextView) newItemView.findViewById(R.id.itemTextType);
        ArrayAdapter<String> adapterType = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, textTypeList);
        textType.setAdapter(adapterType);
        textType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((AutoCompleteTextView) v).showDropDown();
                return false;
            }
        });

        final Spinner spinnerDesc = (Spinner) newItemView.findViewById(R.id.itemSpinnerDesc);
        ArrayAdapter<String> adapterDesc = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, spinnerDescList);
        spinnerDesc.setAdapter(adapterDesc);
        // TODO change to AutoCompleteTextView + (textType onSelectAction will fill this with desc.(+clear))

        final EditText textAmount = (EditText) newItemView.findViewById(R.id.itemTextAmount);
        textAmount.setHint(R.string.set_amount_hint);

        this.setView(newItemView);

        this.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Item item = new Item();

                Integer day = null;
                try {
                    day = Integer.valueOf(textDay.getText().toString());
                    // TODO test if needed - Id say if its empty, it will not parse
                    if (day == null || day == 0){
                        new MessageDialog(context, "Day is not selected!").show();
                        return;
                    }
                } catch (NumberFormatException e){ // TODO test
                    new MessageDialog(context, "Inserted day is not valid!").show();
                    return;
                }
                item.setDay(day);

                BigDecimal amount = null;
                try {
                    amount = BigDecimal.valueOf(Double.valueOf(textAmount.getText().toString()));
                    // TODO test if needed - Id say if its empty, it will not parse
                    if (amount == null){
                        new MessageDialog(context, "Amount is not selected!").show();
                        return;
                    }
                } catch (NumberFormatException e){ // TODO test
                    new MessageDialog(context, "Inserted amount is not valid!").show();
                    return;
                }
                item.setAmount(amount);

                String description = (String) spinnerDesc.getSelectedItem();
                if (description == null){
                    description = "";
                }
                item.setDescription(description);

                String type = textType.getText().toString();
                if (type == null || type.equals("")){ // TODO test
                    new MessageDialog(context, "Type is not selected!").show();
                    return;
                }
                item.setType(type);

                item.setIncome(toggleButtonIncome.isChecked()); // TODO test

                performAddItem(item);
            }
        });

        this.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        toggleButtonIncome.setChecked(false);
        setUpTypes(false);

    }

    private void setUpTypes(boolean income) {
        textTypeList.clear();
        for (ItemType type : types){
            if (type.getIncome().equals(income)){
                textTypeList.add(type.getName());
            }
        }
        textType.getText().clear();
        // dunno why dropdown not changed when getText().clear() is called
        textType.setAdapter(new ArrayAdapter<String>(textType.getContext(), android.R.layout.simple_spinner_dropdown_item, textTypeList));
    }

    public abstract void performAddItem(Item item);
}
