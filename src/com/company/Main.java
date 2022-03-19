package com.company;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Output output = new Output();
        output.print();
    }
}

/**
 * Reads user input: source base, target base and number to convert
 */
class UserInput {

    public String[] getBaseNumbers() {
        Scanner scn = new Scanner(System.in);
        System.out.print("\nEnter two numbers in format: {source base} {target base} (To quit type /exit) ");
        return scn.nextLine().split(" ");
    }

    public String[] getNumberToConvert(String[] bases) {
        Scanner scn = new Scanner(System.in);
        System.out.printf("\nEnter number in base %s to convert to base %s (To go back type /back) ", bases[0], bases[1]);
        return scn.nextLine().split("\\.");
    }
}

/**
 * Converts number to a desired base
 */
class NumberBaseConverter {

    public void convert(String[] inputBases, String[] inputNumber) {
        int source = Integer.parseInt(inputBases[0]);
        int target = Integer.parseInt(inputBases[1]);
        String result;

        if (inputNumber.length == 2) {  // check if it is decimal fraction
            result = convertDecimalFraction(source, target, inputNumber);
        } else {    // it is a whole number
            result = convertWholeNumber(source, target, inputNumber);
        }
        System.out.printf("Conversion result: %s\n", result);
    }

    public String convertDecimalFraction(int sourceBase, int targetBase, String[] number) {
        String integerPart = number[0];
        String fractionPart = number[1];


        // converts integer part to any base
        if (!integerPart.equals("0")) {     // if not 0 => convert, otherwise leave it as it was
            integerPart = new BigInteger(integerPart, sourceBase).toString(targetBase);
        }

        // check https://www.rapidtables.com/convert/number/base-converter.html?x=0.234&sel1=10&sel2=7
        // to understand this steps
        // converts fraction to 10 base first
        // For example, (0.xy)35 = (33 * 35^-1) + (34 * 35^-2),
        // where 33 is Character.digit() and 35 is Math.pow(sourceBase, raise)
        int raise = -1;
        double fractionDecimal = 0;
        for (int i = 0; i < fractionPart.length(); i++) { // reads every character of fraction part and converts it to decimal
            double decimal = Character.digit(fractionPart.charAt(i), sourceBase) * Math.pow(sourceBase, raise);
            fractionDecimal += decimal;
            raise--;
        }

        // converts decimal fraction to any base
        // For example, floor(0.97061224489795918367×176) = 23428220, where 23428220 is division
        // 23428220/17, where 17 is divisor
        BigDecimal division = new BigDecimal(String.valueOf((int) Math.floor(fractionDecimal * (Math.pow(targetBase, 5)))));
        BigDecimal divisor = new BigDecimal(String.valueOf(targetBase));
        BigDecimal[] divide;

        int count = 0;
        String remainder;
        StringBuilder convertFraction = new StringBuilder();

        // divide division no more than 5 times
        while (count < 5) {
            divide = division.divideAndRemainder(divisor); // store the result of division and remainder
            division = divide[0]; // redefine new division
            remainder = new BigInteger(String.valueOf(divide[1])).toString(targetBase); // convert remainder to target base
            convertFraction.append(remainder); // add conversion result to stringBuilder
            count++;
        }

        // gathering reversed fraction part
        fractionPart = "." + convertFraction.reverse(); // according to math rules of conversion remainders should be counted from the end to the top
        return integerPart.concat(fractionPart);
    }

    public String convertWholeNumber(int sourceBase, int targetBase, String[] number) {
        String wholeNumber = number[0];
        return new BigInteger(wholeNumber, sourceBase).toString(targetBase);
    }
}


/**
 * Outputs the result of conversion
 */
class Output {
    UserInput input = new UserInput();
    NumberBaseConverter converter = new NumberBaseConverter();

    public void print() {
        String[] bases = input.getBaseNumbers();
        while (!bases[0].equals("/exit")) {
            String[] number = input.getNumberToConvert(bases);
            while (!number[0].equals("/back")) {
                converter.convert(bases, number);
                number = input.getNumberToConvert(bases);
            }
            bases = input.getBaseNumbers();
        }
    }
}
