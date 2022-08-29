package com.iox_prime.neomemo;
/*Author: Bigyan Chapagain
* 8 May, 2017
* Project: Cognitive Text Editor
* App Name: neo memo */

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;
    EditText editTextTitle, editTextMemo;
    ImageButton save, clear, speak, list, clipboard;
    int id;
//  String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        dbHelper = new DatabaseHelper(this);
        editTextTitle = (EditText) findViewById(R.id.edit_text_title);
        editTextMemo = (EditText) findViewById(R.id.edit_text_memo);
        save = (ImageButton) findViewById(R.id.save_button);
        clear = (ImageButton) findViewById(R.id.clear_button);
        speak = (ImageButton) findViewById(R.id.speak_button);
        list = (ImageButton) findViewById(R.id.list_button);
        clipboard = (ImageButton) findViewById(R.id.clipboard_button);

        id = getIntent().getIntExtra("id", 0);
//        Log.d(TAG, "onCreate() returned: " + id);

        if (id > 0)//UPDATE the database
        {
            UserInfo info = dbHelper.getUserInfo(id + "");
            editTextTitle.setText(info.title);
            editTextMemo.setText(info.memo);
        }
        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
                //todo: move cursor to the end of text
                //todo: or remove cursor temporarily
            }
        });
        clipboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String memoValue = editTextMemo.getText().toString();
                String titleValue = editTextTitle.getText().toString();
                boolean validation = true;
                if ((memoValue.length() == 0) && (titleValue.length() == 0)) {
                    validation = false;
                    Toast.makeText(MainActivity.this, "Add some text first.", Toast.LENGTH_SHORT).show();
                }

                if (validation) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    if (titleValue.length() == 0) {
                        clipboard.setText(memoValue);
                    } else {
                        clipboard.setText(concatenateText(titleValue, memoValue));
                    }
                    Toast.makeText(MainActivity.this, "Copied to clipboard.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editTextValue = editTextMemo.getText().toString();
                String editTextTopicValue = editTextTitle.getText().toString();

                boolean validation = true;
                if ((editTextValue.length() == 0) && (editTextTopicValue.length() == 0)) {
                    validation = false;
                    Toast.makeText(MainActivity.this, "No text to clear.",
                            Toast.LENGTH_SHORT).show();
                }

                if (validation) {
                    AlertDialog diaBox = AskOption();
                    diaBox.show();
                }
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titleValue = editTextTitle.getText().toString();
                String memoValue = editTextMemo.getText().toString();
                ContentValues cv = new ContentValues();
                cv.put("title", titleValue);
                cv.put("memo", memoValue);

                boolean validation = true;
                if ((titleValue.length() == 0) && (memoValue.length() == 0)) {
                    validation = false;
                    Toast.makeText(MainActivity.this, "Nothing to save.",
                            Toast.LENGTH_SHORT).show();
                }
                if ((id == 0) && validation)//INSERT data into the database
                {
                    dbHelper.insertUserInfo(cv);
                    keepTextBoxesClean();
                    Toast.makeText(MainActivity.this, "Saved.", Toast.LENGTH_LONG).show();
                }
                if ((id != 0) && validation) {//UPDATE data into the database
                    dbHelper.updateUserInfo(cv, id);
                    Toast.makeText(MainActivity.this, "Edited.", Toast.LENGTH_LONG).show();
                    keepTextBoxesClean();
                    id = 0;//keep id clean after the UPDATE operation.
                }
            }

        });
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ListActivity.class));
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String memoValue = editTextMemo.getText().toString();
                String titleValue = editTextTitle.getText().toString();

                boolean validation = true;
                if ((memoValue.length() == 0) && (titleValue.length() == 0)) {
                    Toast.makeText(MainActivity.this, "No text to clear.", Toast.LENGTH_SHORT).show();
                    validation = false;
                }

                if (validation) {
                    AlertDialog diaBox = AskOption();
                    diaBox.show();
                } else {
                    editTextMemo.setError("Please enter some text.");

                }
            }
        });
    }

    private void keepTextBoxesClean() {
        editTextMemo.setText("");
        editTextTitle.setText("");
    }

    public AlertDialog AskOption()//Delete Alert Dialog
    {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Clear all text?")
                .setMessage("Are you sure?")
                .setIcon(R.drawable.ic_clear_text)
                .setPositiveButton("Clear", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        keepTextBoxesClean();
                        id = 0;//refresh the id of database.
                        Toast.makeText(MainActivity.this, "Text cleared",
                                Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return alertDialog;
    }

    private void promptSpeechInput() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        //Localization can be done here.
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something...");
        try {
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(MainActivity.this,
                    "Your device doesn't support speech recognition",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int request_code, int result_code, Intent intent) {
        super.onActivityResult(request_code, result_code, intent);
        switch (request_code) {
            case 100:
                if (result_code == RESULT_OK && intent != null) {
                    ArrayList<String> result = intent.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (editTextTitle.hasFocus()) {
                        if (editTextTitle.length() == 0) {
                            //todo: AutoCapitalize and punctuation
                            editTextTitle.setText(result.get(0));

                        } else {
                            editTextTitle.setText(concatenateText(editTextTitle.getText().toString(),
                                    result.get(0)));
                            //todo: set cursor to end
                        }
                    } else { //In default, if indent doesn't blink anywhere, whatever spoken, it will go to huge text box.
                        if (editTextMemo.length() == 0) {
                            //todo: AutoCapitalize and punctuation
                            editTextMemo.setText(result.get(0));

                        } else {
                            editTextMemo.setText(concatenateText(editTextMemo.getText().toString(),
                                    result.get(0)));
                            //todo: set cursor to end
                        }
                    }

                }
                break;
        }
    }

    public String concatenateText(String string1, String string2) {
        StringBuilder sb = new StringBuilder();
        return sb.append(string1).append(" ").append(string2).toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        /*if(id>0){
            exitAlert();
        }*/
        if ((editTextMemo.length() != 0) || editTextTitle.length() != 0) {
            exitAlert();
        } else super.onBackPressed();
        //works like super.onDestroy() of a Context.
    }

    public void exitAlert() {
        android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(this);
        dialog.setTitle("Exit?");
        dialog.setMessage("You may want to save as a memo.");
        dialog.setNegativeButton("Back to editor", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //exit completely to home screen.
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
//        dialog.setNeutralButton() // We don't need this now.
        dialog.show();
    }
}
