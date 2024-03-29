/*
 * @fileoverview    {NintendoJoystickLocal}
 *
 * @version         2.0
 *
 * @author          Dyson Arley Parra Tilano <dysontilano@gmail.com>
 *
 * @copyright       Dyson Parra
 * @see             github.com/DysonParra
 *
 * History
 * @version 1.0     Implementation done.
 * @version 2.0     Documentation added.
 */
package com.project.dev.joystick.name.nintendo.type;

import com.project.dev.joystick.name.generic.setter.GenericJoystickButtonSetter;
import com.project.dev.joystick.name.generic.type.GenericJoystickLocal;
import com.project.dev.joystick.name.nintendo.NintendoJoystick;
import com.project.dev.joystick.name.nintendo.setter.NintendoJoystickButtonSetter;

/**
 * FIXME: Description of {@code NintendoJoystickLocal}. Contiene las definiciones y operaciones para
 * un joystick de nombre nintendo que funciona localmente.
 *
 * @author Dyson Parra
 * @since 11
 */
public class NintendoJoystickLocal extends GenericJoystickLocal implements NintendoJoystick {

    /**
     * TODO: Description of {@code NintendoJoystickLocal}.
     *
     * @throws java.lang.Exception
     */
    public NintendoJoystickLocal() throws Exception {
        super(JOYSTICK_NAME);
        GenericJoystickButtonSetter buttonSetter = new NintendoJoystickButtonSetter();
        buttonSetter.setGenericJoystickButtons(this);
    }
}
