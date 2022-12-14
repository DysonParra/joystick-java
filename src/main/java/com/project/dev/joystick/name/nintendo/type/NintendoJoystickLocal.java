/*
 * @fileoverview    {NintendoJoystickLocal} se encarga de realizar tareas específicas.
 *
 * @version         2.0
 *
 * @author          Dyson Arley Parra Tilano <dysontilano@gmail.com>
 *
 * @copyright       Dyson Parra
 * @see             github.com/DysonParra
 *
 * History
 * @version 1.0     Implementación realizada.
 * @version 2.0     Documentación agregada.
 */
package com.project.dev.joystick.name.nintendo.type;

import com.project.dev.joystick.name.generic.setter.GenericJoystickButtonSetter;
import com.project.dev.joystick.name.generic.type.GenericJoystickLocal;
import com.project.dev.joystick.name.nintendo.NintendoJoystick;
import com.project.dev.joystick.name.nintendo.setter.NintendoJoystickButtonSetter;

/**
 * FIXME: Definición de {@code NintendoJoystickLocal}. Contiene las definiciones y operaciones para
 * un joystick de nombre nintendo que funciona localmente.
 *
 * @author Dyson Parra
 * @since 1.8
 */
public class NintendoJoystickLocal extends GenericJoystickLocal implements NintendoJoystick {

    /**
     * TODO: Definición de {@code NintendoJoystickLocal}.
     *
     * @throws java.lang.Exception
     */
    public NintendoJoystickLocal() throws Exception {
        super(JOYSTICK_NAME);
        GenericJoystickButtonSetter buttonSetter = new NintendoJoystickButtonSetter();
        buttonSetter.setGenericJoystickButtons(this);
    }
}
