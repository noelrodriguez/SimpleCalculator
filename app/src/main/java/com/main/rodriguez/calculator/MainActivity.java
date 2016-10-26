package com.main.rodriguez.calculator;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Hashtable;
import java.util.Stack;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    Hashtable<String, Integer> mostPrecendence = new Hashtable<>();
    Stack operators = new Stack();
    Stack values = new Stack();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set most precendence operators
        mostPrecendence.put("+",0);
        mostPrecendence.put("-",0);
        mostPrecendence.put("*",1);
        mostPrecendence.put("/",1);
        mostPrecendence.put("^",2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void clearInput(View view) {
        TextView userInput = (TextView)findViewById(R.id.textView1);
        Button clear = (Button)findViewById(R.id.button11);
        if (userInput.getText() != null) {
            userInput.setText("0");
            clear.setText("AC");
        }
    }

    public void addInput(View view) {
        // Variables
        TextView userInput = (TextView)findViewById(R.id.textView1);
        Button input = (Button)findViewById(view.getId());
        Button clear = (Button)findViewById(R.id.button11);

        // Enable any previously disabled operators
        enableOperators();

        // Check to see if open parenthesis was inserted
        if ("^/*-+()".indexOf(input.getText().charAt(input.getText().length()-1)) != -1){
            disableOperators();
        }

        if (userInput.length() == 1 && userInput.getText().toString().equals("0")){
            userInput.setText(input.getText());
            clear.setText("C");
        } else {
            userInput.append(input.getText());
        }
    }

    public void disableOperators() {
        findViewById(R.id.button13).setEnabled(false);
        findViewById(R.id.button14).setEnabled(false);
        findViewById(R.id.button15).setEnabled(false);
        findViewById(R.id.button16).setEnabled(false);
        findViewById(R.id.button17).setEnabled(false);
        findViewById(R.id.button18).setEnabled(false);
    }

    public void enableOperators() {
        findViewById(R.id.button13).setEnabled(true);
        findViewById(R.id.button14).setEnabled(true);
        findViewById(R.id.button15).setEnabled(true);
        findViewById(R.id.button16).setEnabled(true);
        findViewById(R.id.button17).setEnabled(true);
        findViewById(R.id.button18).setEnabled(true);
    }

    public void compute(View view) {
        TextView input = (TextView)findViewById(R.id.textView1);
        StringTokenizer stringTokenizer = new StringTokenizer(input.getText().toString(),"^/*-+()",true);
        String nextElement;

        while (stringTokenizer.hasMoreTokens()){
            nextElement = stringTokenizer.nextToken();
            if (isDigit(nextElement)){
                values.push(nextElement);
            } else if ("^/*-+(".indexOf(nextElement) != -1){
                operators.push(nextElement);
            } else if (nextElement.equals("(")){

            }
        }
    }

    public boolean isDigit(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // My testing equation is 3+9*(10-(5+2))-10/2=25
    public boolean checkPrecedence(String input) {
        if (mostPrecendence.get(operators.peek().toString()) > mostPrecendence.get(input)){

        }
        return false;
    }
}
