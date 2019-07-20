/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.lab.farinajf.activemq.jms.example;

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
public class Main {
    private static final int MAX = 10;

    public static void main(String[] args) throws Exception {
        Connection c = null;

        try
        {
            //0.-
            final Queue q = ActiveMQJMSClient.createQueue("queue00");

            //1.- Creamos la Factoria de conexion
            final ConnectionFactory cf = new ActiveMQJMSConnectionFactory("tcp://localhost:61616");

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
            }
        }
        finally
        {
            if (c != null) c.close();
        }
    }
}
