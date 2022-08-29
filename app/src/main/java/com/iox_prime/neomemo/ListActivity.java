package com.iox_prime.neomemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;
/**
 * Created by user on 5/19/2017.
 */

public class ListActivity extends Activity implements PopupMenu.OnMenuItemClickListener {
    DatabaseHelper dbHelper;
    String TAG = "ListActivity";
    ListView listView;
    String currentId;
    ImageButton buttonAddMemo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.i(TAG, "onCreate: ");
        setContentView(R.layout.activity_list);
        dbHelper = new DatabaseHelper(this);
        buttonAddMemo = (ImageButton) findViewById(R.id.add_memo_button);

        listView = (ListView) findViewById(R.id.list_view);
        listView.setFocusableInTouchMode(true);

        /**if(listView.getAdapter().getCount()==0){
         buttonAddMemo.setVisibility(View.GONE);
         } // ENGENDERED ERROR.*/

        buttonAddMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListActivity.this, MainActivity.class));
            }
        });
    }

    public void showPopUp(View v) {
        currentId = v.getTag().toString();
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(ListActivity.this);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.my_pop_up, popupMenu.getMenu());
        popupMenu.show();

    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.i(TAG, "onResume");

        populateUserListView();
    }

    public void populateUserListView() {
        listView.setAdapter(new UserListAdapter(this, 0, dbHelper.getUserList()));
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.edit:
                Intent intent = new Intent(ListActivity.this, MainActivity.class);
                //I pass id by parsing String to Integer.
                intent.putExtra("id", Integer.parseInt(currentId));
//                Log.d(TAG, "onMenuItemClick: " + currentId);
                startActivity(intent);
                return true;
            case R.id.delete:
                deleteUserInfo(currentId);
                return true;
            case R.id.copy_2_clipboard:
                copy2clipboard();
                return true;
            default:
                return false;
        }
    }

    private void copy2clipboard() {
        UserInfo info = dbHelper.getUserInfo(currentId);
        String title = info.title;
        String memo = info.memo;
        StringBuilder sb = new StringBuilder();
        String clipboardValue = sb.append(title).append(" ").append(memo).toString();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboard.setText(clipboardValue);
        Toast.makeText(ListActivity.this, "Copied to clipboard.", Toast.LENGTH_SHORT).show();
    }

    public void deleteUserInfo(String id) {
        AlertDialog diaBox = AskOption(id);
        diaBox.show();
//        populateUserListView();//refresh as we delete
//        Toast.makeText(ListActivity.this,"Deleted",Toast.LENGTH_SHORT).show();
    }

    public AlertDialog AskOption(final String id)//Delete Alert Dialog
    {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Delete?")
                .setMessage("Are you sure?")
                .setIcon(R.drawable.ic_delete)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dbHelper.deleteUserInfo(id);
                        populateUserListView();//refresh as we delete
                        Toast.makeText(ListActivity.this, "Deleted.",
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

    @Override
    protected void onStart() {
        super.onStart();
//        Log.i(TAG, "onStart: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        Log.i(TAG, "onRestart: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Log.i(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Log.i(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Log.i(TAG, "onDestroy: ");
    }
}
