package sk.brecka.uxmobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import sk.brecka.uxmobile.model.study.Task;
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

    private static final String[] items = {"jeden", "dva"};

    public static Dialog buildTestDialog(final Activity activity) {
        String jsonString = "{\n" +
                "\t\"type\":\"alert\",\n" +
                "\t\"title\":\"Welcome title\",\n" +
                "\t\"message\":\"Welcome message\"\n" +
                "}";
        try {
            return buildAlertDialog2(activity, new JSONObject(jsonString), null, null, null)
                    .setMultiChoiceItems(items, new boolean[]{false, false}, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            Log.d("UxMobile", "onClick: " + which);
                        }
                    })


                    .create();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Dialog buildWelcomeDialog(final Activity activity, final UxMobileSession session) throws JSONException {
        return buildAlertDialog(activity, Config.get().getWelcomeDialogJson(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // positive
                try {
                    Config.get().setParticipatedInStudy(true);
                    buildInstructionDialog(activity, session).show();
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
                Config.get().setParticipatedInStudy(false);
            }
        });
    }

    public static Dialog buildInstructionDialog(final Activity activity, final UxMobileSession session) throws JSONException {
        return buildAlertDialog(activity, Config.get().getInstructionDialogJson(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // positive
                try {
                    buildTaskDialog(activity, session).show();
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

    public static Dialog buildTaskDialog(final Activity activity, final UxMobileSession session) throws JSONException {
        final Task task = Config.get().getCurrentTask();
        final JSONObject dialog = Config.get().getTaskDialogJson()
                .put("title", task.getTitle())
                .put("message", task.getMessage());

        return buildAlertDialog(activity, dialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // positive
                session.startTest();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // neutral
                session.skipTest();
                session.requestTask();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // negative
                session.cancelTest();
            }
        });
    }

    public static Dialog buildTaskCompletionDialog(final Activity activity, final UxMobileSession session) throws JSONException {
        return buildAlertDialog(activity, Config.get().getTaskCompletionDialogJson(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // positive
                try {
                    session.completeTest();
                    buildThankYouDialog(activity, session).show();
                } catch (JSONException e) {
                    Log.e("UxMobile", "onClick: ", e);
                }
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // neutral
                session.skipTest();
                session.requestTask();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // negative
            }
        });
    }

    public static Dialog buildThankYouDialog(final Activity activity, final UxMobileSession session) throws JSONException {
        return buildAlertDialog(activity, Config.get().getThankYouDialogJson(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // positive
                session.requestTest();
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

    private static AlertDialog buildAlertDialog(final Activity activity, JSONObject dialogJson, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener neutralListener, DialogInterface.OnClickListener negativeListener) throws JSONException {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

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

    private static AlertDialog.Builder buildAlertDialog2(final Activity activity, JSONObject dialogJson, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener neutralListener, DialogInterface.OnClickListener negativeListener) throws JSONException {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

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

        return builder;
    }

    private DialogBuilder() {
        // intentionally blank
    }
}
