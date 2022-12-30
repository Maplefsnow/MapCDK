package me.maplef.mapcdk.utils;

import me.maplef.mapcdk.CDK;

import java.util.HashMap;
import java.util.Map;

public class CDKLib {
    // store CDKs temporarily
    // a key is created when a player types "/mapcdk newcdk"
    // a key is removed when a player closes MapCDK GUI
    public static Map<String, CDK> cdkMap = new HashMap<>();
}
