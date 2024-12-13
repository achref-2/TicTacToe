package com.example.tp_project;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class HistoryActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private LinearLayout historyLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        historyLayout = findViewById(R.id.historyLayout);

        displayHistory();
    }

    private void displayHistory() {
        Cursor cursor = null;
        try {
            historyLayout.removeAllViews();

            cursor = dbHelper.getHistory();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    final int gameId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                    String player1 = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PLAYER_1));
                    String player2 = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PLAYER_2));
                    String winner = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WINNER));
                    TextView gameHistoryTextView = new TextView(this);
                    gameHistoryTextView.setText(player1 + " vs " + player2 + " - Winner: " +
                            (winner != null && !winner.isEmpty() ? winner : "Draw"));
                    gameHistoryTextView.setTextSize(12);
                    gameHistoryTextView.setTextColor(getResources().getColor(android.R.color.white));
                    gameHistoryTextView.setPadding(0, 0, 0, 16);

                    Button deleteButton = new Button(this);
                    deleteButton.setText("Delete");
                    deleteButton.setOnClickListener(v -> deleteGame(gameId));

                    Button updateButton = new Button(this);
                    updateButton.setText("Update");
                    updateButton.setOnClickListener(v -> showUpdateDialog(gameId));

                    LinearLayout gameLayout = new LinearLayout(this);
                    gameLayout.setOrientation(LinearLayout.HORIZONTAL);
                    gameLayout.setPadding(0, 8, 0, 8);

                    gameLayout.addView(gameHistoryTextView);
                    gameLayout.addView(deleteButton);
                    gameLayout.addView(updateButton);

                    historyLayout.addView(gameLayout);

                } while (cursor.moveToNext());
            } else {
                TextView noHistoryTextView = new TextView(this);
                noHistoryTextView.setText("No game history available.");
                noHistoryTextView.setTextSize(16);
                noHistoryTextView.setTextColor(getResources().getColor(android.R.color.white));
                historyLayout.addView(noHistoryTextView);
            }
        } catch (Exception e) {
            Log.e("HistoryActivity", "Error loading history", e);
            historyLayout.removeAllViews();
            TextView errorTextView = new TextView(this);
            errorTextView.setText("Error loading history: " + e.getMessage());
            errorTextView.setTextSize(16);
            errorTextView.setTextColor(getResources().getColor(android.R.color.white));
            historyLayout.addView(errorTextView);
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    private void deleteGame(int gameId) {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to delete this game from history?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        boolean isDeleted = dbHelper.deleteGame(gameId);
                        if (isDeleted) {
                            Toast.makeText(HistoryActivity.this, "Game deleted", Toast.LENGTH_SHORT).show();
                            displayHistory();
                        } else {
                            Toast.makeText(HistoryActivity.this, "Error deleting game", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showUpdateDialog(int gameId) {
        final String[] winners = {"Player 1", "Player 2", "Draw"};
        new AlertDialog.Builder(this)
                .setTitle("Update Winner")
                .setItems(winners, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String newWinner = winners[which];
                        boolean isUpdated = dbHelper.updateGameWinner(gameId, newWinner);
                        if (isUpdated) {
                            Toast.makeText(HistoryActivity.this, "Game winner updated", Toast.LENGTH_SHORT).show();
                            displayHistory();  // Refresh the history list
                        } else {
                            Toast.makeText(HistoryActivity.this, "Error updating winner", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
    }
}
