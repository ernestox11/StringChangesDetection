package com.identificador;

import com.identificador.testing.TestValidator;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        //--STRINGS USED FOR INDIVIDUAL TESTS
        //String originalString="Pues mira, entre quinientos cincuenta y seis ciento, cincuenta y cuarenta";
        //String transformedString="Pues mira, entre 556 150 190 y cuarenta";

        TestValidator validation = new TestValidator();

        //---INDIVIDUAL TEST
        //validation.singleValidation(originalString,transformedString);

        //---MASS TESTS
        validation.validate();
    }
}
