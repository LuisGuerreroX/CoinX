package com.altice.hojuelita.coinx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;


public class MyConversorActivity extends AppCompatActivity {

    private String[] data = {"DOP", "USD", "EUR"};
    private Spinner mSpinner, mSpinner2;
    private TextView textView;
    private String resText;
    private String text = "0";
    private boolean wasDecimal, wasEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_conversor);

        mSpinner = findViewById(R.id.spinner);
        mSpinner2 = findViewById(R.id.spinner2);

        EditText mEditText = findViewById(R.id.editText);
        textView = findViewById(R.id.textView3);

        Button mButton = findViewById(R.id.button);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner2.setAdapter(adapter);

        mButton.setOnClickListener( (View v) -> mEditText.setText("0"));

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            //Utilizamos los listeners en los spinners para tambien convertir si se cambia la moneda.
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (text.equals("")) {
                    text = "0";
                }
                convert();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            //Utilizamos los listeners en los spinners para tambien convertir si se cambia la moneda.
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (text.equals("")) {
                    text = "0";
                }
                convert();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Variable booleana para conocer si el texto en el editor estaba vacio o no.
                if(mEditText.getText().toString().equals("")) {
                    wasEmpty = true;
                } else {
                    wasDecimal = mEditText.getText().toString().contains(".");
                    wasEmpty = false;
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            //Realizamos toda la operacion en vivo por medio de este listener.
            @Override
            public void afterTextChanged(Editable s) {
                //Declaramos el formato que utilizaremos para reformatear el editor de texto.
                DecimalFormat format = new DecimalFormat("#,###.###");

                //Declaramos variables que utilizaremos.
                String formatedNumber, formatedText;

                //Extraemos el string del editor de texto y lo limpiamos de "$ " y ","
                text = s.toString();
                text = text.replace("$ ", "");
                text = text.replaceAll(",", "");

                //Apagamos el listener para re-editar el texto y no encerrarnos en un bucle.
                mEditText.removeTextChangedListener(this);

                /*Si el editor de texto no se encontraba vacio y ahora si, formatear y mostrar un 0.
                Esto sirve para que no falle el reformato del editor de texto cuando este se borra
                por completo.*/
                if (!wasEmpty && text.equals("")) {
                    mEditText.setText(text);
                    mEditText.addTextChangedListener(this);
                    resText = String.format(getString(R.string.cash_symbol), 0.0);
                    textView.setText(resText);

                /*Si el editor de texto estaba vacio y luego tiene un ".", mostrar un 0.
                * Esto es necesario porque un punto no se puede operar como un numero de por si.*/
                } else if (wasEmpty && text.equals(".")) {
                    formatedText = "$ 0.";
                    mEditText.setText(formatedText);
                    mEditText.setSelection(mEditText.getText().length());
                    mEditText.addTextChangedListener(this);
                } else {

                    /*Si el numero en el editor de texto no era decimal y ahora contiene un punto,
                    activamos el listener y seguimos esperando entrada por el usuario.*/
                    if (!wasDecimal && text.contains(".")) {
                        mEditText.addTextChangedListener(this);

                    /*En todo caso contrario el numero es valido y procedemos a convertir.*/
                    } else {
                        convert();
                        formatedNumber = format.format(Double.valueOf(text));
                        formatedText = "$ " + formatedNumber;
                        mEditText.setText(formatedText);
                        mEditText.setSelection(mEditText.getText().length());
                        mEditText.addTextChangedListener(this);
                    }
                }
            }
        });
    }

    /*Funcion para gestionar como debe hacerse la conversion dependiendo de que monedas
    se encuentren seleccionadas en los spinners.*/
    public void convert() {
        if (mSpinner.getSelectedItem() == mSpinner2.getSelectedItem()) {
            showSame(text);
        } else if (mSpinner.getSelectedItem().equals("DOP") && mSpinner2.getSelectedItem().equals("USD")) {
            convertDopToUsd(text);
        } else if (mSpinner.getSelectedItem().equals("DOP") && mSpinner2.getSelectedItem().equals("EUR")) {
            convertDopToEur(text);
        } else if (mSpinner.getSelectedItem().equals("USD") && mSpinner2.getSelectedItem().equals("DOP")) {
            convertUsdToDop(text);
        } else if (mSpinner.getSelectedItem().equals("USD") && mSpinner2.getSelectedItem().equals("EUR")) {
            convertUsdToEur(text);
        } else if (mSpinner.getSelectedItem().equals("EUR") && mSpinner2.getSelectedItem().equals("DOP")) {
            convertEurToDop(text);
        } else if (mSpinner.getSelectedItem().equals("EUR") && mSpinner2.getSelectedItem().equals("USD")) {
            convertEurToUsd(text);
        }
    }

    //Funciones para las conversiones de monedas.
    public void showSame(String moneda) {
        //Hacemos un try para intentar la conversion.
        try {
            //Tomamos la entrada, la convertimos a double para aplicarle el formato final al String.
            resText = String.format(getString(R.string.cash_symbol), Double.parseDouble(moneda));
            //Lo mostramos en el textView.
            textView.setText(resText);
        //Si el editor de texto solo tiene un . al momento de la conversion, muestra 0. en vez de eso.
        } catch (NumberFormatException e) {
            showSame("0.");
        }
    }

    public void convertDopToUsd(String moneda) {
        double tasaDopToUsd = 49.30;
        try {
            resText = String.format(getString(R.string.cash_symbol), Double.parseDouble(moneda) / tasaDopToUsd);
            textView.setText(resText);
        } catch (NumberFormatException e) {
            showSame("0.");
        }
    }

    public void convertDopToEur(String moneda) {
        double tasaDopToEur = 59.51;
        try {
            resText = String.format(getString(R.string.cash_symbol), Double.parseDouble(moneda) / tasaDopToEur);
            textView.setText(resText);
        } catch (NumberFormatException e) {
            showSame("0.");
        }
    }

    public void convertUsdToDop(String moneda) {
        double tasaUsdToDop = 49.30;
        try {
            resText = String.format(getString(R.string.cash_symbol), Double.parseDouble(moneda) * tasaUsdToDop);
            textView.setText(resText);
        } catch (NumberFormatException e) {
            showSame("0.");
        }
    }

    public void convertUsdToEur(String moneda) {
        double tasaUsdToEur = 1.20;
        try {
            resText = String.format(getString(R.string.cash_symbol), Double.parseDouble(moneda) / tasaUsdToEur);
            textView.setText(resText);
        } catch (NumberFormatException e) {
            showSame("0.");
        }
    }

    public void convertEurToDop(String moneda) {
        double tasaEurToDop = 59.51;
        try {
            resText = String.format(getString(R.string.cash_symbol), Double.parseDouble(moneda) * tasaEurToDop);
            textView.setText(resText);
        } catch (NumberFormatException e) {
            showSame("0.");
        }
    }

    public void convertEurToUsd(String moneda) {
        double tasaEurToUsd = 1.20;
        try {
            resText = String.format(getString(R.string.cash_symbol), Double.parseDouble(moneda) * tasaEurToUsd);
            textView.setText(resText);
        } catch (NumberFormatException e) {
            showSame("0.");
        }
    }

}
