package com.vakuor.knightsandgoldmines.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.vakuor.knightsandgoldmines.Main;

/** Launches the desktop (LWJGL) application. */
public class DesktopLauncher {
    public static void main(String[] args) {
        createApplication();
    }

    @SuppressWarnings("UnusedReturnValue")
    private static LwjglApplication createApplication() {
        return new LwjglApplication(new Main(), getDefaultConfiguration());
    }

    private static LwjglApplicationConfiguration getDefaultConfiguration() {
        LwjglApplicationConfiguration configuration = new LwjglApplicationConfiguration();
        configuration.title = "Knights and goldmines";
        configuration.width = 1280;
        configuration.height = 720;
        for (int size : new int[] { 128, 64, 32, 16 }) {
            configuration.addIcon("main/libgdx" + size + ".png", FileType.Internal);
        }
        return configuration;
    }
}