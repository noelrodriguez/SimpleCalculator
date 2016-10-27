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
    Stack<String> operators = new Stack();
    Stack<Double> values = new Stack();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set most precedence operators
        mostPrecendence.put("(",0);
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
        TextView fullInput = (TextView)findViewById(R.id.textView1);
        Button clear = (Button)findViewById(R.id.button11);
        if (fullInput.getText() != null) {
            fullInput.setText("0");
            clear.setText("AC");
        }
    }

    public void addInput(View view) {
        // Variables
        TextView fullInput = (TextView)findViewById(R.id.textView1);
        Button input = (Button)findViewById(view.getId());
        Button clear = (Button)findViewById(R.id.button11);

        // Enable any previously disabled operators
        enableOperators();

        // Check to see if open parenthesis was inserted
        /*if ("^/*-+()".indexOf(input.getText().charAt(input.getText().length()-1)) != -1){
            disableOperators();
        }*/

        // If first input, will set clear button to C and set text to the input
        // Else, appends the input
        if (fullInput.length() == 1 && fullInput.getText().toString().equals("0")){
            fullInput.setText(input.getText());
            clear.setText("C");
        } else {
            fullInput.append(input.getText());
        }
    }

    public void restrictingOperators(String input) {

    }

    // Disables corresponding operators
    public void disableOperators() {
        findViewById(R.id.button13).setEnabled(false);
        findViewById(R.id.button14).setEnabled(false);
        findViewById(R.id.button15).setEnabled(false);
        findViewById(R.id.button16).setEnabled(false);
        findViewById(R.id.button17).setEnabled(false);
    }

    // Enables corresponding operators
    public void enableOperators() {
        findViewById(R.id.button13).setEnabled(true);
        findViewById(R.id.button14).setEnabled(true);
        findViewById(R.id.button15).setEnabled(true);
        findViewById(R.id.button16).setEnabled(true);
        findViewById(R.id.button17).setEnabled(true);
        findViewById(R.id.button18).setEnabled(true);
    }

    // Computes the finalized input while checking for precedence of operators
    public void compute(View view) {
        // Variables
        TextView input = (TextView)findViewById(R.id.textView1);
        StringTokenizer stringTokenizer = new StringTokenizer(input.getText().toString(),"^/*-+()",true);
        String nextElement;
        String curOperator;
        double value1, value2;

        while (stringTokenizer.hasMoreTokens()){
            nextElement = stringTokenizer.nextToken();
            // If next token is an operator
            if ("+-*/^()".indexOf(nextElement) != -1){
                // If operator is left-parenthesis push to stack
                // (is of precedence 0 since any precedence operator can follow a parenthesis)
                if (nextElement.equals("(")){
                    operators.push(nextElement);
                // If operator is right-parenthesis peek and pop until reach left-parenthesis
                // (reason why left-parenthesis can have precedence of 0)
                } else if (nextElement.equals(")")){
                    while (!operators.peek().equals("(")){
                        curOperator = operators.pop();
                        value2 = values.pop();
                        value1 = values.pop();
                        values.push(mathCalculation(curOperator,value1,value2));
                    }
                    operators.pop();
                // If operator already in stack has higher precedence
                } else if (checkPrecedence(nextElement)){
                    curOperator = operators.pop();
                    value2 = values.pop();
                    value1 = values.pop();
                    values.push(mathCalculation(curOperator,value1,value2));
                    operators.push(nextElement);
                // Else operator on stack must have lower precedence
                } else {
                    operators.push(nextElement);
                }
            // Else next token must be a value
            } else {
                values.push(Double.parseDouble(nextElement));
            }
        }
        while (!operators.isEmpty()) {
            curOperator = operators.pop();
            value2 = values.pop();
            value1 = values.pop();
            values.push(mathCalculation(curOperator, value1, value2));
        }
        input.setText(String.format("%.2f",values.pop()));
    }

    // Technically don't need this since if not operator, must be value
    public boolean isDigit(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // My testing equation is 3+9*(10-(5+2))-10/2=25
    // Checks precedence of operator currently on stack to determine to push or not
    public boolean checkPrecedence(String input) {
        if (operators.isEmpty()) {
            return false;
        } else if (mostPrecendence.get(operators.peek()) > mostPrecendence.get(input)){
            return true;
        }
        return false;
    }

    // Does the actual mathematical calculation based on the operator
    public double mathCalculation(String operator, double val1, double val2) {
        double total;
        switch (operator) {
            case "+":
                total = val1+val2;
                break;
            case "-":
                total = val1-val2;
                break;
            case "*":
                total = val1*val2;
                break;
            case "/":
                total = val1/val2;
                break;
            case "^":
                total = Math.pow(val1,val2);
                break;
            default:
                total = 0;
                break;
        }
        return total;
    }
}
