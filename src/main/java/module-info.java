module com.kludwisz.fishcracker {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.logging;
    requires com.github.kwhat.jnativehook;

    opens com.kludwisz.fishcracker to javafx.fxml;
    exports com.kludwisz.fishcracker;
}