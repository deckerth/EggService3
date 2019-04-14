package com.deckerth.thomas.eggservice.model;

public interface Member {

    enum Gusto {
        SOFT("SOFT"), MEDIUM("MEDIUM"), HARD("HARD"), FRIED_EGG("FRIED_EGG"), SCRAMBLED_EGG("FRIED_EGG"), NO_EGG("NO_EGG");

        private final String gustoStr;

        Gusto(String gusto) {
            this.gustoStr = gusto;
        }

        static public Gusto fromString(String gusto) {
            return Gusto.valueOf(gusto);
        }
    }

    String getName();
    Gusto getGusto();
    void setGusto(Gusto newGusto);
    String getGustoFormatted();
    String getGustoSymbol();
}
