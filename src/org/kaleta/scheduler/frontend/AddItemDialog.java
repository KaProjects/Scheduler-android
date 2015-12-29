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

        this.setTitle(R.string.dialog_add_item_title);

        View newItemView = View.inflate(context, R.layout.add_item, null);

        final EditText textDay = (EditText) newItemView.findViewById(R.id.itemTextDay);
        textDay.setHint(R.string.hint_set_day);

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
        toggleButtonIncome.setTextOn(context.getResources().getString(R.string.toggle_income_text));
        toggleButtonIncome.setTextOff(context.getResources().getString(R.string.toggle_expense_text));
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
        textAmount.setHint(R.string.hint_set_amount);

        this.setView(newItemView);

        this.setPositiveButton(R.string.button_add_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Item item = new Item();

                try {
                    Integer day = Integer.valueOf(textDay.getText().toString());
                    item.setDay(day);
                } catch (NumberFormatException e){
                    new MessageDialog(context, "Inserted day is not valid!").show();
                    return;
                }

                try {
                    BigDecimal amount = BigDecimal.valueOf(Double.valueOf(textAmount.getText().toString()));
                    item.setAmount(amount);
                } catch (NumberFormatException e){
                    new MessageDialog(context, "Inserted amount is not valid!").show();
                    return;
                }

                String description = (String) spinnerDesc.getSelectedItem();
                if (description == null){
                    description = "";
                }
                item.setDescription(description);

                String type = textType.getText().toString();
                if (type.equals("")){
                    new MessageDialog(context, "Type is not selected!").show();
                    return;
                }
                item.setType(type);

                item.setIncome(toggleButtonIncome.isChecked());

                performAddItem(item);
            }
        });

        this.setNegativeButton(R.string.button_cancel_text, new DialogInterface.OnClickListener() {
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
