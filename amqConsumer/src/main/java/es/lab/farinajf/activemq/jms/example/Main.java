/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.lab.farinajf.activemq.jms.example;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory;

/**
 *
 * @author fran
 */
public class Main {
    public static void main(String[] args) throws Exception {
        Connection c    = null;
        boolean    stop = false;

        try
        {
            //0.- Engancha con el destino (Queue)
            final Queue q = ActiveMQJMSClient.createQueue("queue00");

            //1.- Creamos la Factoria de conexion
            final ConnectionFactory cf = new ActiveMQJMSConnectionFactory("tcp://localhost:61616");

            //2.- Crea una conexion JMS
            c = cf.createConnection();

            c.start();

            //3.- Crea la sesion
            final Session s = c.createSession(false, Session.AUTO_ACKNOWLEDGE);

            //4.- Crea un consumidor
            final MessageConsumer mc = s.createConsumer(q);

            while (stop == false)
            {
                //5.- Recibe el mensaje
                final TextMessage m = (TextMessage) mc.receive(5000);

                if (m != null) System.out.println("Recibido ------------> " + m.getText());
                else
                {
                    System.out.println("Recibido ------------> NULL.");
                    stop = true;
                }

                Thread.currentThread().sleep(5000);
            }
        }
        finally
        {
            if (c!= null) c.close();
        }
    }
}
