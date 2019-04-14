package com.deckerth.thomas.eggservice.model;

import com.deckerth.thomas.eggservice.R;
import com.deckerth.thomas.eggservice.views.MainActivity;

public class MemberEntity implements Member {

    private String name;

    private Member.Gusto gusto;

    public MemberEntity(String newName) {
        name = newName;
        gusto = Gusto.NO_EGG;
    }

    public MemberEntity(String newName, Gusto aGusto) {
        name = newName;
        if (aGusto == null) {
            gusto = Gusto.NO_EGG;
        } else {
            gusto = aGusto;
        }
    }

    public MemberEntity(String newName, String gustoSymbol) {
        name = newName;
        gusto = Gusto.NO_EGG;
        if (gustoSymbol.contentEquals("SOFT")) {
            gusto = Gusto.SOFT;
        } else if (gustoSymbol.contentEquals("MEDIUM")) {
            gusto = Gusto.MEDIUM;
        } else if (gustoSymbol.contentEquals("HARD")) {
            gusto = Gusto.HARD;
        } else if (gustoSymbol.contentEquals("FRIED_EGG")) {
            gusto = Gusto.FRIED_EGG;
        } else if (gustoSymbol.contentEquals("SCRAMBLED_EGG")) {
            gusto = Gusto.SCRAMBLED_EGG;
        } else if (gustoSymbol.contentEquals("NO_EGG")) {
            gusto = Gusto.NO_EGG;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Gusto getGusto() {
        if (gusto == null) {
            return Gusto.NO_EGG;
        } else {
            return gusto;
        }
    }

    @Override
    public void setGusto(Gusto newGusto) {
        gusto = newGusto;
    }

    @Override
    public String getGustoFormatted() {
        switch (gusto) {
            case SOFT: return MainActivity.Current.getStringFromResource(R.string.gusto_soft);
            case MEDIUM: return MainActivity.Current.getStringFromResource(R.string.gusto_medium);
            case HARD: return MainActivity.Current.getStringFromResource(R.string.gusto_hard);
            case FRIED_EGG: return MainActivity.Current.getStringFromResource(R.string.gusto_fried_egg);
            case SCRAMBLED_EGG: return MainActivity.Current.getStringFromResource(R.string.gusto_scrambled_egg);
            case NO_EGG: return MainActivity.Current.getStringFromResource(R.string.gusto_no_egg);
        }
        return null;
    }

    @Override
    public String getGustoSymbol() {
        switch (gusto) {
            case SOFT: return "SOFT";
            case MEDIUM: return "MEDIUM";
            case HARD: return "HARD";
            case SCRAMBLED_EGG: return "SCRAMBLED_EGG";
            case FRIED_EGG: return "FRIED_EGG";
            case NO_EGG: return "NO_EGG";
        }
        return null;
   }
}
