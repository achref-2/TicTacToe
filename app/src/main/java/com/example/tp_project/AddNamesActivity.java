package com.example.tp_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddNamesActivity extends AppCompatActivity {

    private EditText player1NameInput;
    private EditText player2NameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_names);

        player1NameInput = findViewById(R.id.player1_name);
        player2NameInput = findViewById(R.id.player2_name);
    }


    public void startGame(View view) {
        String player1Name = player1NameInput.getText().toString().trim();
        String player2Name = player2NameInput.getText().toString().trim();

        if (player1Name.isEmpty() || player2Name.isEmpty()) {
            Toast.makeText(this, "Please enter both player names", Toast.LENGTH_SHORT).show();
        } else {

            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("PLAYER_1_NAME", player1Name);
            intent.putExtra("PLAYER_2_NAME", player2Name);
            startActivity(intent);
        }
    }
}
