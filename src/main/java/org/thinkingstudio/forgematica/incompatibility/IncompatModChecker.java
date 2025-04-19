package org.thinkingstudio.forgematica.incompatibility;

public class IncompatModChecker {
    public static boolean isValkyrienSkiesLoaded() {
        return ModVersionCheckHelper.doesModVersionSatisfyPredicate("valkyrienskies", "2.4.0");
    }
}
