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
public class TopicDurableConsumer {
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
        int                       timeoutSleep   = _TIMEOUT_SLEEP;
        int                       timeoutReceive = _TIMEOUT_RECEIVE;

        if (args.length < 3)
        {
            System.err.println("Ejecuta: TopicDurableConsumer <nombreTopic> <subscriptionName> <clientId> <timeoutSleep> <timeoutReceive>");
            System.exit(1);
        }

        final String subscriptionName = args[1];
        final String clientId         = args[2];

        switch (args.length)
        {
            case 4:
                    timeoutSleep   = Integer.parseInt(args[3]);
                    timeoutReceive = timeoutSleep;
                    break;
            case 5:
                    timeoutSleep   = Integer.parseInt(args[3]);
                    timeoutReceive = Integer.parseInt(args[4]);
                    break;
            default: break;
        }

        System.out.println("Parametros:");
        System.out.println("\t - topic:            " + args[0]);
        System.out.println("\t - clientId:         " + clientId);
        System.out.println("\t - subscriptionName: " + subscriptionName);
        System.out.println("\t - timeout-sleep:    " + timeoutSleep);
        System.out.println("\t - timeout-receive:  " + timeoutReceive);

        try
        {
            //0.- Engancha con el destino
            final javax.jms.Topic t = ActiveMQJMSClient.createTopic(args[0]);

            //1.- Creamos la Factoria de conexion
            final String URI_AMQ = _getURL();
            System.out.println("Conectando: " + URI_AMQ);
            final javax.jms.ConnectionFactory cf = new ActiveMQJMSConnectionFactory(URI_AMQ);

            //2.- Crea una conexion JMS
            c = cf.createConnection();

            //3.- Se establece el clientId de la conexion
            c.setClientID(clientId);

            //4.- Se inicia la conexion
            c.start();

            //5.- Crea una sesion
            final javax.jms.Session s = c.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);

            //6.- Crea un Consumidor
            mc = s.createDurableConsumer(t, subscriptionName);

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
