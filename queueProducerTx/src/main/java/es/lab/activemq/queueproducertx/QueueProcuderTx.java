/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package es.lab.activemq.queueproducertx;

import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.jms.TextMessage;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory;

/**
 *
 * @author fran
 */
public class QueueProcuderTx {
    private static final long _TIMEOUT = 1000;

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
        javax.jms.JMSContext c = null;

        if (args.length < 4)
        {
            System.err.println("\t Ejecuta: QueueProcuderTx <url> <nombreCola> <username> <password> <numMensajes> <timeout (s)> <expirity (s)");
            System.exit(1);
        }

        final String url             = args[0];
        final String queueName       = args[1];
        final String username        = args[2];
        final String password        = args[3];
        final int    numMensajes     = (args.length > 4) ? Integer.parseInt(args[4]) : 1;
        final long   timeout         = (args.length > 5) ? Integer.parseInt(args[5]) * 1000 : _TIMEOUT;
        final long   expirityTimeout = (args.length > 6) ? Integer.parseInt(args[6]) * 1000 : -1;

        System.out.println("Parametros:");
        System.out.println("\t - Conectando   : " + url);
        System.out.println("\t - cola         : " + queueName);
        System.out.println("\t - num. mensajes: " + numMensajes);
        System.out.println("\t - timeout (ms):  " + timeout);
        System.out.println("\t - expirity (ms): " + expirityTimeout);
        System.out.println("\t - username:      " + username);
        System.out.println("\t - password:      " + password);

        try
        {
            //0.- Engancha con el destino
            final Queue q = ActiveMQJMSClient.createQueue(queueName);

            //1.- Creamos la Factoria de conexion
            final ConnectionFactory cf = new ActiveMQJMSConnectionFactory(url);

            //2.- Crea un contexto JMS
            c = cf.createContext(username, password, JMSContext.SESSION_TRANSACTED);

            //4.- Crea un Productor
            final JMSProducer p = c.createProducer();

            //5.- Establecemos el tiempo de vida del mensaje
            if (expirityTimeout > 0) p.setTimeToLive(expirityTimeout);

            p.setDeliveryMode(DeliveryMode.PERSISTENT);

            for (int i = 0; i < numMensajes; i++)
            {
                //6.- Crea el mensaje de texto
                final TextMessage m = c.createTextMessage("Mensaje " + i + ".");

                //7.- Envia el mensaje
                p.send(q, m);

                System.out.println("Enviando ------------> " + m.getJMSMessageID() + "|" + m.getJMSCorrelationID() + ": " + m.getText());

                Thread.currentThread().sleep(timeout);
            }

            //8.- Commit
            c.commit();
            //s.rollback();
        }
        finally
        {
            if (c != null) c.close();
        }
    }
}
