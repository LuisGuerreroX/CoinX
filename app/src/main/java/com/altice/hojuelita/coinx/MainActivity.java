package com.altice.hojuelita.coinx;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Boton para el activity del Conversor.
        Button conversor = findViewById(R.id.my_button0);

        //Boton para el dialog del about.
        Button about = findViewById(R.id.my_button1);

        //Intent para el activity del conversor.
        conversor.setOnClickListener((View v) -> {
                    Intent intent = new Intent(this, MyConversorActivity.class);
                    startActivity(intent);
                });

        //Boton para el dialogo del about.
        about.setOnClickListener(v -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.about))
                    .setMessage(getString(R.string.message));
            alert.show();
        });


    }

}
