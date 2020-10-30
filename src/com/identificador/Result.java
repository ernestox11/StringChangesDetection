package com.identificador;

public class Result {
    int startIndex,endIndex;
    String transformedText;

    public Result(){
        this.startIndex = 0;
        this.endIndex = 0;
        this.transformedText = "";
    }

    public Result(int startIndex, int endIndex, String transformedText) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.transformedText = transformedText;
    }

    public int getStartIndex() {
        return this.startIndex;
    }

    public int getEndIndex() {
        return this.endIndex;
    }

    public String getTransformedText() {
        return this.transformedText;
    }

    public void setResult(int startIndex, int endIndex, String transformedText){
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.transformedText = transformedText;
    }

    public Result getResult(){
        Result result= new Result();
        result.setResult(this.startIndex,this.endIndex,this.transformedText);
        return result;
    }

    public String getResultString(){
        return "( " + this.startIndex + " , " + this.endIndex + " , " + this.transformedText + " )";
    }
}