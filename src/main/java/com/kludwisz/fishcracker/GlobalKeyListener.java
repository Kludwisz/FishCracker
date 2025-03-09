package com.kludwisz.fishcracker;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GlobalKeyListener implements NativeKeyListener {
    private Consumer<Void> f3cAction;

    private boolean f3Pressed = false;
    private boolean f3c = false;

    public void setF3CAction(Consumer<Void> action) {
        f3cAction = action;
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        if (e.getKeyCode() == NativeKeyEvent.VC_F3) {
            f3Pressed = true;
        }

        if (!f3c && f3Pressed && e.getKeyCode() == NativeKeyEvent.VC_C) {
            //System.out.println("F3 + C detected globally!");
            f3c = true;

            if (f3cAction != null) {
                f3cAction.accept(null);
            }
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        if (e.getKeyCode() == NativeKeyEvent.VC_F3) {
            f3Pressed = false;
            f3c = false;
        }
        if (e.getKeyCode() == NativeKeyEvent.VC_C) {
            f3c = false;
        }
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {}

    // --------------------------------------------------------------------------------------

    public static void register(GlobalKeyListener gkl) {
        try {
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.OFF);
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(gkl);
        }
        catch (Exception e) {
            System.err.println("Could not set up global key listener!");
            e.printStackTrace();
        }
    }
}
