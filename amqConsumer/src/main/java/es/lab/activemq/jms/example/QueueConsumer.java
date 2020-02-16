/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.lab.activemq.jms.example;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory;

/**
 *
 * @author fran
 */
public class QueueConsumer {
    private static final int _TIMEOUT_RECEIVE = 1000;
    private static final int _TIMEOUT_SLEEP   = 1000;

    public static void main(String[] args) throws Exception {
        final String QUEUE_NAME;
        final String host = "localhost";
        final String port = "61616";
        Connection   c    = null;

        if (args.length != 1)
        {
            System.err.println("Consumer args: <queue_name>");

            System.exit(1);
        }

        QUEUE_NAME =args[0];

        try
        {
            //0.-
            final ConnectionFactory cf = new ActiveMQJMSConnectionFactory("tcp://" + host + ":" + port);

            //1
            c = cf.createConnection();

            //2.- Crea la sesion
            final Session s = c.createSession(false, Session.AUTO_ACKNOWLEDGE);

            //3.- Se subscribe a la cola
            final Queue q = s.createQueue(QUEUE_NAME);

            //4.- Crea un consumidor
            final MessageConsumer mc = s.createConsumer(q);

            while (true)
            {
                //5.- Recibe el mensaje
                final TextMessage m = (TextMessage) mc.receive(_TIMEOUT_RECEIVE);

                if (m != null) System.out.println("Recibido ------------> " + m.getText());
                else
                {
                    System.out.println("Recibido ------------> NULL.");
                }

                Thread.currentThread().sleep(_TIMEOUT_SLEEP);
            }
        }
        finally
        {
            if (c!= null) c.close();
        }
    }
}
