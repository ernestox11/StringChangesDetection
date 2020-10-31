package com.identificador.testing;

public class TestSample {
    private String orginal;
    private String transformed;
    private int nExpectedTransformations;

    public TestSample(String orginal, String transformed) {
        this.orginal = orginal;
        this.transformed = transformed;
        this.nExpectedTransformations = -1;
    }

    public TestSample(String orginal, String transformed, int nExpectedTransformation) {
        this.orginal = orginal;
        this.transformed = transformed;
        this.nExpectedTransformations = nExpectedTransformation;
    }

    public String getOriginal() {
        return orginal;
    }

    public String getTransformed() {
        return transformed;
    }

    public int getnExpectedTransformations() {
        return nExpectedTransformations;
    }
}
