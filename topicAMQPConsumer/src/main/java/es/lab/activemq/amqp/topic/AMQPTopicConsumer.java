/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.lab.activemq.amqp.topic;

import org.apache.qpid.jms.JmsConnectionFactory;

/**
 *
 * @author fran
 */
public class AMQPTopicConsumer {
    private static final int _TIMEOUT_RECEIVE = 1000;
    private static final int _TIMEOUT_SLEEP   = 1000;

    /**
     *
     * @return
     */
    private static String _getURL() {
        return "failover:(amqp://localhost:61616,amqp://localhost:61616)?"
                + "failover.maxReconnectAttempts=20";
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        javax.jms.Connection c              = null;
        String               username       = null;
        String               password       = null;
        int                  timeoutSleep   = _TIMEOUT_SLEEP;
        int                  timeoutReceive = _TIMEOUT_RECEIVE;

        if (args.length < 1)
        {
            System.err.println("Ejecuta: AMQPTopicConsumer <nombreTopic> <timeoutSleep> <timeoutReceive> <username> <password>");
            System.exit(1);
        }

        if (args.length >= 2) timeoutSleep   = Integer.parseInt(args[1]);
        if (args.length >= 3) timeoutReceive = Integer.parseInt(args[2]);
        if (args.length >= 4) username       = args[3];
        if (args.length >= 5) password       = args[4];

        System.out.println("Parametros:");
        System.out.println("\t - topic:           " + args[0]);
        System.out.println("\t - timeout-sleep:   " + timeoutSleep);
        System.out.println("\t - timeout-receive: " + timeoutReceive);
        System.out.println("\t - username:        " + username);
        System.out.println("\t - password:        " + password);

        try
        {
            //0.- Engancha con el destino
            //final javax.jms.Topic t = ActiveMQJMSClient.createTopic(args[0]);

            //1.- Creamos la Factoria de conexion
            final String URI_AMQ = _getURL();
            System.out.println("Conectando: " + URI_AMQ);
            final javax.jms.ConnectionFactory cf = new JmsConnectionFactory(URI_AMQ);

            //2.- Crea una conexion JMS
            c = cf.createConnection(username, password);

            c.start();

            //3.- Crea una sesion
            final javax.jms.Session s = c.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);

            //4.- Enganchamos con el destino
            final javax.jms.Topic t = s.createTopic(args[0]);

            //5.- Crea un Consumidor
            final javax.jms.MessageConsumer mc = s.createConsumer(t);

            while (true)
            {
                //5.- Recibe el mensaje
                final javax.jms.TextMessage m = (javax.jms.TextMessage) mc.receive(timeoutReceive);

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
            if (c != null) c.close();
        }
    }
}
