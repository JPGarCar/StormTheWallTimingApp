package ui.widgets;

import models.Run;
import ui.UIAppLogic;

/**
 * Used to create {@link ui.widgets.CustomHBox}(s) from a type parameter.
 *
 * @param <T>   The CustomHBox to be built.
 */
public interface CustomHBoxFactory<T> {

    /**
     * Creates an object (CustomHBox) of the self.
     *
     * @param run   Run to be used to create the CustomHBox.
     * @param controller    Controller to connect to the CustomHBox.
     * @return  The object created from self.
     */
    T newObject(Run run, UIAppLogic controller);
}
