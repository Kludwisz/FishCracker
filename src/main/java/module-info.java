module com.kludwisz.fishcracker {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.logging;

    requires mc.core;
    requires mc.seed;
    requires mc.math;
    requires mc.noise;
    requires mc.biome;
    requires mc.feature;
    requires java.desktop;

    opens com.kludwisz.fishcracker to javafx.fxml;

    exports com.kludwisz.fishcracker;
    exports com.kludwisz.fishcracker.math;
    exports com.kludwisz.fishcracker.cracker;
}