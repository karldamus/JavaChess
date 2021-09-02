package users;

import java.util.Hashtable;

public class ColourScheme {
    // colour scheme dictionary follows String[] pattern: {darkColour, lightColour}
    Hashtable<SchemePresets, String[]> colourSchemeDict = new Hashtable<SchemePresets, String[]>() {{
        put(SchemePresets.DEFAULT, new String[]{"#007E08", "#EBEBEB"});
        put(SchemePresets.BLACK_AND_WHITE, new String[]{"#0A0A0A", "#EBEBEB"});
        put(SchemePresets.PINK_MIST, new String[]{"#fa04b4", "#EBEBEB"});
//        put()
    }};

    public ColourScheme() {

    }

    public ColourScheme(SchemePresets schemePreset) {

    }
}
