/*
 * @fileoverview {FileName} se encarga de realizar tareas especificas.
 *
 * @version             1.0
 *
 * @author              Dyson Arley Parra Tilano <dysontilano@gmail.com>
 * Copyright (C) Dyson Parra
 *
 * @History v1.0 --- La implementacion de {FileName} fue realizada el 31/07/2022.
 * @Dev - La primera version de {FileName} fue escrita por Dyson A. Parra T.
 */
package com.project.dev.joystick.name.generic.type;

import com.project.dev.joystick.button.GenericButton;
import com.project.dev.joystick.listener.JoystickServerListener;
import com.project.dev.joystick.name.generic.GenericRemoteJoystick;
import com.project.dev.udp.generic.GenericUdpServer;
import com.project.dev.udp.generic.listener.UdpServerListener;
import java.util.regex.Pattern;

/**
 * FIXME: Definición de {@code GenericJoystickServer}. Contiene las definiciones y operaciones para
 * un joystick que se comunica con otro através de la web y para uno que unicamente recibe las
 * peticiones.
 *
 * @author Dyson Parra
 * @since 1.8
 */
public class GenericJoystickServer extends GenericRemoteJoystick {

    private GenericUdpServer udpServer = null;                                                      // Servidor udp del joystick.
    private Thread thread = null;                                                                   // Hilo donde se ejecuta el servidor udp del joystick.
    protected JoystickServerListener onJoystickServerListener = null;                               // Crea listener al joystick cliente.
    protected Thread clientUpdateProcessor = new Thread(this::onClientUpdateButtonStatesRequest);   // Usada para procesar las peticiones de actalización del cliente.

    /**
     * TODO: Definición de {@code GenericJoystickServer}.
     *
     * @throws java.lang.Exception
     */
    public GenericJoystickServer() throws Exception {
        super(GenericRemoteJoystick.JOYSTICK_TYPE_SERVER);
    }

    /**
     * TODO: Definición de {@code GenericJoystickServer}.
     *
     * @param name es el nombre del joystick.
     * @throws java.lang.Exception
     */
    public GenericJoystickServer(String name) throws Exception {
        super(GenericRemoteJoystick.JOYSTICK_TYPE_SERVER, name);
    }

    /**
     * TODO: Definición de {@code getOnJoystickServerListener}.
     *
     * @return
     */
    public final JoystickServerListener getOnJoystickServerListener() {
        return onJoystickServerListener;
    }

    /**
     * TODO: Definición de {@code setOnJoystickServerListener}.
     *
     * @param onJoystickServerListener
     */
    public final void setOnJoystickServerListener(JoystickServerListener onJoystickServerListener) {
        this.onJoystickServerListener = onJoystickServerListener;
    }

    /**
     * TODO: Definición de {@code isRunningJoystickServer}.
     *
     * @return
     */
    public final boolean isRunningJoystickServer() {
        return udpServer.isStartServer();
    }

    /**
     * FIXME: Definición de {@code setButtons}. Asigna botones al joystick.
     *
     * @param newButtons son los botones que se asignarán como los botones del joystick.
     * @throws java.lang.Exception
     */
    @Override
    public final void setButtons(GenericButton[] newButtons) throws Exception {
        try {
            if (udpServer != null && udpServer.isStartServer())                             // Si el servidor udp se está ejecutando.
                udpServer.stop();                                                           // Para el servidor udp.

            this.buttons = newButtons;                                                      // Almacena los botones indicados como parametro.
            this.buttonQuantity = this.buttons.length;                                      // Almacena la cantidad de botones del control.
            this.prevButtonsStates = new byte[buttonQuantity];
            this.actualButtonsStates = new byte[buttonQuantity];
            for (int i = 0; i < actualButtonsStates.length; i++) {
                actualButtonsStates[i] = BUTTON_UNPRESSED;
                prevButtonsStates[i] = BUTTON_UNPRESSED;
            }
            int maxButtonNameSize = GenericButton.MAX_BUTTON_NAME_SIZE;                     // Almacena el tamaño máximo del nombre de cada botón.

            int messageSize = maxButtonNameSize > buttonQuantity
                    ? maxButtonNameSize : buttonQuantity;                                      // Almacena el tamaño de lso mensajes como el mayor entre la máxima longitud del nombre de cada botón y la cantidad de botones.

            udpServer = new GenericUdpServer(messageSize);                                  // Inicializa el servidor udp con tamaño de mensaje indicado.
            udpServer.setOnRequestListener(new UdpServerListener() {
                @Override
                public void onConnectRequest() {

                }

                @Override
                public void onDisconnectRequest() {
                    if (onJoystickServerListener != null)                                   // Si el joystick servidor tiene un listener asociado.
                        onJoystickServerListener.onClientDisconnected();
                }

                @Override
                public boolean onGenericRequest(byte[] requestMessage) {
                    //System.out.println(new String(getPrevButtonsStates()));
                    //System.out.println(new String(getActualButtonsStates()));
                    //System.out.println(new String(requestMessage));

                    if (requestMessage.length != actualButtonsStates.length)
                        return false;

                    prevButtonsStates = actualButtonsStates;                                // Almacena los estados anteriores de los botones.
                    actualButtonsStates = requestMessage;                                   // Obtiene los nuevos estados de los botones.
                    onClientUpdateButtonStatesRequest();                                    // Ejecuta acción si se actualizan los estados de los botones.
                    return true;
                }

                @Override
                public String onGenericRequestWithGenericResponse(byte[] requestMessage) {  // Si recibió una petici´n genérica con respuesta genérica.
                    String clientMessage = new String(requestMessage);                      // Obtiene el mensaje del cliente.
                    //System.out.println("'" + clientMessage + "'");
                    switch (clientMessage) {                                                // Evalúa el tipo de mensaje recibido.
                        case GET_BUTTON_QUANTITY_REQUEST:                                   // Si recibió mensaje de obtener la cantidad de botones.
                            return String.valueOf(getButtonQuantity());                     // Devuelve mensaje con la cantidad de botones del joystick.

                        case GET_JOYSTICK_NAME_REQUEST:                                     // Si recibió mensaje de obtener el nombre del joystick.
                            return getName();                                               // Devuelve mensaje con el nombre del joystick.

                        default:
                            if (Pattern.matches(GET_BUTTON_NAME_REQUEST_REGEX, clientMessage)) {
                                int buttonNumber = Integer.valueOf(clientMessage.substring(clientMessage.indexOf(':') + 1));
                                if (buttonNumber < getButtonQuantity()) {
                                    GenericButton actualButton = buttons[buttonNumber];
                                    return actualButton.getName();
                                } else
                                    return GenericUdpServer.RESPONSE_REJECT;
                            } else
                                return GenericUdpServer.RESPONSE_REJECT;
                    }
                }

                @Override
                public void onConfirmRequest() {
                    if (onJoystickServerListener != null)                                   // Si el joystick servidor tiene un listener asociado.
                        onJoystickServerListener.onClientConnected();
                }

                @Override
                public void onTimeOutRequest() {

                }
            });

            serverIpAddress = udpServer.getServerIpAddress();
            serverPort = udpServer.getServerPort();
        } catch (Exception e) {
            throw new Exception("Invalid buttons");
        }
    }

