package org.kaleta.scheduler.frontend.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import org.kaleta.scheduler.R;

/**
 * Created by Stanislav Kaleta on 11.10.2015.
 */
public class MessageDialog extends AlertDialog.Builder {
    public MessageDialog(Context context, String msg) {
        super(context);
        this.setMessage(msg);
        this.setPositiveButton(R.string.button_ok_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }
}
