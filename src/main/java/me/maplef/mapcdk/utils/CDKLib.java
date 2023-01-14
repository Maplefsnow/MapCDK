package me.maplef.mapcdk.utils;

import me.maplef.mapcdk.CDK;

import java.util.HashMap;
import java.util.Map;

public class CDKLib {
    // store CDKs temporarily
    // a key is created when a player types "/mapcdk newcdk"
    public static Map<String, CDK> cdkMap = new HashMap<>();
}
