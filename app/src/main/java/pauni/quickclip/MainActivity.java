package pauni.quickclip;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    void toastThis (CharSequence charSeq) {
        Toast.makeText(getApplicationContext(), charSeq, Toast.LENGTH_SHORT);
    }

    void copyToClipboard() {
        //android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        //android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", textView.getText());
        //clipboard.setPrimaryClip(clip);
    }
}