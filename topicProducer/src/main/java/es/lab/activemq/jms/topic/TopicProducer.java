/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.lab.activemq.jms.topic;

import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import javax.jms.Topic;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory;

/**
 *
 * @author fran
 */
public class TopicProducer {
    private static final int _TIMEOUT = 1000;

    /**
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        javax.jms.Connection c = null;

        if (args.length < 4)
        {
            System.err.println("\t Ejecuta: TopicProducer <url> <nombreTopic> <username> <password> <numMensajes> <timeout (s)>");
            System.exit(1);
        }

        final String url            = args[0];
        final String topicName      = args[1];
        final String username       = args[2];
        final String password       = args[3];
        final int    numMensajes    = (args.length > 4) ? Integer.parseInt(args[4]) : 1;
        final long   timeout        = (args.length > 5) ? Integer.parseInt(args[5]) * 1000 : _TIMEOUT;

        System.out.println("Parametros:");
        System.out.println("\t - Conectando   : " + url);
        System.out.println("\t - topic        : " + topicName);
        System.out.println("\t - num. mensajes: " + numMensajes);
        System.out.println("\t - timeout (ms) : " + timeout);
        System.out.println("\t - username     : " + username);
        System.out.println("\t - password     : " + password);

        try
        {
            //0.- Engancha con el destino
            final Topic t = ActiveMQJMSClient.createTopic(topicName);

            //1.- Creamos la Factoria de conexion
            final ConnectionFactory cf = new ActiveMQJMSConnectionFactory(url);

            //2.- Crea una conexion JMS
            c = cf.createConnection(username, password);

            //3.- Crea una sesion
            final javax.jms.Session s = c.createSession(javax.jms.Session.AUTO_ACKNOWLEDGE);

            //4.- Crea un Productor
            final MessageProducer p = s.createProducer(t);

            for (int i = 0; i < numMensajes; i++)
            {
                //5.- Crea el mensaje de texto
                final TextMessage m = s.createTextMessage("Mensaje " + i + ".");

                //6 Envia el mensaje
                p.send(m);

                System.out.println("Enviando ------------> " + m.getJMSMessageID() + "|" + m.getJMSCorrelationID() + ": " + m.getText());

                Thread.currentThread().sleep(timeout);
            }
        }
        finally
        {
            if (c != null) c.close();
        }
    }
}
