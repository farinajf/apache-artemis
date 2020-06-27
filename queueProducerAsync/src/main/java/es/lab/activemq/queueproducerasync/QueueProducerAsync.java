/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package es.lab.activemq.queueproducerasync;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.jms.CompletionListener;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory;

/**
 *
 * @author fran
 */
public class QueueProducerAsync {
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
        JMSContext context = null;

        if (args.length < 4)
        {
            System.err.println("\t Ejecuta: QueueProducerAsync <url> <nombreCola> <username> <password> <numMensajes> <timeout (s)>");
            System.exit(1);
        }

        final String url             = args[0];
        final String queueName       = args[1];
        final String username        = args[2];
        final String password        = args[3];
        final int    numMensajes     = (args.length > 4) ? Integer.parseInt(args[4]) : 1;
        final long   timeout         = (args.length > 5) ? Integer.parseInt(args[5]) * 1000 : _TIMEOUT;

        System.out.println("Parametros:");
        System.out.println("\t - Conectando   : " + url);
        System.out.println("\t - cola         : " + queueName);
        System.out.println("\t - num. mensajes: " + numMensajes);
        System.out.println("\t - timeout (ms):  " + timeout);
        System.out.println("\t - username:      " + username);
        System.out.println("\t - password:      " + password);

        try
        {
            //0.- Engancha con el destino
            final Queue q = ActiveMQJMSClient.createQueue(queueName);

            //1.- Creamos la Factoria de conexion
            final ConnectionFactory cf = new ActiveMQJMSConnectionFactory(url);

            //2.- Crea un contexto JMS
            context = cf.createContext(username, password, JMSContext.SESSION_TRANSACTED);

            //3.- Crea un productor
            final JMSProducer p = context.createProducer();

            //4.- Establecemos el listener
            p.setAsync(new CompletionListener() {
                @Override
                public void onCompletion(final Message msg) {
                    try
                    {
                        System.out.println("ACK: " + msg.getJMSMessageID() + "|" + msg.getJMSCorrelationID());
                    }
                    catch (JMSException e) {e.printStackTrace();}
                }

                @Override
                public void onException(final Message msg, final Exception e) {
                    e.printStackTrace();

                    try
                    {
                        if (msg != null) System.out.println("Rollback: " + msg.getJMSMessageID() + "|" + msg.getJMSCorrelationID());
                    }
                    catch (JMSException ex) {ex.printStackTrace();}
                }
            });

            for (int i = 0; i < numMensajes; i++)
            {
                //5.-
                final CountDownLatch latch = new CountDownLatch(i);

                //6.- Crea el mensaje de texto
                final TextMessage m = context.createTextMessage("Mensaje " + i + ".");

                System.out.println("Enviando ------------> " + m.getJMSMessageID() + "|" + m.getJMSCorrelationID() + ": " + m.getText());

                //7.- Envia el mensaje
                p.send(q, m);

                //8.- Esperamos por el listener
                if (!latch.await(timeout, TimeUnit.MILLISECONDS)) throw new IllegalStateException("Completion listener not called as expected.");

                //9.- Commit;
                context.commit();

                Thread.sleep(timeout);
            }
        }
        finally
        {
            if (context != null) context.close();
        }
    }
}
