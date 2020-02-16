/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.lab.activemq.jms.example;

import javax.jms.Connection;
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
    private static final int MAX = 1;

    /**
     *
     * @return
     */
    private static String _getURL() {
        return "tcp://localhost:61616" + "?ha=true&retryInterval=1000&retryIntervalMultiplier=1.0&reconnectAtempts=-1";
    }

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Connection c = null;

        if (args.length != 2)
        {
            System.err.println("\t Ejecuta: QueueProducer <nombreCola> <timeout-ms>");
            System.exit(1);
        }

        int timeout = Integer.parseInt(args[1]);

        try
        {
            //0.- Engancha con el destino
            final Queue q = ActiveMQJMSClient.createQueue(args[0]);

            //1.- Creamos la Factoria de conexion
            final String URI_AMQ = _getURL();
            System.out.println("Conectando: " + URI_AMQ);
            final ConnectionFactory cf = new ActiveMQJMSConnectionFactory(URI_AMQ);

            //2.- Crea una conexion JMS
            c = cf.createConnection();

            c.start();

            //3.- Crea una sesion
            // createSession(transated, aknowledgwMode)
            final Session s = c.createSession(false, Session.AUTO_ACKNOWLEDGE);

            //4.- Crea un Productor
            final MessageProducer p = s.createProducer(q);


            for (int i = 0; i < MAX; i++)
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
