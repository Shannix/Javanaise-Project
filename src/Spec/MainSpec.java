/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Spec;

import java.util.ArrayList;

/**
 *
 * @author scra
 */
public class MainSpec {

    public static void main(String[] args) {
        ArrayList<Thread> bursts = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            bursts.add(new Thread(new SpecIrc()));
        }

        for (Thread burst : bursts) {
            burst.start();
        }
    }
}
