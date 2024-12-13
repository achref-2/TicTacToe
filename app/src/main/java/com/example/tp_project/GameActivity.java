package com.example.tp_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {
    private Game game;
    private TextView status;
    private GridLayout board;
    private DatabaseHelper dbHelper;

    private String player1Name;
    private String player2Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_page);

        dbHelper = new DatabaseHelper(this);
        game = new Game();
        status = findViewById(R.id.statusTextView);
        board = findViewById(R.id.boardGrid);

        player1Name = getIntent().getStringExtra("PLAYER_1_NAME");
        player2Name = getIntent().getStringExtra("PLAYER_2_NAME");


        status.setText(player1Name + "'s turn");

        for (int i = 0; i < board.getChildCount(); i++) {
            Button button = (Button) board.getChildAt(i);
            button.setTag("" + (i / 3) + (i % 3));
            button.setOnClickListener(this::onCellClick);
        }

        Button viewHistoryButton = findViewById(R.id.btnViewHistory);
        viewHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHistory(v);
            }
        });
    }

    public void onCellClick(View view) {
        Button button = (Button) view;
        String tag = button.getTag().toString();
        int row = Character.getNumericValue(tag.charAt(0));
        int col = Character.getNumericValue(tag.charAt(1));

        if (game.makeMove(row, col)) {
            button.setText(String.valueOf(game.getCurrentPlayer()));


            char winner = game.checkWinner();
            if (winner != '-') {
                if (winner == 'D') {
                    status.setText("It's a draw!");
                    saveGameHistory("Draw");
                } else {

                    String winnerName = (winner == 'X') ? player1Name : player2Name;
                    status.setText(winnerName + " wins!");
                    saveGameHistory(winnerName);
                }
                disableBoard();
            } else {
                game.switchPlayer();
                String currentPlayerName = (game.getCurrentPlayer() == 'X') ? player1Name : player2Name;
                status.setText(currentPlayerName + "'s turn");
            }
        }
    }

    private void disableBoard() {
        for (int i = 0; i < board.getChildCount(); i++) {
            board.getChildAt(i).setEnabled(false);
        }
    }

    private void saveGameHistory(String winner) {
        try {
            dbHelper.insertGame(player1Name, player2Name, winner);
        } catch (Exception e) {
            e.printStackTrace();
            status.setText("Error saving game history");
        }
    }


    public void viewHistory(View view) {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }
}
