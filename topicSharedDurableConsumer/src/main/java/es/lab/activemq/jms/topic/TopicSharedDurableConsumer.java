/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.lab.activemq.jms.topic;

import javax.jms.TextMessage;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory;

/**
 *
 * @author fran
 */
public class TopicSharedDurableConsumer {
    private static final int _TIMEOUT_RECEIVE = 1000;
    private static final int _TIMEOUT_SLEEP   = 1000;

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
        javax.jms.Connection      c              = null;
        javax.jms.MessageConsumer mc             = null;
        String                    username       = null;
        String                    password       = null;
        int                       timeoutSleep   = _TIMEOUT_SLEEP;
        int                       timeoutReceive = _TIMEOUT_RECEIVE;

        if (args.length < 2)
        {
            System.err.println("Ejecuta: TopicSharedDurableConsumer <nombreTopic> <subscriptionName> <timeoutSleep> <timeoutReceive> <username> <password>");
            System.exit(1);
        }

        final String subscriptionName = args[1];

        if (args.length >= 3) timeoutSleep   = Integer.parseInt(args[2]);
        if (args.length >= 4) timeoutReceive = Integer.parseInt(args[3]);
        if (args.length >= 5) username       = args[4];
        if (args.length >= 6) password       = args[5];

        System.out.println("Parametros:");
        System.out.println("\t - topic:            " + args[0]);
        System.out.println("\t - subscriptionName: " + subscriptionName);
        System.out.println("\t - timeout-sleep:    " + timeoutSleep);
        System.out.println("\t - timeout-receive:  " + timeoutReceive);
        System.out.println("\t - username:         " + username);
        System.out.println("\t - password:         " + password);

        try
        {
            //0.- Engancha con el destino
            final javax.jms.Topic t = ActiveMQJMSClient.createTopic(args[0]);

            //1.- Creamos la Factoria de conexion
            final String URI_AMQ = _getURL();
            System.out.println("Conectando: " + URI_AMQ);
            final javax.jms.ConnectionFactory cf = new ActiveMQJMSConnectionFactory(URI_AMQ);

            //2.- Crea una conexion JMS
            c = cf.createConnection(username, password);

            //3.- Se establece el clientId de la conexion
            //if (clientId != null) c.setClientID(clientId);

            //4.- Se inicia la conexion
            c.start();

            //5.- Crea una sesion
            final javax.jms.Session s = c.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);

            //6.- Crea un Consumidor
            mc = s.createSharedDurableConsumer(t, subscriptionName);

            while (true)
            {
                //7.- Recibe el mensaje
                final javax.jms.TextMessage m = (TextMessage) mc.receive(timeoutReceive);

                if (m != null) System.out.println("Recibido ------------> " + m.getText());
                else
                {
                    System.out.println("Recibido ------------> NULL.");
                }

                Thread.currentThread().sleep(timeoutSleep);
            }
        }
        finally
        {
            if (mc != null) mc.close();
            if (c  != null) c.close();
        }
    }
}
