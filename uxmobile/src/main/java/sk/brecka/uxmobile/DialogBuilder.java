package sk.brecka.uxmobile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import sk.brecka.uxmobile.util.Config;

/**
 * Created by matej on 8.2.2018.
 */

public class DialogBuilder {

    private static final String KEY_DIALOG_ID = "dialog_id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_TYPE = "type";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_BODY = "body";
    private static final String KEY_BUTTON_POSITIVE = "positive";
    private static final String KEY_BUTTON_NEGATIVE = "negative";
    private static final String KEY_BUTTON_NEUTRAL = "neutral";

    private static final String VALUE_TYPE_ALERT = "alert";

    public static Dialog buildTestDialog(final Context context) {
        String jsonString = "{\n" +
                "\t\"type\":\"alert\",\n" +
                "\t\"title\":\"Welcome title\",\n" +
                "\t\"message\":\"Welcome message\"\n" +
                "}";
        try {
            return buildAlertDialog(context, new JSONObject(jsonString), null, null, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Dialog buildWelcomeDialog(final Context context) throws JSONException {
        return buildAlertDialog(context, Config.get().getWelcomeDialogJson(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // positive
                try {
                    buildInstructionDialog(context).show();
                } catch (JSONException e) {
                    Log.e("UxMobile", "onClick: ", e);
                }
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // neutral
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // negative
            }
        });
    }

    public static Dialog buildInstructionDialog(final Context context) throws JSONException {
        return buildAlertDialog(context, Config.get().getInstructionDialogJson(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // positive
                try {
                    buildTaskDialog(context).show();
                } catch (JSONException e) {
                    Log.e("UxMobile", "onClick: ", e);
                }
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // neutral
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // negative
            }
        });
    }

    public static Dialog buildTaskDialog(final Context context) throws JSONException {
        return buildAlertDialog(context, Config.get().getTaskDialogJson(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // positive
                try {
                    buildTaskCompletionDialog(context).show();
                } catch (JSONException e) {
                    Log.e("UxMobile", "onClick: ", e);
                }
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // neutral
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // negative
            }
        });
    }

    public static Dialog buildTaskCompletionDialog(final Context context) throws JSONException {
        return buildAlertDialog(context, Config.get().getTaskCompletionDialogJson(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // positive
                try {
                    buildThankYouDialog(context).show();
                } catch (JSONException e) {
                    Log.e("UxMobile", "onClick: ", e);
                }
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // neutral
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // negative
            }
        });
    }

    public static Dialog buildThankYouDialog(final Context context) throws JSONException {
        return buildAlertDialog(context, Config.get().getThankYouDialogJson(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // positive
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // neutral
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // negative
            }
        });
    }

    private static AlertDialog buildAlertDialog(final Context context, JSONObject dialogJson, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener neutralListener, DialogInterface.OnClickListener negativeListener) throws JSONException {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if (dialogJson.has(KEY_TITLE)) {
            builder.setTitle(dialogJson.getString(KEY_TITLE));
        }

        if (dialogJson.has(KEY_MESSAGE)) {
            builder.setMessage(dialogJson.getString(KEY_MESSAGE));
        }

        // TODO: checkovat listenery na null?
        if (dialogJson.has(KEY_BUTTON_POSITIVE)) {
            builder.setPositiveButton(dialogJson.getString(KEY_BUTTON_POSITIVE), positiveListener);
        }

        if (dialogJson.has(KEY_BUTTON_NEUTRAL)) {
            builder.setNeutralButton(dialogJson.getString(KEY_BUTTON_NEUTRAL), neutralListener);
        }

        if (dialogJson.has(KEY_BUTTON_NEGATIVE)) {
            builder.setNegativeButton(dialogJson.getString(KEY_BUTTON_NEGATIVE), negativeListener);
        }

        // TODO: body parsovanie

        return builder.create();
    }

    private DialogBuilder() {
        // intentionally blank
    }
}
