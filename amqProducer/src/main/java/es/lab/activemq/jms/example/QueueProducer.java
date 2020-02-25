/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.lab.activemq.jms.example;

import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory;

/**
 *
 * @author fran
 */
public class QueueProducer {
    private static final int _NUM_MENSAJES = 1;
    private static final int _TIMEOUT      = 1000;

    /**
     *
     * @return
     */
    private static String _getURL() {
        return "tcp://localhost:61616" + "?ha=true&retryInterval=1000&retryIntervalMultiplier=1.0&reconnectAttempts=-1";
    }

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        javax.jms.Connection c           = null;
        String               username    = null;
        String               password    = null;
        int                  numMensajes = _NUM_MENSAJES;
        int                  timeout     = _TIMEOUT;

        if (args.length < 1)
        {
            System.err.println("\t Ejecuta: QueueProducer <nombreCola> <numMensajes> <timeout-ms> <username> <password>");
            System.exit(1);
        }

        if (args.length >= 2) numMensajes = Integer.parseInt(args[1]);
        if (args.length >= 3) timeout     = Integer.parseInt(args[2]);
        if (args.length >= 4) username    = args[3];
        if (args.length >= 5) password    = args[4];

        System.out.println("Parametros:");
        System.out.println("\t - cola:          " + args[0]);
        System.out.println("\t - num. mensajes: " + numMensajes);
        System.out.println("\t - timeout:       " + timeout);
        System.out.println("\t - username:      " + username);
        System.out.println("\t - password:      " + password);

        try
        {
            //0.- Engancha con el destino
            final Queue q = ActiveMQJMSClient.createQueue(args[0]);

            //1.- Creamos la Factoria de conexion
            final String URI_AMQ = _getURL();
            System.out.println("Conectando: " + URI_AMQ);
            final ConnectionFactory cf = new ActiveMQJMSConnectionFactory(URI_AMQ);

            //2.- Crea una conexion JMS
            c = cf.createConnection(username, password);

            c.start();

            //3.- Crea una sesion
            // createSession(transated, aknowledgwMode)
            final Session s = c.createSession(false, Session.AUTO_ACKNOWLEDGE);

            //4.- Crea un Productor
            final MessageProducer p = s.createProducer(q);

            for (int i = 0; i < numMensajes; i++)
            {
                //5.- Crea el mensaje de texto
                final TextMessage m = s.createTextMessage("Mensaje " + i + ".");

                System.out.println("Enviando ------------> " + m.getText());

                //6 Envia el mensaje
                p.send(m);

                Thread.currentThread().sleep(timeout);
            }
        }
        finally
        {
            if (c != null) c.close();
        }
    }
}
