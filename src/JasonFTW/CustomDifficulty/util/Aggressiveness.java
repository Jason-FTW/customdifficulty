package jasonftw.CustomDifficulty.util;

public enum Aggressiveness {
    AGGRESSIVE,
    FRIENDLY,
    NULL,
    PASSIVE;
	
	private Aggressiveness() {}
    
    private Aggressiveness(String string2, int n2) {}

    public static Aggressiveness valueOfString(String string) {
        if (string == null) {
            return NULL;
        }
        if (string.startsWith("a") || string.startsWith("A")) {
            return AGGRESSIVE;
        }
        if (string.startsWith("p") || string.startsWith("P")) {
            return PASSIVE;
        }
        if (string.startsWith("f") || string.startsWith("F")) {
            return FRIENDLY;
        }
        return NULL;
    }
}

