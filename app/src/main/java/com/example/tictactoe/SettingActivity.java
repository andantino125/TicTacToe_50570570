package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

public class SettingActivity extends AppCompatActivity {
    private Switch mDarkModeSwitch, mAndroidFirstSwitch, mSoundEffectSwitch;
    private TextView mDifficultyTextView;
    private int mDifficulty;
    private static final int SETTING = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        boolean mDarkMode = getIntent().getBooleanExtra("DarkMode", false);
        boolean mAndroidFirst = getIntent().getBooleanExtra("AndroidFirst", false);
        boolean mSoundEffect = getIntent().getBooleanExtra("SoundEffect", true);
        mDifficulty = getIntent().getIntExtra("Difficulty", TicTacToeGame.HARD);

        super.onCreate(savedInstanceState);
        setTheme(mDarkMode ? R.style.Theme_TicTacToeDark : R.style.Theme_TicTacToe);
        setContentView(R.layout.activity_setting);

        mDarkModeSwitch = (Switch) findViewById(R.id.switch_dark_mode);
        mAndroidFirstSwitch = (Switch) findViewById(R.id.switch_android_first);
        mSoundEffectSwitch = (Switch) findViewById(R.id.switch_sound_effect);
        mDifficultyTextView = (TextView) findViewById(R.id.switch_difficulty);

        mDarkModeSwitch.setChecked(mDarkMode);
        mAndroidFirstSwitch.setChecked(mAndroidFirst);
        mSoundEffectSwitch.setChecked(mSoundEffect);

        if (mDifficulty == TicTacToeGame.EASY)
            mDifficultyTextView.setText(R.string.level_1);
        else if (mDifficulty == TicTacToeGame.MEDIUM)
            mDifficultyTextView.setText(R.string.level_2);
        else if (mDifficulty == TicTacToeGame.HARD)
            mDifficultyTextView.setText(R.string.level_3);
    }

    //--- To inflate the option menu; this adds items to the action bar if it is present
    //--- The only item in the menu is "Done"
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuItem menuItem = menu.add(Menu.NONE, 1000, Menu.NONE, R.string.setting_done);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }


    //--- Handler for Done button
    //--- To return the setting parameters to Main Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        intent.putExtra("DarkMode", mDarkModeSwitch.isChecked());
        intent.putExtra("AndroidFirst", mAndroidFirstSwitch.isChecked());
        intent.putExtra("SoundEffect", mSoundEffectSwitch.isChecked());
        intent.putExtra("Difficulty", mDifficulty);
        setResult(SETTING, intent);
        finish();

        return true;
    }

    //--- To show the difficulty selection dialog
    public void chooseDifficulty(View view) {
        String[] listItems = {getResources().getString(R.string.level_1), getResources().getString(R.string.level_2), getResources().getString(R.string.level_3)};

        new AlertDialog.Builder(SettingActivity.this)
                .setTitle(getResources().getString(R.string.choose_difficulty))
                .setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mDifficulty = i;
                        mDifficultyTextView.setText(listItems[i]);
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }
}