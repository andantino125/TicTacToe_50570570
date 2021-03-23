package com.example.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    // Represents the internal state of the game
    private TicTacToeGame mGame;
    // Buttons making up the board
    private Button[] mBoardButtons;
    // Various text displayed
    private TextView mInfoTextView, mLevelTextView, mWinTextView, mDrawTextView, mLoseTextView;
    private ImageView mResultImage;

    // Game Over
    Boolean mGameOver;
    int mWinner, iWin, iDraw, iLoss;

    // Game config
    Boolean mDarkMode, mAndroidFirst, mSoundEffect;
    int mDifficulty;

    public static final char HUMAN_PLAYER = 'X';
    public static final char COMPUTER_PLAYER = 'O';

    public static final int BLACK_COLOR = Color.rgb(0, 0, 0);
    public static final int BLUE_COLOR = Color.rgb(0, 0, 200);
    public static final int GREEN_COLOR = Color.rgb(0, 200, 0);
    public static final int RED_COLOR = Color.rgb(200, 0, 0);

    public static final int AUDIO_MOVE = 0;
    public static final int AUDIO_WIN = 1;
    public static final int AUDIO_LOSE = 2;

    private static final int SETTING = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPreferences();
        createView();
    }

    private void createView() {
        setTheme(mDarkMode ? R.style.Theme_TicTacToeDark : R.style.Theme_TicTacToe);
        setContentView(R.layout.activity_main);

        ActionBar bar = getSupportActionBar();
        LinearLayout currentLayout = (LinearLayout) findViewById(R.id.main_layout);

        if (mDarkMode) {
            bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
            currentLayout.setBackgroundColor(getResources().getColor(R.color.Metallic_Silver));
        } else {
            bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple_500)));
            currentLayout.setBackgroundColor(getResources().getColor(R.color.white));
        }

        mBoardButtons = new Button[TicTacToeGame.BOARD_SIZE];
        mBoardButtons[0] = (Button) findViewById(R.id.button0);
        mBoardButtons[1] = (Button) findViewById(R.id.button1);
        mBoardButtons[2] = (Button) findViewById(R.id.button2);
        mBoardButtons[3] = (Button) findViewById(R.id.button3);
        mBoardButtons[4] = (Button) findViewById(R.id.button4);
        mBoardButtons[5] = (Button) findViewById(R.id.button5);
        mBoardButtons[6] = (Button) findViewById(R.id.button6);
        mBoardButtons[7] = (Button) findViewById(R.id.button7);
        mBoardButtons[8] = (Button) findViewById(R.id.button8);
        mInfoTextView = (TextView) findViewById(R.id.information);
        mLevelTextView = (TextView) findViewById(R.id.textView_level);
        mWinTextView = (TextView) findViewById(R.id.textView_win);
        mDrawTextView = (TextView) findViewById(R.id.textView_draw);
        mLoseTextView = (TextView) findViewById(R.id.textView_loss);
        mResultImage = (ImageView) findViewById(R.id.resultImage);

        mGame = new TicTacToeGame();
        startNewGame(mAndroidFirst ? COMPUTER_PLAYER : HUMAN_PLAYER);
    }

    //--- OnClickListener for Restart a New Game Button
    public void newGame(View v) {
        startNewGame(mAndroidFirst ? COMPUTER_PLAYER : HUMAN_PLAYER);
    }

    //--- OnClickListener for Clear Button
    public void clearHistory(View v) {
        iWin = 0;
        iDraw = 0;
        iLoss = 0;
        savePreferences(mDarkMode, mAndroidFirst, mSoundEffect, mDifficulty, iWin, iDraw, iLoss);
        setInfoView();
    }

    //--- Set up the game board.
    private void startNewGame(char first_move) {
        mGameOver = false;
        mGame.clearBoard();
        mResultImage.setImageResource(android.R.color.transparent);

        //---Reset all buttons
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
        }

        if (first_move == COMPUTER_PLAYER) {
            mInfoTextView.setText(R.string.android_turn);
            int move = mGame.getComputerMove(mDifficulty);
            setMove(TicTacToeGame.COMPUTER_PLAYER, move);
            mWinner = 0;
        } else {
            mWinner = -1;
        }
        setInfoView();
    }

    //---Handles clicks on the game board buttons
    private class ButtonClickListener implements View.OnClickListener {
        int location;

        public ButtonClickListener(int location) {
            this.location = location;
        }

        @Override
        public void onClick(View v) {
            if (!mGameOver) {
                if (mBoardButtons[location].isEnabled()) {
                    setMove(TicTacToeGame.HUMAN_PLAYER, location);
                    //--- If no winner yet, let the computer make a move
                    mWinner = mGame.checkForWinner();

                    if (mWinner == 0) {
                        mInfoTextView.setText(R.string.android_turn);
                        int move = mGame.getComputerMove(mDifficulty);
                        setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                        mWinner = mGame.checkForWinner();
                    }
                    setStatus();
                    setInfoView();
                }
            }
        }
    }

    private void setMove(char player, int location) {
        mGame.setMove(player, location);
        mBoardButtons[location].setEnabled(false);
        mBoardButtons[location].setText(String.valueOf(player));
        if (player == TicTacToeGame.HUMAN_PLAYER) {
            mBoardButtons[location].setTextColor(GREEN_COLOR);
            if (mSoundEffect) {
                playAudio(AUDIO_MOVE);
            }
        } else {
            mBoardButtons[location].setTextColor(RED_COLOR);
        }
    }

    //--- Update the current status in the game.
    private void setStatus() {
        if (mWinner == 1) {
            mGameOver = true;
            iDraw++;
        } else if (mWinner == 2) {
            mGameOver = true;
            iWin++;
            if (mSoundEffect) {
                playAudio(AUDIO_WIN);
            }
            playAnimation();
        } else if (mWinner == 3) {
            mGameOver = true;
            iLoss++;
            if (mSoundEffect) {
                playAudio(AUDIO_LOSE);
            }
        }
        savePreferences(mDarkMode, mAndroidFirst, mSoundEffect, mDifficulty, iWin, iDraw, iLoss);
    }

    //--- Update the information view component.
    private void setInfoView() {
        if (mWinner == -1) {
            mInfoTextView.setTextColor(BLACK_COLOR);
            mInfoTextView.setText(R.string.human_first);
        } else if (mWinner == 0) {
            mInfoTextView.setTextColor(BLACK_COLOR);
            mInfoTextView.setText(R.string.human_turn);
        } else if (mWinner == 1) {
            mInfoTextView.setTextColor(BLUE_COLOR);
            mInfoTextView.setText(R.string.tie);
        } else if (mWinner == 2) {
            mInfoTextView.setTextColor(GREEN_COLOR);
            mInfoTextView.setText(R.string.human_won);
            mResultImage.setImageResource(R.drawable.medal);
        } else if (mWinner == 3) {
            mInfoTextView.setTextColor(RED_COLOR);
            mInfoTextView.setText(R.string.android_won);
        }
        StringBuilder levelText = new StringBuilder();
        levelText.append(getResources().getString(R.string.level)).append("\n");
        if (mDifficulty == TicTacToeGame.EASY)
            levelText.append(getResources().getString(R.string.level_1));
        else if (mDifficulty == TicTacToeGame.MEDIUM)
            levelText.append(getResources().getString(R.string.level_2));
        else if (mDifficulty == TicTacToeGame.HARD)
            levelText.append(getResources().getString(R.string.level_3));
        mLevelTextView.setText(levelText.toString());

        mWinTextView.setText(new StringBuilder().append(getResources().getString(R.string.win)).append("\n").append(iWin).toString());
        mDrawTextView.setText(new StringBuilder().append(getResources().getString(R.string.draw)).append("\n").append(iDraw).toString());
        mLoseTextView.setText(new StringBuilder().append(getResources().getString(R.string.loss)).append("\n").append(iLoss).toString());
    }

    //--- Save the state of current game
    //--- One usual case is save the state before screen rotation
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        for (int i = 0; i < mBoardButtons.length; i++) {
            outState.putCharSequence("Board" + i, mBoardButtons[i].getText());
        }

        outState.putBoolean("DarkMode", mDarkMode);
        outState.putBoolean("AndroidFirst", mAndroidFirst);
        outState.putBoolean("SoundEffect", mSoundEffect);
        outState.putInt("Difficulty", mDifficulty);
        outState.putInt("Winner", mWinner);
        outState.putBoolean("GameOver", mGameOver);
        outState.putInt("Win", iWin);
        outState.putInt("Draw", iDraw);
        outState.putInt("Loss", iLoss);
    }

    //--- Restore the state of current game
    //--- One usual case is restore the state after screen rotation
    protected void onRestoreInstanceState(Bundle savedState) {
        for (int i = 0; i < mBoardButtons.length; i++) {
            CharSequence player = savedState.getCharSequence("Board" + i);
            mBoardButtons[i].setText(player);
            if (player.equals(String.valueOf(HUMAN_PLAYER))) {
                mBoardButtons[i].setTextColor(GREEN_COLOR);
                mBoardButtons[i].setEnabled(false);
                mGame.setMove(player.charAt(0), i);
            } else if (player.equals(String.valueOf(COMPUTER_PLAYER))) {
                mBoardButtons[i].setTextColor(RED_COLOR);
                mBoardButtons[i].setEnabled(false);
                mGame.setMove(player.charAt(0), i);
            } else {
                mBoardButtons[i].setEnabled(true);
            }
        }

        mDarkMode = savedState.getBoolean("DarkMode");
        mAndroidFirst = savedState.getBoolean("AndroidFirst");
        mSoundEffect = savedState.getBoolean("SoundEffect");
        mDifficulty = savedState.getInt("Difficulty");
        mWinner = savedState.getInt("Winner");
        mGameOver = savedState.getBoolean("GameOver");
        iWin = savedState.getInt("Win");
        iDraw = savedState.getInt("Draw");
        iLoss = savedState.getInt("Loss");
        setInfoView();
    }

    //--- To inflate the option menu; this adds items to the action bar if it is present
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_options, menu);
        return true;
    }

    //--- Option item handler
    //--- 1. Setting, 2. Exit
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("DarkMode", mDarkMode);
            bundle.putBoolean("AndroidFirst", mAndroidFirst);
            bundle.putBoolean("SoundEffect", mSoundEffect);
            bundle.putInt("Difficulty", mDifficulty);
            intent.putExtras(bundle);
            startActivityForResult(intent, SETTING);
            return true;
        } else if (item.getItemId() == R.id.menu_quit) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.exit_title)
                    .setMessage(R.string.exit_message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.exit_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.exit_no, null).show();
            return true;
        }
        return false;
    }

    //--- To retrieve the result from another activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SETTING) {
            boolean originTheme = mDarkMode;
            int originDifficulty = mDifficulty;
            mDarkMode = data.getBooleanExtra("DarkMode", false);
            mAndroidFirst = data.getBooleanExtra("AndroidFirst", false);
            mSoundEffect = data.getBooleanExtra("SoundEffect", true);
            mDifficulty = data.getIntExtra("Difficulty", TicTacToeGame.HARD);
            savePreferences(mDarkMode, mAndroidFirst, mSoundEffect, mDifficulty, iWin, iDraw, iLoss);

            if (mDarkMode != originTheme) {
                Bundle bundle = new Bundle();
                onSaveInstanceState(bundle);
                createView();
                onRestoreInstanceState(bundle);
            } else if (mDifficulty != originDifficulty) {
                StringBuilder levelText = new StringBuilder();
                levelText.append(getResources().getString(R.string.level)).append("\n");
                if (mDifficulty == TicTacToeGame.EASY)
                    levelText.append(getResources().getString(R.string.level_1));
                else if (mDifficulty == TicTacToeGame.MEDIUM)
                    levelText.append(getResources().getString(R.string.level_2));
                else if (mDifficulty == TicTacToeGame.HARD)
                    levelText.append(getResources().getString(R.string.level_3));
                mLevelTextView.setText(levelText.toString());
            }
        }
    }

    //--- Saving user preferences
    //--- 1. Game configuration 2. Game statistics
    public void savePreferences(Boolean darkMode, Boolean androidFirst, Boolean soundEffect, int difficulty, int won, int draw, int lose) {
        SharedPreferences pref = getSharedPreferences("TicTacToe", MODE_PRIVATE);
        pref.edit().putBoolean("DarkMode", darkMode).apply();
        pref.edit().putBoolean("AndroidFirst", androidFirst).apply();
        pref.edit().putBoolean("SoundEffect", soundEffect).apply();
        pref.edit().putInt("Difficulty", difficulty).apply();
        pref.edit().putInt("Win", won).apply();
        pref.edit().putInt("Draw", draw).apply();
        pref.edit().putInt("Loss", lose).apply();
    }

    //--- Loading user preferences
    //--- 1. Game configuration 2. Game statistics
    public void loadPreferences() {
        SharedPreferences pref = getSharedPreferences("TicTacToe", MODE_PRIVATE);
        mDarkMode = pref.getBoolean("DarkMode", false);
        mAndroidFirst = pref.getBoolean("AndroidFirst", false);
        mSoundEffect = pref.getBoolean("SoundEffect", true);
        mDifficulty = pref.getInt("Difficulty", TicTacToeGame.HARD);
        iWin = pref.getInt("Win", 0);
        iDraw = pref.getInt("Draw", 0);
        iLoss = pref.getInt("Loss", 0);
    }

    //--- Play audio effect
    private void playAudio(int iAction) {
        String audioFile = "";

        switch (iAction) {
            case AUDIO_MOVE:
                audioFile = "android.resource://"
                        + getPackageName() + "/" + R.raw.human_move;
                break;
            case AUDIO_WIN:
                audioFile = "android.resource://"
                        + getPackageName() + "/" + R.raw.win;
                break;
            case AUDIO_LOSE:
                audioFile = "android.resource://"
                        + getPackageName() + "/" + R.raw.lose;
                break;
        }

        Uri uri = Uri.parse(audioFile);
        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
    }

    //--- Play animation
    private void playAnimation() {
        mResultImage.setImageResource(R.drawable.medal);
        ScaleAnimation fade_in = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        fade_in.setDuration(1000);
        fade_in.setFillAfter(true);
        mResultImage.startAnimation(fade_in);
    }
}