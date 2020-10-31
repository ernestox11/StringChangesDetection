package com.identificador;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Identifier {

    //--STRUCTURES
    String originalString; //Original string copy
    String transformedString; //Transformed string copy
    String[] o; //Original string segments
    String[] oc; //Original string clean segments
    String[] t; //Transformed string segments
    String[] tc; //Transformed string clean segments

    //--INDEXES-- NOTE: oStringStart, oStringEnd, tStringStart, tStringEnd are used to generate the results.
    int oStringCurrent, oStringStart, oStringEnd;
    int tStringCurrent, tStringStart, tStringEnd;
    int oCurrent;
    int ocCurrent;
    int tCurrent;
    int tcCurrent;
    //POSITION INSIDE EACH SEGMENT
    int oLastChangeInsideSegment;
    int tLastChangeInsideSegment;

    //FLAG TO INDICATE A CHANGE ON THE PREVIOUS SEGMENT
    boolean changeState;
    //ADDITIONAL STRUCTURES TO GENERATE RESULTS
    String transformedText;
    ArrayList<Result> resultsList;

    //--CONSTRUCTOR--///////////////////////////////////////////////////////////////////////////////////////////////////
    public Identifier(String os, String ts) {
        originalString = os;
        transformedString = ts;
        o = splitString(originalString);
        oc = cleanString(originalString);
        t = splitString(transformedString);
        tc = cleanString(transformedString);

        oStringCurrent = 0;
        oStringStart = 0;
        oStringEnd = 0;
        tStringCurrent = 0;
        tStringStart = 0;
        tStringEnd = 0;
        oCurrent = 0;
        ocCurrent = 0;
        tCurrent = 0;
        tcCurrent = 0;

        oLastChangeInsideSegment = 0;
        tLastChangeInsideSegment = 0;

        changeState = false;

        transformedText = "";
        resultsList = new ArrayList<>();
    }

    //--HELPER FUNCTIONS--//////////////////////////////////////////////////////////////////////////////////////////////

    private String[] splitString(String s) { //--Generate array of String segments
        List<String> stringSegments = new ArrayList<>(Arrays.asList(s.split(" ", 0)));
        stringSegments.removeAll(Arrays.asList("", null));
        String[] segments = new String[stringSegments.size()];
        stringSegments.toArray(segments);
        return segments;
    }

    private String[] cleanString(String s) { //--Generate array of String segments with non-alphanumerical characters removed
        List<String> stringSegments = new ArrayList<>(Arrays.asList(s.split("(?U)[^\\p{Alpha}0-9']+", 0)));
        stringSegments.removeAll(Arrays.asList("", null));
        String[] segments = new String[stringSegments.size()];
        stringSegments.toArray(segments);
        return segments;
    }

    private void oCurrentIncrease() { //--Only increase oCurrent and oStringCurrent
        oStringCurrent = originalString.indexOf(o[oCurrent], oStringCurrent) + o[oCurrent].length();
        oCurrent++;
        oLastChangeInsideSegment = 0;
    }

    private void oCurrentANDocCurrentIncrease() { //--Increase oCurrent and the corresponding ocCurrent
        if (oc[ocCurrent].equals(o[oCurrent])) {
            ocCurrent++;
        } else { //Increase ocCurrent to the first clean segment contained in the next non-clean orginal segment
            while (o[oCurrent].indexOf(oc[ocCurrent], oLastChangeInsideSegment) != -1) {
                ocCurrent++;
                oLastChangeInsideSegment = o[oCurrent].indexOf(oc[ocCurrent], oLastChangeInsideSegment) + oc[ocCurrent].length();
            }
        }
        oLastChangeInsideSegment = 0;
        oCurrentIncrease();
    }

    private void ocCurrentANDoCurrentIncrease() { //--Increase ocCurrent (and oCurrent if necessary)
        ocCurrent++;
        //If current original clean segment isn't cointained in current non-clean original segment
        if (o[oCurrent].indexOf(oc[ocCurrent], oLastChangeInsideSegment) == -1) {
            oCurrentIncrease();
        } else {
            oLastChangeInsideSegment = o[oCurrent].indexOf(oc[ocCurrent], oLastChangeInsideSegment) + oc[ocCurrent].length();
        }
    }

    private void tCurrentIncrease() { //--Only increase tCurrent and tStringCurrent
        tStringCurrent = transformedString.indexOf(t[tCurrent], tStringCurrent) + t[tCurrent].length();
        tCurrent++;
        tLastChangeInsideSegment = 0;
    }

    private void tCurrentANDtcCurrentIncrease() { ////--Increase tCurrent and the corresponding tcCurrent
        if (tc[tcCurrent].equals(t[tCurrent])) {
            tcCurrent++;
        } else { //Increase tcCurrent to the first clean segment contained in the next non-clean transformed segment
            while (t[tCurrent].indexOf(tc[tcCurrent], tLastChangeInsideSegment) != -1) {
                tcCurrent++;
                tLastChangeInsideSegment = t[tCurrent].indexOf(tc[tcCurrent], tLastChangeInsideSegment) + tc[tcCurrent].length();
            }
        }
        tLastChangeInsideSegment = 0;
        tCurrentIncrease();
    }

    private void tcCurrentANDtCurrentIncrease() { //Increase tcCurrent (and tCurrent if necessary)
        tcCurrent++;
        //If current clean transformed segment isn't cointained in current non-clean transformedsegment
        if (t[tCurrent].indexOf(tc[tcCurrent], tLastChangeInsideSegment) == -1) {
            tCurrentIncrease();
        } else {
            tLastChangeInsideSegment = t[tCurrent].indexOf(tc[tcCurrent], tLastChangeInsideSegment) + tc[tcCurrent].length();
        }
    }

    private void addResult() {//--Add obtained result to the results list
        transformedText = transformedString.substring(tStringStart, tStringEnd + 1);
        Result result = new Result();
        result.setResult(oStringStart, oStringEnd, transformedText);
        resultsList.add(result);
    }

    //--MAIN FUNCTION--/////////////////////////////////////////////////////////////////////////////////////////////////
    public ArrayList<Result> identifyChanges() {

        while (oCurrent < o.length) {

//--------- CASE 1 - Current original segment EQUALS Current transformed segment ---------------------------------------
            if (t[tCurrent].equals(o[oCurrent])) {
                //System.out.println("o: "+o[oCurrent] + " | equals | t: " + t[tCurrent]);//TEST

                //END OF TRANSFORMATION DETECTED
                if (changeState) { //--If current o segment equals current t segment and changes were made on the previous segment
                    //System.out.println("Last transformation index: " + o[oCurrent-1]);//TEST
                    changeState = false;
                    oStringEnd = oStringCurrent - 1;
                    tStringEnd = tStringCurrent - 1;
                    //System.out.println("Last transformed character on oString: "+originalString.charAt(oStringEnd));
                    //System.out.println("Last transformed character on tString: "+transformedString.charAt(tStringEnd));
                    addResult(); // Store result on results list <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                }
                // Move to the next segments
                if (oCurrent + 1 < o.length) {
                    oCurrentANDocCurrentIncrease();
                    tCurrentANDtcCurrentIncrease();
                } else {
                    //System.out.println("---END OF STRINGS REACHED---");//TEST
                    oCurrent = o.length; //SE RECOMIENDA USAR BREAK
                }

            }//-- CASE 1 - END

//--------- CASE 2 - Current original segment IS DIFFERENT TO Current transformed segment -------------------------------
            else {
                //System.out.println("o: "+o[oCurrent] + " | is not equal to | t: " + t[tCurrent]);//TEST
                //START OF TRANSFORMATION DETECTED
                if (!changeState) {
                    //System.out.println("Transformation starts on: " + o[oCurrent] + " | " + tc[tcCurrent]);//TEST
                    changeState = true;
                    if (oCurrent == 0) {
                        oStringStart = tStringStart = 0;
                    } else {
                        oStringStart = originalString.indexOf(o[oCurrent], oStringCurrent);
                        tStringStart = transformedString.indexOf(t[tCurrent], tStringCurrent);
                        //System.out.println("First transformed o character: " + originalString.charAt(oStringStart));//TEST
                        //System.out.println("First transformed t character: " + transformedString.charAt(tStringStart));//TEST
                    }

                }
//--------- CASE 2.1 - Segments ARE DIFFERENT, they belong to a transformation AND the end has been reached
                if (oCurrent == o.length - 1 || tCurrent == t.length - 1 || tcCurrent == tc.length - 1) {
                    //System.out.println("--- END (With a transformation) ---");//TEST
                    oCurrent = o.length; //SE RECOMIENDA USAR BREAK
                    oStringEnd = originalString.length() - 1;
                    tStringEnd = transformedString.length() - 1;
                    addResult();
                }

//--------- CASE 2.2 - Current transformed segmed is contained inside Current original Segment (When words have adjacent symbols: .pal pal. )
                else if (o[oCurrent].toLowerCase().indexOf(t[tCurrent].toLowerCase(), oLastChangeInsideSegment) != -1) {
                    //System.out.println("t: "+t[tCurrent] + " | is contained inside | o: " + o[oCurrent]);//TEST
                    oLastChangeInsideSegment = o[oCurrent].toLowerCase().indexOf(t[tCurrent].toLowerCase(), oLastChangeInsideSegment) + t[tCurrent].length();
                    //If oLastChangeInsideSegment equals the end of the current original segment OR the rest of the segment doesn't contain alphanumerical characters
                    if (oLastChangeInsideSegment == o[oCurrent].length()
                            || o[oCurrent].substring(oLastChangeInsideSegment).matches("(?U)[^\\p{Alpha}0-9']+")) {
                        //System.out.println("End of original segment reached!");//TEST
                        oCurrentANDocCurrentIncrease();
                    }
                    tCurrentANDtcCurrentIncrease();
                }

//--------- CASE 2.3 - Current clean transformed segment EQUALS current original segment (When a '¿' or any symbol is added to the transformed string)
                else if (tc[tcCurrent].equals(o[oCurrent])) {
                    //System.out.println("o: "+o[oCurrent] + " | equals | tc: " + tc[tcCurrent]);//TEST
                    oCurrentANDocCurrentIncrease();
                    tcCurrentANDtCurrentIncrease();
                }

//--------- CASE 2.4 - Current transformed clean segment is contained inside current original segment
                else if (o[oCurrent].toLowerCase().indexOf(tc[tcCurrent].toLowerCase(), oLastChangeInsideSegment) != -1) {
                    //System.out.println("tc: "+tc[tcCurrent] + " | is contained inside | o: " + o[oCurrent]);
                    oLastChangeInsideSegment = o[oCurrent].toLowerCase().indexOf(tc[tcCurrent].toLowerCase(), oLastChangeInsideSegment) + tc[tcCurrent].length();
                    //If oLastChangeInsideSegment equals the end of the current transformed segment OR the rest of the segment doesn't contain alphanumerical characterses
                    if (oLastChangeInsideSegment == o[oCurrent].length()
                            || o[oCurrent].substring(oLastChangeInsideSegment).matches("(?U)[^\\p{Alpha}0-9']+")) {
                        //System.out.println("End of original segment reached!");
                        oCurrentANDocCurrentIncrease();
                    }
                    tcCurrentANDtCurrentIncrease();
                }

//--------- CASE 2.5 - Current transformed segment is a number
                else if (t[tCurrent].matches("-?\\d+[\\.,]?[\\d]?%?") || t[tCurrent].toUpperCase().matches("^M{0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})$")) {
                    //System.out.println("t: "+t[tCurrent] + " is a number!");//TEST

                    //If the next transformed segment is the last transformed segment
                    if (tCurrent + 1 == t.length - 1) {
                        //System.out.println("tCurrent: " + tCurrent + " | t.length: " + t.length + " t[tCurrent]: " + t[tCurrent + 1]);//TEST

                        //If the last segment of both strings are equal
                        if (o[o.length - 1].equals((t[t.length - 1]))) {
                            //System.out.println("The last segments are equal!");//TEST
                            oCurrent = o.length - 1;
                            oStringCurrent = originalString.length() - o[oCurrent].length();
                            //System.out.println("originalString.length: " + originalString.length() + " oString Current: "+ oStringCurrent);//TEST
                        }
                        //If the last segment of both strings are different
                        else {
                            //System.out.println("The last segments are different!");//TEST
                            oCurrent = o.length; //SE RECOMIENDA USAR BREAK
                            oStringEnd = originalString.length();
                            tStringEnd = transformedString.length() - 1;
                            addResult();
                        }
                    }
                    //If the next transformed segment is not the last and is not a number
                    else if (!(t[tCurrent + 1].matches("-?\\d+[\\.,]?[\\d]?%?") ||
                            t[tCurrent + 1].toUpperCase().matches("^M{0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})$"))
                            && tcCurrent + 2 < tc.length) {
                        //System.out.println("Searching for the right match!");//TEST
                        ocCurrentANDoCurrentIncrease();
                        String tcNextSegment1 = tc[tcCurrent + 1];
                        String tcNextSegment2 = tc[tcCurrent + 2];
                        tcCurrentANDtCurrentIncrease();
                        while (!(oc[ocCurrent].equals(tcNextSegment1) && oc[ocCurrent + 1].equals(tcNextSegment2)) && tcCurrent < tc.length && ocCurrent < oc.length - 1) {
                            //System.out.println("oCurrent= "+oc[ocCurrent] +" "+oc[ocCurrent+1]+ " tCurrent: "+tc[tcCurrent] + " "+ tc[tcCurrent+1]);//TEST
                            ocCurrentANDoCurrentIncrease();
                        }
                    } else {
                        tCurrentANDtcCurrentIncrease();
                    }
                }

//--------- CASE 2.6 - Current transformed clean segment is a number
                else if (tc[tcCurrent].matches("-?\\d+[\\.,]?[\\d]?%?") || tc[tcCurrent].toUpperCase().matches("^M{0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})$")) {
                    //System.out.println("tc: "+tc[tcCurrent] + " is a number!");//TEST
                    //If current clean t segment is different to current t segment AND current clean t segment is contained inside current t segment
                    if (!tc[tcCurrent].equals(t[tCurrent]) && t[tCurrent].indexOf(tc[tcCurrent], tLastChangeInsideSegment) != -1) {
                        tLastChangeInsideSegment += tc[tcCurrent].length();//Se incrementa el indice interno de t
                    }
                    tcCurrentANDtCurrentIncrease();
                }
//--------- CASE 2.7 - Default case
                else {
                    oCurrentANDocCurrentIncrease();
                }
            }

        }// WHILE END
        return resultsList;

    }// MAIN FUNCTION END
}
