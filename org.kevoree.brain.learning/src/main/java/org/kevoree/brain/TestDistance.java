package org.kevoree.brain;

import org.kevoree.brain.util.Distance;

/**
 * Created by assaa_000 on 8/14/2014.
 */
public class TestDistance {
    public static void main (String[] args){
        String s1="Assaad";
        String s2="Assaad 2";
        System.out.println("Distance "+ Distance.levenshteinDistance(s1,s2));
    }
}
