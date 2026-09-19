package com.lx862.mtrscripting.api;

public class ClassRule {
    private final String match;
    private final boolean isWildcard;
    private final boolean allowAll;

    public ClassRule(String match, boolean isWildcard) {
        this.match = match;
        this.isWildcard = isWildcard;
        this.allowAll = match.equals("*");
    }

    public static ClassRule parse(String str) {
        if(str.endsWith(".*")) {
            return new ClassRule(str.substring(0, str.length() - 2), true);
        } else {
            return new ClassRule(str, false);
        }
    }

    public boolean match(String className) {
        return allowAll || (isWildcard ? className.startsWith(match) : className.equals(match));
    }
}
