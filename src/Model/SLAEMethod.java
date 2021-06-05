package Model;

public class SLAEMethod {
    protected int sumCount = 0, substrCount = 0,multCount = 0, divCount = 0;

    protected void nullifyCounters() {
        sumCount = 0;
        substrCount = 0;
        multCount = 0;
        divCount = 0;
    }

    public int getSumCount() {
        return sumCount;
    }

    public int getSubstrCount() {
        return substrCount;
    }

    public int getMultCount() {
        return multCount;
    }

    public int getDivCount() {
        return divCount;
    }

}
