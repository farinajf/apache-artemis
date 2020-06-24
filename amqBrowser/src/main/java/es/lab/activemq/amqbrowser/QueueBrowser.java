/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package es.lab.activemq.amqbrowser;

import java.util.Enumeration;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory;

/**
 *
 * @author fran
 */
public class QueueBrowser {

    /***************************************************************************/
    /*                         Metodos Privados                                */
    /***************************************************************************/

    /***************************************************************************/
    /*                         Metodos Protegidos                              */
    /***************************************************************************/

    /***************************************************************************/
    /*                            Constructores                                */
    /***************************************************************************/

    /***************************************************************************/
    /*                         Metodos Publicos                                */
    /***************************************************************************/
    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        javax.jms.Connection c = null;

        if (args.length < 4)
        {
            System.err.println("\t Ejecuta: QueueProducer <url> <nombreCola> <username> <password> ");
            System.exit(1);
        }

        final String url            = args[0];
        final String queueName      = args[1];
        final String username       = args[2];
        final String password       = args[3];

        System.out.println("Parametros:");
        System.out.println("\t - Conectando   : " + url);
        System.out.println("\t - cola         : " + queueName);
        System.out.println("\t - username:      " + username);
        System.out.println("\t - password:      " + password);

        try
        {
            //0.- Engancha con el destino
            final Queue q = ActiveMQJMSClient.createQueue(queueName);

            //1.- Creamos la Factoria de conexion
            final ConnectionFactory cf = new ActiveMQJMSConnectionFactory(url);

            //2.- Crea una conexion JMS
            c = cf.createConnection(username, password);

            c.start();

            //3.- Crea una sesion
            // createSession(transated, aknowledgwMode)
            final Session s = c.createSession(false, Session.AUTO_ACKNOWLEDGE);

            //4.- Crea un Productor
            final javax.jms.QueueBrowser browser = s.createBrowser(q);

            // 5.. Browse the messages on the queue
            Enumeration e = browser.getEnumeration();

            while (e.hasMoreElements() == true)
            {
                TextMessage msj = (TextMessage) e.nextElement();
                System.out.println("\t ---> " + msj.getText());
            }
        }
        finally
        {
            if (c != null) c.close();
        }
    }
}
