package deng.darkskydict;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import deng.darkskydict.util.YoudaoDict;

public class MainActivity extends Activity {

    private static final int FINISHED = 1;
    private ScrollView scrollView;
    private EditText editText;
    private ImageButton button;
    private TextView textView;
    private Thread queryThread;
    private Runnable queryRunnable;
    private AlertDialog aboutDialog;
    private final Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            // TODO 自动生成的方法存根
            if (msg.what == FINISHED) {
                try {
                    textView.setText(YoudaoDict.handleJson(new JSONObject(
                            (String) msg.obj)));
                } catch (JSONException e) {
                    // TODO 自动生成的 catch 块
                    textView.setText((String) msg.obj);
                    textView.append("\n");
                    textView.append(e.toString());
                }
            }
            super.handleMessage(msg);
        }

    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO 自动生成的方法存根
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scrollView = findViewById(R.id.scrollView);
        editText = findViewById(R.id.editText);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        ImageButton cross = findViewById(R.id.cross);
        editText.requestFocus();
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        queryRunnable = () -> {
            // TODO 自动生成的方法存根
            String ret = YoudaoDict.lookUpAWord(editText.getText()
                    .toString().trim());
            Message msg = handler.obtainMessage();
            msg.what = FINISHED;
            msg.obj = ret;
            handler.sendMessage(msg);
        };

        /*
          输入法中按发送键查询
         */
        editText.setOnEditorActionListener((v, actionId, event) -> {
            // TODO 自动生成的方法存根
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                button.performClick();
            }
            return true;
        });

        button.setOnClickListener(v -> {
            // TODO 自动生成的方法存根
            View view = getWindow().peekDecorView();
            if (view != null) {
                InputMethodManager inputManger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManger.hideSoftInputFromWindow(view.getWindowToken(),
                        0);
            }
            if (queryThread == null || !queryThread.isAlive()) {
                queryThread = new Thread(queryRunnable);
                queryThread.start();
            } else {
                Toast.makeText(getApplicationContext(), "Querying...",
                        Toast.LENGTH_SHORT).show();
            }
            scrollView.smoothScrollTo(0, 0);
        });

        button.setOnLongClickListener(view -> {
            String clipboardText = "";
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null) {
                if (clipboard.hasPrimaryClip()) {
                    clipboardText = clipboard.getPrimaryClip().getItemAt(0).getText().toString();
                }
            }
            editText.setText(clipboardText);
            button.performClick();
            return true;
        });

        cross.setOnLongClickListener(v -> {
            // TODO 自动生成的方法存根
            aboutDialog = new AlertDialog.Builder(MainActivity.this)
                    .create();
            aboutDialog.show();
            aboutDialog.getWindow().setContentView(R.layout.dialog_about);
            aboutDialog.getWindow().findViewById(R.id.button_ok)
                    .setOnClickListener(v1 -> aboutDialog.dismiss());
            return true;
        });
        textView.setOnLongClickListener(v -> {
            // TODO 自动生成的方法存根
            ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clip.setPrimaryClip(ClipData.newPlainText(null, textView.getText().toString()));
            Toast.makeText(getApplicationContext(), "Copied to clipboard.",
                    Toast.LENGTH_SHORT).show();
            return true;
        });
        cross.setOnClickListener(v -> {
            // TODO 自动生成的方法存根
            editText.setText("");
            editText.requestFocus();
            manager.showSoftInput(editText, 0);
        });

        textView.setOnTouchListener((view, motionEvent) -> {
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            return false;
        });
        manager.showSoftInput(editText, 0);
    }

}