package com.identificador;

import com.identificador.testing.TestValidator;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        //--STRINGS USED FOR INDIVIDUAL TESTS
        String originalString="Las.Palabras% otra";
        String transformedString="Las palabras otra";

        TestValidator validation = new TestValidator();

        //---INDIVIDUAL TEST
        validation.singleValidation(originalString,transformedString);

        //---MASS TESTS
        //validation.validate();
    }
}
