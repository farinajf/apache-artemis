/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package es.lab.activemq.amqrequester;

import java.util.Date;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory;

/**
 *
 * @author fran
 */
public class AMQRequester {
    private static final long _TIMEOUT = 1000;

    /***************************************************************************/
    /*                         Metodos Privados                                */
    /***************************************************************************/
    /**
     *
     * @param m
     * @return
     * @throws JMSException
     */
    private static String _getText(final Message m) throws JMSException {
        return "(" + new Date() + ") " + Thread.currentThread().getName() +  " Recibido ------------> " + m.getJMSMessageID() + "|" + m.getJMSCorrelationID() + ": " + m.getBody(String.class);
    }

    /**
     *
     * @param context
     * @param consumer
     */
    private static void _receive(final javax.jms.JMSContext context, final javax.jms.JMSConsumer consumer) {

        try
        {
            final Message m = (TextMessage) consumer.receive();

            if (m != null)
            {
                System.out.println(_getText(m));

                context.commit();
            }
        }
        catch (JMSException e)
        {
            context.rollback();

            e.printStackTrace();
        }
    }

    /***************************************************************************/
    /*                         Metodos Protegidos                              */
    /***************************************************************************/

    /***************************************************************************/
    /*                            Constructores                                */
    /***************************************************************************/

    /***************************************************************************/
    /*                         Metodos Publicos                                */
    /***************************************************************************/
    public static void main(String[] args) throws InterruptedException {
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

            //6.- Creadmos una cola temporal de respuestas
            final TemporaryQueue replyQueue = c.createTemporaryQueue();

            //7.- Creamos el Consumidor
            final JMSConsumer consumer = c.createConsumer(replyQueue);

            for (int i = 0; i < numMensajes; i++)
            {
                //8.- Crea el mensaje de texto
                final TextMessage m = c.createTextMessage("Mensaje " + i + ".");

                //9.- Se establece el canal de respuesta
                m.setJMSReplyTo(replyQueue);

                //10.- Envia el mensaje
                p.send(q, m);

                System.out.println("Enviando ------------> " + m.getJMSMessageID() + "|" + m.getJMSCorrelationID() + ": " + m.getText());

                //11.- Commit
                c.commit();

                //12.- Procesamos la respuesta
                _receive(c, consumer);

                //13.- Sleep
                Thread.currentThread().sleep(timeout);
            }
        }
        catch (JMSException e)
        {
            if (c != null) c.rollback();
            if (c != null) c.close();
        }
        finally
        {
            if (c != null) c.close();
        }
    }
}
