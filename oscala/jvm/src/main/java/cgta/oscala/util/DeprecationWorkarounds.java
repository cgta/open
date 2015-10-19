package cgta.oscala.util;

//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman
// All Rights Reserved
// please contact ben@jackman.biz
// for licensing inquiries
// Created by bjackman @ 7/11/14 1:00 AM
//////////////////////////////////////////////////////////////

/**
 * This class exists because Scala doesn't have a suppress warnings annotations
 */
public class DeprecationWorkarounds {
    @SuppressWarnings("deprecation")
    public static void stopThread(Thread t) {
        t.stop();
    }
}
