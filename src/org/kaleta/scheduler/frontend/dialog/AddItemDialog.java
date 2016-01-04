package org.kaleta.scheduler.frontend.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import org.kaleta.scheduler.R;
import org.kaleta.scheduler.backend.entity.Item;
import org.kaleta.scheduler.backend.entity.ItemType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Stanislav Kaleta on 11.10.2015.
 */
public abstract class AddItemDialog extends AlertDialog.Builder {
    private List<ItemType> types;

    private List<String> textTypeList;
    private List<String> textDescList;

    private ToggleButton toggleButtonIncome;
    private AutoCompleteTextView textType;
    private AutoCompleteTextView textDesc;

    public AddItemDialog(final Context context, List<ItemType> types) {
        super(context);
        this.types = types;
        textTypeList = new ArrayList<>();
        textDescList = new ArrayList<>();
        this.setTitle(R.string.dialog_add_item_title);
        initComponents();
    }

    private void initComponents() {
        View newItemView = View.inflate(getContext(), R.layout.add_item, null);

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
        toggleButtonIncome.setTextOn(getContext().getResources().getString(R.string.toggle_income_text));
        toggleButtonIncome.setTextOff(getContext().getResources().getString(R.string.toggle_expense_text));
        toggleButtonIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpTypes(((ToggleButton) v).isChecked());
            }
        });

        textType = (AutoCompleteTextView) newItemView.findViewById(R.id.itemTextType);
        textType.setThreshold(1);
        ArrayAdapter<String> adapterType = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, textTypeList);

        textType.setAdapter(adapterType);
        textType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((AutoCompleteTextView) v).showDropDown();
                return false;
            }
        });
        textType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                setUpDescriptions(s.toString());
            }
        });

        textDesc = (AutoCompleteTextView) newItemView.findViewById(R.id.itemTextDesc);
        textDesc.setThreshold(1);
        ArrayAdapter<String> adapterDesc = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, textDescList);
        textDesc.setAdapter(adapterDesc);
        textDesc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((AutoCompleteTextView) v).showDropDown();
                return false;
            }
        });

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
                    new MessageDialog(getContext(), "Inserted day is not valid!").show();
                    return;
                }

                try {
                    BigDecimal amount = BigDecimal.valueOf(Double.valueOf(textAmount.getText().toString()));
                    item.setAmount(amount);
                } catch (NumberFormatException e){
                    new MessageDialog(getContext(), "Inserted amount is not valid!").show();
                    return;
                }

                String description = textDesc.getText().toString();
                // desc. can be empty string
                item.setDescription(description);

                String type = textType.getText().toString();
                if (type.equals("")){
                    new MessageDialog(getContext(), "Type is not selected!").show();
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
        textType.setAdapter(new ArrayAdapter<>(textType.getContext(), android.R.layout.simple_spinner_dropdown_item, textTypeList));
    }

    private void setUpDescriptions(String typeName){
        textDescList.clear();
        for (ItemType type : types){
            if (type.getName().equals(typeName)){
                textDescList.addAll(type.getPreparedDescriptions());
            }
        }
        textDesc.getText().clear();
        // dunno why dropdown not changed when getText().clear() is called
        textDesc.setAdapter(new ArrayAdapter<>(textDesc.getContext(), android.R.layout.simple_spinner_dropdown_item, textDescList));
    }

    public abstract void performAddItem(Item item);
}
