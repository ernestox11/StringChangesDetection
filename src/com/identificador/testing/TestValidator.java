package com.identificador.testing;

import com.identificador.Identifier;
import com.identificador.Result;

import java.util.ArrayList;

public class TestValidator {
    ArrayList<TestSample> tests;
    int maxTransformations;
    float averageTransformations;

    private String generateTestString(String originalString, ArrayList<Result> changesDetected) {
        String originalCopy = originalString;
        String segment;
        StringBuilder generatedString = new StringBuilder();
        int changes = changesDetected.size();
        int startIndex, endIndex;

        for (int i = changes - 1; i >= 0; i--) {
            startIndex = changesDetected.get(i).getStartIndex();
            endIndex = changesDetected.get(i).getEndIndex();

            if (endIndex < originalCopy.length()) {
                segment = originalCopy.substring(endIndex + 1);
                generatedString.insert(0, segment);
            }
            generatedString.insert(0, changesDetected.get(i).getTransformedText());

            if (startIndex > 0) {
                originalCopy = originalCopy.substring(0, startIndex);
                if (i == 0) {
                    generatedString.insert(0, originalCopy);
                }
            }
        }
        //System.out.println("Generated test string: " + generatedString.toString());
        return generatedString.toString();
    }

    public void singleValidation(String o, String t) {
        Identifier identifier = new Identifier(o, t);
        ArrayList<Result> results = identifier.identifyChanges();

        //Display List with Changes
        if (results.size() > 0) {
            System.out.println("--- Original String ---");
            System.out.println(o);
            System.out.println("--- Transformed String ---");
            System.out.println(t);
            System.out.println("--- Detected transformations ---");
            for (int p = 0; p < results.size(); p++) {
                System.out.println(results.get(p).getResultString());
            }
        }
    }

    public void validate() {
        generateTestSamples();
        int invalidStrings = 0;
        int testsSize = this.tests.size();
        maxTransformations = 0;
        averageTransformations = 0.0f;
        String testString;
        Identifier identifier;

        for (int i = 0; i < testsSize; i++) {
            //Extracting values associated to the current change
            int nExpectedTransformations = tests.get(i).getnExpectedTransformations();
            String originalString = tests.get(i).getOriginal();
            String transformedString = tests.get(i).getTransformed();
            identifier = new Identifier(originalString, transformedString);
            ArrayList<Result> results = identifier.identifyChanges();
            //System.out.println(results.get(i).getEndIndex());

            //Generating a test string to compare against the real transformed string
            if (!results.isEmpty()) {
                testString = generateTestString(originalString, results);
                if (results.size() > maxTransformations) {
                    maxTransformations = results.size();
                }
            } else {
                testString = transformedString;
            }

            boolean areDifferent = !testString.equals(transformedString);
            boolean notExpectedSize = nExpectedTransformations != -1 && nExpectedTransformations != results.size();

            if (areDifferent || notExpectedSize) {
                System.out.println("------Error------");
                System.out.println("Original: " + tests.get(i).getOriginal() + "|");
                System.out.println("Expected: " + tests.get(i).getTransformed() + "|");
                if (areDifferent) {
                    System.out.println("Obtained: " + testString + "|\n");
                }
                if (notExpectedSize) {
                    System.out.println("Expected: " + nExpectedTransformations + ", Obtained: " + results.size() + "|\n");
                }

                invalidStrings++;
            } else {
                /*System.out.println("------VALID TRANSFORMATION!------");
                System.out.println("Original: " + tests.get(i).getOriginal() + "|");
                System.out.println("Expected: " + tests.get(i).getTransformed() + "|");
                System.out.println("Obtained: " + testString + "|\n");*/
            }
        }

        if (invalidStrings > 0) {
            System.out.println(invalidStrings + " out of " + testsSize + " strings are invalid!");
        } else {
            System.out.println("All " + testsSize + " tests passed!");
        }

        //--VALIDATION STATS
        int[] transformationsCount = new int[maxTransformations + 1];
        System.out.println("Maximum transformations: " + maxTransformations);

        for (int i = 0; i < testsSize; i++) {
            //Extracting values associated to the current change
            String originalString = tests.get(i).getOriginal();
            String transformedString = tests.get(i).getTransformed();
            identifier = new Identifier(originalString, transformedString);
            ArrayList<Result> results = identifier.identifyChanges();
            //System.out.println(results.get(i).getEndIndex());

            //Generating a test string to compare against the real transformed string
            if (!results.isEmpty()) {
                transformationsCount[results.size()]++;
            } else {
                transformationsCount[0]++;
            }
        }

        System.out.println("\n---TRANSFORMATION STATS---");
        for (int i = 0; i < transformationsCount.length; i++) {
            if (i != 1) {
                System.out.println(i + " transformations: " + transformationsCount[i] + " tests.");
            } else {
                System.out.println(i + " transformation: " + transformationsCount[i] + " tests.");
            }
            averageTransformations += (i * transformationsCount[i]);
        }
        averageTransformations /= testsSize;
        System.out.println("\nWeighted average of transformations: " + (float) Math.round(averageTransformations * 1000f) / 1000f);
    }


