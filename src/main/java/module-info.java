module com.kludwisz.fishcracker {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.logging;
    requires com.github.kwhat.jnativehook;

    requires mc.core;
    requires mc.seed;
    requires mc.math;
    requires mc.noise;
    requires mc.biome;
    requires mc.terrain;
    requires mc.feature;

    opens com.kludwisz.fishcracker to javafx.fxml;
    exports com.kludwisz.fishcracker;
    exports com.kludwisz.fishcracker.controls;
    opens com.kludwisz.fishcracker.controls to javafx.fxml;
}