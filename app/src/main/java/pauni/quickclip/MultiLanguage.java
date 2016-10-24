package pauni.quickclip;

import android.content.Context;

/**
 * Created by Roni on 15.10.2016.
 */

public class MultiLanguage {
    static String toast_connected;
    static String language = "english";
    Context context;

    MultiLanguage(Context context) {
        this.context = context;
        init();
    }

    void init() {
        switch (language) {
            case "german":
                toast_connected = context.getString(R.string.toastConnected_german);
                break;
            case "english":
                toast_connected = context.getString(R.string.toastConnected_english);
        }

    }

    static void setLanguage(String string) {
        language = string;
    }
}
