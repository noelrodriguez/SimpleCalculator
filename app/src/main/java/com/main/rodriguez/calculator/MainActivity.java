package com.main.rodriguez.calculator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Stack;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    // Variables
    Hashtable<String, Integer> mostPrecedence = new Hashtable<>();
    Stack<String> operators = new Stack<>();
    Stack<Double> values = new Stack<>();
    boolean clearFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set most precedence operators
        mostPrecedence.put(getResources().getString(R.string.opNegative),0);
        mostPrecedence.put(getResources().getString(R.string.opLeftParenthesis),0);
        mostPrecedence.put(getResources().getString(R.string.opPlus),0);
        mostPrecedence.put(getResources().getString(R.string.opMinus),0);
        mostPrecedence.put(getResources().getString(R.string.opMultiplication),1);
        mostPrecedence.put(getResources().getString(R.string.opDivision),1);
        mostPrecedence.put(getResources().getString(R.string.opPower),2);
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
        TextView fullInput = (TextView)findViewById(R.id.inputTextView);
        Button clear = (Button)findViewById(R.id.clearButton);
        if (fullInput.getText() != null) {
            fullInput.setText("0");
            clear.setText(R.string.allClear);
            clearFlag = true;
            values.clear();
            operators.clear();
        }
    }

    public void deleteInput(View view) {
        TextView fullInput = (TextView)findViewById(R.id.inputTextView);
        Button clear = (Button)findViewById(R.id.clearButton);

        if (fullInput.getText().length() == 1 || fullInput.getText().toString().matches("[a-zA-Z\\s]+")){
            fullInput.setText("0");
            clear.setText(R.string.allClear);
            clearFlag = true;
        } else if (!clearFlag && fullInput.getText().length() > 1){
            fullInput.setText(fullInput.getText().subSequence(0,fullInput.getText().length()-1));
        }
    }

    // My testing equation is 3+9*(10-(5+2))-10/2=25
    // and 9+(2*6-1)
    public void addInput(View view) {
        // Variables
        TextView fullInput = (TextView)findViewById(R.id.inputTextView);
        Button input = (Button)findViewById(view.getId());
        Button clear = (Button)findViewById(R.id.clearButton);

        // If first input, will set clear button to C and set text to the input
        // Else, appends the input
        if (clearFlag || fullInput.getText().toString().matches("[a-zA-Z\\s]+")){
            fullInput.setText(input.getText());
            clear.setText(R.string.clear);
            clearFlag = false;
        } else {
            fullInput.append(input.getText());
        }
    }

    // Computes the finalized input while checking for precedence of operators
    public void compute(View view) {
        // Variables
        TextView input = (TextView)findViewById(R.id.inputTextView);
        StringTokenizer stringTokenizer = new StringTokenizer(input.getText().toString(),getResources().getString(R.string.operators),true);
        String nextElement, output;
        DecimalFormat df = new DecimalFormat("##.#####");

        try {
            while (stringTokenizer.hasMoreTokens()) {
                nextElement = stringTokenizer.nextToken();
                // If next token is an operator
                if (getResources().getString(R.string.operators).indexOf(nextElement) != -1) {
                    // If operator is left-parenthesis push to stack
                    // (is of precedence 0 since any precedence operator can follow a parenthesis)
                    if (nextElement.equals(getResources().getString(R.string.opLeftParenthesis)) ||
                            nextElement.equals(getResources().getString(R.string.opNegative))) {
                        operators.push(nextElement);
                        // If operator is right-parenthesis peek and pop until reach left-parenthesis
                        // (reason why left-parenthesis can have precedence of 0)
                    } else if (nextElement.equals(getResources().getString(R.string.opRightParenthesis))) {
                        while (!operators.peek().equals(getResources().getString(R.string.opLeftParenthesis))) {
                            values.push(mathCalculation());
                        }
                        operators.pop();
                        // If operator already in stack has higher precedence
                    } else if (checkPrecedence(nextElement)) {
                        values.push(mathCalculation());
                        operators.push(nextElement);
                        // Else operator on stack must have lower precedence
                    } else {
                        operators.push(nextElement);
                    }
                    // Else next token must be a value
                } else {
                    // Determines if negative sign was inserted to negate value
                        values.push(Double.parseDouble(nextElement));
                }
            }
            // Makes sure that if they don't enter any operators it just spits value back out
            if (!operators.isEmpty()) {
                while (!operators.isEmpty()) {
                    values.push(mathCalculation());
                }
                if (values.size() > 1)
                    throw new Exception();
            }
            output = df.format(values.pop());
            input.setText(output);
        // Catch any exception
        // Since math is correct, only exceptions being thrown will come from incorrect input
        } catch (Exception e){
            input.setText(R.string.invalidInput);
            values.clear();
            operators.clear();
        }
    }

    // Checks precedence of operator currently on stack to determine to push or not
    public boolean checkPrecedence(String input) {
        if (operators.isEmpty()) {
            return false;
        } else if (operators.peek().equals(getResources().getString(R.string.opLeftParenthesis))){
            return false;
        } else if (mostPrecedence.get(operators.peek()) >= mostPrecedence.get(input)){
            return true;
        }
        return false;
    }

    // Does the actual mathematical calculation based on the operator
    public double mathCalculation() throws Exception{
        try {
            double total, val1, val2;
            String operator = operators.pop();
            if (operator.equals(getResources().getString(R.string.opNegative))){
                val1 = values.pop();
                total = val1 * -1;
            } else {
                val2 = values.pop();
                val1 = values.pop();
                switch (operator) {
                    case "+":
                        total = val1 + val2;
                        break;
                    case "\u2212":
                        total = val1 - val2;
                        break;
                    case "*":
                        total = val1 * val2;
                        break;
                    case "/":
                        total = val1 / val2;
                        break;
                    case "^":
                        total = Math.pow(val1, val2);
                        break;
                    default:
                        total = 0;
                        break;
                }
            }
            return total;
        } catch (Exception e) {
            System.out.println("Inside computation throw exception");
            throw new Exception();
        }
    }
}