    /**
     * TODO: Definición de {@code setServerIpAddress}.
     *
     */
    @Override
    protected final void setServerIpAddress(String serverIpAddress) throws Exception {
        throw new UnsupportedOperationException("Cant't set ipAddres to a server.");
    }

    /**
     * TODO: Definición de {@code setServerPort}.
     *
     */
    @Override
    protected final void setServerPort(int serverPort) throws Exception {
        this.serverPort = serverPort;
        this.udpServer.setServerPort(serverPort);
    }

    /**
     * FIXME: Definición de {@code onButtonStateChanged}. Invocado cuando se cambia el estado del
     * componenet genérico.
     *
     * @param buttonNumber es el número del botón que cambió.
     * @return Si fue posible cambiar el estado del botón.
     */
    @Override
    public boolean onButtonStateChanged(int buttonNumber, byte newButtonState) {
        System.out.println("States updated in server");
        return true;
    }

    /**
     * FIXME: Definición de {@code startServer}. Inicia el servidor del joystick.
     *
     * @return si se pudo iniciar el servidor.
     */
    public final boolean startServer() {
        if (udpServer != null && !udpServer.isStartServer()) {                  // Si el servidor udp ya se creo y no está corriendo.
            thread = new Thread(udpServer);                                     // Crea un nuevo hilo con el servidor udp.
            thread.start();                                                     // Inicia el servidor udp.
            return true;                                                        // Devuelve indicando operación exitosa.
        } else                                                                    // Si el servidor udp no se ha creado o está ejecutandose.
            return false;                                                       // Devuelve indicando operación no exitosa.
    }

    /**
     * FIXME: Definición de {@code stopServer}. Inicia el servidor del joystick.
     *
     * @return si se pudo parar el servidor.
     */
    public final boolean stopServer() {
        if (udpServer != null && udpServer.isStartServer()) {                   // Si el servidor udp ya se creo y está corriendo.
            udpServer.stop();                                                   // Para el servidor.
            return true;                                                        // Devuelve indicando operación exitosa.
        } else                                                                    // Si el servidor udp no se ha creado o no está ejecutandose.
            return false;                                                       // Devuelve indicando operación no exitosa.
    }

    /**
     * FIXME: Definición de {@code onClientUpdateButtonStatesRequest}. Realiza una acción luego de
     * que se obtenga una petición de actualización de estado de los botones del joystick genérico.
     */
    private void onClientUpdateButtonStatesRequest() {
        for (int i = 0; i < actualButtonsStates.length; i++)
            //if (actualButtonsStates[i] != prevButtonsStates[i])               // Si el botón cambió de estado.
            switch (actualButtonsStates[i]) {
                case BUTTON_UNPRESSED:                                      // El estado es Unpressed.
                case BUTTON_RELEASED:                                       // El estado es released.
                    if (actualButtonsStates[i] != prevButtonsStates[i])     // Si el botón cambió de estado.
                        buttons[i].unTouchButton();
                    break;

                case BUTTON_TYPED:                                          // El estado es typed.
                case BUTTON_PRESSED:                                        // El estado es Pressed.
                    buttons[i].touchButton();
                    break;
            }
    }
}
