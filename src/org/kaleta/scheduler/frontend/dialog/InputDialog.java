package org.kaleta.scheduler.frontend.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;
import org.kaleta.scheduler.R;

/**
 * Created by Stanislav Kaleta on 11.10.2015.
 */
public abstract class InputDialog extends AlertDialog.Builder{
    private EditText textField;

    public InputDialog(Context context, int titleId, int hintId) {
        super(context);
        this.setTitle(titleId);
        textField = new EditText(context);
        textField.setHint(hintId);
        this.setView(textField);

        this.setPositiveButton(R.string.button_ok_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onPositiveClick(InputDialog.this.getContext(), textField.getText().toString());
            }
        });

        this.setNegativeButton(R.string.button_cancel_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }

    public abstract void onPositiveClick(Context context, String insertedValue);
}