    public void generateTestSamples() {
        this.tests = new ArrayList<>();
        this.tests.add(new TestSample("Las.Palabras% otra", "Las palabras otra"));
        this.tests.add(new TestSample("Las.Palabras%", "Las palabras"));
        this.tests.add(new TestSample("Pues son quinientos doce mas trecientos trece si me pagas cuarenta y dos", "Pues son 512 mas 313 si me pagas 42", 3));
        this.tests.add(new TestSample("Tengo entre cinco y diez euros", "Tengo entre cinco y diez euros"));
        this.tests.add(new TestSample("Tengo entre cinco mil dos cientos cincuenta y diez mil quinientos euros", "Tengo entre cinco mil dos cientos cincuenta y diez mil quinientos euros"));
        this.tests.add(new TestSample("Tengo entre cinco mil dos cientos cincuenta euros y diez mil quinientos euros", "Tengo entre cinco mil dos cientos cincuenta euros y 10500 euros"));
        this.tests.add(new TestSample("Pues mira, entre quinientos cincuenta y seis ciento, cincuenta euros.", "Pues mira, entre 556 150 euros."));
        this.tests.add(new TestSample("Pues mira, entre quinientos cincuenta y seis cientos, cincuenta euros.", "Pues mira, entre 556 cientos, 50 euros."));
        this.tests.add(new TestSample("Estamos viendo el canal La Uno ok", "Estamos viendo el canal La Uno ok"));
        this.tests.add(new TestSample("Todo ocurrió en el siglo cinco, fue hace mucho tiempo", "Todo ocurrió en el siglo V, fue hace mucho tiempo"));
        this.tests.add(new TestSample("el numero es uno once dos tres cuatro cinco trece y otros mas", "el numero es 1 once 2 3 4 5 13 y otros mas"));
        this.tests.add(new TestSample("en vez de poner setenta sesenta y nueve colocó", "en vez de poner 70 69 colocó"));
        this.tests.add(new TestSample("María diez. El acceso", "María 10. El acceso"));
        this.tests.add(new TestSample("personas hace veintidos. años.", "personas hace 22 años."));
        this.tests.add(new TestSample("El, hombre con", "El hombre con"));
        this.tests.add(new TestSample("uno, dos y bueno ya termino", "uno, dos y bueno ya termino"));
        this.tests.add(new TestSample("ir Fue? Adonde dijo ella que iba a ir?", "ir Fue? Adonde dijo ella ¿que iba a ir?"));
        this.tests.add(new TestSample("treinta y cinco mil? Treinta y", "treinta y cinco mil? Treinta y"));
        this.tests.add(new TestSample("centro? No es un matiz No es un detalle. A", "centro? No es un matiz No es un detalle. A"));
        this.tests.add(new TestSample("El, hombre con", "El hombre con"));
        this.tests.add(new TestSample("Región. Buenas tardes. Normalidad en", "Región. Buenas tardes. Normalidad en"));
        this.tests.add(new TestSample("El, no A Gareth del No le hemos visto en el", "El no A Gareth del No le hemos visto en el"));
        this.tests.add(new TestSample("En. Una segunda fase en dos mil", "En una segunda fase en dos mil"));
        this.tests.add(new TestSample("Tengo once centimos", "Tengo once centimos"));
        this.tests.add(new TestSample("Tengo once punto dos euros", "Tengo 11.2 euros"));
        this.tests.add(new TestSample("Hay un once por ciento de descuento", "Hay un 11% de descuento"));
        this.tests.add(new TestSample("Hola, como, estas?", "Hola ¿como estas?"));
        this.tests.add(new TestSample("Hola, como estas?", "Hola ¿como estas?"));
        this.tests.add(new TestSample("Fomento. Para. La ejecución", "Fomento. Para la ejecución"));
        this.tests.add(new TestSample("Fomento Para. La ejecución", "Fomento Para. La ejecución"));
        this.tests.add(new TestSample("Para. La ejecución", "Para la ejecución"));
        this.tests.add(new TestSample("para. La ejecución", "para. La ejecución"));
        this.tests.add(new TestSample("Qué pasó en el año dos mil? Aca hay mas contenido", "¿Qué pasó en el año 2000? Aca hay mas contenido"));
        this.tests.add(new TestSample("Qué pasó en el año dos mil?", "¿Qué pasó en el año 2000?"));
        this.tests.add(new TestSample("Pudo suceder en el año mil novecientos?", "¿Pudo suceder en el año 1900?"));
        this.tests.add(new TestSample("Que edad tienes? Veinte?", "¿Que edad tienes? 20?"));
        this.tests.add(new TestSample("Una total de preguntas de diez Cual es tu edad?", "Una total de preguntas de 10 ¿Cual es tu edad?"));
        this.tests.add(new TestSample("Son solo cinco!. no hay mas", "Son solo 5!. no hay mas"));
        this.tests.add(new TestSample("Son solo cinco! no hay mas", "Son solo 5! no hay mas"));
        this.tests.add(new TestSample("Otra prueba cinco. tres. cuarenta. y.. seis.. fin", "Otra prueba 5. 3 46.. fin"));
        this.tests.add(new TestSample("Esta. es. una,. lista de..... simbolos , . cinco. . seis ,", "Esta es una,. lista de..... simbolos , . 5 . 6 ,"));
        this.tests.add(new TestSample("Esta es una lista de simbolos , . cinco . seis ,", "Esta es una lista de simbolos , . 5 . 6 ,"));
        this.tests.add(new TestSample("esta prueba y un por lo tanto por y lo tanto estoy divagando y por lo tanto", "esta prueba y un por lo tanto por y lo tanto estoy divagando y por lo tanto"));
        this.tests.add(new TestSample("tengo cinco en x y cinco en y por lo tanto", "tengo 5 en x y 5 en y por lo tanto"));
        this.tests.add(new TestSample("el quinientos cincuenta y dos y por son cuatro en resultado", "el 552 y por son 4 en resultado"));
        this.tests.add(new TestSample("el quinientos cincuenta y dos por son cuatro en resultado", "el 552 por son 4 en resultado"));
        this.tests.add(new TestSample("el quinientos cincuenta y dos por dos son cuatro en resultado", "el 552 por dos son 4 en resultado"));
        this.tests.add(new TestSample("deducción del cien por cien de los intereses de los", "deducción del 100% de los intereses de los"));
        this.tests.add(new TestSample("tengo cinco amigos", "tengo 5 amigos"));
        this.tests.add(new TestSample("dos y dos son cuatro", "dos y dos son cuatro"));
        this.tests.add(new TestSample("había una vez...", "había una vez..."));
        this.tests.add(new TestSample("tengo una manzana", "tengo una manzana"));
        this.tests.add(new TestSample("Hasta diciembre van a entrar. Treinta y una mil toneladas",
                "Hasta diciembre van a entrar. 31000 toneladas"));
        this.tests.add(
                new TestSample("de treinta con dos euros por tonelada. Algo para", "de 30,2 euros por tonelada. Algo para"));
        this.tests.add(
                new TestSample("K cuatro quinientos, donde España nunca obtuvo el", "K 4 500, donde España nunca obtuvo el"));
        this.tests.add(new TestSample("fresco entre los diez y los quince", "fresco entre los diez y los quince"));
        this.tests.add(new TestSample("La M seis cientos, siete en vista real y", "La M 607 en vista real y"));
        this.tests.add(new TestSample("uno, dos, tres, cuatro", "uno, dos, tres, cuatro"));
        this.tests.add(new TestSample("uno. dos millones de personas", "uno. dos millones de personas"));
        this.tests.add(new TestSample("uno. cinco por ciento de las personas en Madrid sufren de deficiencia de vitamina D",
                "uno. cinco por ciento de las personas en Madrid sufren de deficiencia de vitamina D"));
        this.tests.add(new TestSample("el numero de contacto es ocho cientos siete siete cinco dos tres cuatro",
                "el numero de contacto es ocho cientos siete siete cinco dos tres cuatro"));
        this.tests.add(new TestSample("quinientas, personas afectadas", "quinientas, personas afectadas"));
        this.tests.add(new TestSample("quinientas. Personas afectadas", "quinientas. Personas afectadas"));
        this.tests.add(new TestSample("un millon. Son las personas felices", "un millon. Son las personas felices"));
        this.tests.add(new TestSample("las ganancias se traducen en dos millones, cuatrocientos treinta ",
                "las ganancias se traducen en dos millones, cuatrocientos treinta "));
        this.tests.add(new TestSample("quinientas personas afectadas", "quinientas personas afectadas"));
        this.tests.add(new TestSample("     Espacios al principio", "     Espacios al principio"));
        this.tests.add(new TestSample("Espacios     en medio", "Espacios     en medio"));
        this.tests.add(new TestSample("Espacios al final     ", "Espacios al final     "));
        this.tests.add(new TestSample("Ulices es el cuarto hermano de Natasha", "Ulices es el cuarto hermano de Natasha"));
        this.tests
                .add(new TestSample("Ora dices que te gusta el negro, ora dices que te gusta el blanco: eres muy cambiante.",
                        "Ora dices que te gusta el negro, ora dices que te gusta el blanco: eres muy cambiante."));
        this.tests.add(new TestSample("El partido dura noventa minutos. En dos partes de cuarenta y cinco",
                "El partido dura noventa minutos. En dos partes de cuarenta y cinco"));
        this.tests.add(new TestSample(
                "La Universidad de Extremadura y el Ayuntamiento de Cáceres ponen en marcha una campaña de sesibilización",
                "La Universidad de Extremadura y el Ayuntamiento de Cáceres ponen en marcha una campaña de sesibilización"));
        this.tests.add(new TestSample("martes diez de septiembre.Titulares", "martes 10 de septiembre.Titulares"));
        this.tests.add(
                new TestSample("martes     diez de septiembre.     Titulares", "martes     10 de septiembre.     Titulares"));
        this.tests
                .add(new TestSample("el estadio Romano. Dos partidos amistosos", "el estadio Romano. Dos partidos amistosos"));
        this.tests.add(new TestSample("H O L A", "H O L A"));
        this.tests.add(new TestSample("Noventa y cinco", "Noventa y cinco"));
        this.tests.add(new TestSample("doscientos mil cien doscientos mil cien", "doscientos mil cien doscientos mil cien"));
        this.tests.add(new TestSample("doscientos un mil", "doscientos un mil"));
        this.tests.add(new TestSample("doscientos un mil doscientos y mil", "doscientos un mil doscientos y mil"));
        this.tests.add(new TestSample("cinco", "cinco"));
        this.tests.add(new TestSample("tecnología cinco G y una de las", "tecnología 5 G y una de las"));
        this.tests.add(new TestSample("para lo que tienen seis meses de plazo", "para lo que tienen 6 meses de plazo"));
        this.tests.add(new TestSample("Se trata de dejar espacio al cinco G", "Se trata de dejar espacio al 5 G"));
        this.tests.add(new TestSample("El Gobierno Central destinará ciento cuarenta y",
                "El Gobierno Central destinará ciento cuarenta y"));
        this.tests.add(new TestSample("cinco millones de euros en ayudas", "cinco millones de euros en ayudas"));
        this.tests.add(new TestSample("veinticuatro de julio titulares", "veinticuatro de julio titulares"));
        this.tests.add(new TestSample("niño de dos años sufrió un ahogamiento", "niño de dos años sufrió un ahogamiento"));
        this.tests.add(new TestSample("uno de sus protagonistas", "uno de sus protagonistas"));
        this.tests.add(
                new TestSample("Y ayer tarde hizo lo propio con otros dos", "Y ayer tarde hizo lo propio con otros dos"));
        this.tests.add(new TestSample("superar los cuarenta grados jornada con", "superar los 40 grados jornada con"));
        this.tests.add(new TestSample("Cinco Gene", "Cinco Gene"));
        this.tests.add(new TestSample("A partir de hoy noventa", "A partir de hoy noventa"));
        this.tests.add(new TestSample("y cinco municipios del norte de la", "y cinco municipios del norte de la"));
        this.tests.add(new TestSample("tendrán un plazo de seis meses", "tendrán un plazo de 6 meses"));
        this.tests.add(new TestSample("tecnología telefónica del cinco G se inicia en cuatro",
                "tecnología telefónica del cinco G se inicia en cuatro"));
        this.tests.add(new TestSample("esta semana dos de Baleares Mallorca", "esta semana dos de Baleares Mallorca"));
        this.tests
                .add(new TestSample("Central destinará ciento cuarenta y cinco millones", "Central destinará 145 millones"));
        this.tests.add(new TestSample("Veintidós", "Veintidós"));
        this.tests.add(new TestSample("llega a los treinta y", "llega a los treinta y"));
        this.tests.add(new TestSample("tres días la deuda comercial global", "tres días la deuda comercial global"));
        this.tests.add(new TestSample("de las comunidades autónomas asciende a cuatro mil cuatro",
                "de las comunidades autónomas asciende a cuatro mil cuatro"));
        this.tests.add(new TestSample("sesenta y ocho millones de euros", "sesenta y ocho millones de euros"));
        this.tests.add(new TestSample("en los ayuntamientos tardan hasta sesenta y un días de",
                "en los ayuntamientos tardan hasta 61 días de"));
        this.tests.add(
                new TestSample("cinco concejales en ese consistorio de no", "cinco concejales en ese consistorio de no"));
        this.tests.add(new TestSample("Barruecos en el dos mil y tiene que dimitir ipso facto",
                "Barruecos en el 2000 y tiene que dimitir ipso facto"));
        this.tests.add(new TestSample("Partido Socialista tiene cinco concejales", "Partido Socialista tiene 5 concejales"));
        this.tests.add(new TestSample("Condenado a siete", "Condenado a siete"));
        this.tests.add(new TestSample("al Ayuntamiento casi sesenta y dos mil euros", "al Ayuntamiento casi 62000 euros"));
        this.tests
                .add(new TestSample("Evoluciona favorablemente el niño de dos", "Evoluciona favorablemente el niño de dos"));
        this.tests.add(new TestSample("Dos incendios obligaron a activar el", "Dos incendios obligaron a activar el"));
        this.tests.add(new TestSample("nivel uno de peligrosidad", "nivel uno de peligrosidad"));
        this.tests.add(new TestSample("Treinta y dos", "Treinta y dos"));
        this.tests.add(new TestSample("chavales de seis grupos de viaje", "chavales de 6 grupos de viaje"));
        this.tests.add(new TestSample("trescientos euros", "trescientos euros"));
        this.tests.add(new TestSample("Tienen entre dieciocho y veinticuatro años", "Tienen entre 18 y 24 años"));
        this.tests.add(new TestSample("su plan de viaje incluyen Al menos dos", "su plan de viaje incluyen Al menos dos"));
        this.tests.add(new TestSample("El grupo tiene veintidós", "El grupo tiene veintidós"));
        this.tests.add(new TestSample("chavales de otros dieciocho países de todo el mundo",
                "chavales de otros 18 países de todo el mundo"));
        this.tests.add(new TestSample("dos de los autómatas que han competido por España en",
                "dos de los autómatas que han competido por España en"));
        this.tests.add(new TestSample("para estar allí participar y ganamos cinco Trofeo",
                "para estar allí participar y ganamos 5 Trofeo"));
        this.tests.add(new TestSample("y ha conseguido dieciocho", "y ha conseguido dieciocho"));
        this.tests.add(new TestSample("De los veinticinco alumnos de la", "De los 25 alumnos de la"));
        this.tests.add(
                new TestSample("clase de sólo ocho han tenido el permiso de", "clase de sólo 8 han tenido el permiso de"));
        this.tests.add(new TestSample("Es uno de los que", "Es uno de los que"));
        this.tests.add(new TestSample("los dos Conoceis la historia", "los dos Conoceis la historia"));
        this.tests.add(new TestSample("No uno", "No uno"));
        this.tests.add(new TestSample("uno de sus protagonistas en directo", "uno de sus protagonistas en directo"));
        this.tests.add(new TestSample("uno de los valores más importantes", "uno de los valores más importantes"));
        this.tests.add(new TestSample("Vamos siendo uno", "Vamos siendo uno"));
        this.tests.add(new TestSample("estamos empezamos cada uno en", "estamos empezamos cada uno en"));
        this.tests.add(new TestSample("En dos mil doce", "En dos mil doce"));
        this.tests.add(new TestSample("en el dos mil doce con él", "en el 2012 con él"));
        this.tests.add(
                new TestSample("siete años yo ya tenía ahora tengo sesenta", "siete años yo ya tenía ahora tengo sesenta"));
        this.tests.add(new TestSample("y dos Será tenía cincuenta y cinco", "y dos Será tenía cincuenta y cinco"));
        this.tests.add(new TestSample("no es lo que uno quiere el", "no es lo que uno quiere el"));
        this.tests.add(new TestSample("Han pasado siete años", "Han pasado 7 años"));
        this.tests.add(
                new TestSample("por israelí en España estuvo diez temporadas", "por israelí en España estuvo 10 temporadas"));
        this.tests.add(new TestSample("jugará en el equipo de Almendralejo dos temporada",
                "jugará en el equipo de Almendralejo dos temporada"));
        this.tests.add(new TestSample("este equipo luchar los noventa y", "este equipo luchar los noventa y"));
        this.tests.add(new TestSample("cinco minutos porque somos un equipo humilde",
                "cinco minutos porque somos un equipo humilde"));
        this.tests.add(new TestSample("habrá que pelearlo al noventa y cinco", "habrá que pelearlo al noventa y cinco"));
        this.tests.add(new TestSample("minutos noventa y cinco minutos peleando", "minutos 95 minutos peleando"));
        this.tests.add(new TestSample("proceden las dos nuevas", "proceden las dos nuevas"));
        this.tests.add(new TestSample("por cuatro temporadas encara trico como un jugador",
                "por 4 temporadas encara trico como un jugador"));
        this.tests.add(new TestSample("montenegrino A sus veintiocho años y con su uno noventa",
                "montenegrino A sus veintiocho años y con su uno noventa"));
        this.tests.add(new TestSample("y dos de altura", "y dos de altura"));
        this.tests.add(new TestSample("Es que repetimos máximas de cuarenta grados que",
                "Es que repetimos máximas de 40 grados que"));
        this.tests.add(new TestSample("valores entre los treinta y siete y", "valores entre los treinta y siete y"));
        this.tests.add(new TestSample("los cuarenta y un grados en el noreste y en general en los",
                "los 41 grados en el noreste y en general en los"));
        this.tests.add(new TestSample("Cuarenta y uno", "Cuarenta y uno"));
        this.tests.add(new TestSample("de vuelta aquí en la Uno después del", "de vuelta aquí en la Uno después del"));
        this.tests.add(
                new TestSample("dos y dos son cuatro cuatro y dos son seis", "dos y dos son cuatro cuatro y dos son seis"));
        this.tests.add(new TestSample("ayer compre dos mil vacas y cinco mil quinientos becerros",
                "ayer compre 2000 vacas y 5500 becerros"));
        this.tests.add(new TestSample("La prueba de seiscientos cincuenta y siete mil ciento doce",
                "La prueba de seiscientos cincuenta y siete mil ciento doce"));
        this.tests.add(
                new TestSample("La prueba de novecientos cincuenta y cuatro", "La prueba de novecientos cincuenta y cuatro"));
        this.tests.add(new TestSample("La prueba de novecientos noventa y nueve mil novecientos noventa y nueve",
                "La prueba de novecientos noventa y nueve mil novecientos noventa y nueve"));
        this.tests.add(new TestSample("Mi numero es seis cinco ocho quince cero ocho sesenta ext quinientos uno",
                "Mi numero es seis cinco ocho quince cero ocho sesenta ext quinientos uno"));
        this.tests.add(new TestSample("yo quiero tener un millon de amigos", "yo quiero tener un millon de amigos"));
        this.tests.add(new TestSample("en los años mil seiscientos", "en los años mil seiscientos"));
        this.tests.add(new TestSample("tengo dos mil veintitrés", "tengo dos mil veintitrés"));
        this.tests.add(new TestSample("Si tengo cinco manzanas y le doy cuatro y otras cuatro a pedro que me queda?",
                "Si tengo 5 manzanas y le doy 4 y otras 4 a pedro ¿que me queda?"));
        this.tests.add(new TestSample("descuento del cincuenta porciento", "descuento del 50 porciento"));
        this.tests.add(new TestSample("cinco grados de alcohol", "cinco grados de alcohol"));
        this.tests.add(new TestSample("el tráfico y establecer como vía alternativa la AP dos",
                "el tráfico y establecer como vía alternativa la AP dos"));
        this.tests.add(new TestSample(" dos en el eje", " dos en el eje"));
        this.tests.add(new TestSample("Mañana a las diez cuarenta. En", "Mañana a las 10 40. En"));
        this.tests.add(new TestSample("salida por la A dos. Un", "salida por la A dos. Un"));
        this.tests.add(new TestSample("las diez y diez. La paisana el", "las 10 y 10. La paisana el"));
        this.tests.add(new TestSample("más de ocho mil", "más de ocho mil"));
        this.tests.add(new TestSample("tres vehículos el sesenta y", "tres vehículos el sesenta y"));
        this.tests.add(new TestSample("treinta y tres vehículos el sesenta y cuatro por ciento",
                "treinta y tres vehículos el sesenta y cuatro por ciento"));
        this.tests.add(new TestSample("tres vehículos el sesenta y cuatro por ciento son",
                "tres vehículos el sesenta y cuatro por ciento son"));
        this.tests.add(new TestSample("cuatro por ciento", "cuatro por ciento"));
        this.tests.add(new TestSample("ha sido un peligro desde tres deberá yo", "ha sido un peligro desde 3 deberá yo"));
        this.tests.add(new TestSample("de dieciocho", "de dieciocho"));
        this.tests.add(new TestSample("diecinueve años era peligro", "diecinueve años era peligro"));
        this.tests.add(new TestSample("de hace treinta años", "de hace 30 años"));
        this.tests.add(new TestSample("treinta años", "treinta años"));
        this.tests.add(new TestSample("diecinueve años era peligro ya", "diecinueve años era peligro ya"));
        this.tests.add(new TestSample("son salidas de hace treinta años", "son salidas de hace 30 años"));
        this.tests.add(new TestSample("de cada tres condenas", "de cada 3 condenas"));
        this.tests.add(new TestSample(" En dos mil dieciocho hubo más", " En 2018 hubo más"));
        this.tests.add(new TestSample("de cada tres condenas", "de cada 3 condenas"));
        this.tests.add(new TestSample("de ochenta", "de ochenta"));
        this.tests.add(
                new TestSample("y nueve mil sentencias condenatorias casi", "y nueve mil sentencias condenatorias casi"));
        this.tests.add(new TestSample("un nueve por ciento más que el año anterior", "un 9% más que el año anterior"));
        this.tests.add(new TestSample("de cada tres condenas", "de cada 3 condenas"));
        this.tests.add(
                new TestSample(" En dos mil dieciocho hubo más de ochenta", " En dos mil dieciocho hubo más de ochenta"));
        this.tests.add(new TestSample("y nueve", "y nueve"));
        this.tests.add(new TestSample("mil sentencias condenatorias casi un nueve por ciento más",
                "mil sentencias condenatorias casi un nueve por ciento más"));
        this.tests.add(new TestSample("de cinco mil", "de cinco mil"));
        this.tests.add(
                new TestSample(" En mil doscientos de esos casos es su condena", " En 1200 de esos casos es su condena"));
        this.tests.add(new TestSample("hay en prisión más de cinco mil", "hay en prisión más de cinco mil"));
        this.tests.add(new TestSample(" En mil", " En mil"));
        this.tests.add(new TestSample("doscientos de esos casos es su condena principal",
                "doscientos de esos casos es su condena principal"));
        this.tests.add(new TestSample("aumentaron el año pasado un cincuenta y dos por ciento",
                "aumentaron el año pasado un cincuenta y dos por ciento"));
        this.tests.add(new TestSample("y dos por ciento", "y dos por ciento"));
        this.tests.add(new TestSample("Veintiséis y", "Veintiséis y"));
        this.tests.add(new TestSample("adicciones al juego aumentaron el año pasado un cincuenta y dos por ciento",
                "adicciones al juego aumentaron el año pasado un cincuenta y dos por ciento"));
        this.tests.add(new TestSample("cincuenta y dos por ciento", "cincuenta y dos por ciento"));
        this.tests.add(new TestSample("cuarenta y un años y deudas y créditos pendientes",
                "cuarenta y un años y deudas y créditos pendientes"));
        this.tests.add(new TestSample(" Cuarenta y uno por ciento de las", " Cuarenta y uno por ciento de las"));
        this.tests.add(new TestSample("Veintiséis y cuarenta y un años y deudas y créditos",
                "Veintiséis y cuarenta y un años y deudas y créditos"));
        this.tests.add(new TestSample(" En dos mil dieciocho eran nuevos casos", " En 2018 eran nuevos casos"));
        this.tests.add(new TestSample("lo que más crece un cincuenta y dos por ciento son los enganchados",
                "lo que más crece un 52% son los enganchados"));
        this.tests.add(new TestSample("ciento son los enganchados", "ciento son los enganchados"));
        this.tests.add(new TestSample(" Cuarenta y uno por", " Cuarenta y uno por"));
        this.tests.add(new TestSample(" Cuarenta y uno por ciento de las personas tratadas por adicciones",
                " Cuarenta y uno por ciento de las personas tratadas por adicciones"));
        this.tests.add(new TestSample("siete años", "siete años"));
        this.tests.add(new TestSample("ciento de las personas tratadas por adicciones",
                "ciento de las personas tratadas por adicciones"));
        this.tests.add(new TestSample(" En dos mil dieciocho eran nuevos casos", " En 2018 eran nuevos casos"));
        this.tests.add(new TestSample("lo que más crece un cincuenta", "lo que más crece un cincuenta"));
        this.tests.add(new TestSample("y dos por ciento son los", "y dos por ciento son los"));
        this.tests.add(new TestSample(" El y dos por ciento de los hombres demandan", " El y 2% de los hombres demandan"));
        this.tests.add(new TestSample("ciento de los hombres demandan", "ciento de los hombres demandan"));
        this.tests.add(
                new TestSample("Es una tendencia que comenzó hace siete años", "Es una tendencia que comenzó hace 7 años"));
        this.tests
                .add(new TestSample(" y videojuegos un veinticinco por ciento", " y videojuegos un veinticinco por ciento"));
        this.tests.add(
                new TestSample("En juego online el treinta y dos por ciento", "En juego online el treinta y dos por ciento"));
        this.tests.add(new TestSample("En juego la el crimen y dos por ciento de un sesenta y dos por ciento",
                "En juego la el crimen y dos por ciento de un sesenta y dos por ciento"));
        this.tests.add(new TestSample("ciento de", "ciento de"));
        this.tests
                .add(new TestSample(" y videojuegos un veinticinco por ciento", " y videojuegos un veinticinco por ciento"));
        this.tests.add(new TestSample("videojuegos un veinticinco", "videojuegos un veinticinco"));
        this.tests.add(new TestSample("por ciento en el caso de las", "por ciento en el caso de las"));
        this.tests.add(new TestSample(" un sesenta y dos por ciento", " un sesenta y dos por ciento"));
        this.tests.add(new TestSample("un nueve por ciento de los", "un 9% de los"));
        this.tests.add(new TestSample("primera vez un nueve por ciento de los", "primera vez un 9% de los"));
        this.tests.add(new TestSample("un nueve por ciento de los", "un 9% de los"));
        this.tests.add(new TestSample("tienen su primer contacto con las drogas entre los catorce y",
                "tienen su primer contacto con las drogas entre los catorce y"));
        this.tests.add(new TestSample("los diecisiete años", "los 17 años"));
        this.tests.add(new TestSample(" La policía ha detenido a nueve", " La policía ha detenido a nueve"));
        this.tests.add(new TestSample("a siete mujeres a las que obligaban", "a 7 mujeres a las que obligaban"));
        this.tests.add(new TestSample("contacto con las drogas entre los catorce y los diecisiete",
                "contacto con las drogas entre los catorce y los diecisiete"));
        this.tests.add(new TestSample("drogas entre los catorce y los diecisiete años", "drogas entre los 14 y los 17 años"));
        this.tests.add(new TestSample("detenido a nueve proxenetas y ha liberado", "detenido a 9 proxenetas y ha liberado"));
        this.tests.add(new TestSample("a siete mujeres", "a 7 mujeres"));
        this.tests.add(new TestSample("a siete mujeres a las que obligaban", "a 7 mujeres a las que obligaban"));
        this.tests.add(new TestSample("que cada treinta segundos una persona", "que cada 30 segundos una persona"));
        this.tests.add(new TestSample("cuarenta millones de personas", "cuarenta millones de personas"));
        this.tests.add(new TestSample("veintidós años es objetivo de", "veintidós años es objetivo de"));
        this.tests.add(new TestSample("Internacional del Trabajo sufren más de cuarenta",
                "Internacional del Trabajo sufren más de cuarenta"));
        this.tests.add(new TestSample("millones de personas", "millones de personas"));
        this.tests.add(new TestSample("trabajo y durante dos años le explotaron en una finca",
                "trabajo y durante dos años le explotaron en una finca"));
        this.tests.add(new TestSample(" tan sólo veintidós años es objetivo de", " tan sólo 22 años es objetivo de"));
        this.tests.add(new TestSample("Con tan sólo veintidós años es objetivo de", "Con tan sólo 22 años es objetivo de"));
        this.tests.add(new TestSample("los días treinta de la mañana a las tardes", "los días 30 de la mañana a las tardes"));
        this.tests.add(new TestSample("cuarenta millones de personas en situación de esclavitud",
                "cuarenta millones de personas en situación de esclavitud"));
        this.tests.add(new TestSample("treinta segundos", "treinta segundos"));
        this.tests.add(new TestSample(" Habrá tres personas más explotadas", " Habrá 3 personas más explotadas"));
        this.tests.add(new TestSample("Trabajo calcula que puede haber más de cuarenta millones",
                "Trabajo calcula que puede haber más de 40 millones"));
        this.tests.add(new TestSample("mundo cada treinta segundos", "mundo cada 30 segundos"));
        this.tests.add(new TestSample("entre veinte y veinticinco años", "entre 20 y 25 años"));
        this.tests.add(new TestSample("todo el mundo cada treinta segundos", "todo el mundo cada 30 segundos"));
        this.tests.add(new TestSample(" Habrá tres personas más", " Habrá 3 personas más"));
        this.tests.add(new TestSample("suelen ser hombres de entre veinte y", "suelen ser hombres de entre veinte y"));
        this.tests.add(new TestSample("veinticinco años de países de Europa del Este o",
                "veinticinco años de países de Europa del Este o"));
        this.tests.add(new TestSample(" Sólo el uno por ciento son rescatadas", " Sólo el 1% son rescatadas"));
        this.tests.add(new TestSample("Cinco Algunas madrileñas participarán a mediados de",
                "Cinco Algunas madrileñas participarán a mediados de"));
        this.tests.add(new TestSample(" Sólo el uno por ciento son", " Sólo el 1% son"));
        this.tests.add(new TestSample("Cinco", "Cinco"));
        this.tests.add(new TestSample("de más de cien países", "de más de 100 países"));
        this.tests.add(new TestSample(" Cinco chicas se han impuesto", " Cinco chicas se han impuesto"));
        this.tests.add(new TestSample("desarrollado cinco", "desarrollado cinco"));
        this.tests.add(new TestSample("alumnas de dieciséis años de un instituto de Móstoles",
                "alumnas de 16 años de un instituto de Móstoles"));
        this.tests.add(new TestSample("que la han desarrollado cinco", "que la han desarrollado cinco"));
        this.tests.add(new TestSample("alumnas de dieciséis años de", "alumnas de 16 años de"));
        this.tests.add(new TestSample("y dos mentores", "y dos mentores"));
        this.tests.add(new TestSample("cinco proyectos a mediados de", "cinco proyectos a mediados de"));
        this.tests.add(new TestSample("Los cinco", "Los cinco"));
        this.tests.add(new TestSample("Francisco ganaran quince", "Francisco ganaran quince"));
        this.tests.add(new TestSample("Francisco ganaran quince mil dólares para formación",
                "Francisco ganaran 15000 dólares para formación"));
        this.tests.add(new TestSample("mil hectáreas de campo Los", "mil hectáreas de campo Los"));
        this.tests.add(new TestSample("en dos mil hectáreas de campo Los", "en 2000 hectáreas de campo Los"));
        this.tests.add(new TestSample("Tres días", "Tres días"));
        this.tests.add(new TestSample("poder recibir agua dos mil hectáreas de regadío de las",
                "poder recibir agua 2000 hectáreas de regadío de las"));
        this.tests.add(
                new TestSample("producido también la inundación de ochenta", "producido también la inundación de ochenta"));
        this.tests.add(new TestSample("al sifón de Albelda dos mil", "al sifón de Albelda dos mil"));
        this.tests.add(new TestSample("en tres días", "en 3 días"));
        this.tests.add(new TestSample("espera que la reciban en tres días", "espera que la reciban en 3 días"));
        this.tests.add(new TestSample("dos actuaciones para restablecer", "dos actuaciones para restablecer"));
        this.tests.add(
                new TestSample("de ciento veintiséis kilómetros y se han cortado", "de 126 kilómetros y se han cortado"));
        this.tests.add(new TestSample("diez", "diez"));
        this.tests.add(new TestSample("canal tiene una longitud de ciento veintiséis kilómetros y",
                "canal tiene una longitud de 126 kilómetros y"));
        this.tests.add(new TestSample("se han cortado diez", "se han cortado diez"));
        this.tests.add(new TestSample("de cien mil euros", "de 100000 euros"));
        this.tests.add(new TestSample("una multa de cien", "una multa de cien"));
        this.tests.add(new TestSample("mil euros después de el año localizado", "mil euros después de el año localizado"));
        this.tests.add(new TestSample("apenas cuarenta y ocho", "apenas cuarenta y ocho"));
        this.tests.add(new TestSample("visto a uno solo", "visto a uno solo"));
        this.tests
                .add(new TestSample(" lo habitual hace apenas cuarenta y ocho", " lo habitual hace apenas cuarenta y ocho"));
        this.tests.add(new TestSample("apenas cuarenta y ocho horas", "apenas 48 horas"));
        this.tests.add(new TestSample("de tres cuerpos de la Guardia", "de 3 cuerpos de la Guardia"));
        this.tests.add(new TestSample("día prácticamente hemos visto a uno solo", "día prácticamente hemos visto a uno solo"));
        this.tests.add(
                new TestSample("de la Policía Portuaria durante doce horas", "de la Policía Portuaria durante 12 horas"));
        this.tests.add(new TestSample("Ahora si encontramos agentes de tres", "Ahora si encontramos agentes de tres"));
        this.tests.add(new TestSample("Portuaria durante doce horas ininterrumpidas cada día",
                "Portuaria durante 12 horas ininterrumpidas cada día"));
        this.tests.add(new TestSample("la Policía Portuaria durante doce horas ininterrumpidas",
                "la Policía Portuaria durante 12 horas ininterrumpidas"));
        this.tests.add(new TestSample(" y por cientos de años", " y por cientos de años"));
        this.tests.add(new TestSample("Reconoce", "Reconoce"));
        this.tests.add(new TestSample("Reconoce", "Reconoce"));
        this.tests.add(new TestSample("Buenas tardes", "Buenas tardes"));
        this.tests.add(new TestSample(" Se Avecina un nuevo mazazo", " Se Avecina un nuevo mazazo"));
        this.tests.add(new TestSample("(Música)", "(Música)"));
        this.tests.add(new TestSample("En", "En"));
        this.tests.add(new TestSample("a la banca", "a la banca"));
        this.tests.add(new TestSample(" El abogado", " El abogado"));
        this.tests.add(new TestSample("dejó hospitalario Albacete contará con", "dejó hospitalario Albacete contará con"));
        this.tests.add(
                new TestSample("el día de hoy ante este tribunal del jurado", "el día de hoy ante este tribunal del jurado"));
        this.tests.add(new TestSample("de la Unión Europea", "de la Unión Europea"));
        this.tests.add(new TestSample(" abre", " abre"));
        this.tests.add(new TestSample("noventa mil metros cuadrados", "noventa mil metros cuadrados"));
        this.tests.add(new TestSample(" casi", " casi"));
        this.tests.add(new TestSample("la puerta a", "la puerta a"));
        this.tests.add(new TestSample("la vida de ellos de obra nueva", "la vida de ellos de obra nueva"));
        this.tests.add(new TestSample("anular el IRPF", "anular el IRPF"));
        this.tests.add(new TestSample(" H El índice de Referencia de", " H El índice de Referencia de"));
        this.tests.add(new TestSample(" aumentará", " aumentará"));
        this.tests.add(new TestSample("El día veintisiete de febrero del dos mil dieciocho",
                "El día veintisiete de febrero del dos mil dieciocho"));
        this.tests.add(new TestSample("Préstamos Hipotecarios que los encarece", "Préstamos Hipotecarios que los encarece"));
        this.tests.add(new TestSample("el número de quirófanos y", "el número de quirófanos y"));
        this.tests.add(new TestSample("se potenciarán las", "se potenciarán las"));
        this.tests.add(new TestSample("Dio muerte", "Dio muerte"));
        this.tests.add(new TestSample("Lo considera abusivo y da la razón", "Lo considera abusivo y da la razón"));
        this.tests.add(new TestSample("unidades de cuidados críticos", "unidades de cuidados críticos"));
        this.tests.add(new TestSample(" Es lo que", " Es lo que"));
        this.tests.add(new TestSample("a los clientes", "a los clientes"));
        this.tests.add(new TestSample("contempla el Plan", "contempla el Plan"));
        this.tests.add(new TestSample("El", "El"));
        this.tests.add(new TestSample(" hijo menor de su pareja sentimental", " hijo menor de su pareja sentimental"));
        this.tests.add(new TestSample("Una opinión no vinculante pero que se suele tener en cuenta",
                "Una opinión no vinculante pero que se suele tener en cuenta"));
        this.tests.add(new TestSample("Funcional de estas obras que ha presentado esta mañana el",
                "Funcional de estas obras que ha presentado esta mañana el"));
        this.tests.add(new TestSample("presidente de Castilla", "presidente de Castilla"));
        this.tests.add(new TestSample(" para", " para"));
        this.tests.add(new TestSample("El pequeño de ocho años de edad", "El pequeño de 8 años de edad"));
        this.tests.add(new TestSample("Gabriel Cruz Ramírez", "Gabriel Cruz Ramírez"));
        this.tests.add(new TestSample(" sí", " sí"));
        this.tests.add(new TestSample("Informe favorable demasiado es pugnar", "Informe favorable demasiado es pugnar"));
        this.tests.add(new TestSample("Sí", "Sí"));
        this.tests.add(new TestSample(" Hola", " Hola"));
        this.tests.add(new TestSample("El abogado general de la Unión Europea para que",
                "El abogado general de la Unión Europea para que"));
        this.tests.add(new TestSample(" Buenas tardes", " Buenas tardes"));
        this.tests.add(new TestSample("se revoque por falta", "se revoque por falta"));
        this.tests.add(
                new TestSample("Este plan está dotado con ciento tres millones", "Este plan está dotado con 103 millones"));
        this.tests.add(new TestSample("de transparencia", "de transparencia"));
        this.tests.add(new TestSample(" la aplicación", " la aplicación"));
        this.tests.add(new TestSample("de euros", "de euros"));
        this.tests.add(
                new TestSample(" Está previsto que las obras empiecen en el", " Está previsto que las obras empiecen en el"));
        this.tests.add(new TestSample("Ana", "Ana"));
        this.tests.add(new TestSample("del Índice de Referencia", "del Índice de Referencia"));
        this.tests.add(new TestSample("primer trimestre", "primer trimestre"));
        this.tests.add(new TestSample(" El año que viene", " El año que viene"));
        this.tests.add(new TestSample("Julia Quezada", "Julia Quezada"));
        this.tests.add(new TestSample(" la", " la"));
        this.tests.add(new TestSample("de Préstamos Hipotecarios con el", "de Préstamos Hipotecarios con el"));
        this.tests.add(new TestSample("asesina confesa del niño Gabriel", "asesina confesa del niño Gabriel"));
        this.tests.add(new TestSample(" La banca", " La banca"));
        this.tests.add(
                new TestSample("Cruz ha reconocido esta mañana en el juicio", "Cruz ha reconocido esta mañana en el juicio"));
        this.tests.add(new TestSample("El proyecto de", "El proyecto de"));
        this.tests.add(new TestSample("española graba actualmente hipotecas por un valor de algo",
                "española graba actualmente hipotecas por un valor de algo"));
        this.tests.add(new TestSample("que mató", "que mató"));
        this.tests.add(new TestSample("reforma del Hospital General", "reforma del Hospital General"));
        this.tests.add(new TestSample("más de diecisiete mil millones de euros", "más de 17000 millones de euros"));
        this.tests.add(new TestSample(" afecta a", " afecta a"));
        this.tests.add(new TestSample("al pequeño", "al pequeño"));
        this.tests.add(new TestSample(" pero asegura que no tuvo intención y que fue",
                " pero asegura que no tuvo intención y que fue"));
        this.tests.add(new TestSample("Universitario de Albacete se", "Universitario de Albacete se"));
        this.tests.add(new TestSample("un", "un"));
        this.tests.add(new TestSample("accidental", "accidental"));
        this.tests.add(
                new TestSample("va a licitar y adjudicar antes de que acabe", "va a licitar y adjudicar antes de que acabe"));
        this.tests.add(new TestSample("millón de ciudadanos españoles y", "millón de ciudadanos españoles y"));
        this.tests.add(new TestSample(" según las entidades", " según las entidades"));
        this.tests.add(new TestSample("Por", "Por"));
        this.tests.add(new TestSample(" Ello", " Ello"));
        this.tests.add(new TestSample("el presente año es", "el presente año es"));
        this.tests.add(new TestSample("de afectados", "de afectados"));
        this.tests.add(new TestSample("se ha declarado", "se ha declarado"));
        this.tests.add(new TestSample("el plazo que maneja el Gobierno regional y lo",
                "el plazo que maneja el Gobierno regional y lo"));
        this.tests.add(new TestSample("supone una cantidad de entre dos", "supone una cantidad de entre dos"));
        this.tests.add(new TestSample("cientos cincuenta y tres", "cientos cincuenta y tres"));
        this.tests.add(new TestSample("inocente de la acusación", "inocente de la acusación"));
        this.tests.add(new TestSample("ha avanzado hoy", "ha avanzado hoy"));
        this.tests.add(new TestSample("cientos euros mensuales", "cientos euros mensuales"));
        this.tests.add(new TestSample(" abonados de más", " abonados de más"));
        this.tests.add(new TestSample("de asesinato", "de asesinato"));
        this.tests.add(new TestSample(" Fiscalía y", " Fiscalía y"));
        this.tests.add(new TestSample("el presidente en el stand de la Junta", "el presidente en el stand de la Junta"));
        this.tests.add(new TestSample("En cada hipoteca", "En cada hipoteca"));
        this.tests.add(new TestSample("acusación particular", "acusación particular"));
        this.tests.add(new TestSample(" Piden", " Piden"));
        this.tests.add(new TestSample("en el recinto", "en el recinto"));
        this.tests.add(new TestSample(" es decir", " es decir"));
        this.tests.add(new TestSample(" por el", " por el"));
        this.tests.add(new TestSample("para ella prisión permanente revisable es cómo se de",
                "para ella prisión permanente revisable es cómo se de"));
        this.tests.add(new TestSample("ferial supone la construcción en la práctica",
                "ferial supone la construcción en la práctica"));
        this.tests.add(
                new TestSample("pago de intereses superiores en dos puntos", "pago de intereses superiores en dos puntos"));
        this.tests.add(
                new TestSample("de un centro nuevo con cerca de setecientas", "de un centro nuevo con cerca de setecientas"));
        this.tests.add(new TestSample("a los del Euribor", "a los del Euribor"));
        this.tests.add(new TestSample(" la", " la"));
        this.tests.add(new TestSample("Qué tal", "Qué tal"));
        this.tests.add(new TestSample(" Están", " Están"));
        this.tests.add(new TestSample("camas", "camas"));
        this.tests.add(new TestSample("Unión de Consumidores de Extremadura", "Unión de Consumidores de Extremadura"));
        this.tests.add(new TestSample("Buenas tardes tras", "Buenas tardes tras"));
        this.tests.add(new TestSample("se duplicará el espacio disponible y el", "se duplicará el espacio disponible y el"));
        this.tests.add(new TestSample("celebra el informe y señala que se abre", "celebra el informe y señala que se abre"));
        this.tests.add(new TestSample("la acusada", "la acusada"));
        this.tests.add(new TestSample(" ha declarado", " ha declarado"));
        this.tests.add(new TestSample("número de quirófanos", "número de quirófanos"));
        this.tests
                .add(new TestSample("la puerta a bonificaciones al consumidor", "la puerta a bonificaciones al consumidor"));
        this.tests.add(new TestSample("su hija por", "su hija por"));
        this.tests.add(new TestSample("hasta alcanzar casi treinta", "hasta alcanzar casi treinta"));
        this.tests.add(new TestSample(" Las", " Las"));
        this.tests.add(new TestSample("Que va", "Que va"));
        this.tests.add(new TestSample("videoconferencia", "videoconferencia"));
        this.tests.add(new TestSample(" esta tarde", " esta tarde"));
        this.tests.add(new TestSample(" También van", " También van"));
        this.tests.add(new TestSample("unidades de críticos", "unidades de críticos"));
        this.tests.add(new TestSample("a ocurrir", "a ocurrir"));
        this.tests.add(new TestSample(" Se se declara", " Se se declara"));
        this.tests.add(new TestSample("a declarar como", "a declarar como"));
        this.tests.add(new TestSample("crecen hasta los sesenta y nueve puestos y", "crecen hasta los 69 puestos y"));
        this.tests.add(new TestSample("nulo ese índice", "nulo ese índice"));
        this.tests.add(new TestSample("testigos", "testigos"));
        this.tests.add(new TestSample(" Varios familiares de Gabriel", " Varios familiares de Gabriel"));
        this.tests.add(new TestSample(" entre ellos los", " entre ellos los"));
        this.tests.add(new TestSample("se construye un edificio de hospitales de día",
                "se construye un edificio de hospitales de día"));
        this.tests.add(new TestSample("de Sr", "de Sr"));
        this.tests.add(new TestSample(" PH Voy a", " PH Voy a"));
        this.tests.add(new TestSample("propios padres", "propios padres"));
        this.tests.add(new TestSample("diferenciados", "diferenciados"));
        this.tests.add(new TestSample(" Uno", " Uno"));
        this.tests.add(new TestSample("tener derecho a que me", "tener derecho a que me"));
        this.tests.add(new TestSample("Todos a", "Todos a"));
        this.tests.add(new TestSample("de ellos oncológico", "de ellos oncológico"));
        this.tests.add(new TestSample(" una", " una"));
        this.tests.add(new TestSample("lo modifiquen desde que", "lo modifiquen desde que"));
        this.tests.add(new TestSample("puerta cerrada", "puerta cerrada"));
        this.tests.add(new TestSample(" Cecilio Aranda", " Cecilio Aranda"));
        this.tests.add(new TestSample(" Buenas tardes", " Buenas tardes"));
        this.tests.add(new TestSample("de las mejores", "de las mejores"));
        this.tests.add(new TestSample("yo constituye la hipoteca y", "yo constituye la hipoteca y"));
        this.tests.add(new TestSample("plantas quirúrgicas del país", "plantas quirúrgicas del país"));
        this.tests.add(new TestSample(" Una atención de urgencia", " Una atención de urgencia"));
        this.tests.add(new TestSample("ahora me", "ahora me"));
        this.tests.add(new TestSample("verdaderamente extraordinaria y", "verdaderamente extraordinaria y"));
        this.tests.add(new TestSample(" sobre todo una atencion", " sobre todo una atencion"));
        this.tests.add(new TestSample("lo referencia al Euribor dará como consecuencia",
                "lo referencia al Euribor dará como consecuencia"));
        this.tests.add(new TestSample("la devolución de los importes que yo he pagado de más",
                "la devolución de los importes que yo he pagado de más"));
        this.tests.add(new TestSample("Buenas tardes", "Buenas tardes"));
        this.tests.add(new TestSample(" Van a declarar a", " Van a declarar a"));
        this.tests.add(new TestSample("A las enfermedades más", "A las enfermedades más"));
        this.tests.add(new TestSample("Se calcula que en seis", "Se calcula que en seis"));
        this.tests.add(new TestSample("puerta cerrada por expreso deseo de", "puerta cerrada por expreso deseo de"));
        this.tests.add(new TestSample("graves que probablemente van", "graves que probablemente van"));
        this.tests.add(
                new TestSample("meses se sabrá si el día de PH está sujeto", "meses se sabrá si el día de PH está sujeto"));
        this.tests.add(new TestSample("la familia", "la familia"));
        this.tests.add(new TestSample(" esta mañana", " esta mañana"));
        this.tests.add(new TestSample("a estar mejor residenciada en Albacete", "a estar mejor residenciada en Albacete"));
        this.tests.add(new TestSample("a la directiva", "a la directiva"));
        this.tests.add(new TestSample(" La primera en hacerlo era Ana", " La primera en hacerlo era Ana"));
        this.tests.add(new TestSample("que en casi ningún sitio en la región en la piel y en este",
                "que en casi ningún sitio en la región en la piel y en este"));
        this.tests.add(
                new TestSample("comunitaria sobre cláusulas abusivas desde", "comunitaria sobre cláusulas abusivas desde"));
        this.tests.add(new TestSample("Julia", "Julia"));
        this.tests.add(new TestSample("país", "país"));
        this.tests.add(new TestSample("la UCE", "la UCE"));
        this.tests.add(new TestSample(" señala", " señala"));
        this.tests.add(new TestSample(" no obstante", " no obstante"));
        this.tests.add(new TestSample("que estaba entre lágrimas pedía", "que estaba entre lágrimas pedía"));
        this.tests.add(new TestSample("que el consumidor", "que el consumidor"));
        this.tests.add(new TestSample("en varias ocasiones perdón a", "en varias ocasiones perdón a"));
        this.tests.add(new TestSample("El Hospital de Albacete será uno", "El Hospital de Albacete será uno"));
        this.tests.add(
                new TestSample("ya puede mover ficha que se puede empezar a", "ya puede mover ficha que se puede empezar a"));
        this.tests.add(new TestSample("los familiares de Gabriel por haber", "los familiares de Gabriel por haber"));
        this.tests.add(new TestSample("de los de referencia en", "de los de referencia en"));
        this.tests.add(new TestSample("reclamar ya pre- porque hay", "reclamar ya pre- porque hay"));
        this.tests.add(new TestSample("matado al pequeño insiste en que fue", "matado al pequeño insiste en que fue"));
        this.tests.add(new TestSample("toda España a un centro sanitario", "toda España a un centro sanitario"));
        this.tests.add(new TestSample("que presentar la reclamación extrajudicial y que",
                "que presentar la reclamación extrajudicial y que"));
        this.tests.add(new TestSample("un accidente", "un accidente"));
        this.tests.add(new TestSample(" pero", " pero"));
        this.tests.add(new TestSample("para los próximos cincuenta ó sesenta", "para los próximos cincuenta ó sesenta"));
        this.tests.add(new TestSample("por supuesto decir que nosotros desde", "por supuesto decir que nosotros desde"));
        this.tests.add(new TestSample("ha cambiado su versión sobre el día en el que",
                "ha cambiado su versión sobre el día en el que"));
        this.tests.add(new TestSample("años", "años"));
        this.tests.add(new TestSample(" Lo ha dicho", " Lo ha dicho"));
        this.tests.add(new TestSample("la Unión de Consumidores", "la Unión de Consumidores"));
        this.tests.add(new TestSample("fue detenida", "fue detenida"));
        this.tests.add(new TestSample("así el presidente de Castilla- La Mancha Emiliano",
                "así el presidente de Castilla- La Mancha Emiliano"));
        this.tests.add(new TestSample(" García", " García"));
        this.tests.add(new TestSample("vamos a hacer gratuitamente para", "vamos a hacer gratuitamente para"));
        this.tests.add(new TestSample(" Asegura que acudía a abdicar con la intención de",
                " Asegura que acudía a abdicar con la intención de"));
        this.tests.add(new TestSample("Page", "Page"));
        this.tests.add(new TestSample(" en la", " en la"));
        this.tests.add(new TestSample("que cualquier extremeño afectado pueda finalmente",
                "que cualquier extremeño afectado pueda finalmente"));
        this.tests.add(new TestSample("suicidarse", "suicidarse"));
        this.tests.add(new TestSample("presentación de la maqueta de ese proyecto tan",
                "presentación de la maqueta de ese proyecto tan"));
        this.tests.add(new TestSample("recuperar sus derechos", "recuperar sus derechos"));
        this.tests.add(new TestSample(" Otros cuatro testigos", " Otros 4 testigos"));
        this.tests.add(new TestSample("Ana Julia", "Ana Julia"));
        this.tests.add(new TestSample("costoso", "costoso"));
        this.tests.add(new TestSample("según García Page", "según García Page"));
        this.tests.add(new TestSample("entidades", "entidades"));
        this.tests.add(new TestSample("quien ha pedido no verla El juicio", "quien ha pedido no verla El juicio"));
        this.tests.add(new TestSample("Que se ha aprovechado para", "Que se ha aprovechado para"));
        this.tests.add(new TestSample("con más hipotecas formalizadas con", "con más hipotecas formalizadas con"));
        this.tests.add(new TestSample("por la tarde", "por la tarde"));
        this.tests.add(new TestSample("hacer", "hacer"));
        this.tests.add(new TestSample("este índice", "este índice"));
        this.tests.add(new TestSample("se ha reanudado ya con otras", "se ha reanudado ya con otras"));
        this.tests.add(new TestSample("un proyecto más ambicioso", "un proyecto más ambicioso"));
        this.tests.add(new TestSample(" El movimiento de obras", " El movimiento de obras"));
        this.tests.add(new TestSample("ahora cuestionado De confirmarse este varapalo a",
                "ahora cuestionado De confirmarse este varapalo a"));
        this.tests.add(new TestSample("dos declaraciones", "dos declaraciones"));
        this.tests.add(new TestSample("se pondrá en marcha", "se pondrá en marcha"));
        this.tests.add(new TestSample("la banca les supondría un", "la banca les supondría un"));
        this.tests.add(new TestSample(" Ahora mismo están declarando", " Ahora mismo están declarando"));
        this.tests.add(new TestSample("en el primer trimestre del próximo año", "en el primer trimestre del próximo año"));
        this.tests.add(new TestSample("coste entre los siete mil y los cuarenta y cuatro",
                "coste entre los siete mil y los cuarenta y cuatro"));
        this.tests.add(new TestSample("ya los familiares directos de", "ya los familiares directos de"));
        this.tests.add(new TestSample("mil millones de euros", "mil millones de euros"));
        this.tests.add(new TestSample(" según Goldman Sachs", " según Goldman Sachs"));
        this.tests.add(new TestSample("Gabriel a puerta cerrada", "Gabriel a puerta cerrada"));
        this.tests.add(new TestSample("sin fecha de finalización", "sin fecha de finalización"));
        this.tests.add(new TestSample(" (Música)", " (Música)"));
        this.tests.add(new TestSample("Con", "Con"));
        this.tests.add(new TestSample("La financiación autonómica centrada", "La financiación autonómica centrada"));
        this.tests.add(new TestSample("llanto entrecortado durante toda la declaración",
                "llanto entrecortado durante toda la declaración"));
        this.tests.add(new TestSample(" Quezada", " Quezada"));
        this.tests.add(new TestSample("este jueves el primer pleno", "este jueves el primer pleno"));
        this.tests.add(new TestSample("dice que es inocente", "dice que es inocente"));
        this.tests.add(new TestSample("La Comisión de", "La Comisión de"));
        this.tests.add(new TestSample("del nuevo ese periodo de sesiones", "del nuevo ese periodo de sesiones"));
        this.tests.add(new TestSample(" aunque reconoce que mató al pequeño", " aunque reconoce que mató al pequeño"));
        this.tests.add(new TestSample("Explotación del Trasvase Tajo Segura", "Explotación del Trasvase Tajo Segura"));
        this.tests.add(new TestSample("en la Asamblea", "en la Asamblea"));
        this.tests.add(new TestSample(" la vicepresidenta y", " la vicepresidenta y"));
        this.tests.add(new TestSample("ha autorizado un trasvase de", "ha autorizado un trasvase de"));
        this.tests.add(new TestSample("consejera de Hacienda", "consejera de Hacienda"));
        this.tests.add(new TestSample("Gabriel", "Gabriel"));
        this.tests.add(new TestSample(" Añade por accidente y yo simplemente", " Añade por accidente y yo simplemente"));
        this.tests.add(new TestSample("dieciséis", "dieciséis"));
        this.tests.add(new TestSample("Pilar", "Pilar"));
        this.tests.add(new TestSample(" Blanco", " Blanco"));
        this.tests.add(new TestSample(" Morales", " Morales"));
        this.tests.add(new TestSample("comparecerá a petición", "comparecerá a petición"));
        this.tests.add(new TestSample("le tape la boca", "le tape la boca"));
        this.tests.add(new TestSample("coma uno hectómetros cúbicos para el", "coma uno hectómetros cúbicos para el"));
        this.tests.add(new TestSample("propia", "propia"));
        this.tests.add(new TestSample(" Yo no quería", " Yo no quería"));
        this.tests.add(new TestSample("mes de septiembre", "mes de septiembre"));
        this.tests.add(new TestSample("A esta hora", "A esta hora"));
        this.tests.add(new TestSample(" Además", " Además"));
        this.tests.add(new TestSample("hacerle daño al niño e", "hacerle daño al niño e"));
        this.tests.add(new TestSample("a encontrarse los envases", "a encontrarse los envases"));
        this.tests.add(new TestSample(" comienza el pleno del Senado", " comienza el pleno del Senado"));
        this.tests.add(new TestSample(" que", " que"));
        this.tests.add(new TestSample("Yo", "Yo"));
        this.tests.add(new TestSample(" Sólo", " Sólo"));
        this.tests.add(new TestSample("de cabecera en situación excepcional", "de cabecera en situación excepcional"));
        this.tests.add(new TestSample("también abordará este asunto", "también abordará este asunto"));
        this.tests.add(new TestSample("quería que se callara al niño", "quería que se callara al niño"));
        this.tests
                .add(new TestSample(" Esta reunión ha contado por primera vez", " Esta reunión ha contado por primera vez"));
        this.tests.add(new TestSample("se han registrado", "se han registrado"));
        this.tests.add(new TestSample("tres preguntas al respecto para el", "tres preguntas al respecto para el"));
        this.tests.add(new TestSample("Quería matar al niño", "Quería matar al niño"));
        this.tests.add(new TestSample("Por la participación sin", "Por la participación sin"));
        this.tests.add(new TestSample("Gobierno y una interpelación a cargo del Partido Popular",
                "Gobierno y una interpelación a cargo del Partido Popular"));
        this.tests.add(new TestSample(" Yo no quería quitarle la vida", " Yo no quería quitarle la vida"));
        this.tests.add(new TestSample("voto de los municipios ribereños de Entrepeñas y",
                "voto de los municipios ribereños de Entrepeñas y"));
        this.tests.add(new TestSample("Buendía", "Buendía"));
        this.tests.add(new TestSample("Sobre los retrasos", "Sobre los retrasos"));
        this.tests.add(new TestSample("De los días de la", "De los días de la"));
        this.tests.add(new TestSample("de las entregas a cuenta a", "de las entregas a cuenta a"));
        this.tests.add(new TestSample("búsqueda", "búsqueda"));
        this.tests.add(new TestSample("Desde el pasado uno", "Desde el pasado uno"));
        this.tests.add(new TestSample("las regiones", "las regiones"));
        this.tests.add(new TestSample(" Los", " Los"));
        this.tests.add(new TestSample("sólo recuerda con claridad cuando dejó la camiseta del",
                "sólo recuerda con claridad cuando dejó la camiseta del"));
        this.tests.add(new TestSample("de octubre se han derivado dos", "de octubre se han derivado dos"));
        this.tests.add(new TestSample(" cientos", " cientos"));
        this.tests
                .add(new TestSample("grupos consideran que se está asfixiando", "grupos consideran que se está asfixiando"));
        this.tests.add(new TestSample("niño en la zona Vida y", "niño en la zona Vida y"));
        this.tests.add(
                new TestSample("noventa y siete hectómetros cúbicos de agua", "noventa y siete hectómetros cúbicos de agua"));
        this.tests.add(new TestSample("a las comunidades autónomas y", "a las comunidades autónomas y"));
        this.tests.add(new TestSample(" en el", " en el"));
        this.tests.add(new TestSample("yo toqué la camiseta y yo puse la camiseta Lleva",
                "yo toqué la camiseta y yo puse la camiseta Lleva"));
        this.tests.add(new TestSample("hacia el sureste", "hacia el sureste"));
        this.tests.add(new TestSample("caso de Extremadura calculan que ha dejado de recibir hasta",
                "caso de Extremadura calculan que ha dejado de recibir hasta"));
        this.tests.add(new TestSample("De ellos el sesenta y nueve por ciento", "De ellos el sesenta y nueve por ciento"));
        this.tests.add(new TestSample("dos cientos millones de euros", "dos cientos millones de euros"));
        this.tests.add(new TestSample("Porque yo quería que me encontrara", "Porque yo quería que me encontrara"));
        this.tests.add(new TestSample("ha sido para regadío", "ha sido para regadío"));
        this.tests.add(new TestSample("y el treinta y uno por ciento para consumo humano", "y el 31% para consumo humano"));
        this.tests.add(new TestSample("Los", "Los"));
        this.tests.add(new TestSample(" veinticuatro mil alumnos de", " veinticuatro mil alumnos de"));
        this.tests.add(new TestSample("Porque no podía", "Porque no podía"));
        this.tests.add(new TestSample("El Consejo de", "El Consejo de"));
        this.tests.add(new TestSample("la Universidad de Extremadura", "la Universidad de Extremadura"));
        this.tests.add(new TestSample("amar ya una contradicción", "amar ya una contradicción"));
        this.tests.add(new TestSample(" Antes había dicho", " Antes había dicho"));
        this.tests.add(new TestSample("Gobierno ha anunciado", "Gobierno ha anunciado"));
        this.tests.add(new TestSample("comienzan hoy las clases unos tres", "comienzan hoy las clases unos tres"));
        this.tests.add(new TestSample("que lo hizo para dar esperanzas", "que lo hizo para dar esperanzas"));
        this.tests.add(
                new TestSample("que presentará un recurso si hoy se aprueba", "que presentará un recurso si hoy se aprueba"));
        this.tests.add(new TestSample("mil quinientos Lo hacen por", "mil quinientos Lo hacen por"));
        this.tests.add(new TestSample("al padre", "al padre"));
        this.tests.add(new TestSample(" Además ha contado que el día de", " Además ha contado que el día de"));
        this.tests.add(new TestSample("un envío para regar porque considera", "un envío para regar porque considera"));
        this.tests.add(new TestSample("primera vez", "primera vez"));
        this.tests.add(new TestSample(" según el rector", " según el rector"));
        this.tests.add(new TestSample("su detención con el cuerpo del", "su detención con el cuerpo del"));
        this.tests.add(new TestSample("que no", "que no"));
        this.tests.add(new TestSample("se frena el descenso de estudiantes de los últimos",
                "se frena el descenso de estudiantes de los últimos"));
        this.tests.add(new TestSample("niño en el coche iba a dejarlo en", "niño en el coche iba a dejarlo en"));
        this.tests.add(new TestSample("se está dando prioridad", "se está dando prioridad"));
        this.tests.add(new TestSample("años", "años"));
        this.tests.add(new TestSample("el garaje", "el garaje"));
        this.tests.add(new TestSample(" Dedicar subir", " Dedicar subir"));
        this.tests.add(new TestSample("a la", "a la"));
        this.tests.add(new TestSample("Probablemente por la gratuidad de la matrícula",
                "Probablemente por la gratuidad de la matrícula"));
        this.tests.add(new TestSample("a casa", "a casa"));
        this.tests.add(new TestSample(" escribir cartas de perdón a su hija y su entonces",
                " escribir cartas de perdón a su hija y su entonces"));
        this.tests.add(new TestSample("cuenca cedente", "cuenca cedente"));
        this.tests.add(
                new TestSample(" Éste será el argumento de los recursos que", " Éste será el argumento de los recursos que"));
        this.tests.add(new TestSample("pareja y", "pareja y"));
        this.tests.add(new TestSample("se presenten a partir de ahora", "se presenten a partir de ahora"));
        this.tests.add(new TestSample("Primera clase de Física de primero de", "Primera clase de Física de primero de"));
        this.tests.add(new TestSample("Coger todo", "Coger todo"));
        this.tests.add(new TestSample("informática y", "informática y"));
        this.tests.add(new TestSample("el medicamento que llevaba y tomármelo y echarme en el",
                "el medicamento que llevaba y tomármelo y echarme en el"));
        this.tests.add(new TestSample("Pudiendo desembarazarse", "Pudiendo desembarazarse"));
        this.tests.add(new TestSample("de primero de telemática en el Campus", "de primero de telemática en el Campus"));
        this.tests.add(new TestSample("sofá", "sofá"));
        this.tests.add(new TestSample("cien hectómetros cúbicos más", "cien hectómetros cúbicos más"));
        this.tests.add(new TestSample("del Águila", "del Águila"));
        this.tests.add(new TestSample("Saber a", "Saber a"));
        this.tests.add(new TestSample("al Tajo", "al Tajo"));
        this.tests.add(new TestSample("en Mérida", "en Mérida"));
        this.tests.add(new TestSample(" Diferente", " Diferente"));
        this.tests.add(new TestSample("lo que yo iba", "lo que yo iba"));
        this.tests.add(new TestSample(" es una de las cuestiones", " es una de las cuestiones"));
        this.tests.add(new TestSample(" Los regantes del Tajo han", " Los regantes del Tajo han"));
        this.tests.add(new TestSample("temas independencia claramente", "temas independencia claramente"));
        this.tests.add(new TestSample(" pues", " pues"));
        this.tests.add(new TestSample("que más han llamado la atención y por esto le han",
                "que más han llamado la atención y por esto le han"));
        this.tests.add(new TestSample("tenido que", "tenido que"));
        this.tests.add(new TestSample("preguntado a la", "preguntado a la"));
        this.tests.add(new TestSample("dejar de utilizar agua para", "dejar de utilizar agua para"));
        this.tests.add(new TestSample("Pasa de cómo de niño", "Pasa de cómo de niño"));
        this.tests.add(new TestSample("salida a los abogados Es", "salida a los abogados Es"));
        this.tests.add(new TestSample("regadío al", "regadío al"));
        this.tests.add(new TestSample("a mayor", "a mayor"));
        this.tests.add(new TestSample("una llama la atención", "una llama la atención"));
        this.tests.add(new TestSample("mismo tiempo que se garantizaba el agua para el",
                "mismo tiempo que se garantizaba el agua para el"));
        this.tests.add(new TestSample("en un momento nada en muy poco tiempo", "en un momento nada en muy poco tiempo"));
        this.tests.add(new TestSample("la realitat nuevo", "la realitat nuevo"));
        this.tests.add(new TestSample(" Bueno", " Bueno"));
        this.tests.add(new TestSample("riego en el Levante", "riego en el Levante"));
        this.tests.add(new TestSample("Yo con expectativas", "Yo con expectativas"));
        this.tests.add(new TestSample("ha pasado un año", "ha pasado un año"));
        this.tests.add(new TestSample("de salida", "de salida"));
        this.tests.add(new TestSample(" aquí rapidito", " aquí rapidito"));
        this.tests.add(new TestSample("y medio podría intentarlo otra forma", "y medio podría intentarlo otra forma"));
        this.tests.add(new TestSample(" Creo", " Creo"));
        this.tests.add(new TestSample("Eso es absolutamente", "Eso es absolutamente"));
        this.tests
                .add(new TestSample("acabar la carrera en lo mínimo posible y", "acabar la carrera en lo mínimo posible y"));
        this.tests.add(new TestSample("que no se le", "que no se le"));
        this.tests.add(
                new TestSample("contradictorio con la normativa vigente en", "contradictorio con la normativa vigente en"));
        this.tests.add(new TestSample("entra en el mercado laboral en", "entra en el mercado laboral en"));
        this.tests.add(new TestSample("había preguntado antes exactamente sobre por qué iba",
                "había preguntado antes exactamente sobre por qué iba"));
        this.tests.add(new TestSample("materia de agua y de", "materia de agua y de"));
        this.tests.add(new TestSample("cuanto pueda", "cuanto pueda"));
        this.tests.add(new TestSample(" Llegaba tranquila porque más o menos", " Llegaba tranquila porque más o menos"));
        this.tests.add(new TestSample("a dedicar", "a dedicar"));
        this.tests.add(new TestSample(" pero", " pero"));
        this.tests.add(new TestSample("medio ambiente en nuestro país y en", "medio ambiente en nuestro país y en"));
        this.tests.add(new TestSample("conocí a mi compañero", "conocí a mi compañero"));
        this.tests.add(new TestSample("Eso es lo que", "Eso es lo que"));
        this.tests.add(new TestSample("la Unión", "la Unión"));
        this.tests.add(new TestSample("pero ahora con la presentación de la primera",
                "pero ahora con la presentación de la primera"));
        this.tests.add(new TestSample("ella manifiesta", "ella manifiesta"));
        this.tests.add(
                new TestSample("Europea El Ejecutivo espera que los nuevos", "Europea El Ejecutivo espera que los nuevos"));
        this.tests.add(new TestSample("clase de un poco más asustar", "clase de un poco más asustar"));
        this.tests.add(new TestSample(" verdad", " verdad"));
        this.tests.add(new TestSample(" Sí", " Sí"));
        this.tests.add(new TestSample(" sí", " sí"));
        this.tests.add(new TestSample(" Esta tarde", " Esta tarde"));
        this.tests.add(new TestSample(" declara los", " declara los"));
        this.tests.add(new TestSample("argumentos cambian el signo de las resoluciones judiciales",
                "argumentos cambian el signo de las resoluciones judiciales"));
        this.tests.add(new TestSample("padres de Gabriel", "padres de Gabriel"));
        this.tests.add(new TestSample("El", "El"));
        this.tests.add(new TestSample(" rector Antonio", " rector Antonio"));
        this.tests.add(new TestSample("a puerta cerrada", "a puerta cerrada"));
        this.tests.add(new TestSample("Ya que desde", "Ya que desde"));
        this.tests.add(new TestSample("Hidalgo habla de continuidad Destacan", "Hidalgo habla de continuidad Destacan"));
        this.tests.add(new TestSample(" La Junta de Andalucía investiga la muerte de",
                " La Junta de Andalucía investiga la muerte de"));
        this.tests.add(new TestSample("dos mil quince los tribunales", "dos mil quince los tribunales"));
        this.tests.add(new TestSample("las dos nuevas titulaciones y también", "las dos nuevas titulaciones y también"));
        this.tests.add(new TestSample("un hombre en", "un hombre en"));
        this.tests.add(new TestSample("han rechazado", "han rechazado"));
        this.tests.add(new TestSample("que se detiene", "que se detiene"));
        this.tests.add(new TestSample("Granada por una meningitis por", "Granada por una meningitis por"));
        this.tests.add(new TestSample("los veintiséis recursos presentados", "los 26 recursos presentados"));
        this.tests.add(new TestSample("el", "el"));
        this.tests.add(new TestSample("listeria", "listeria"));
        this.tests.add(new TestSample(" Hay que", " Hay que"));
        this.tests.add(new TestSample("y hoy se sentarán por primera", "y hoy se sentarán por primera"));
        this.tests.add(new TestSample("descenso de alumnos probablemente ha tenido su",
                "descenso de alumnos probablemente ha tenido su"));
        this.tests.add(new TestSample("ver si está vinculado o no con el resto de casos de este",
                "ver si está vinculado o no con el resto de casos de este"));
        this.tests.add(new TestSample("vez los", "vez los"));
        this.tests.add(new TestSample("efecto lo del efecto de la matrícula", "efecto lo del efecto de la matrícula"));
        this.tests.add(new TestSample("brote", "brote"));
        this.tests.add(new TestSample("municipios ribereños de Entrepeñas y Buendía en",
                "municipios ribereños de Entrepeñas y Buendía en"));
        this.tests.add(new TestSample("Lo", "Lo"));
        this.tests.add(new TestSample(" dice el último", " dice el último"));
        this.tests.add(new TestSample("la Comisión del Trasvase con voz", "la Comisión del Trasvase con voz"));
        this.tests.add(new TestSample("De la bonificación de la matrícula", "De la bonificación de la matrícula"));
        this.tests.add(new TestSample("parte de la Consejería", "parte de la Consejería"));
        this.tests.add(new TestSample("de Salud", "de Salud"));
        this.tests.add(new TestSample(" que", " que"));
        this.tests.add(new TestSample("Pero sin voto", "Pero sin voto"));
        this.tests.add(new TestSample(" Es bueno", " Es bueno"));
        this.tests.add(new TestSample("Si uno cumple", "Si uno cumple"));
        this.tests.add(new TestSample("además ha detectado un afectado", "además ha detectado un afectado"));
        this.tests.add(new TestSample("estar en", "estar en"));
        this.tests.add(new TestSample("las expectativas de", "las expectativas de"));
        this.tests.add(new TestSample("más Se trata de una mujer", "más Se trata de una mujer"));
        this.tests.add(new TestSample("los órganos", "los órganos"));
        this.tests.add(new TestSample("aprobar en primera convocatoria son una", "aprobar en primera convocatoria son una"));
        this.tests.add(new TestSample("que comió la carne", "que comió la carne"));
        this.tests.add(new TestSample(" La mecha", " La mecha"));
        this.tests.add(new TestSample("de", "de"));
        this.tests.add(new TestSample("parte de los veinticuatro mil alumnos que abren el",
                "parte de los 24000 alumnos que abren el"));
        this.tests.add(new TestSample("el cuatro de agosto y que ha tenido un niño que está en",
                "el 4 de agosto y que ha tenido un niño que está en"));
        this.tests.add(new TestSample("decisión", "decisión"));
        this.tests.add(new TestSample(" La", " La"));
        this.tests.add(new TestSample("curso en la", "curso en la"));
        this.tests.add(new TestSample("observación", "observación"));
        this.tests.add(new TestSample("Mancomunidad de Municipios", "Mancomunidad de Municipios"));
        this.tests.add(new TestSample("Universidad de Extremadura en", "Universidad de Extremadura en"));
        this.tests.add(new TestSample("ribereños entra con voz pero sin voto", "ribereños entra con voz pero sin voto"));
        this.tests.add(new TestSample("los campus de Badajoz", "los campus de Badajoz"));
        this.tests.add(new TestSample(" Cáceres", " Cáceres"));
        this.tests.add(new TestSample("Son ya dos", "Son ya dos"));
        this.tests.add(new TestSample(" cientos", " cientos"));
        this.tests.add(new TestSample(" Once", " Once"));
        this.tests.add(new TestSample(" Los enfermos", " Los enfermos"));
        this.tests.add(new TestSample(" Y bueno", " Y bueno"));
        this.tests.add(new TestSample(" agradecemos en", " agradecemos en"));
        this.tests.add(new TestSample(" Mérida y Plasencia", " Mérida y Plasencia"));
        this.tests.add(new TestSample("en Andalucía por el", "en Andalucía por el"));
        this.tests.add(new TestSample("la decisión del Ministerio este", "la decisión del Ministerio este"));
        this.tests.add(new TestSample("son diecinueve centros y nueve institutos de investigación",
                "son 19 centros y 9 institutos de investigación"));
        this.tests.add(new TestSample("brote", "brote"));
        this.tests.add(new TestSample(" Treinta y siete", " Treinta y siete"));
        this.tests.add(new TestSample("respecto", "respecto"));
        this.tests.add(new TestSample(" Creo que es insisto", " Creo que es insisto"));
        this.tests.add(new TestSample(" muy positivo", " muy positivo"));
        this.tests.add(new TestSample("pero es un paso", "pero es un paso"));
        this.tests.add(new TestSample("personas siguen ingresadas", "personas siguen ingresadas"));
        this.tests.add(new TestSample(" Dieciséis son mujeres", " Dieciséis son mujeres"));
        this.tests.add(new TestSample("La UE ofrece", "La UE ofrece"));
        this.tests.add(new TestSample("embarazadas", "embarazadas"));
        this.tests.add(new TestSample("más de setenta grados impartidos por más", "más de 70 grados impartidos por más"));
        this.tests.add(new TestSample("Muy insuficiente", "Muy insuficiente"));
        this.tests.add(new TestSample("de dos mil", "de dos mil"));
        this.tests.add(new TestSample("La crónica política en el día en el que", "La crónica política en el día en el que"));
        this.tests.add(new TestSample("para resolver los problemas que tenemos en Castilla",
                "para resolver los problemas que tenemos en Castilla"));
        this.tests.add(new TestSample("profesores", "profesores"));
        this.tests.add(new TestSample(" El presupuesto", " El presupuesto"));
        this.tests.add(new TestSample("han vuelto a", "han vuelto a"));
        this.tests.add(new TestSample("La Mancha con los", "La Mancha con los"));
        this.tests.add(new TestSample("supera los ciento sesenta millones", "supera los 160 millones"));
        this.tests.add(new TestSample("reunirse peso y Podemos", "reunirse peso y Podemos"));
        this.tests.add(new TestSample("trasvases que", "trasvases que"));
        this.tests.add(new TestSample("de euros", "de euros"));
        this.tests.add(
                new TestSample("para tratar de desbloquear la investidura", "para tratar de desbloquear la investidura"));
        this.tests.add(new TestSample("se vienen", "se vienen"));
        this.tests.add(new TestSample("La huella recuerda que las", "La huella recuerda que las"));
        this.tests.add(new TestSample("de Pedro Sánchez", "de Pedro Sánchez"));
        this.tests.add(new TestSample("aprobando periódicamente cada mes ante", "aprobando periódicamente cada mes ante"));
        this.tests.add(new TestSample("novatadas están prohibidas", "novatadas están prohibidas"));
        this.tests.add(new TestSample(" al parecer", " al parecer"));
        this.tests.add(new TestSample(" sin acuerdo", " sin acuerdo"));
        this.tests.add(new TestSample("la escasa superficie encharcada en las", "la escasa superficie encharcada en las"));
        this.tests.add(new TestSample("e invita a víctimas", "e invita a víctimas"));
        this.tests.add(new TestSample(" Los dos partidos", " Los dos partidos"));
        this.tests.add(new TestSample("Tablas de Daimiel", "Tablas de Daimiel"));
        this.tests.add(new TestSample("y testigos", "y testigos"));
        this.tests.add(new TestSample("defienden desde Andalucía la", "defienden desde Andalucía la"));
        this.tests.add(new TestSample(" La Comisión también estudiará un envío", " La Comisión también estudiará un envío"));
        this.tests.add(new TestSample("a denunciar las", "a denunciar las"));
        this.tests.add(new TestSample(" Yo creo que en algunos sitios se pasan", " Yo creo que en algunos sitios se pasan"));
        this.tests.add(new TestSample("necesidad repiten de que", "necesidad repiten de que"));
        this.tests.add(new TestSample("de emergencia a este parque nacional", "de emergencia a este parque nacional"));
        this.tests.add(new TestSample("mucho", "mucho"));
        this.tests.add(new TestSample(" Yo tengo amigos", " Yo tengo amigos"));
        this.tests.add(new TestSample("haya un Gobierno cuanto antes", "haya un Gobierno cuanto antes"));
        this.tests.add(new TestSample(" En el caso", " En el caso"));
        this.tests.add(new TestSample("que han que hicieron", "que han que hicieron"));
        this.tests.add(new TestSample("de la formación", "de la formación"));
        this.tests.add(new TestSample("marcha el primer año el año pasado y lo han pasado muy",
                "marcha el primer año el año pasado y lo han pasado muy"));
        this.tests.add(new TestSample("morada", "morada"));
        this.tests.add(new TestSample(" con matices", " con matices"));
        this.tests.add(new TestSample(" Desde", " Desde"));
        this.tests.add(new TestSample("Los tres grupos", "Los 3 grupos"));
        this.tests.add(new TestSample("mal", "mal"));
        this.tests.add(new TestSample(" Soy yo", " Soy yo"));
        this.tests.add(new TestSample("el Partido Popular reprochan a", "el Partido Popular reprochan a"));
        this.tests.add(new TestSample("parlamentarios de las Cortes de", "parlamentarios de las Cortes de"));
        this.tests.add(new TestSample("Pienso que en algunos sitios", "Pienso que en algunos sitios"));
        this.tests.add(new TestSample("Pedro Sánchez la incapacidad", "Pedro Sánchez la incapacidad"));
        this.tests.add(new TestSample(" según ellos", " según ellos"));
        this.tests.add(new TestSample(" para Llegar a", " para Llegar a"));
        this.tests.add(new TestSample("Castilla-La Mancha han presentado una proposición no",
                "Castilla-La Mancha han presentado una proposición no"));
        this.tests.add(new TestSample("se pasa entre pueblo", "se pasa entre pueblo"));
        this.tests.add(new TestSample("acuerdos", "acuerdos"));
        this.tests.add(new TestSample("de ley en la que manifiestan su apoyo", "de ley en la que manifiestan su apoyo"));
        this.tests.add(new TestSample("En fin", "En fin"));
        this.tests.add(new TestSample("a los pueblos ribereños por su", "a los pueblos ribereños por su"));
        this.tests.add(new TestSample(" es una cosa que si puedes evitarla incluso mejor",
                " es una cosa que si puedes evitarla incluso mejor"));
        this.tests.add(new TestSample("presencia en la", "presencia en la"));
        this.tests.add(new TestSample("Desde todos los frentes", "Desde todos los frentes"));
        this.tests.add(new TestSample("Comisión del Trasvase Tajo Segura", "Comisión del Trasvase Tajo Segura"));
        this.tests.add(new TestSample(" Las decisiones", " Las decisiones"));
        this.tests.add(new TestSample("En", "En"));
        this.tests.add(new TestSample(" La Universidad de", " La Universidad de"));
        this.tests.add(new TestSample("se apunta la necesidad de que", "se apunta la necesidad de que"));
        this.tests.add(new TestSample("que se toman en ella", "que se toman en ella"));
        this.tests.add(new TestSample("dicen desde el PSOE", "dicen desde el PSOE"));
        this.tests.add(new TestSample("Extremadura están convencidos de", "Extremadura están convencidos de"));
        this.tests.add(new TestSample("se forme gobierno", "se forme gobierno"));
        this.tests.add(new TestSample(" pero los matices de unos", " pero los matices de unos"));
        this.tests.add(new TestSample("que otro tipo de bienvenida es posible", "que otro tipo de bienvenida es posible"));
        this.tests.add(new TestSample("y otros", "y otros"));
        this.tests.add(new TestSample("Repercuten directamente en la vida de esos municipios",
                "Repercuten directamente en la vida de esos municipios"));
        this.tests.add(new TestSample("son importantes para peso y Podemos", "son importantes para peso y Podemos"));
        this.tests.add(new TestSample(" Lo más urgente es frenar a la derecha", " Lo más urgente es frenar a la derecha"));
        this.tests.add(new TestSample(" Nosotros", " Nosotros"));
        this.tests.add(new TestSample("La vuelta de los universitarios a", "La vuelta de los universitarios a"));
        this.tests.add(new TestSample("Se", "Se"));
        this.tests.add(new TestSample("las aulas", "las aulas"));
        this.tests.add(new TestSample(" Imágenes", " Imágenes"));
        this.tests.add(new TestSample("apoya la", "apoya la"));
        this.tests.add(new TestSample("como ésta se repetirán en", "como ésta se repetirán en"));
        this.tests.add(new TestSample("presencia de los municipios", "presencia de los municipios"));
        this.tests.add(new TestSample("los campos extremeños Son las novatadas", "los campos extremeños Son las novatadas"));
        this.tests.add(new TestSample("Creemos que no podemos arriesgarnos a reeditar",
                "Creemos que no podemos arriesgarnos a reeditar"));
        this.tests.add(new TestSample("ribereños en", "ribereños en"));
        this.tests.add(new TestSample("de los estudiantes veteranos a", "de los estudiantes veteranos a"));
        this.tests.add(new TestSample("Un try facilitó a nivel estatal", "Un try facilitó a nivel estatal"));
        this.tests.add(new TestSample("la", "la"));
        this.tests.add(new TestSample("los de nuevo ingreso", "los de nuevo ingreso"));
        this.tests.add(new TestSample("Comisión del Trasvase y", "Comisión del Trasvase y"));
        this.tests.add(new TestSample("El Rectorado las prohíbe", "El Rectorado las prohíbe"));
        this.tests.add(new TestSample(" pero", " pero"));
        this.tests.add(new TestSample("además se pide que sea su presencia real y efectiva",
                "además se pide que sea su presencia real y efectiva"));
        this.tests.add(new TestSample("se mantienen", "se mantienen"));
        this.tests.add(new TestSample(" Así que", " Así que"));
        this.tests.add(new TestSample(" a", " a"));
        this.tests.add(new TestSample("Nosotros no queremos política", "Nosotros no queremos política"));
        this.tests.add(new TestSample("través de campañas", "través de campañas"));
        this.tests.add(new TestSample("de derecha en Andalucía", "de derecha en Andalucía"));
        this.tests.add(new TestSample("de sensibilización esperan que", "de sensibilización esperan que"));
        this.tests.add(new TestSample("pero tampoco la queremos en España", "pero tampoco la queremos en España"));
        this.tests.add(new TestSample("Y eso yo creo", "Y eso yo creo"));
        this.tests.add(new TestSample("esa bienvenida sea respetuosa", "esa bienvenida sea respetuosa"));
        this.tests.add(new TestSample(" Yo espero que", " Yo espero que"));
        this.tests.add(new TestSample("es importante", "es importante"));
        this.tests.add(new TestSample("en Cáceres", "en Cáceres"));
        this.tests.add(new TestSample(" la universidad", " la universidad"));
        this.tests.add(new TestSample("unos y otros recapaciten y", "unos y otros recapaciten y"));
        this.tests.add(new TestSample("y al mismo tiempo solicitamos", "y al mismo tiempo solicitamos"));
        this.tests.add(new TestSample(" tal y", " tal y"));
        this.tests.add(new TestSample("y el Ayuntamiento lucharán para que sean libres de",
                "y el Ayuntamiento lucharán para que sean libres de"));
        this.tests.add(new TestSample("dejen formar un gobierno en este país", "dejen formar un gobierno en este país"));
        this.tests.add(new TestSample(" Lo dejé Pablo", " Lo dejé Pablo"));
        this.tests.add(new TestSample("como se ha acordado con", "como se ha acordado con"));
        this.tests.add(new TestSample("conductas sexistas", "conductas sexistas"));
        this.tests.add(new TestSample("y no lo", "y no lo"));
        this.tests.add(new TestSample("el presidente de Castilla-La", "el presidente de Castilla-La"));
        this.tests.add(new TestSample("Para", "Para"));
        this.tests.add(new TestSample("cometa El error por tercera vez de impedir un presidente",
                "cometa El error por tercera vez de impedir un presidente"));
        this.tests.add(new TestSample("Mancha por parte de", "Mancha por parte de"));
        this.tests.add(new TestSample("advertirles se repartirán dípticos como este",
                "advertirles se repartirán dípticos como este"));
        this.tests.add(new TestSample("socialista y un gobierno socialista", "socialista y un gobierno socialista"));
        this.tests.add(new TestSample("los otros grupos parlamentarios la constitución",
                "los otros grupos parlamentarios la constitución"));
        this.tests.add(new TestSample(" con", " con"));
        this.tests.add(new TestSample("Ya casado", "Ya casado"));
        this.tests.add(new TestSample("de una mesa", "de una mesa"));
        this.tests.add(new TestSample("una pirámide sobre varios comportamientos de acoso",
                "una pirámide sobre varios comportamientos de acoso"));
        this.tests.add(new TestSample("y a", "y a"));
        this.tests.add(new TestSample("De trabajo en torno al agua", "De trabajo en torno al agua"));
        this.tests.add(new TestSample("sexual va", "sexual va"));
        this.tests.add(new TestSample("Rivera que su bloque de derecha y", "Rivera que su bloque de derecha y"));
        this.tests.add(new TestSample("por nivel de gravedad", "por nivel de gravedad"));
        this.tests.add(
                new TestSample("Desde el PP también apoyan la participación", "Desde el PP también apoyan la participación"));
        this.tests.add(new TestSample("especialmente casado", "especialmente casado"));
        this.tests.add(new TestSample("de acoso", "de acoso"));
        this.tests.add(new TestSample(" tocamientos", " tocamientos"));
        this.tests.add(new TestSample("de los pueblos", "de los pueblos"));
        this.tests.add(new TestSample("que está obsesionado con", "que está obsesionado con"));
        this.tests.add(new TestSample("por emisión de imagen en redes sociales y hasta finalizar de",
                "por emisión de imagen en redes sociales y hasta finalizar de"));
        this.tests.add(new TestSample("ribereños en la Comisión de Explotación del Trasvase Tajo",
                "ribereños en la Comisión de Explotación del Trasvase Tajo"));
        this.tests.add(new TestSample("llevarse ya un", "llevarse ya un"));
        this.tests.add(new TestSample("la violación", "la violación"));
        this.tests.add(new TestSample("Segura de", "Segura de"));
        this.tests.add(new TestSample("porcentaje de Ribera no demuestran ese patriotismo",
                "porcentaje de Ribera no demuestran ese patriotismo"));
        this.tests.add(new TestSample(" no", " no"));
        this.tests.add(new TestSample("los populares aseguran que", "los populares aseguran que"));
        this.tests.add(new TestSample("levantando bandera", "levantando bandera"));
        this.tests.add(new TestSample("Lo más grave", "Lo más grave"));
        this.tests.add(new TestSample("es una reivindicación", "es una reivindicación"));
        this.tests.add(new TestSample("Y para el PP", "Y para el PP"));
        this.tests.add(new TestSample("de esto es que resulta", "de esto es que resulta"));
        this.tests.add(
                new TestSample("histórica y piden que se constituya la mesa", "histórica y piden que se constituya la mesa"));
        this.tests.add(new TestSample("por encima de todo", "por encima de todo"));
        this.tests.add(new TestSample("que hasta el tercer nivel tanto chicas", "que hasta el tercer nivel tanto chicas"));
        this.tests.add(new TestSample("del agua para blindar la Posición común de Castilla-La",
                "del agua para blindar la Posición común de Castilla-La"));
        this.tests.add(new TestSample("está la necesidad", "está la necesidad"));
        this.tests.add(new TestSample("chicas no lo ven", "chicas no lo ven"));
        this.tests.add(new TestSample("Mancha", "Mancha"));
        this.tests.add(new TestSample("de despejar incertidumbres de dar seguridad al",
                "de despejar incertidumbres de dar seguridad al"));
        this.tests.add(new TestSample("como un acoso sexista", "como un acoso sexista"));
        this.tests.add(new TestSample("ciudadano", "ciudadano"));
        this.tests.add(new TestSample("sobre todos los", "sobre todos los"));
        this.tests.add(new TestSample("Estamos totalmente de acuerdo", "Estamos totalmente de acuerdo"));
        this.tests.add(new TestSample(" Una situación que", " Una situación que"));
        this.tests.add(new TestSample(" aseguran", " aseguran"));
        this.tests.add(new TestSample(" no está en los", " no está en los"));
        this.tests.add(new TestSample("chicos porque forman parte del entorno", "chicos porque forman parte del entorno"));
        this.tests.add(new TestSample("que vamos a apoyar", "que vamos a apoyar"));
        this.tests.add(new TestSample("planes de", "planes de"));
        this.tests.add(new TestSample("y el", "y el"));
        this.tests.add(new TestSample("a los pueblos ribereños", "a los pueblos ribereños"));
        this.tests.add(new TestSample("Pedro Sánchez", "Pedro Sánchez"));
        this.tests.add(new TestSample(" que sólo busca", " que sólo busca"));
        this.tests.add(new TestSample("ocio nocturno", "ocio nocturno"));
        this.tests.add(new TestSample(" Un folleto que también recoge", " Un folleto que también recoge"));
        this.tests.add(new TestSample(" porque entendemos que", " porque entendemos que"));
        this.tests.add(new TestSample("mantenerse en el poder que vamos a unas elecciones",
                "mantenerse en el poder que vamos a unas elecciones"));
        this.tests.add(new TestSample(" Pues", " Pues"));
        this.tests.add(new TestSample("a quién acudir y cómo actuar", "a quién acudir y cómo actuar"));
        this.tests.add(new TestSample("Castilla-La Mancha también como la", "Castilla-La Mancha también como la"));
        this.tests.add(new TestSample("mire usted", "mire usted"));
        this.tests.add(new TestSample("en caso", "en caso"));
        this.tests.add(new TestSample("cuenca del Tajo", "cuenca del Tajo"));
        this.tests.add(new TestSample(" pues tiene mucho que decir", " pues tiene mucho que decir"));
        this.tests.add(new TestSample(" Hay", " Hay"));
        this.tests.add(new TestSample("Ha quedado marcado que", "Ha quedado marcado que"));
        this.tests.add(new TestSample("de detectar esas conductas", "de detectar esas conductas"));
        this.tests.add(new TestSample("hay un señor que es incapaz de dialogar", "hay un señor que es incapaz de dialogar"));
        this.tests.add(new TestSample(" Esta campaña cacereña es complementaria a la",
                " Esta campaña cacereña es complementaria a la"));
        this.tests.add(new TestSample("Y", "Y"));
        this.tests.add(new TestSample(" por lo tanto", " por lo tanto"));
        this.tests.add(new TestSample("y de negociar y de llegar", "y de negociar y de llegar"));
        this.tests.add(new TestSample("que ya ha puesto en marcha la propia universidad en sus",
                "que ya ha puesto en marcha la propia universidad en sus"));
        this.tests.add(new TestSample(" siempre", " siempre"));
        this.tests.add(new TestSample("a acuerdos que señala que se llama Pedro Sánchez",
                "a acuerdos que señala que se llama Pedro Sánchez"));
        this.tests.add(new TestSample("redes sociales", "redes sociales"));
        this.tests.add(new TestSample("vamos a estar apoyando", "vamos a estar apoyando"));
        this.tests.add(new TestSample("Y porqué", "Y porqué"));
        this.tests.add(new TestSample(" Permítanme la", " Permítanme la"));
        this.tests.add(new TestSample("Aplicable en", "Aplicable en"));
        this.tests.add(new TestSample("a las reivindicaciones", "a las reivindicaciones"));
        this.tests.add(new TestSample("expresión coloquial", "expresión coloquial"));
        this.tests.add(new TestSample("todos los campos", "todos los campos"));
        this.tests.add(new TestSample("históricas de", "históricas de"));
        this.tests.add(new TestSample("Este país va como pollo sin cabeza", "Este país va como pollo sin cabeza"));
        this.tests.add(new TestSample("extremeños", "extremeños"));
        this.tests.add(new TestSample(" La", " La"));
        this.tests.add(new TestSample("esta región", "esta región"));
        this.tests.add(new TestSample(" pero siempre", " pero siempre"));
        this.tests.add(new TestSample("porque el señor Pedro Sánchez", "porque el señor Pedro Sánchez"));
        this.tests.add(new TestSample("Universidad de Extremadura queremos darle la bienvenida a",
                "Universidad de Extremadura queremos darle la bienvenida a"));
        this.tests.add(new TestSample("pasando por", "pasando por"));
        this.tests.add(new TestSample("lo único que le preocupa el a esa cabeza es su sillón",
                "lo único que le preocupa el a esa cabeza es su sillón"));
        this.tests.add(new TestSample("los nuevos estudiantes como se merecen", "los nuevos estudiantes como se merecen"));
        this.tests.add(new TestSample("esa celebración cuanto antes de la Mesa Regional por",
                "esa celebración cuanto antes de la Mesa Regional por"));
        this.tests.add(new TestSample("Depende", "Depende"));
        this.tests.add(new TestSample("el agua en el que", "el agua en el que"));
        this.tests.add(new TestSample("de ti", "de ti"));
        this.tests.add(new TestSample("Con este panorama", "Con este panorama"));
        this.tests.add(new TestSample(" los plazos siguen corriendo", " los plazos siguen corriendo"));
        this.tests.add(new TestSample("tienen que estar todos los colectivos implicados al",
                "tienen que estar todos los colectivos implicados al"));
        this.tests.add(new TestSample("Vino al anotadas que discrimina", "Vino al anotadas que discrimina"));
        this.tests.add(new TestSample("de cara a una investidura que a día de hoy parece difícil",
                "de cara a una investidura que a día de hoy parece difícil"));
        this.tests.add(new TestSample("respecto", "respecto"));
        this.tests.add(new TestSample("los comportamientos que implican", "los comportamientos que implican"));
        this.tests.add(new TestSample("Decirles que la sequía", "Decirles que la sequía"));
        this.tests.add(new TestSample("esa falta de respeto", "esa falta de respeto"));
        this.tests.add(new TestSample("ha provocado un importante pérdida en", "ha provocado un importante pérdida en"));
        this.tests.add(new TestSample("humillación", "humillación"));
        this.tests.add(new TestSample("la producción de miel que", "la producción de miel que"));
        this.tests.add(new TestSample("acoso", "acoso"));
        this.tests.add(new TestSample(" abuso", " abuso"));
        this.tests.add(new TestSample(" por leve que sea", " por leve que sea"));
        this.tests.add(new TestSample(" no", " no"));
        this.tests.add(
                new TestSample("en Guadalajara se cifra en un sesenta por", "en Guadalajara se cifra en un sesenta por"));
        this.tests.add(new TestSample("pueden ser de", "pueden ser de"));
        this.tests.add(new TestSample("ciento", "ciento"));
        this.tests.add(new TestSample(" Por eso", " Por eso"));
        this.tests.add(new TestSample("ninguna manera", "ninguna manera"));
        this.tests.add(new TestSample(" Ha considerado en una tradición ni un juego",
                " Ha considerado en una tradición ni un juego"));
        this.tests.add(new TestSample("El", "El"));
        this.tests.add(new TestSample(" presidente de", " presidente de"));
        this.tests.add(new TestSample(" desde la Asociación de Apicultores", " desde la Asociación de Apicultores"));
        this.tests.add(new TestSample("en la novatada", "en la novatada"));
        this.tests.add(new TestSample("la Junta ha inaugurado esta mañana el curso escolar",
                "la Junta ha inaugurado esta mañana el curso escolar"));
        this.tests.add(new TestSample("de esta provincia se suman a", "de esta provincia se suman a"));
        this.tests
                .add(new TestSample("Entre tres mil cuatro cientos y tres mil", "Entre tres mil cuatro cientos y tres mil"));
        this.tests.add(new TestSample(" Lo ha hecho en el colegio", " Lo ha hecho en el colegio"));
        this.tests.add(
                new TestSample("las peticiones de los ganaderos para que la", "las peticiones de los ganaderos para que la"));
        this.tests.add(new TestSample("seis Cientos nuevos", "seis Cientos nuevos"));
        this.tests.add(new TestSample("Junta y el Gobierno central tome medidas para paliar",
                "Junta y el Gobierno central tome medidas para paliar"));
        this.tests.add(new TestSample("alumnos se incorporan este curso a", "alumnos se incorporan este curso a"));
        this.tests.add(new TestSample("Manuel Altolaguirre de Málaga", "Manuel Altolaguirre de Málaga"));
        this.tests.add(new TestSample(" ocho cientos mil", " ocho cientos mil"));
        this.tests.add(new TestSample("Los efectos de la falta de lluvia", "Los efectos de la falta de lluvia"));
        this.tests.add(new TestSample("la Universidad de Extremadura", "la Universidad de Extremadura"));
        this.tests.add(new TestSample("escolares de Infantil Primaria", "escolares de Infantil Primaria"));
        this.tests.add(new TestSample("en Cáceres", "en Cáceres"));
        this.tests.add(new TestSample(" la fiesta final de las", " la fiesta final de las"));
        this.tests.add(new TestSample("y Educación Especial protagonizan hoy", "y Educación Especial protagonizan hoy"));
        this.tests.add(new TestSample("novatadas será el jueves en el recinto hípico",
                "novatadas será el jueves en el recinto hípico"));
        this.tests.add(new TestSample("la vuelta al", "la vuelta al"));
        this.tests.add(new TestSample("De", "De"));
        this.tests.add(new TestSample(" Mayo a septiembre", " Mayo a septiembre"));
        this.tests.add(new TestSample("cole aquí en Andalucía", "cole aquí en Andalucía"));
        this.tests.add(new TestSample(" Con ellos se incorporan también a", " Con ellos se incorporan también a"));
        this.tests.add(new TestSample("Las lluvias en la provincia", "Las lluvias en la provincia"));
        this.tests.add(new TestSample("las aulas", "las aulas"));
        this.tests.add(new TestSample("de Guadalajara han sido escasas", "de Guadalajara han sido escasas"));
        this.tests.add(new TestSample("Ciento", "Ciento"));
        this.tests.add(new TestSample(" No hay néctar ni polen en el", " No hay néctar ni polen en el"));
        this.tests.add(new TestSample("Veintisiete mil docentes se lo más", "Veintisiete mil docentes se lo más"));
        this.tests.add(new TestSample("campo y las abejas no tienen", "campo y las abejas no tienen"));
        this.tests.add(new TestSample("de siete mil centros", "de 7000 centros"));
        this.tests.add(new TestSample("que comer y sin comida", "que comer y sin comida"));
        this.tests.add(new TestSample("La Junta cifra en ochenta", "La Junta cifra en ochenta"));
        this.tests.add(new TestSample("no producen ya de miel", "no producen ya de miel"));
        this.tests.add(new TestSample(" Este año se va a reducir en más de", " Este año se va a reducir en más de"));
        this.tests.add(new TestSample("millones la inversión en los colegios", "millones la inversión en los colegios"));
        this.tests.add(new TestSample("un sesenta por ciento", "un sesenta por ciento"));
        this.tests.add(new TestSample("se han destinado", "se han destinado"));
        this.tests.add(new TestSample("nueve millones más", "nueve millones más"));
        this.tests.add(new TestSample("Y se traduce en que en que lo que", "Y se traduce en que en que lo que"));
        this.tests.add(new TestSample("dicen para renovar mobiliario y material didáctico",
                "dicen para renovar mobiliario y material didáctico"));
        this.tests.add(new TestSample("vamos a", "vamos a"));
        this.tests.add(new TestSample("tener que intentar es", "tener que intentar es"));
        this.tests.add(new TestSample("mantener nuestra nuestras colmenas", "mantener nuestra nuestras colmenas"));
        this.tests.add(new TestSample("Los estudiantes internacionales", "Los estudiantes internacionales"));
        this.tests.add(new TestSample(" Este", " Este"));
        this.tests.add(new TestSample(" pero sin producción", " pero sin producción"));
        this.tests.add(new TestSample("cuatrimestre tiene cuatro cientos", "cuatrimestre tiene cuatro cientos"));
        this.tests.add(new TestSample(" veinte", " veinte"));
        this.tests.add(new TestSample("Este nuevo curso viene", "Este nuevo curso viene"));
        this.tests.add(new TestSample(" Además", " Además"));
        this.tests.add(new TestSample(" una bajada en el número", " una bajada en el número"));
        this.tests.add(new TestSample("alumnos de", "alumnos de"));
        this.tests.add(new TestSample("marcado en Andalucía por una", "marcado en Andalucía por una"));
        this.tests.add(new TestSample("de abejas dentro de las colmenas porque la reina deja",
                "de abejas dentro de las colmenas porque la reina deja"));
        this.tests.add(new TestSample("diferentes países", "diferentes países"));
        this.tests.add(new TestSample(" El acto se ha celebrado de manera", " El acto se ha celebrado de manera"));
        this.tests.add(new TestSample("bajada de la media de", "bajada de la media de"));
        this.tests.add(new TestSample("de poner", "de poner"));
        this.tests.add(new TestSample("simultánea en el", "simultánea en el"));
        this.tests.add(new TestSample("alumnos en clase debido a la", "alumnos en clase debido a la"));
        this.tests.add(new TestSample("Y por otro lado", "Y por otro lado"));
        this.tests.add(new TestSample("Palacio de La Generala de Cáceres y en la Facultad",
                "Palacio de La Generala de Cáceres y en la Facultad"));
        this.tests.add(new TestSample("caída de la natalidad", "caída de la natalidad"));
        this.tests.add(new TestSample(" Otra novedad es el aumento de horas", " Otra novedad es el aumento de horas"));
        this.tests.add(new TestSample("varroa", "varroa"));
        this.tests.add(new TestSample(" que es el principal", " que es el principal"));
        this.tests.add(
                new TestSample("de Documentación y Comunicación en Badajoz", "de Documentación y Comunicación en Badajoz"));
        this.tests.add(new TestSample("lectivas", "lectivas"));
        this.tests.add(new TestSample("enemigo de las colmenas", "enemigo de las colmenas"));
        this.tests.add(new TestSample(" Este parasito al haber menos", " Este parasito al haber menos"));
        this.tests.add(new TestSample("Llamada Welcome VI que tiene", "Llamada Welcome VI que tiene"));
        this.tests.add(new TestSample("En varias asignaturas", "En varias asignaturas"));
        this.tests.add(new TestSample(" Hoy ha sido", " Hoy ha sido"));
        this.tests.add(new TestSample("cría hace más daño a las abejas", "cría hace más daño a las abejas"));
        this.tests.add(new TestSample("además previstas", "además previstas"));
        this.tests.add(new TestSample("una mañana", "una mañana"));
        this.tests.add(new TestSample(" Por", " Por"));
        this.tests.add(
                new TestSample("varias actividades y visitas guiadas para", "varias actividades y visitas guiadas para"));
        this.tests.add(new TestSample("de mucho ajetreo y muchos nervios a las puertas de los",
                "de mucho ajetreo y muchos nervios a las puertas de los"));
        this.tests.add(
                new TestSample("eso los apicultores han sumado al resto de", "eso los apicultores han sumado al resto de"));
        this.tests.add(new TestSample("que estos", "que estos"));
        this.tests.add(new TestSample("colegios", "colegios"));
        this.tests.add(new TestSample("ganaderos de la región", "ganaderos de la región"));
        this.tests.add(new TestSample("estudiantes empiezan a relacionarse", "estudiantes empiezan a relacionarse"));
        this.tests.add(new TestSample("para solicitar a la Junta y al Gobierno", "para solicitar a la Junta y al Gobierno"));
        this.tests.add(new TestSample("y sepan moverse por", "y sepan moverse por"));
        this.tests.add(new TestSample("central que tome medidas para paliar las consecuencias por",
                "central que tome medidas para paliar las consecuencias por"));
        this.tests.add(new TestSample("nuestros campos y ciudades", "nuestros campos y ciudades"));
        this.tests.add(new TestSample(" Vale", " Vale"));
        this.tests.add(new TestSample("Los siete años", "Los 7 años"));
        this.tests.add(new TestSample("la falta de agua", "la falta de agua"));
        this.tests
                .add(new TestSample(" pues aquella allí la puerta del capital", " pues aquella allí la puerta del capital"));
        this.tests.add(new TestSample(" Luego", " Luego"));
        this.tests.add(new TestSample(" Voy a primero a y", " Voy a primero a y"));
        this.tests.add(new TestSample("La", "La"));
        this.tests.add(new TestSample(" primera y más fácil de", " primera y más fácil de"));
        this.tests.add(new TestSample("lo explicó Deborah la puerta", "lo explicó Deborah la puerta"));
        this.tests.add(new TestSample("me llamo", "me llamo"));
        this.tests.add(new TestSample("cara a las instituciones", "cara a las instituciones"));
        this.tests.add(new TestSample("De", "De"));
        this.tests.add(new TestSample("Voy a segundo", "Voy a segundo"));
        this.tests.add(new TestSample(" ahí", " ahí"));
        this.tests.add(new TestSample(" Supongo que será el tema de", " Supongo que será el tema de"));
        this.tests.add(new TestSample("formación y competencias", "formación y competencias"));
        this.tests.add(new TestSample(" Me llamo Julia", " Me llamo Julia"));
        this.tests.add(new TestSample(" Así", " Así"));
        this.tests.add(new TestSample("las", "las"));
        this.tests.add(new TestSample("Necesita un trabajador en un entorno", "Necesita un trabajador en un entorno"));
        this.tests.add(new TestSample("de despistados llegaban hoy los", "de despistados llegaban hoy los"));
        this.tests.add(new TestSample("ayudas económicas mínimo en", "ayudas económicas mínimo en"));
        this.tests.add(new TestSample("laboral que está cambiando", "laboral que está cambiando"));
        this.tests.add(new TestSample(" Algunas", " Algunas"));
        this.tests.add(new TestSample("más peques a clase", "más peques a clase"));
        this.tests.add(new TestSample(" Por lo", " Por lo"));
        this.tests.add(new TestSample("el en módulos en el", "el en módulos en el"));
        this.tests.add(new TestSample("respuestas Se están dando", "respuestas Se están dando"));
        this.tests.add(new TestSample("general sin", "general sin"));
        this.tests
                .add(new TestSample("índice que se aplica en módulos ayudas a", "índice que se aplica en módulos ayudas a"));
        this.tests.add(new TestSample("en Badajoz en augura Internacional", "en Badajoz en augura Internacional"));
        this.tests.add(new TestSample("muchas ganas que me está pasando muy bien en",
                "muchas ganas que me está pasando muy bien en"));
        this.tests.add(new TestSample("la alimentación", "la alimentación"));
        this.tests.add(new TestSample("Un foro con expertos en educación", "Un foro con expertos en educación"));
        this.tests.add(new TestSample(" investigación y", " investigación y"));
        this.tests.add(new TestSample("vacaciones", "vacaciones"));
        this.tests.add(new TestSample("de las colmenas para sobre poner todo el esfuerzo que",
                "de las colmenas para sobre poner todo el esfuerzo que"));
        this.tests.add(new TestSample("empresas", "empresas"));
        this.tests.add(new TestSample("Dejan a o no", "Dejan a o no"));
        this.tests.add(new TestSample(" Sí", " Sí"));
        this.tests.add(new TestSample("están haciendo sería fundamental", "están haciendo sería fundamental"));
        this.tests.add(new TestSample("pero no quiero estudiar", "pero no quiero estudiar"));
        this.tests.add(new TestSample(" Anda por menudo problema", " Anda por menudo problema"));
        this.tests.add(new TestSample(" No ya", " No ya"));
        this.tests.add(new TestSample("Incluso", "Incluso"));
        this.tests.add(new TestSample("ayudas a", "ayudas a"));
        this.tests.add(new TestSample("Grado master e idiomas", "Grado master e idiomas"));
        this.tests.add(new TestSample("Por", "Por"));
        this.tests.add(new TestSample("la compra de de tanques de", "la compra de de tanques de"));
        this.tests.add(new TestSample("una suma de aprendizajes que no garantizan un",
                "una suma de aprendizajes que no garantizan un"));
        this.tests.add(new TestSample("Qué quieres venir entonces al cole y no", "Qué quieres venir entonces al cole y no"));
        this.tests.add(new TestSample("agua para poderle llevar", "agua para poderle llevar"));
        this.tests.add(new TestSample("buen puesto de", "buen puesto de"));
        this.tests.add(new TestSample("quiere estudiar", "quiere estudiar"));
        this.tests.add(new TestSample(" No sé para ver a los amigos de", " No sé para ver a los amigos de"));
        this.tests.add(new TestSample("agua a las colmenas", "agua a las colmenas"));
        this.tests.add(new TestSample(" Calidad no", " Calidad no"));
        this.tests.add(new TestSample("trabajo", "trabajo"));
        this.tests.add(new TestSample(" Ahora internacional surge", " Ahora internacional surge"));
        this.tests.add(new TestSample("Juan cambió de compañero", "Juan cambió de compañero"));
        this.tests.add(new TestSample("tiene por qué bajar", "tiene por qué bajar"));
        this.tests.add(new TestSample("como espacio de debate sobre", "como espacio de debate sobre"));
        this.tests.add(new TestSample("Eso te hace ilusión", "Eso te hace ilusión"));
        this.tests.add(new TestSample(" Si", " Si"));
        this.tests.add(new TestSample("Lo que significa es que vamos", "Lo que significa es que vamos"));
        this.tests
                .add(new TestSample("qué educación no solo universitaria sino", "qué educación no solo universitaria sino"));
        this.tests.add(new TestSample("a recoger menos porque las abejas", "a recoger menos porque las abejas"));
        this.tests.add(new TestSample("desde los", "desde los"));
        this.tests.add(new TestSample("Está claro que hoy vuelven al cole por", "Está claro que hoy vuelven al cole por"));
        this.tests.add(new TestSample("no han podido trabajar", "no han podido trabajar"));
        this.tests.add(new TestSample("primeros niveles es necesaria para aumentar la empleabilidad",
                "primeros niveles es necesaria para aumentar la empleabilidad"));
        this.tests.add(new TestSample("ver a los", "ver a los"));
        this.tests
                .add(new TestSample("y tienen que comer para poder sobrevivir", "y tienen que comer para poder sobrevivir"));
        this.tests.add(new TestSample(" con", " con"));
        this.tests.add(new TestSample("de las personas", "de las personas"));
        this.tests
                .add(new TestSample("amigos porque las asignaturas tampoco se", "amigos porque las asignaturas tampoco se"));
        this.tests.add(new TestSample("lo cual no vamos a poder quitarles", "lo cual no vamos a poder quitarles"));
        this.tests.add(new TestSample(" Vamos a tener que", " Vamos a tener que"));
        this.tests.add(new TestSample("Proponer un sistema de", "Proponer un sistema de"));
        this.tests.add(new TestSample("les ve", "les ve"));
        this.tests.add(new TestSample("dejar más reservas", "dejar más reservas"));
        this.tests.add(new TestSample("aprendizaje basado en competencias", "aprendizaje basado en competencias"));
        this.tests.add(new TestSample("muy entusiasmados de ocasión física", "muy entusiasmados de ocasión física"));
        this.tests.add(new TestSample(" Así eres deportista", " Así eres deportista"));
        this.tests.add(new TestSample("que también evitaría que muchos que no se adaptan al",
                "que también evitaría que muchos que no se adaptan al"));
        this.tests.add(new TestSample("No hay", "No hay"));
        this.tests.add(new TestSample("sistema se", "sistema se"));
        this.tests.add(new TestSample("Y", "Y"));
        this.tests.add(new TestSample("derecho en esta primavera y en verano", "derecho en esta primavera y en verano"));
        this.tests.add(new TestSample("vean abocados al fracaso", "vean abocados al fracaso"));
        this.tests.add(new TestSample(" Por otra parte", " Por otra parte"));
        this.tests.add(new TestSample("qué es lo que menos te apetece", "qué es lo que menos te apetece"));
        this.tests.add(new TestSample(" pues la vida apicultores que han", " pues la vida apicultores que han"));
        this.tests.add(new TestSample("a los empresarios se les hace difícil detectar el talento",
                "a los empresarios se les hace difícil detectar el talento"));
        this.tests.add(new TestSample("de ir", "de ir"));
        this.tests.add(new TestSample("tenido ya que alimentarlas porque no tenían para",
                "tenido ya que alimentarlas porque no tenían para"));
        this.tests.add(new TestSample("al cole más dramática", "al cole más dramática"));
        this.tests.add(new TestSample("comer las abejas y", "comer las abejas y"));
        this.tests.add(new TestSample("Ya que tienen", "Ya que tienen"));
        this.tests.add(new TestSample("Educación", "Educación"));
        this.tests.add(new TestSample("de cara al invierno", "de cara al invierno"));
        this.tests.add(new TestSample(" pues va a haber que alimentarla", " pues va a haber que alimentarla"));
        this.tests.add(new TestSample("que traducir los", "que traducir los"));
        this.tests.add(new TestSample("física", "física"));
        this.tests.add(new TestSample(" lo demás y lo que menos", " lo demás y lo que menos"));
        this.tests.add(new TestSample("seguramente", "seguramente"));
        this.tests.add(new TestSample("expedientes", "expedientes"));
        this.tests.add(new TestSample(" Pues claro", " Pues claro"));
        this.tests.add(new TestSample("El Ejecutivo", "El Ejecutivo"));
        this.tests.add(new TestSample("académicos ordenado", "académicos ordenado"));
        this.tests.add(
                new TestSample("como estos niños y niñas que acuden hoy al", "como estos niños y niñas que acuden hoy al"));
        this.tests.add(new TestSample("regional estudiará la petición de los", "regional estudiará la petición de los"));
        this.tests.add(new TestSample("por calificaciones y", "por calificaciones y"));
        this.tests.add(new TestSample("colegio público", "colegio público"));
        this.tests.add(new TestSample(" Virgen de la Capilla de Jaén", " Virgen de la Capilla de Jaén"));
        this.tests.add(new TestSample("asignaturas y no por", "asignaturas y no por"));
        this.tests.add(new TestSample("Lo hace más", "Lo hace más"));
        this.tests.add(new TestSample("apicultores de Guadalajara", "apicultores de Guadalajara"));
        this.tests.add(new TestSample(" en este momento estamos mirando", " en este momento estamos mirando"));
        this.tests.add(new TestSample("competencias", "competencias"));
        this.tests.add(new TestSample("de ocho", "de ocho"));
        this.tests.add(new TestSample(" cientos mil alumnos", " cientos mil alumnos"));
        this.tests.add(new TestSample("esa propuesta que se nos hacen", "esa propuesta que se nos hacen"));
        this.tests.add(new TestSample("adquiridas", "adquiridas"));
        this.tests.add(new TestSample(" Capacidades como la de liderazgo", " Capacidades como la de liderazgo"));
        this.tests.add(new TestSample(" la", " la"));
        this.tests.add(new TestSample("en dos mil quinientos cincuenta", "en dos mil quinientos cincuenta"));
        this.tests.add(new TestSample("LA PAZ desde Guadalajara", "LA PAZ desde Guadalajara"));
        this.tests.add(new TestSample("de trabajo en", "de trabajo en"));
        this.tests.add(new TestSample("y uno centros", "y uno centros"));
        this.tests.add(new TestSample("para ver", "para ver"));
        this.tests.add(new TestSample("equipo", "equipo"));
        this.tests.add(new TestSample(" la proactividad son las actitudes que más están",
                " la proactividad son las actitudes que más están"));
        this.tests.add(new TestSample("de toda Andalucía", "de toda Andalucía"));
        this.tests.add(new TestSample(" Aunque este curso", " Aunque este curso"));
        this.tests.add(new TestSample("si realmente", "si realmente"));
        this.tests.add(new TestSample("demandando", "demandando"));
        this.tests.add(new TestSample("la Junta ha invertido", "la Junta ha invertido"));
        this.tests.add(new TestSample("podemos avanzar más directamente", "podemos avanzar más directamente"));
        this.tests.add(
                new TestSample("Y en ello también los trabajadores de las", "Y en ello también los trabajadores de las"));
        this.tests.add(new TestSample("la cifra récord de seis mil seis", "la cifra récord de seis mil seis"));
        this.tests.add(new TestSample("con el sector apícola", "con el sector apícola"));
        this.tests.add(new TestSample(" Simplemente el hecho de", " Simplemente el hecho de"));
        this.tests.add(new TestSample("empresas de los", "empresas de los"));
        this.tests.add(new TestSample("cientos diecinueve", "cientos diecinueve"));
        this.tests.add(new TestSample("permitir el acceso", "permitir el acceso"));
        this.tests.add(new TestSample("distintos ámbitos", "distintos ámbitos"));
        this.tests.add(new TestSample(" tanto en la función pública", " tanto en la función pública"));
        this.tests.add(new TestSample("millones de euros y presume de la mejor ratio media de la",
                "millones de euros y presume de la mejor ratio media de la"));
        this.tests.add(new TestSample("como en el", "como en el"));
        this.tests.add(new TestSample("última década", "última década"));
        this.tests.add(new TestSample("sector privado", "sector privado"));
        this.tests.add(new TestSample(" Es fundamental también que", " Es fundamental también que"));
        this.tests.add(new TestSample("La", "La"));
        this.tests.add(new TestSample("A alimentación no ecológica por", "A alimentación no ecológica por"));
        this.tests.add(new TestSample("empecemos a", "empecemos a"));
        this.tests.add(new TestSample(" asignatura pendiente siguen", " asignatura pendiente siguen"));
        this.tests.add(new TestSample("falta de pastos naturales", "falta de pastos naturales"));
        this.tests.add(new TestSample("tomar conciencia que si hacemos bien nuestro trabajo",
                "tomar conciencia que si hacemos bien nuestro trabajo"));
        this.tests.add(new TestSample("siendo las aulas prefabricadas", "siendo las aulas prefabricadas"));
        this.tests.add(new TestSample("en la ganadería también beneficia", "en la ganadería también beneficia"));
        this.tests.add(new TestSample("que no nos limitamos a ver si no nos limitamos al",
                "que no nos limitamos a ver si no nos limitamos al"));
        this.tests.add(new TestSample(" El dos mil dieciocho", " El dos mil dieciocho"));
        this.tests.add(new TestSample("directamente a los", "directamente a los"));
        this.tests.add(new TestSample("presentismo", "presentismo"));
        this.tests.add(new TestSample("nueva inversión en obras en", "nueva inversión en obras en"));
        this.tests.add(new TestSample("apicultores ecológicos que hay muchos en Castilla-La Mancha",
                "apicultores ecológicos que hay muchos en Castilla-La Mancha"));
        this.tests.add(new TestSample("el diecinueve", "el diecinueve"));
        this.tests.add(new TestSample("Tener una", "Tener una"));
        this.tests.add(new TestSample("ha empezado hace nada y", "ha empezado hace nada y"));
        this.tests.add(new TestSample("Los apicultores apuntan además que", "Los apicultores apuntan además que"));
        this.tests.add(new TestSample("actividad laboral plana en la que", "actividad laboral plana en la que"));
        this.tests.add(new TestSample("son escasa todavía y a este ritmo tardará mucho daño en",
                "son escasa todavía y a este ritmo tardará mucho daño en"));
        this.tests.add(new TestSample("la falta de producción se", "la falta de producción se"));
        this.tests.add(new TestSample("no incentivamos Estamos también", "no incentivamos Estamos también"));
        this.tests.add(new TestSample("suma la crisis de precios que están por", "suma la crisis de precios que están por"));
        this.tests
                .add(new TestSample("haciendo un flaco favor a generar empleo", "haciendo un flaco favor a generar empleo"));
        this.tests.add(new TestSample(" Es", " Es"));
        this.tests.add(new TestSample("Reducir la", "Reducir la"));
        this.tests.add(new TestSample("los suelos", "los suelos"));
        this.tests.add(new TestSample(" dicen un cincuenta por ciento más", " dicen un 50% más"));
        this.tests.add(new TestSample("que tanto queremos", "que tanto queremos"));
        this.tests.add(new TestSample(" el número de", " el número de"));
        this.tests.add(new TestSample("bajos", "bajos"));
        this.tests.add(new TestSample(" por", " por"));
        this.tests.add(new TestSample("Durante estos cuatro días se abordarán", "Durante estos 4 días se abordarán"));
        this.tests.add(new TestSample("barracones o o", "barracones o o"));
        this.tests.add(new TestSample("lo que vaticina un duro", "lo que vaticina un duro"));
        this.tests.add(
                new TestSample("experiencias en los ámbitos de la educación", "experiencias en los ámbitos de la educación"));
        this.tests.add(new TestSample(" la", " la"));
        this.tests.add(new TestSample("aulas prefabricada", "aulas prefabricada"));
        this.tests.add(new TestSample(" El mantenimiento de los centros", " El mantenimiento de los centros"));
        this.tests.add(new TestSample("año para la Denominación de Origen", "año para la Denominación de Origen"));
        this.tests.add(new TestSample("investigación y", "investigación y"));
        this.tests.add(new TestSample("Miel", "Miel"));
        this.tests.add(new TestSample("el empleo", "el empleo"));
        this.tests.add(new TestSample(" Pero ahora internacional no", " Pero ahora internacional no"));
        this.tests.add(new TestSample("También", "También"));
        this.tests.add(
                new TestSample(" tenemos queja porque realmente necesitamos", " tenemos queja porque realmente necesitamos"));
        this.tests.add(new TestSample("de la", "de la"));
        this.tests.add(new TestSample("quedará ahí", "quedará ahí"));
        this.tests.add(
                new TestSample("Se ampliará trescientos sesenta y un días a través de", "Se ampliará 361 días a través de"));
        this.tests.add(
                new TestSample("mayor inversión en obra en adecuar nuestro", "mayor inversión en obra en adecuar nuestro"));
        this.tests.add(new TestSample("Alcarria no (Música)", "Alcarria no (Música)"));
        this.tests.add(new TestSample("una red social", "una red social"));
        this.tests.add(new TestSample("centro la", "centro la"));
        this.tests.add(new TestSample(" La red ahora una especie de Linkedin", " La red ahora una especie de Linkedin"));
        this.tests.add(new TestSample("Confederación de ampas andaluzas", "Confederación de ampas andaluzas"));
        this.tests.add(new TestSample("que pueda reunir a diferentes agentes en torno a un mismo",
                "que pueda reunir a diferentes agentes en torno a un mismo"));
        this.tests.add(new TestSample("pide también más innovación y", "pide también más innovación y"));
        this.tests.add(new TestSample("La", "La"));
        this.tests.add(new TestSample("proyecto", "proyecto"));
        this.tests.add(new TestSample(" sobre todo", " sobre todo"));
        this.tests.add(
                new TestSample(" que no se cierren unidades en la escuela", " que no se cierren unidades en la escuela"));
        this.tests.add(new TestSample("Vuelta Ciclista a España llega mañana a", "Vuelta Ciclista a España llega mañana a"));
        this.tests.add(new TestSample("pública", "pública"));
        this.tests.add(new TestSample("Guadalajara", "Guadalajara"));
        this.tests.add(new TestSample(" donde ya están ultimando", " donde ya están ultimando"));
        this.tests.add(new TestSample("Presidente", "Presidente"));
        this.tests.add(new TestSample(" ahora Cáceres se", " ahora Cáceres se"));
        this.tests.add(new TestSample("Algo que de seguir", "Algo que de seguir"));
        this.tests.add(new TestSample("todo el dispositivo para la", "todo el dispositivo para la"));
        this.tests.add(new TestSample("suma al Día Mundial para la", "suma al Día Mundial para la"));
        this.tests.add(new TestSample("al ritmo actual", "al ritmo actual"));
        this.tests.add(new TestSample(" aseguran", " aseguran"));
        this.tests.add(new TestSample("carrera Además", "carrera Además"));
        this.tests.add(new TestSample(" El viernes también tendrá", " El viernes también tendrá"));
        this.tests.add(new TestSample("Prevención del Suicidio", "Prevención del Suicidio"));
        this.tests.add(new TestSample(" Lo", " Lo"));
        this.tests.add(new TestSample("podría en apenas dos cursos provocar el cierre de centros",
                "podría en apenas dos cursos provocar el cierre de centros"));
        this.tests.add(new TestSample("meta aquí en", "meta aquí en"));
        this.tests.add(new TestSample("ha hecho con este acto con", "ha hecho con este acto con"));
        this.tests.add(new TestSample("Castilla-La Mancha en Toledo en esa", "Castilla-La Mancha en Toledo en esa"));
        this.tests.add(new TestSample("diversos colectivos", "diversos colectivos"));
        this.tests.add(new TestSample("etapa se rendirá homenaje", "etapa se rendirá homenaje"));
        this.tests.add(new TestSample("sociales en el Ayuntamiento una", "sociales en el Ayuntamiento una"));
        this.tests.add(new TestSample("al ciclista Federico Martín Bahamontes el Águila de",
                "al ciclista Federico Martín Bahamontes el Águila de"));
        this.tests.add(new TestSample("jornada para reflexionar", "jornada para reflexionar"));
        this.tests.add(new TestSample("Más", "Más"));
        this.tests.add(new TestSample(" más", " más"));
        this.tests.add(new TestSample("Toledo", "Toledo"));
        this.tests.add(new TestSample(" No", " No"));
        this.tests.add(new TestSample("y abordar los desafíos que plantea", "y abordar los desafíos que plantea"));
        this.tests.add(new TestSample("de ocho mil estudiantes aquí en", "de 8000 estudiantes aquí en"));
        this.tests.add(new TestSample("se cumplen sesenta", "se cumplen sesenta"));
        this.tests.add(new TestSample("esta conducta", "esta conducta"));
        this.tests.add(new TestSample(" La segunda causa de muerte", " La segunda causa de muerte"));
        this.tests.add(new TestSample("Andalucía se enfrentan desde hoy y hasta el jueves a",
                "Andalucía se enfrentan desde hoy y hasta el jueves a"));
        this.tests.add(new TestSample("años de su victoria en el Tour", "años de su victoria en el Tour"));
        this.tests.add(new TestSample("entre los jóvenes de", "entre los jóvenes de"));
        this.tests.add(new TestSample("los exámenes", "los exámenes"));
        this.tests.add(new TestSample("de Francia", "de Francia"));
        this.tests.add(new TestSample(" Esta mañana", " Esta mañana"));
        this.tests.add(new TestSample(" El Ayuntamiento ha", " El Ayuntamiento ha"));
        this.tests.add(new TestSample("entre quince y veintinueve años", "entre 15 y 29 años"));
        this.tests.add(new TestSample("de selectividad ahora llamada prueba", "de selectividad ahora llamada prueba"));
        this.tests.add(new TestSample("vuelto a colocar la estatua", "vuelto a colocar la estatua"));
        this.tests.add(new TestSample("según la OMS cada", "según la OMS cada"));
        this.tests.add(new TestSample("de Evaluación de Bachillerato para el acceso a la",
                "de Evaluación de Bachillerato para el acceso a la"));
        this.tests.add(new TestSample("de bronce de Bahamontes que", "de bronce de Bahamontes que"));
        this.tests.add(new TestSample("año ochocientas mil personas acaban con sus vidas en todo",
                "año 800000 personas acaban con sus vidas en todo"));
        this.tests.add(new TestSample("Universidad", "Universidad"));
        this.tests.add(new TestSample("apareció destrozada el pasado mes de julio en el mirad ero",
                "apareció destrozada el pasado mes de julio en el mirad ero"));
        this.tests.add(new TestSample("el mundo", "el mundo"));
        this.tests.add(new TestSample("No es", "No es"));
        this.tests.add(new TestSample("de Toledo", "de Toledo"));
        this.tests.add(new TestSample("Esta", "Esta"));
        this.tests
                .add(new TestSample("precisamente ampliar la oferta de plazas", "precisamente ampliar la oferta de plazas"));
        this.tests.add(new TestSample(" una vez", " una vez"));
        this.tests.add(new TestSample("jornada pretende", "jornada pretende"));
        this.tests.add(new TestSample("que se han", "que se han"));
        this.tests.add(
                new TestSample("incidir en que existen casos en los que se", "incidir en que existen casos en los que se"));
        this.tests.add(new TestSample("adjudicado las de junio", "adjudicado las de junio"));
        this.tests.add(new TestSample(" pero", " pero"));
        this.tests
                .add(new TestSample("puede evitar el suicidio y hacer visible", "puede evitar el suicidio y hacer visible"));
        this.tests.add(new TestSample("todavía quedan en el", "todavía quedan en el"));
        this.tests.add(new TestSample("Pero que haya", "Pero que haya"));
        this.tests.add(new TestSample("este", "este"));
        this.tests.add(new TestSample("conjunto de", "conjunto de"));
        this.tests.add(new TestSample("suerte", "suerte"));
        this.tests.add(new TestSample(" lo que hace", " lo que hace"));
        this.tests.add(new TestSample("problema eliminando Tabues tras", "problema eliminando Tabues tras"));
        this.tests.add(new TestSample("universidades", "universidades"));
        this.tests.add(new TestSample(" Esta podría", " Esta podría"));
        this.tests.add(new TestSample("falta y que respeten a", "falta y que respeten a"));
        this.tests.add(new TestSample("ser la última Selectividad de", "ser la última Selectividad de"));
        this.tests.add(new TestSample("la figura", "la figura"));
        this.tests.add(new TestSample(" No a", " No a"));
        this.tests.add(new TestSample("septiembre", "septiembre"));
        this.tests.add(new TestSample(" La comunidad", " La comunidad"));
        this.tests.add(new TestSample("la romana", "la romana"));
        this.tests.add(new TestSample(" sino a la de", " sino a la de"));
        this.tests.add(new TestSample("Hoy", "Hoy"));
        this.tests.add(new TestSample(" Además", " Además"));
        this.tests.add(new TestSample("educativa estudia la posibilidad", "educativa estudia la posibilidad"));
        this.tests.add(new TestSample(" como sucede en", " como sucede en"));
        this.tests.add(new TestSample("bronce para que", "bronce para que"));
        this.tests.add(new TestSample("no el comienzo", "no el comienzo"));
        this.tests.add(new TestSample(" Hoy", " Hoy"));
        this.tests.add(new TestSample("otras comunidades", "otras comunidades"));
        this.tests.add(new TestSample("no lo han respetado en ningún momento desde que empezaron",
                "no lo han respetado en ningún momento desde que empezaron"));
        this.tests.add(new TestSample("se ha demostrado que", "se ha demostrado que"));
        this.tests.add(new TestSample("De llevar al mes", "De llevar al mes"));
        this.tests.add(new TestSample("hablar del suicidio rompiendo", "hablar del suicidio rompiendo"));
        this.tests.add(new TestSample("de julio el examen de segunda", "de julio el examen de segunda"));
        this.tests.add(new TestSample("mitos y", "mitos y"));
        this.tests.add(new TestSample("convocatoria", "convocatoria"));
        this.tests.add(new TestSample(" El la de junio la", " El la de junio la"));
        this.tests.add(new TestSample("Empezaron a romper raro", "Empezaron a romper raro"));
        this.tests.add(new TestSample("apología ayuda a prevenir lo pues la crisis suicida son",
                "apología ayuda a prevenir lo pues la crisis suicida son"));
        this.tests.add(new TestSample("carrera carreras", "carrera carreras"));
        this.tests.add(new TestSample("Un parón al bulo", "Un parón al bulo"));
        this.tests
                .add(new TestSample(" no sea que no han parado de hacer cosas", " no sea que no han parado de hacer cosas"));
        this.tests.add(new TestSample("gastos", "gastos"));
        this.tests.add(new TestSample("más demandadas en Andalucía", "más demandadas en Andalucía"));
        this.tests.add(new TestSample("De", "De"));
        this.tests.add(new TestSample("fueron Medicina Enfermería y Educación Primaria",
                "fueron Medicina Enfermería y Educación Primaria"));
        this.tests.add(new TestSample("Atentos porque", "Atentos porque"));
        this.tests.add(new TestSample("pérdida de", "pérdida de"));
        this.tests.add(new TestSample("el casco histórico de Cuenca", "el casco histórico de Cuenca"));
        this.tests.add(new TestSample("conciencia Es transitoria originadas", "conciencia Es transitoria originadas"));
        this.tests
                .add(new TestSample("se dispone a celebrar una cita histórica", "se dispone a celebrar una cita histórica"));
        this.tests.add(new TestSample(" Este", " Este"));
        this.tests.add(new TestSample("por crisis psicológicas o sociales", "por crisis psicológicas o sociales"));
        this.tests.add(new TestSample(" De actuación", " De actuación"));
        this.tests.add(new TestSample("Todo", "Todo"));
        this.tests.add(new TestSample(" UN", " UN"));
        this.tests.add(new TestSample("fin de semana", "fin de semana"));
        this.tests.add(new TestSample("repentina", "repentina"));
        this.tests.add(new TestSample("cumpleaños", "cumpleaños"));
        this.tests.add(new TestSample(" El aeropuerto de Málaga cumple", " El aeropuerto de Málaga cumple"));
        this.tests.add(new TestSample("va a tener lugar la", "va a tener lugar la"));
        this.tests.add(new TestSample("cien años de vida a los", "cien años de vida a los"));
        this.tests.add(new TestSample("representación de la Reconquista de la", "representación de la Reconquista de la"));
        this.tests.add(new TestSample("En", "En"));
        this.tests.add(new TestSample(" Muchos casos superadas son pasajeras", " Muchos casos superadas son pasajeras"));
        this.tests.add(new TestSample("actos de celebración han", "actos de celebración han"));
        this.tests.add(new TestSample("ciudad por las tropas", "ciudad por las tropas"));
        this.tests.add(new TestSample("asistido hoy el presidente de la", "asistido hoy el presidente de la"));
        this.tests.add(new TestSample("del rey Alfonso VIII en el año mil ciento setenta y siete",
                "del rey Alfonso VIII en el año mil ciento setenta y siete"));
        this.tests.add(new TestSample("Junta y", "Junta y"));
        this.tests.add(new TestSample("contará con varios actos", "contará con varios actos"));
        this.tests.add(new TestSample("Es la voz soul", "Es la voz soul"));
        this.tests.add(new TestSample("el ministro de Fomento", "el ministro de Fomento"));
        this.tests.add(new TestSample(" El malagueño", " El malagueño"));
        this.tests.add(new TestSample("De marcado carácter medieval", "De marcado carácter medieval"));
        this.tests.add(new TestSample("entre los cantantes extremeños", "entre los cantantes extremeños"));
        this.tests.add(new TestSample("es el decano", "es el decano"));
        this.tests.add(new TestSample("y ahora lanza", "y ahora lanza"));
        this.tests.add(new TestSample("de los aeropuertos españoles y en este siglo",
                "de los aeropuertos españoles y en este siglo"));
        this.tests.add(new TestSample("su segunda muestra pictórica Gene", "su segunda muestra pictórica Gene"));
        this.tests.add(new TestSample("de vida ha", "de vida ha"));
        this.tests.add(new TestSample("Unas", "Unas"));
        this.tests.add(new TestSample("García nos muestra", "García nos muestra"));
        this.tests.add(new TestSample("prestado servicio a cerca de cuatro cientos millones de",
                "prestado servicio a cerca de 400 millones de"));
        this.tests.add(new TestSample("ciento cincuenta", "ciento cincuenta"));
        this.tests.add(new TestSample("vendidos retratos de míticos cantaores", "vendidos retratos de míticos cantaores"));
        this.tests.add(new TestSample("pasajeros", "pasajeros"));
        this.tests.add(new TestSample("personas participarán durante el próximo fin de semana",
                "personas participarán durante el próximo fin de semana"));
        this.tests.add(new TestSample("gitanos en blanco", "gitanos en blanco"));
        this.tests.add(new TestSample("Actualmente", "Actualmente"));
        this.tests.add(new TestSample("en Cuenca en la puesta", "en Cuenca en la puesta"));
        this.tests.add(new TestSample("y negro y sólo llevan color", "y negro y sólo llevan color"));
        this.tests.add(new TestSample("más de", "más de"));
        this.tests.add(new TestSample("en escena de los", "en escena de los"));
        this.tests.add(new TestSample(" Aquellos en los que dice", " Aquellos en los que dice"));
        this.tests.add(new TestSample("aún fluye la sangre", "aún fluye la sangre"));
        this.tests.add(new TestSample("cincuenta compañías aéreas con esa la", "cincuenta compañías aéreas con esa la"));
        this.tests.add(new TestSample("diferentes actos programados por", "diferentes actos programados por"));
        this.tests.add(new TestSample("capital malagueña", "capital malagueña"));
        this.tests.add(new TestSample(" con cientos de destinos", " con cientos de destinos"));
        this.tests.add(new TestSample("la Asociación de Peñas", "la Asociación de Peñas"));
        this.tests.add(new TestSample(" Mateas El", " Mateas El"));
        this.tests.add(new TestSample(" Grupo de recreación", " Grupo de recreación"));
        this.tests.add(new TestSample("en todo", "en todo"));
        this.tests.add(new TestSample("histórica Conca", "histórica Conca"));
        this.tests.add(new TestSample("En esta sala cantan", "En esta sala cantan"));
        this.tests.add(new TestSample("el mundo", "el mundo"));
        this.tests.add(new TestSample(" Una puerta abierta a Andalucía para el turismo",
                " Una puerta abierta a Andalucía para el turismo"));
        this.tests.add(new TestSample("Porrina de Badajoz", "Porrina de Badajoz"));
        this.tests.add(new TestSample("Vecinos del", "Vecinos del"));
        this.tests.add(new TestSample("La carita Bernardo traerá Juan Peña Lebrijano Lole Montoya",
                "La carita Bernardo traerá Juan Peña Lebrijano Lole Montoya"));
        this.tests.add(new TestSample("Casco Antiguo para conmemorar la reconquista de la",
                "Casco Antiguo para conmemorar la reconquista de la"));
        this.tests.add(new TestSample("ciudad por las tropas del", "ciudad por las tropas del"));
        this.tests.add(new TestSample("Aeropuerto de Málaga Costa", "Aeropuerto de Málaga Costa"));
        this.tests.add(new TestSample("rey Alfonso", "rey Alfonso"));
        this.tests.add(new TestSample(" octavo", " octavo"));
        this.tests.add(new TestSample("Así hasta sumar veintidós cuadros", "Así hasta sumar 22 cuadros"));
        this.tests.add(new TestSample("del Sol se ha convertido con el devenir de los años",
                "del Sol se ha convertido con el devenir de los años"));
        this.tests.add(new TestSample("a", "a"));
        this.tests.add(new TestSample("de quienes Gené", "de quienes Gené"));
        this.tests.add(new TestSample("los almorávides", "los almorávides"));
        this.tests.add(new TestSample(" Consideramos que", " Consideramos que"));
        this.tests.add(new TestSample("considera cantaores y cantaoras que ha", "considera cantaores y cantaoras que ha"));
        this.tests.add(new TestSample("No sólo", "No sólo"));
        this.tests.add(new TestSample("para fomentar no sólo la fiesta", "para fomentar no sólo la fiesta"));
        this.tests.add(new TestSample("marcado un punto de inflexión", "marcado un punto de inflexión"));
        this.tests.add(new TestSample("en", "en"));
        this.tests.add(new TestSample("en sí", "en sí"));
        this.tests.add(new TestSample(" sino el respeto a un casco histórico", " sino el respeto a un casco histórico"));
        this.tests.add(new TestSample("en el cante gitano antes", "en el cante gitano antes"));
        this.tests.add(new TestSample("una importante infraestructura", "una importante infraestructura"));
        this.tests.add(new TestSample("de ponerse", "de ponerse"));
        this.tests.add(new TestSample("europea del transporte", "europea del transporte"));
        this.tests.add(new TestSample(" sino en uno", " sino en uno"));
        this.tests.add(new TestSample("pincel sobre papel", "pincel sobre papel"));
        this.tests.add(new TestSample(" ha estudiado sus vidas", " ha estudiado sus vidas"));
        this.tests.add(new TestSample(" Sus", " Sus"));
        this.tests.add(new TestSample("de los elementos claves del desarrollo económico andaluz",
                "de los elementos claves del desarrollo económico andaluz"));
        this.tests.add(new TestSample("Cómo fomentar la buena", "Cómo fomentar la buena"));
        this.tests.add(new TestSample("personalidades", "personalidades"));
        this.tests.add(new TestSample("convivencia en esas", "convivencia en esas"));
        this.tests.add(new TestSample("Hay que conocer", "Hay que conocer"));
        this.tests.add(new TestSample("Así", "Así"));
        this.tests.add(new TestSample("fiestas", "fiestas"));
        this.tests.add(new TestSample("por ejemplo", "por ejemplo"));
        this.tests.add(new TestSample("debe seguir siendo y para competir", "debe seguir siendo y para competir"));
        this.tests.add(new TestSample("Era necesario que las", "Era necesario que las"));
        this.tests.add(new TestSample("Anika la niña está ahí al fondo", "Anika la niña está ahí al fondo"));
        this.tests.add(new TestSample(" Esa señora", " Esa señora"));
        this.tests.add(new TestSample(" por", " por"));
        this.tests.add(new TestSample("con los grandes", "con los grandes"));
        this.tests.add(new TestSample(" Necesitamos que sumemos esfuerzo", " Necesitamos que sumemos esfuerzo"));
        this.tests.add(new TestSample("peñas y la ciudadanía y sobre todo", "peñas y la ciudadanía y sobre todo"));
        this.tests.add(new TestSample(" los niños supieran", " los niños supieran"));
        this.tests.add(new TestSample("ejemplo", "ejemplo"));
        this.tests.add(new TestSample(" no pudo cantar hasta los sesenta años porque su",
                " no pudo cantar hasta los 60 años porque su"));
        this.tests.add(new TestSample("marido no lo dejaba este", "marido no lo dejaba este"));
        this.tests.add(new TestSample("Que empujemos", "Que empujemos"));
        this.tests.add(new TestSample("Por", "Por"));
        this.tests.add(new TestSample("señor", "señor"));
        this.tests.add(new TestSample(" por ejemplo", " por ejemplo"));
        this.tests.add(new TestSample("todos en la misma dirección", "todos en la misma dirección"));
        this.tests.add(new TestSample("qué razón", "qué razón"));
        this.tests.add(new TestSample("que era era trabajaba en la fragua El Agujetas",
                "que era era trabajaba en la fragua El Agujetas"));
        this.tests.add(new TestSample(" desde", " desde"));
        this.tests.add(new TestSample("se conmemoran estas fiestas de San Mateo y La",
                "se conmemoran estas fiestas de San Mateo y La"));
        this.tests.add(new TestSample("la Administración local", "la Administración local"));
        this.tests.add(new TestSample(" autonómica y la", " autonómica y la"));
        this.tests.add(new TestSample("vaquilla", "vaquilla"));
        this.tests.add(new TestSample(" La", " La"));
        this.tests.add(new TestSample("Nacional", "Nacional"));
        this.tests.add(new TestSample(" contando siempre con ese", " contando siempre con ese"));
        this.tests.add(new TestSample("jornada se de cuenta histórica", "jornada se de cuenta histórica"));
        this.tests.add(new TestSample("Y", "Y"));
        this.tests.add(new TestSample("paraguas de los fondos europeos para", "paraguas de los fondos europeos para"));
        this.tests.add(new TestSample("darán comienzo este", "darán comienzo este"));
        this.tests.add(new TestSample("siempre con una vida para", "siempre con una vida para"));
        this.tests.add(new TestSample("que", "que"));
        this.tests.add(new TestSample("viernes trece de septiembre a las", "viernes 13 de septiembre a las"));
        this.tests.add(new TestSample("corriendo de un lado a otro", "corriendo de un lado a otro"));
        this.tests.add(new TestSample("podamos seguir consolidando y", "podamos seguir consolidando y"));
        this.tests.add(new TestSample("seis de la tarde con la apertura", "seis de la tarde con la apertura"));
        this.tests.add(new TestSample("ampliando y mejorando en términos de eficiencia en",
                "ampliando y mejorando en términos de eficiencia en"));
        this.tests.add(new TestSample("del mercado", "del mercado"));
        this.tests.add(new TestSample("siempre con la ley encima", "siempre con la ley encima"));
        this.tests.add(new TestSample("términos de calidad", "términos de calidad"));
        this.tests.add(new TestSample(" Esta infraestructura", " Esta infraestructura"));
        this.tests.add(new TestSample("de las tres Culturas A", "de las 3 Culturas A"));
        this.tests.add(new TestSample(" todos a modo", " todos a modo"));
        this.tests.add(new TestSample("continuación tendrá lugar", "continuación tendrá lugar"));
        this.tests.add(new TestSample("de captura de pantalla", "de captura de pantalla"));
        this.tests.add(new TestSample(" Están en el momento exacto del", " Están en el momento exacto del"));
        this.tests.add(new TestSample("el pregón Medieval en la Plaza Mayor", "el pregón Medieval en la Plaza Mayor"));
        this.tests.add(new TestSample("quejío", "quejío"));
        this.tests.add(new TestSample("Del pellizco que por", "Del pellizco que por"));
        this.tests.add(new TestSample("El", "El"));
        this.tests.add(new TestSample("Finalizamos con", "Finalizamos con"));
        this.tests.add(new TestSample("eso", "eso"));
        this.tests.add(new TestSample("los deportes", "los deportes"));
        this.tests.add(new TestSample("imágenes de", "imágenes de"));
        this.tests.add(new TestSample("se llama así la", "se llama así la"));
        this.tests.add(new TestSample(" Los equipos andaluces comienzan", " Los equipos andaluces comienzan"));
        this.tests.add(
                new TestSample("la vigesimoséptima maratón fotográfica vía", "la vigesimoséptima maratón fotográfica vía"));
        this.tests.add(new TestSample("la la", "la la"));
        this.tests.add(new TestSample("a preparar sus compromisos del fin de semana tras",
                "a preparar sus compromisos del fin de semana tras"));
        this.tests.add(new TestSample("Bolaños de la", "Bolaños de la"));
        this.tests.add(new TestSample("colección Pues no es no es ni usual Y", "colección Pues no es no es ni usual Y"));
        this.tests.add(new TestSample("el parón de Liga debido a los partidos internacionales",
                "el parón de Liga debido a los partidos internacionales"));
        this.tests.add(new TestSample("Asociación Cultural Torre Prieta", "Asociación Cultural Torre Prieta"));
        this.tests.add(
                new TestSample("yo creo que no hay nadie que lo haya hecho", "yo creo que no hay nadie que lo haya hecho"));
        this.tests.add(new TestSample("Diecinueve participantes", "Diecinueve participantes"));
        this.tests.add(new TestSample("capturaron durante veinticuatro horas", "capturaron durante 24 horas"));
        this.tests.add(new TestSample("En el Sevilla", "En el Sevilla"));
        this.tests.add(
                new TestSample("estas fotografías en color y blanco y negro", "estas fotografías en color y blanco y negro"));
        this.tests.add(new TestSample("Exposición que tiene una narrativa", "Exposición que tiene una narrativa"));
        this.tests.add(new TestSample(" Todos los retratos", " Todos los retratos"));
        this.tests.add(new TestSample(" Faltan varios jugadores", " Faltan varios jugadores"));
        this.tests.add(new TestSample(" entre", " entre"));
        this.tests.add(new TestSample(" lo", " lo"));
        this.tests.add(new TestSample("son en blanco y negro", "son en blanco y negro"));
        this.tests.add(new TestSample(" pero algunos tienen", " pero algunos tienen"));
        this.tests.add(new TestSample("ellos de John", "ellos de John"));
        this.tests.add(new TestSample(" Laboris y Charito Hernández", " Laboris y Charito Hernández"));
        this.tests.add(new TestSample("hicieron siguiendo diferentes temáticas que fueron",
                "hicieron siguiendo diferentes temáticas que fueron"));
        this.tests.add(new TestSample("un detalle en rojo", "un detalle en rojo"));
        this.tests.add(new TestSample(" último fichaje hay que no llegará", " último fichaje hay que no llegará"));
        this.tests.add(new TestSample("conociendo a lo largo de la maratón", "conociendo a lo largo de la maratón"));
        this.tests.add(new TestSample("y cuelgan sobre pared blanca", "y cuelgan sobre pared blanca"));
        this.tests.add(new TestSample(" Son", " Son"));
        this.tests.add(new TestSample("hasta final de semana", "hasta final de semana"));
        this.tests.add(new TestSample(" por", " por"));
        this.tests.add(new TestSample("y otras de", "y otras de"));
        this.tests.add(new TestSample("de aquellos que", "de aquellos que"));
        this.tests.add(new TestSample("lo que tiene complicado debutar el", "lo que tiene complicado debutar el"));
        this.tests.add(new TestSample("temática de temática libre", "temática de temática libre"));
        this.tests.add(new TestSample(" Si quieren ver el", " Si quieren ver el"));
        this.tests.add(new TestSample("siguen en la brecha", "siguen en la brecha"));
        this.tests.add(new TestSample(" Los demás ya", " Los demás ya"));
        this.tests.add(new TestSample("domingo ante el Alavés", "domingo ante el Alavés"));
        this.tests.add(new TestSample(" En el", " En el"));
        this.tests.add(new TestSample("resultado", "resultado"));
        this.tests.add(new TestSample("no viven", "no viven"));
        this.tests.add(new TestSample("Betis", "Betis"));
        this.tests.add(new TestSample(" También son", " También son"));
        this.tests.add(new TestSample("pueden hacerlo hasta finales de septiembre en la Casa de",
                "pueden hacerlo hasta finales de septiembre en la Casa de"));
        this.tests.add(new TestSample("y cuelgan sobre fondos sanguíneos", "y cuelgan sobre fondos sanguíneos"));
        this.tests.add(new TestSample("varios los internacionales que se irán incorporando en",
                "varios los internacionales que se irán incorporando en"));
        this.tests.add(new TestSample("Cultura de Bolaños", "Cultura de Bolaños"));
        this.tests.add(new TestSample(" De Calatrava en Ciudad Real", " De Calatrava en Ciudad Real"));
        this.tests.add(new TestSample("Porque sus recuerdos y mirarlos todos a la vez",
                "Porque sus recuerdos y mirarlos todos a la vez"));
        this.tests.add(new TestSample("estos días como Carballo y Mandy", "estos días como Carballo y Mandy"));
        this.tests.add(new TestSample(" Tello", " Tello"));
        this.tests.add(new TestSample(" lesionado", " lesionado"));
        this.tests.add(new TestSample("está", "está"));
        this.tests.add(new TestSample("prácticamente descartado", "prácticamente descartado"));
        this.tests.add(new TestSample("Parece como si", "Parece como si"));
        this.tests.add(new TestSample("Y la", "Y la"));
        this.tests.add(new TestSample("Es", "Es"));
        this.tests.add(new TestSample("hubiéramos pasado un paño por ese por este por",
                "hubiéramos pasado un paño por ese por este por"));
        this.tests.add(new TestSample("plantilla hizo", "plantilla hizo"));
        this.tests.add(new TestSample("nuestra despedida", "nuestra despedida"));
        this.tests.add(new TestSample(" Hasta mañana", " Hasta mañana"));
        this.tests.add(new TestSample(" Buenas mano", " Buenas mano"));
        this.tests.add(new TestSample("el cante jondo", "el cante jondo"));
        this.tests.add(new TestSample(" No hemos", " No hemos"));
        this.tests.add(new TestSample("la tradicional ofrenda floral en esta canción",
                "la tradicional ofrenda floral en esta canción"));
        this.tests.add(new TestSample("quitado dos años Jura de alguna manera", "quitado dos años Jura de alguna manera"));
        this.tests.add(new TestSample("en Coria como", "en Coria como"));
        this.tests.add(new TestSample("Se", "Se"));
        this.tests.add(new TestSample("y lo hemos modernizado por", "y lo hemos modernizado por"));
        this.tests.add(new TestSample("homenaje a Rogelio", "homenaje a Rogelio"));
        this.tests.add(new TestSample("hace de nuestra Mérida", "hace de nuestra Mérida"));
        this.tests.add(new TestSample("si te das cuenta", "si te das cuenta"));
        this.tests.add(new TestSample("Sosa", "Sosa"));
        this.tests.add(new TestSample(" recientemente fallecido", " recientemente fallecido"));
        this.tests.add(new TestSample("y son son cuadros", "y son son cuadros"));
        this.tests.add(new TestSample("en segunda", "en segunda"));
        this.tests.add(new TestSample(" Hoy estaba prevista", " Hoy estaba prevista"));
        this.tests.add(new TestSample("pop por decir", "pop por decir"));
        this.tests.add(new TestSample("una comparecencia de Caminero", "una comparecencia de Caminero"));
        this.tests.add(new TestSample(" director deportivo para", " director deportivo para"));
        this.tests.add(new TestSample("algo inédito que", "algo inédito que"));
        this.tests.add(new TestSample(" pero decíamos que", " pero decíamos que"));
        this.tests.add(new TestSample("explicar la situación del mercado de fichajes",
                "explicar la situación del mercado de fichajes"));
        this.tests.add(new TestSample("Toma nuestro paso", "Toma nuestro paso"));
        this.tests.add(new TestSample("todos cantan", "todos cantan"));
        this.tests.add(new TestSample(" pero no es así", " pero no es así"));
        this.tests.add(new TestSample(" Los tres en", " Los 3 en"));
        this.tests.add(new TestSample("Pero", "Pero"));
        this.tests.add(new TestSample("y sacar un", "y sacar un"));
        this.tests.add(new TestSample("cabeza de sala", "cabeza de sala"));
        this.tests.add(new TestSample(" no", " no"));
        this.tests.add(new TestSample(" Y eso", " Y eso"));
        this.tests.add(new TestSample(" el jeque Al Thani lo ha impedido", " el jeque Al Thani lo ha impedido"));
        this.tests.add(new TestSample(" acrecentando el", " acrecentando el"));
        this.tests.add(new TestSample("conejo", "conejo"));
        this.tests.add(new TestSample("también forma parte de", "también forma parte de"));
        this.tests.add(new TestSample("malestar", "malestar"));
        this.tests.add(new TestSample("la narrativa", "la narrativa"));
        this.tests.add(new TestSample(" el más grande para", " el más grande para"));
        this.tests.add(new TestSample("de la afición malaguista", "de la afición malaguista"));
        this.tests.add(new TestSample(" Por último", " Por último"));
        this.tests.add(new TestSample("muchos", "muchos"));
        this.tests.add(new TestSample(" El", " El"));
        this.tests.add(new TestSample("al Cádiz cumple hoy ciento nueve años", "al Cádiz cumple hoy 109 años"));
        this.tests.add(new TestSample("Camarón posa mudo franqueado por Manolo Caracol y Ana",
                "Camarón posa mudo franqueado por Manolo Caracol y Ana"));
        this.tests.add(new TestSample("y lo quiere celebrar con su quinta", "y lo quiere celebrar con su quinta"));
        this.tests.add(new TestSample("de la vieja chistera", "de la vieja chistera"));
        this.tests.add(new TestSample("Blanco La Pirineo acá", "Blanco La Pirineo acá"));
        this.tests.add(new TestSample("victoria consecutiva el próximo sábado frente al",
                "victoria consecutiva el próximo sábado frente al"));
        this.tests.add(new TestSample("En vida no cejaron en menospreciarle", "En vida no cejaron en menospreciarle"));
        this.tests.add(new TestSample("Girona", "Girona"));
        this.tests.add(new TestSample(" En el Ramón de Carranza", " En el Ramón de Carranza"));
        this.tests.add(new TestSample("No", "No"));
        this.tests.add(new TestSample("es", "es"));
        this.tests.add(new TestSample("Si no", "Si no"));
        this.tests.add(new TestSample("feliz y yo cuando", "feliz y yo cuando"));
        this.tests.add(new TestSample("cantaba bien", "cantaba bien"));
        this.tests.add(new TestSample("Punto y final", "Punto y final"));
        this.tests.add(new TestSample(" ya con olor a", " ya con olor a"));
        this.tests.add(new TestSample("dos", "dos"));
        this.tests.add(new TestSample(" Ese niño Rubio tiene si se", " Ese niño Rubio tiene si se"));
        this.tests.add(new TestSample("infancia y a", "infancia y a"));
        this.tests.add(new TestSample("a", "a"));
        this.tests.add(new TestSample("va a cantar", "va a cantar"));
        this.tests.add(new TestSample(" Me invitaron", " Me invitaron"));
        this.tests.add(new TestSample("paraíso de la memoria", "paraíso de la memoria"));
        this.tests.add(new TestSample(" Hablamos de", " Hablamos de"));
        this.tests.add(new TestSample("de", "de"));
        this.tests.add(new TestSample(" Odio pinceladas", " Odio pinceladas"));
        this.tests.add(new TestSample("chapa y pintura La", "chapa y pintura La"));
        this.tests.add(new TestSample("pellizcos de cante jondo", "pellizcos de cante jondo"));
        this.tests.add(new TestSample(" Cante", " Cante"));
        this.tests.add(new TestSample("muestra que Manuel filigrana expone", "muestra que Manuel filigrana expone"));
        this.tests.add(new TestSample("de es", "de es"));
        this.tests.add(new TestSample("gitano por excelencia hasta", "gitano por excelencia hasta"));
        this.tests.add(new TestSample("estos días en", "estos días en"));
        this.tests.add(new TestSample("el", "el"));
        this.tests.add(new TestSample("el Ayuntamiento de Tomares", "el Ayuntamiento de Tomares"));
        this.tests.add(new TestSample("la debes a nada", "la debes a nada"));
        this.tests.add(new TestSample("dos de noviembre", "dos de noviembre"));
        this.tests.add(new TestSample(" Ya", " Ya"));
        this.tests.add(new TestSample("del Seiscientos al Mini", "del 600 al Mini"));
        this.tests.add(new TestSample(" pasando", " pasando"));
        this.tests.add(new TestSample("se", "se"));
        this.tests.add(new TestSample("por el cuatro latas", "por el 4 latas"));
        this.tests.add(new TestSample("me quito la", "me quito la"));
        this.tests.add(new TestSample("o el famoso Escarabajo un", "o el famoso Escarabajo un"));
        this.tests.add(new TestSample("venda", "venda"));
        this.tests.add(new TestSample(" tan fiado Meten y", " tan fiado Meten y"));
        this.tests.add(new TestSample("Toma con migo", "Toma con migo"));
        this.tests.add(new TestSample(" café", " café"));
        this.tests.add(new TestSample("recorrido por", "recorrido por"));
        this.tests.add(new TestSample("los vehículos que marcaron", "los vehículos que marcaron"));
        this.tests.add(new TestSample("Válgame Dios compañía", "Válgame Dios compañía"));
        this.tests.add(new TestSample("y es- tan bonita", "y es- tan bonita"));
        this.tests.add(new TestSample("nuestra infancia en", "nuestra infancia en"));
        this.tests.add(new TestSample("que da gusto ver el", "que da gusto ver el"));
        this.tests.add(new TestSample("veinticinco óleos sobre lienzo y manera", "veinticinco óleos sobre lienzo y manera"));
        this.tests.add(new TestSample("Es todo", "Es todo"));
        this.tests.add(new TestSample(" Por nuestra parte", " Por nuestra parte"));
        this.tests.add(new TestSample(" Nos", " Nos"));
        this.tests.add(new TestSample("a si terminamos", "a si terminamos"));
        this.tests.add(new TestSample("vemos mañana Puntuales", "vemos mañana Puntuales"));
        this.tests.add(new TestSample(" que pasen buena", " que pasen buena"));
        this.tests.add(new TestSample("a las dos menos un minuto", "a las dos menos un minuto"));
        this.tests.add(new TestSample(" Gracias", " Gracias"));
        this.tests.add(new TestSample("tarde y la mañana de infarto", "tarde y la mañana de infarto"));
        this.tests.add(new TestSample(" curará", " curará"));
        this.tests.add(new TestSample("Es", "Es"));
        this.tests.add(new TestSample("suelta el bien", "suelta el bien"));
        this.tests.add(new TestSample(" Me ha", " Me ha"));
        this.tests.add(new TestSample("Ha", "Ha"));
        this.tests.add(new TestSample("Decir", "Decir"));
        this.tests.add(new TestSample("atentos", "atentos"));
        this.tests.add(new TestSample("al pronóstico del", "al pronóstico del"));
        this.tests.add(new TestSample("donde la tarde lo", "donde la tarde lo"));
        this.tests.add(new TestSample("tiempo", "tiempo"));
        this.tests.add(new TestSample(" hago", " hago"));
        this.tests.add(new TestSample("Las y ponéis de", "Las y ponéis de"));
        this.tests.add(new TestSample("hacer un chubasco temporal", "hacer un chubasco temporal"));
        this.tests.add(new TestSample("cinco minutos para", "cinco minutos para"));
        this.tests.add(new TestSample("ponéis de cinco minutos para", "ponéis de 5 minutos para"));
        this.tests.add(new TestSample("emplatar vuestra mejor elaboración", "emplatar vuestra mejor elaboración"));
        this.tests.add(new TestSample(" lo más negro", " lo más negro"));
        this.tests.add(new TestSample("emplatar", "emplatar"));
        this.tests.add(new TestSample(" vuestra mejor elaboración Está más negro que",
                " vuestra mejor elaboración Está más negro que"));
        this.tests.add(new TestSample("que el futuro en este programa oye", "que el futuro en este programa oye"));
        this.tests.add(new TestSample(" yo no te he faltado", " yo no te he faltado"));
        this.tests.add(new TestSample("tu futuro en este programa", "tu futuro en este programa"));
        this.tests.add(new TestSample(" Oye no te he", " Oye no te he"));
        this.tests.add(new TestSample("Yo", "Yo"));
        this.tests.add(new TestSample(" Yo ya", " Yo ya"));
        this.tests.add(new TestSample("al respeto", "al respeto"));
        this.tests.add(new TestSample(" a ningún momento", " a ningún momento"));
        this.tests.add(new TestSample("faltado el respeto a", "faltado el respeto a"));
        this.tests.add(new TestSample("No dispara la pistolita", "No dispara la pistolita"));
        this.tests.add(new TestSample(" Son cosas del directo", " Son cosas del directo"));
        this.tests.add(new TestSample("ningún momento", "ningún momento"));
        this.tests.add(new TestSample(" No dispara la pistolita", " No dispara la pistolita"));
        this.tests.add(new TestSample(" Son cosas del", " Son cosas del"));
        this.tests.add(new TestSample("Ponéis de", "Ponéis de"));
        this.tests.add(new TestSample("directo", "directo"));
        this.tests.add(new TestSample("cinco minutos para emplatar vuestra mejor elaboración",
                "cinco minutos para emplatar vuestra mejor elaboración"));
        this.tests.add(new TestSample("Nada", "Nada"));
        this.tests.add(new TestSample(" pero de Ana", " pero de Ana"));
        this.tests.add(new TestSample(" No", " No"));
        this.tests.add(new TestSample("Mar Negro que", "Mar Negro que"));
        this.tests.add(new TestSample("somos los que un grito que tenemos arte", "somos los que un grito que tenemos arte"));
        this.tests.add(new TestSample("Ana", "Ana"));
        this.tests.add(new TestSample(" No somos un grupo", " No somos un grupo"));
        this.tests.add(new TestSample("tu futuro en este programa Oye", "tu futuro en este programa Oye"));
        this.tests.add(new TestSample("Una voz cachondito", "Una voz cachondito"));
        this.tests.add(new TestSample("que tenemos arte", "que tenemos arte"));
        this.tests.add(new TestSample(" Una", " Una"));
        this.tests.add(new TestSample("te he faltado el", "te he faltado el"));
        this.tests.add(new TestSample("yo ya chungo y master", "yo ya chungo y master"));
        this.tests.add(new TestSample("gozada chiringuito ya chungo", "gozada chiringuito ya chungo"));
        this.tests.add(new TestSample(" Y", " Y"));
        this.tests.add(new TestSample("respeto", "respeto"));
        this.tests.add(new TestSample(" a ningún momento", " a ningún momento"));
        this.tests.add(new TestSample(" No dispara la pistolita", " No dispara la pistolita"));
        this.tests.add(new TestSample(" Son", " Son"));
        this.tests.add(
                new TestSample("de Celebrity el miércoles a las diez y diez", "de Celebrity el miércoles a las diez y diez"));
        this.tests
                .add(new TestSample("eso a este chef Celebrity el miércoles a", "eso a este chef Celebrity el miércoles a"));
        this.tests.add(new TestSample("cosas del directo", "cosas del directo"));
        this.tests.add(new TestSample("nueva temporada en la una", "nueva temporada en la una"));
        this.tests.add(new TestSample("las diez y diez nueva temporada en la una", "las 10 y 10 nueva temporada en la una"));
        this.tests.add(new TestSample("Los frigoríficos Guitar", "Los frigoríficos Guitar"));
        this.tests.add(new TestSample("Los frigoríficos", "Los frigoríficos"));
        this.tests.add(new TestSample("Gana", "Gana"));
        this.tests.add(new TestSample(" Freaks de voz", " Freaks de voz"));
        this.tests.add(new TestSample("Guitar Freaks de voy y los productos frescos del",
                "Guitar Freaks de voy y los productos frescos del"));
        this.tests.add(new TestSample("No somos un grupo que", "No somos un grupo que"));
        this.tests
                .add(new TestSample("y los productos frescos del supermercado", "y los productos frescos del supermercado"));
        this.tests.add(new TestSample("tenemos arte", "tenemos arte"));
        this.tests.add(new TestSample(" Una bolsa de chiringuito", " Una bolsa de chiringuito"));
        this.tests.add(new TestSample("de El", "de El"));
        this.tests.add(new TestSample(" Yo ya chungo y eso a este chef Celebrity el miércoles a",
                " Yo ya chungo y eso a este chef Celebrity el miércoles a"));
        this.tests.add(new TestSample("Corte Inglés Patrocinan master", "Corte Inglés Patrocinan master"));
        this.tests.add(new TestSample("las diez y diez nueva temporada en la una", "las 10 y 10 nueva temporada en la una"));
        this.tests.add(new TestSample("Chef Celebrity Quiero", "Chef Celebrity Quiero"));
        this.tests.add(new TestSample("Los frigoríficos", "Los frigoríficos"));
        this.tests.add(new TestSample("bailar con ha encontrado el", "bailar con ha encontrado el"));
        this.tests.add(new TestSample("Vita", "Vita"));
        this.tests.add(new TestSample("cadáver de la chica", "cadáver de la chica"));
        this.tests.add(new TestSample(" Es aquí en invierno", " Es aquí en invierno"));
        this.tests.add(new TestSample("Fresh de voy y los", "Fresh de voy y los"));
        this.tests.add(new TestSample("productos frescos del supermercado de", "productos frescos del supermercado de"));
        this.tests.add(new TestSample("Todo EL jamás me ocultaría una cosa así", "Todo EL jamás me ocultaría una cosa así"));
        this.tests.add(new TestSample("El Corte Inglés", "El Corte Inglés"));
        this.tests.add(new TestSample("Es un buen chico", "Es un buen chico"));
        this.tests.add(new TestSample("Sabía", "Sabía"));
        this.tests.add(new TestSample("patrocinan Master Chef", "patrocinan Master Chef"));
        this.tests.add(new TestSample("Celebrity Quiero bailar con", "Celebrity Quiero bailar con"));
        this.tests.add(new TestSample("encontrado el cadáver de la chica", "encontrado el cadáver de la chica"));
        this.tests.add(new TestSample(" Es aquí en el mercado", " Es aquí en el mercado"));
        this.tests.add(new TestSample("Parece que sabe algo", "Parece que sabe algo"));
        this.tests.add(new TestSample("que tengo cuarenta años y sé", "que tengo 40 años y sé"));
        this.tests.add(new TestSample("Todo EL jamás", "Todo EL jamás"));
        this.tests.add(new TestSample("que soy Asnef y ya está", "que soy Asnef y ya está"));
        this.tests.add(new TestSample(" Cuando me fuerza", " Cuando me fuerza"));
        this.tests.add(new TestSample("me ocultaría una cosa así", "me ocultaría una cosa así"));
        this.tests.add(new TestSample("Es un buen chico", "Es un buen chico"));
        this.tests.add(new TestSample(" A mi me", " A mi me"));
        this.tests.add(new TestSample("parece que sabe algo", "parece que sabe algo"));
        this.tests.add(new TestSample("Mi", "Mi"));
        this.tests.add(new TestSample("a algunas", "a algunas"));
        this.tests.add(new TestSample("Llamó creerte", "Llamó creerte"));
        this.tests.add(new TestSample("pesadillas empiezan al despertar lo", "pesadillas empiezan al despertar lo"));
        this.tests.add(new TestSample(" Tengo cuarenta años y", " Tengo 40 años y"));
        this.tests.add(new TestSample("sé que soy amnésica", "sé que soy amnésica"));
        this.tests.add(new TestSample("Quien", "Quien"));
        this.tests.add(new TestSample("Esta cuando", "Esta cuando"));
        this.tests.add(new TestSample("intentó", "intentó"));
        this.tests.add(new TestSample(" Quiero que recuerda", " Quiero que recuerda"));
        this.tests.add(new TestSample("mi cuerpo", "mi cuerpo"));
        this.tests.add(new TestSample("que por favor", "que por favor"));
        this.tests.add(new TestSample(" venga", " venga"));
        this.tests.add(new TestSample("mi libro a", "mi libro a"));
        this.tests.add(
                new TestSample("algunas pesadillas empiezan al despertar al", "algunas pesadillas empiezan al despertar al"));
        this.tests.add(new TestSample("contando", "contando"));
        this.tests.add(new TestSample(" pero no te gustó nada", " pero no te gustó nada"));
        this.tests.add(new TestSample(" Entra en casa", " Entra en casa"));
        this.tests.add(new TestSample(" No", " No"));
        this.tests.add(new TestSample("confies en abril", "confies en abril"));
        this.tests.add(new TestSample(" Esta noche a las diez cuarenta en", " Esta noche a las 10 40 en"));
        this.tests.add(new TestSample("La uno", "La uno"));
        this.tests.add(new TestSample(" El dueño es un", " El dueño es un"));
        this.tests.add(new TestSample("Intentó", "Intentó"));
        this.tests.add(new TestSample("veterano del Ejército Vive", "veterano del Ejército Vive"));
        this.tests.add(new TestSample("matarme", "matarme"));
        this.tests.add(new TestSample(" Quiero que recuerde", " Quiero que recuerde"));
        this.tests.add(new TestSample("solo en la casa", "solo en la casa"));
        this.tests.add(new TestSample(" Que mal rollo", " Que mal rollo"));
        this.tests.add(new TestSample(" por favor", " por favor"));
        this.tests.add(new TestSample(" A a", " A a"));
        this.tests.add(new TestSample("robarle a un ciego", "robarle a un ciego"));
        this.tests.add(new TestSample(" No creeis", " No creeis"));
        this.tests.add(new TestSample("casa contando", "casa contando"));
        this.tests.add(new TestSample(" pero no te oculto", " pero no te oculto"));
        this.tests.add(new TestSample(" Nada", " Nada"));
        this.tests.add(new TestSample(" Entra en casa", " Entra en casa"));
        this.tests.add(new TestSample("entrar", "entrar"));
        this.tests.add(new TestSample(" No", " No"));
        this.tests.add(new TestSample(" Confíes en", " Confíes en"));
        this.tests.add(new TestSample(" Es fácil", " Es fácil"));
        this.tests.add(new TestSample("nadie esta noche a las diez", "nadie esta noche a las diez"));
        this.tests.add(new TestSample("Dios salir imposible", "Dios salir imposible"));
        this.tests.add(new TestSample("cuarenta", "cuarenta"));
        this.tests.add(new TestSample(" en La uno", " en La uno"));
        this.tests.add(new TestSample(" El", " El"));
        this.tests.add(new TestSample("dueño es un veterano del Ejército Vive", "dueño es un veterano del Ejército Vive"));
        this.tests.add(new TestSample("solo en la casa", "solo en la casa"));
        this.tests.add(new TestSample(" Que", " Que"));
        this.tests.add(new TestSample("No respires el jueves a las diez cuarenta estrenó en la",
                "No respires el jueves a las 10 40 estrenó en la"));
        this.tests.add(new TestSample("mal rollo", "mal rollo"));
        this.tests.add(new TestSample(" Robarle al ciego", " Robarle al ciego"));
        this.tests.add(new TestSample(" No creeis", " No creeis"));
        this.tests.add(new TestSample("Uno", "Uno"));
        this.tests.add(new TestSample("entrar", "entrar"));
        this.tests.add(new TestSample(" Es fácil", " Es fácil"));
        this.tests.add(new TestSample("Mira que bonito los en el", "Mira que bonito los en el"));
        this.tests.add(new TestSample("Dios", "Dios"));
        this.tests.add(new TestSample("pecho", "pecho"));
        this.tests.add(new TestSample(" Si tienes es que no te queda", " Si tienes es que no te queda"));
        this.tests.add(new TestSample("no salir imposible", "no salir imposible"));
        this.tests.add(new TestSample("ninguno", "ninguno"));
        this.tests.add(new TestSample(" que es", " que es"));
        this.tests.add(new TestSample("No respires el jueves", "No respires el jueves"));
        this.tests.add(new TestSample("que no estamos tan ser cocinera", "que no estamos tan ser cocinera"));
        this.tests.add(new TestSample(" Mandar tu llevas", " Mandar tu llevas"));
        this.tests.add(new TestSample("a las diez cuarenta", "a las diez cuarenta"));
        this.tests.add(new TestSample("cuarenta años con tu marido", "cuarenta años con tu marido"));
        this.tests.add(new TestSample(" No tengo cojo", " No tengo cojo"));
        this.tests.add(new TestSample(" No si quieres", " No si quieres"));
        this.tests.add(new TestSample("te", "te"));
        this.tests.add(new TestSample("Estrenó la uno", "Estrenó la uno"));
        this.tests.add(new TestSample("puedo enseñar el vibrador que", "puedo enseñar el vibrador que"));
        this.tests.add(new TestSample("Mira que bonito y en los en", "Mira que bonito y en los en"));
        this.tests.add(new TestSample("llevo lo", "llevo lo"));
        this.tests.add(new TestSample("el pecho", "el pecho"));
        this.tests.add(new TestSample(" Si tienes es que", " Si tienes es que"));
        this.tests.add(new TestSample("que pasa en Calaceite", "que pasa en Calaceite"));
        this.tests.add(new TestSample("no te queda ninguno", "no te queda ninguno"));
        this.tests.add(new TestSample(" que es", " que es"));
        this.tests.add(new TestSample("se", "se"));
        this.tests.add(new TestSample("lo que me gusta", "lo que me gusta"));
        this.tests.add(new TestSample(" Gusta", " Gusta"));
        this.tests.add(new TestSample("queden Calaceite la paisana", "queden Calaceite la paisana"));
        this.tests.add(new TestSample(" El cocinero Mandar tu llevas cuarenta años con tu",
                " El cocinero Mandar tu llevas 40 años con tu"));
        this.tests.add(new TestSample("El", "El"));
        this.tests.add(new TestSample("marido", "marido"));
        this.tests.add(new TestSample(" No tengo cojo", " No tengo cojo"));
        this.tests.add(new TestSample(" No si quieres", " No si quieres"));
        this.tests.add(new TestSample(" te puedo enseñar el", " te puedo enseñar el"));
        this.tests.add(new TestSample("viernes a las diez y", "viernes a las diez y"));
        this.tests.add(new TestSample("vibrador que llevó", "vibrador que llevó"));
        this.tests.add(new TestSample("Lo", "Lo"));
        this.tests.add(new TestSample("en la Uno", "en la Uno"));
        this.tests.add(new TestSample("Lo los hornos", "Lo los hornos"));
        this.tests.add(new TestSample("que pasa en Calaceite", "que pasa en Calaceite"));
        this.tests.add(new TestSample("se", "se"));
        this.tests
                .add(new TestSample("queden Calaceite la paisana el viernes a", "queden Calaceite la paisana el viernes a"));
        this.tests.add(new TestSample("Balay patrocinan la paisana", "Balay patrocinan la paisana"));
        this.tests.add(new TestSample(" Ya te", " Ya te"));
        this.tests.add(new TestSample("las diez y", "las diez y"));
        this.tests.add(new TestSample("diez en la una", "diez en la una"));
        this.tests.add(new TestSample("El streaming más directo", "El streaming más directo"));
        this.tests.add(new TestSample(" Cruzamos todas las", " Cruzamos todas las"));
        this.tests.add(new TestSample("Los hornos", "Los hornos"));
        this.tests.add(new TestSample("puertas de", "puertas de"));
        this.tests.add(new TestSample("la actualidad", "la actualidad"));
        this.tests.add(new TestSample("Balay patrocinan la paisana", "Balay patrocinan la paisana"));
        this.tests.add(new TestSample(" Si", " Si"));
        this.tests.add(new TestSample("cinco veces al día", "cinco veces al día"));
        this.tests.add(new TestSample("No te quedes fuera", "No te quedes fuera"));
        this.tests.add(new TestSample(" No te pierdas nada", " No te pierdas nada"));
        this.tests.add(new TestSample("El exprimir más directo", "El exprimir más directo"));
        this.tests.add(new TestSample(" Damos todas las puertas", " Damos todas las puertas"));
        this.tests.add(new TestSample("de la actualidad", "de la actualidad"));
        this.tests.add(new TestSample("Cambia con nosotros lo", "Cambia con nosotros lo"));
        this.tests.add(new TestSample("cinco", "cinco"));
        this.tests.add(new TestSample("Aranda", "Aranda"));
        this.tests.add(new TestSample("veces al", "veces al"));
        this.tests.add(new TestSample("de Duero Guadalajara", "de Duero Guadalajara"));
        this.tests.add(new TestSample(" La jornada más", " La jornada más"));
        this.tests.add(new TestSample("día", "día"));
        this.tests.add(new TestSample(" Sí o no", " Sí o no"));
        this.tests.add(new TestSample("larga de la", "larga de la"));
        this.tests.add(new TestSample("te de verdad", "te de verdad"));
        this.tests.add(new TestSample("Vuelta oportunidad para los", "Vuelta oportunidad para los"));
        this.tests.add(new TestSample(" Nada cambia con nosotros", " Nada cambia con nosotros"));
        this.tests.add(new TestSample("esprinters", "esprinters"));
        this.tests.add(new TestSample(" Es una llegada", " Es una llegada"));
        this.tests.add(new TestSample("para los más veloces", "para los más veloces"));
        this.tests.add(new TestSample(" Decimoséptima etapa", " Decimoséptima etapa"));
        this.tests.add(new TestSample("de la", "de la"));
        this.tests.add(new TestSample("Anda", "Anda"));
        this.tests.add(new TestSample(" Mañana a las cuatro y diez en", " Mañana a las 4 y 10 en"));
        this.tests.add(new TestSample("de Duero", "de Duero"));
        this.tests.add(new TestSample("la Uno", "la Uno"));
        this.tests.add(new TestSample("y Guadalajara La jornada más larga de", "y Guadalajara La jornada más larga de"));
        this.tests.add(new TestSample("la Vuelta oportunidad para los", "la Vuelta oportunidad para los"));
        this.tests.add(new TestSample("esprinters", "esprinters"));
        this.tests.add(new TestSample(" Es una llegada", " Es una llegada"));
        this.tests.add(new TestSample("para los más veloces", "para los más veloces"));
        this.tests.add(new TestSample(" Decimoséptima", " Decimoséptima"));
        this.tests.add(new TestSample("etapa de la", "etapa de la"));
        this.tests.add(new TestSample("Vuelta dos mil diecinueve", "Vuelta dos mil diecinueve"));
        this.tests.add(new TestSample(" Mañana a las cuatro y diez en", " Mañana a las 4 y 10 en"));
        this.tests.add(new TestSample(" los dos vehículos se", " los dos vehículos se"));
        this.tests.add(new TestSample("incendiado El siniestro", "incendiado El siniestro"));
        this.tests.add(new TestSample("han incendiado El siniestro", "han incendiado El siniestro"));
        this.tests.add(new TestSample("han incendiado El siniestro ha obligado a cortar",
                "han incendiado El siniestro ha obligado a cortar"));
        this.tests.add(new TestSample("ha obligado a cortar el tráfico", "ha obligado a cortar el tráfico"));
        this.tests.add(new TestSample("ha obligado a cortar el tráfico y establecer como",
                "ha obligado a cortar el tráfico y establecer como"));
        this.tests.add(new TestSample("y establecer como", "y establecer como"));
        this.tests.add(new TestSample("vía alternativa la AP dos", "vía alternativa la AP dos"));
        this.tests.add(new TestSample("El tramo Alfajarín Fraga", "El tramo Alfajarín Fraga"));
        this.tests.add(new TestSample("El tramo", "El tramo"));
        this.tests.add(new TestSample("El tramo Alfajarín Fraga", "El tramo Alfajarín Fraga"));
        this.tests.add(new TestSample("es el único tramo", "es el único tramo"));
        this.tests.add(
                new TestSample("Alfajarín Fraga es el único tramo que queda", "Alfajarín Fraga es el único tramo que queda"));
        this.tests.add(new TestSample("es el único tramo que queda sin", "es el único tramo que queda sin"));
        this.tests.add(new TestSample("que queda sin desde Hablar de la", "que queda sin desde Hablar de la"));
        this.tests.add(new TestSample("sin desdoblar de la antigua Nacional", "sin desdoblar de la antigua Nacional"));
        this.tests.add(new TestSample("desde Hablar de la", "desde Hablar de la"));
        this.tests.add(new TestSample("antigua Nacional", "antigua Nacional"));
        this.tests.add(new TestSample("dos en el eje entre Madrid y Barcelona", "dos en el eje entre Madrid y Barcelona"));
        this.tests.add(new TestSample("antigua Nacional", "antigua Nacional"));
        this.tests.add(new TestSample("entre Madrid y Barcelona", "entre Madrid y Barcelona"));
        this.tests.add(new TestSample("entre Madrid y Barcelona", "entre Madrid y Barcelona"));
        this.tests.add(new TestSample("Es el quinto accidente en lo que llevamos de",
                "Es el quinto accidente en lo que llevamos de"));
        this.tests.add(new TestSample("Es el quinto accidente en lo que", "Es el quinto accidente en lo que"));
        this.tests.add(new TestSample("Es el quinto accidente en lo que", "Es el quinto accidente en lo que"));
        this.tests.add(new TestSample("año", "año"));
        this.tests.add(new TestSample(" Los", " Los"));
        this.tests.add(new TestSample("llevamos de año", "llevamos de año"));
        this.tests.add(new TestSample(" Los", " Los"));
        this.tests.add(new TestSample("llevamos de año", "llevamos de año"));
        this.tests.add(new TestSample(" Los", " Los"));
        this.tests.add(new TestSample("municipios de la zona llevan años", "municipios de la zona llevan años"));
        this.tests.add(new TestSample("municipios de la zona llevan años", "municipios de la zona llevan años"));
        this.tests.add(new TestSample("municipios de la zona llevan años", "municipios de la zona llevan años"));
        this.tests.add(new TestSample("reivindicando que se desdoble este", "reivindicando que se desdoble este"));
        this.tests.add(new TestSample("reivindicando que", "reivindicando que"));
        this.tests
                .add(new TestSample("reivindicando que se desdoble este tramo", "reivindicando que se desdoble este tramo"));
        this.tests.add(new TestSample("tramo por el circulan cada", "tramo por el circulan cada"));
        this.tests.add(new TestSample("se desdoble este tramo por el circulan cada día",
                "se desdoble este tramo por el circulan cada día"));
        this.tests.add(new TestSample("por el circulan cada día", "por el circulan cada día"));
        this.tests.add(new TestSample("día más de ocho mil treinta y", "día más de ocho mil treinta y"));
        this.tests.add(new TestSample("tres vehículos el sesenta y", "tres vehículos el sesenta y"));
        this.tests.add(new TestSample("treinta y tres vehículos el sesenta y cuatro por ciento",
                "treinta y tres vehículos el sesenta y cuatro por ciento"));
        this.tests.add(new TestSample("tres vehículos el sesenta y cuatro por ciento son",
                "tres vehículos el sesenta y cuatro por ciento son"));
        this.tests.add(new TestSample("cuatro por ciento", "cuatro por ciento"));
        this.tests.add(new TestSample(" Son camiones", " Son camiones"));
        this.tests.add(new TestSample("son camiones", "son camiones"));
        this.tests.add(new TestSample("camiones", "camiones"));
        this.tests.add(new TestSample(" aunque", " aunque"));
        this.tests.add(new TestSample("Aunque", "Aunque"));
        this.tests.add(new TestSample("Aunque", "Aunque"));
        this.tests.add(new TestSample("tienen bonificaciones para circular", "tienen bonificaciones para circular"));
        this.tests.add(new TestSample("tienen bonificaciones para", "tienen bonificaciones para"));
        this.tests.add(new TestSample("tienen bonificaciones para circular", "tienen bonificaciones para circular"));
        this.tests.add(new TestSample("por la autopista", "por la autopista"));
        this.tests.add(new TestSample(" Los", " Los"));
        this.tests.add(new TestSample("circular por la autopista", "circular por la autopista"));
        this.tests.add(new TestSample("por la autopista", "por la autopista"));
        this.tests.add(new TestSample(" Los vehículos pesados siguen", " Los vehículos pesados siguen"));
        this.tests.add(new TestSample("vehículos pesados siguen", "vehículos pesados siguen"));
        this.tests.add(new TestSample("Los vehículos pesados siguen", "Los vehículos pesados siguen"));
        this.tests.add(new TestSample("desviándose por la", "desviándose por la"));
        this.tests.add(new TestSample("desviándose por la nacional para que era muy feliz",
                "desviándose por la nacional para que era muy feliz"));
        this.tests.add(new TestSample("desviándose por la nacional para que era muy",
                "desviándose por la nacional para que era muy"));
        this.tests.add(new TestSample(" muy feliz", " muy feliz"));
        this.tests.add(new TestSample("nacional para que era muy", "nacional para que era muy"));
        this.tests.add(new TestSample(" muy feliz", " muy feliz"));
        this.tests.add(new TestSample(" Siempre", " Siempre"));
        this.tests.add(new TestSample(" ha sido", " ha sido"));
        this.tests.add(new TestSample("Siempre", "Siempre"));
        this.tests.add(new TestSample(" ha sido muy peligrosa", " ha sido muy peligrosa"));
        this.tests.add(new TestSample(" Por debida", " Por debida"));
        this.tests.add(new TestSample(" Siempre", " Siempre"));
        this.tests.add(new TestSample(" ha sido muy peligrosa", " ha sido muy peligrosa"));
        this.tests.add(new TestSample(" Por", " Por"));
        this.tests.add(new TestSample("muy peligrosa", "muy peligrosa"));
        this.tests.add(new TestSample(" Por", " Por"));
        this.tests.add(new TestSample("tanto camión que hay tienen que pagar autopista",
                "tanto camión que hay tienen que pagar autopista"));
        this.tests.add(new TestSample("debida tanto camión que hay tienen", "debida tanto camión que hay tienen"));
        this.tests.add(new TestSample("debida tanto camión que hay tienen que", "debida tanto camión que hay tienen que"));
        this.tests.add(new TestSample("Entonces todos van por la nacional", "Entonces todos van por la nacional"));
        this.tests.add(new TestSample("que pagar autopista", "que pagar autopista"));
        this.tests.add(new TestSample(" Entonces todos van", " Entonces todos van"));
        this.tests.add(new TestSample("pagar autopista", "pagar autopista"));
        this.tests.add(new TestSample(" Entonces todos van por", " Entonces todos van por"));
        this.tests.add(new TestSample("por la nacional y", "por la nacional y"));
        this.tests.add(new TestSample("la nacional y se libera", "la nacional y se libera"));
        this.tests.add(new TestSample("se libera la autopista", "se libera la autopista"));
        this.tests.add(new TestSample(" Vuelvo a decir de nuevo", " Vuelvo a decir de nuevo"));
        this.tests.add(new TestSample("la autopista", "la autopista"));
        this.tests.add(new TestSample(" Te vuelvo a decir de nuevo", " Te vuelvo a decir de nuevo"));
        this.tests.add(new TestSample(" pues sería la", " pues sería la"));
        this.tests.add(new TestSample("pues sería la solución", "pues sería la solución"));
        this.tests.add(new TestSample(" Es hartazgo", " Es hartazgo"));
        this.tests.add(new TestSample("solución", "solución"));
        this.tests.add(new TestSample(" Es hartazgo", " Es hartazgo"));
        this.tests.add(new TestSample(" También lo expresan los vecinos de la", " También lo expresan los vecinos de la"));
        this.tests.add(
                new TestSample(" También lo expresan los vecinos de la zona", " También lo expresan los vecinos de la zona"));
        this.tests.add(new TestSample("zona", "zona"));
        this.tests.add(new TestSample("Edad que hace falta arreglar la", "Edad que hace falta arreglar la"));
        this.tests.add(new TestSample("Edad que hace falta arreglar la", "Edad que hace falta arreglar la"));
        this.tests.add(new TestSample("el desdoblamiento", "el desdoblamiento"));
        this.tests.add(new TestSample("el desdoblamiento", "el desdoblamiento"));
        this.tests.add(new TestSample("Pero ya", "Pero ya"));
        this.tests.add(new TestSample(" pero ya porque", " pero ya porque"));
        this.tests.add(new TestSample("Pero ya", "Pero ya"));
        this.tests.add(new TestSample(" pero ya porque es tremendo y", " pero ya porque es tremendo y"));
        this.tests.add(new TestSample("es tremendo", "es tremendo"));
        this.tests.add(new TestSample(" siempre", " siempre"));
        this.tests.add(new TestSample("te", "te"));
        this.tests.add(new TestSample("Ese hartazgo también lo expresan los", "Ese hartazgo también lo expresan los"));
        this.tests.add(new TestSample("ha sido un peligro desde tres deberá yo", "ha sido un peligro desde 3 deberá yo"));
        this.tests.add(new TestSample("ha sido un peligro desde tres deberá yo", "ha sido un peligro desde 3 deberá yo"));
        this.tests.add(new TestSample("vecinos de", "vecinos de"));
        this.tests.add(new TestSample("de dieciocho", "de dieciocho"));
        this.tests.add(new TestSample("la zona edad que hace falta", "la zona edad que hace falta"));
        this.tests.add(new TestSample("diecinueve años era peligro", "diecinueve años era peligro"));
        this.tests.add(new TestSample(" ya son salidas", " ya son salidas"));
        this.tests.add(new TestSample(" ya son salidas", " ya son salidas"));
        this.tests.add(new TestSample("arreglar la", "arreglar la"));
        this.tests.add(new TestSample("de hace", "de hace"));
        this.tests.add(new TestSample("de hace treinta años", "de hace 30 años"));
        this.tests.add(new TestSample(" No", " No"));
        this.tests.add(new TestSample("el desdoblamiento", "el desdoblamiento"));
        this.tests.add(new TestSample(" Pero ya", " Pero ya"));
        this.tests.add(new TestSample("treinta años", "treinta años"));
        this.tests.add(new TestSample(" No hay no hay rotondas", " No hay no hay rotondas"));
        this.tests.add(new TestSample("hay no hay rotondas", "hay no hay rotondas"));
        this.tests.add(new TestSample("pero ya porque tremendo siempre", "pero ya porque tremendo siempre"));
        this.tests.add(new TestSample("Piden que se libere de la autopista", "Piden que se libere de la autopista"));
        this.tests.add(new TestSample("Piden que se libere la autopista y", "Piden que se libere la autopista y"));
        this.tests.add(new TestSample("ha sido un peligro desde", "ha sido un peligro desde"));
        this.tests.add(new TestSample("y que se invierta para reformar ese tramo de la",
                "y que se invierta para reformar ese tramo de la"));
        this.tests.add(new TestSample("que se invierta para reformar ese tramo de la carretera",
                "que se invierta para reformar ese tramo de la carretera"));
        this.tests.add(new TestSample("tres deberá yo de dieciocho", "tres deberá yo de dieciocho"));
        this.tests.add(new TestSample("carretera", "carretera"));
        this.tests.add(new TestSample("son salidas de hace treinta años", "son salidas de hace 30 años"));
        this.tests.add(new TestSample(" No", " No"));
        this.tests.add(new TestSample("En", "En"));
        this.tests.add(new TestSample("En", "En"));
        this.tests.add(new TestSample("hay", "hay"));
        this.tests.add(new TestSample("España los", "España los"));
        this.tests.add(new TestSample("España los delitos contra la seguridad", "España los delitos contra la seguridad"));
        this.tests.add(new TestSample("no hay rotondas Piden que se libere de la autopista",
                "no hay rotondas Piden que se libere de la autopista"));
        this.tests.add(new TestSample("delitos contra la seguridad vial son la causa de una",
                "delitos contra la seguridad vial son la causa de una"));
        this.tests.add(new TestSample("vial son la causa de una", "vial son la causa de una"));
        this.tests.add(new TestSample("y que se invierta para reformar ese tramo de la",
                "y que se invierta para reformar ese tramo de la"));
        this.tests.add(new TestSample("de cada tres condenas", "de cada 3 condenas"));
        this.tests.add(new TestSample(" En dos mil dieciocho hubo más", " En 2018 hubo más"));
        this.tests.add(new TestSample("de cada tres condenas", "de cada 3 condenas"));
        this.tests.add(new TestSample(" En dos mil dieciocho hubo más", " En 2018 hubo más"));
        this.tests.add(new TestSample("carretera", "carretera"));
        this.tests.add(new TestSample("de ochenta", "de ochenta"));
        this.tests.add(new TestSample("un nueve por ciento más que el año anterior", "un 9% más que el año anterior"));
        this.tests.add(new TestSample("un 9% más que el año anterior", "un 9% más que el año anterior"));
        this.tests.add(new TestSample("tres vehículos el sesenta por", "tres vehículos el sesenta por"));
    }


}