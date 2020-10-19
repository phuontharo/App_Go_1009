package com.example.app_go_1009;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.LinkedList;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {
    int turn = 0;
    int[][] board = new int[9][9];
    int[][] sameStones = new int[9][9];

    ToaDo rawQ = new ToaDo();

    public static class ToaDo {
        public ToaDo() {
        }

        public ToaDo(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int x, y;

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setButton();
        setMatrix();
    }

    public void setButton() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.YELLOW);
        layout.setPadding(0, 0, 0, 0);
        layout.setLayoutParams(new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
        for (int i = 0; i < 9; i++) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
            for (int j = 0; j < 9; j++) {
                final Button btnTag = new Button(this);
                btnTag.setText("+");
                btnTag.setId(j + 1 + (i * 9));
                btnTag.setBackgroundColor(Color.parseColor("#FFD740"));
                btnTag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buttonInteract(btnTag, turn++ % 2);
                    }
                });
                btnTag.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
                row.addView(btnTag);
            }
            layout.addView(row);
        }

        LinearLayout row = new LinearLayout(this);
        row.setLayoutParams(new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        final Button btnTag = new Button(this);
        btnTag.setText("Reset Board");
        int resID = getResources().getIdentifier("Reset button", "id", getPackageName());
        btnTag.setId(resID);
        btnTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetBoard();
            }
        });
        btnTag.setLayoutParams(new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        row.addView(btnTag);
        layout.addView(row);

        setContentView(layout);
    }

    public void resetBoard() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                refreshStone(i, j);
            }
        }
    }

    public void setMatrix() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                board[i][j] = 2;
            }
        }
    }

    public void changeMatrix(int i, int j, int turn) {
        if (turn == 0)
            board[i][j] = 0;
        else board[i][j] = 1;
    }

    public void showMatrix(int[][] board) {
        StringBuilder matrixValue = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                matrixValue.append(board[i][j]).append(" ");
            }
            matrixValue.append("\n");
        }
        Log.i("Matrix:\n", matrixValue.toString());
    }

    public void buttonInteract(Button btnTag, int turn) {
        int id = Integer.parseInt(String.valueOf(btnTag.getId()));
        int row = (int) ((id - 1) / 9);
        int col = id - 1 - 9 * row;
        changeMatrix(row, col, turn);
        if (checkStonesAround(row, col)) { // neu danh vao k tu siet khi
            changeColor(btnTag, turn);
            btnTag.setEnabled(false);
        } else {
            clearMatrix(row, col);
        }
        showMatrix(board);
    }

    public void changeColor(Button button, int turn) {
        if (turn == 0)
            button.setBackgroundColor(Color.BLACK);
        else button.setBackgroundColor(Color.WHITE);
    }

    public boolean checkStonesAround(int i, int j) {
        // check for top stone
        if (i != 0) {
            if ((board[i][j] == 1 && board[i - 1][j] == 0)
                    || (board[i][j] == 0 && board[i - 1][j] == 1)) { // different color -> check LiveDeath of the different color
                if (!checkLiveDeath(i - 1, j)) clearStones(i - 1, j);
            }
        }
        // check for right stone
        if (j != 8) {
            if ((board[i][j] == 1 && board[i][j + 1] == 0)
                    || (board[i][j] == 0 && board[i][j + 1] == 1)) { // different color -> check LiveDeath of the different color
                if (!checkLiveDeath(i, j + 1)) clearStones(i, j + 1);
            }
        }
        // check for left stone
        if (j != 0) {
            if ((board[i][j] == 1 && board[i][j - 1] == 0)
                    || (board[i][j] == 0 && board[i][j - 1] == 1)) { // different color -> check LiveDeath of the different color
                if (!checkLiveDeath(i, j - 1)) clearStones(i, j - 1);
            }
        }
        // check for bottom stone
        if (i != 8) {
            if ((board[i][j] == 1 && board[i + 1][j] == 0)
                    || (board[i][j] == 0 && board[i + 1][j] == 1)) { // different color -> check LiveDeath of the different color
                if (!checkLiveDeath(i + 1, j)) clearStones(i + 1, j);
            }
        }
        if (!checkLiveDeath(i, j)) { // check LiveDeath of the recent stone
            turn--;
            return false;
        }
        return true;
    }


    public boolean checkLiveDeath(int i, int j) { //  find the empty space
        Queue<ToaDo> sameColor = new LinkedList<ToaDo>();
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) { // danh dau cac dam quan cung mau
                sameStones[x][y] = 0;
            }
        }
        rawQ.setX(i);
        rawQ.setY(j);
        sameColor.add(rawQ);
        sameStones[i][j] = 1;
        while (!sameColor.isEmpty()) {
            Log.i("Matrix", "Live Death: "+sameColor.size());
            i = sameColor.peek().getX();
            j = sameColor.peek().getY();
            // check for top stone
            if (i != 0) {
                if ((board[i][j] == 1 && board[i - 1][j] == 0)
                        || (board[i][j] == 0 && board[i - 1][j] == 1)) { // different color -> stop
                } else if ((board[i][j] == 1 && board[i - 1][j] == 1)
                        || (board[i][j] == 0 && board[i - 1][j] == 0)) { // same color -> add to queue
                    if (sameStones[i - 1][j] == 0) {
                        sameStones[i - 1][j] = 1;
                        rawQ.setX(i - 1);
                        rawQ.setY(j);
                        sameColor.add(rawQ);
                        Log.i("Matrix", "Add");
                    }
                } else {
                    Log.i("Matrix", "Live");
                    return true; // have empty space
                }
            }
            // check for right stone
            if (j != 8) {
                if ((board[i][j] == 1 && board[i][j + 1] == 0)
                        || (board[i][j] == 0 && board[i][j + 1] == 1)) { // different color -> stop
                } else if ((board[i][j] == 1 && board[i][j + 1] == 1)
                        || (board[i][j] == 0 && board[i][j + 1] == 0)) { // same color -> add to queue
                    if (sameStones[i][j + 1] == 0) {
                        sameStones[i][j + 1] = 1;
                        rawQ.setX(i);
                        rawQ.setY(j + 1);
                        sameColor.add(rawQ);
                        Log.i("Matrix", "Add");
                    }
                } else {
                    Log.i("Matrix", "Live");
                    return true; // have empty space
                }
            }
            // check for left stone
            if (j != 0) {
                if ((board[i][j] == 1 && board[i][j - 1] == 0)
                        || (board[i][j] == 0 && board[i][j - 1] == 1)) { // different color -> stop
                } else if ((board[i][j] == 1 && board[i][j - 1] == 1)
                        || (board[i][j] == 0 && board[i][j - 1] == 0)) { // same color -> add to queue
                    if (sameStones[i][j - 1] == 0) {
                        sameStones[i][j - 1] = 1;
                        rawQ.setX(i);
                        rawQ.setY(j - 1);
                        sameColor.add(rawQ);
                        Log.i("Matrix", "Add");
                    }
                } else {
                    Log.i("Matrix", "Live");
                    return true; // have empty space
                }
            }
            // check for bottom stone
            if (i != 8) {
                if ((board[i][j] == 1 && board[i + 1][j] == 0)
                        || (board[i][j] == 0 && board[i + 1][j] == 1)) { // different color -> stop
                } else if ((board[i][j] == 1 && board[i + 1][j] == 1)
                        || (board[i][j] == 0 && board[i + 1][j] == 0)) { // same color -> add to queue
                    if (sameStones[i + 1][j] == 0) {
                        sameStones[i + 1][j] = 1;
                        rawQ.setX(i + 1);
                        rawQ.setY(j);
                        sameColor.add(rawQ);
                        Log.i("Matrix", "Add");
                    }
                } else {
                    Log.i("Matrix", "Live");
                    return true;
                }// have empty space
            }
            showMatrix(sameStones);
            sameColor.remove();
        }
        Log.i("Matrix", "Death");
        return false;
    }

    public void clearMatrix(int i, int j) {
        board[i][j] = 2;
    }

    public void clearColor(int i, int j) {
        String buttonID = "" + (j + 1 + i * 9);
        int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
        Button button = ((Button) findViewById(resID));
        button.setBackgroundColor(Color.parseColor("#FFD740"));
        // and set enabled button
        button.setEnabled(true);
    }

    public void refreshStone(int i, int j) {
        clearMatrix(i, j);
        clearColor(i, j);
    }

    public void clearStones(int i, int j) {
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                sameStones[x][y] = 0;
            }
        }
        sameStones[i][j] = 1;
        Queue<ToaDo> sameColor = new LinkedList<ToaDo>();
        rawQ.setX(i);
        rawQ.setY(j);
        sameColor.add(rawQ);
        while (!sameColor.isEmpty()) {
            i = sameColor.peek().getX();
            j = sameColor.peek().getY();
            // check for top stone
            if (i != 0) {
                if ((board[i][j] == 1 && board[i - 1][j] == 1)
                        || (board[i][j] == 0 && board[i - 1][j] == 0)) { // same color -> delete
                    if (sameStones[i - 1][j] == 0) {
                        sameStones[i - 1][j] = 1;
                        rawQ.setX(i - 1);
                        rawQ.setY(j);
                        sameColor.add(rawQ);
                    }
                }
            }
            // check for right stone
            if (j != 8) {
                if ((board[i][j] == 1 && board[i][j + 1] == 1)
                        || (board[i][j] == 0 && board[i][j + 1] == 0)) { // same color -> delete
                    if (sameStones[i][j + 1] == 0) {
                        sameStones[i][j + 1] = 1;
                        rawQ.setX(i);
                        rawQ.setY(j + 1);
                        sameColor.add(rawQ);
                    }
                }
            }
            // check for left stone
            if (j != 0) {
                if ((board[i][j] == 1 && board[i][j - 1] == 1)
                        || (board[i][j] == 0 && board[i][j - 1] == 0)) { // same color -> add to queue
                    if (sameStones[i][j - 1] == 0) {
                        sameStones[i][j - 1] = 1;
                        rawQ.setX(i);
                        rawQ.setY(j - 1);
                        sameColor.add(rawQ);
                    }
                }
            }
            // check for bottom stone
            if (i != 8) {
                if ((board[i][j] == 1 && board[i + 1][j] == 1)
                        || (board[i][j] == 0 && board[i + 1][j] == 0)) { // same color -> add to queue
                    if (sameStones[i + 1][j] == 0) {
                        sameStones[i + 1][j] = 1;
                        rawQ.setX(i + 1);
                        rawQ.setY(j);
                        sameColor.add(rawQ);
                    }
                }
            }

            sameColor.remove();
        }
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                if (sameStones[x][y] == 1) {
                    refreshStone(x, y);
                }
            }
        }
    }
}