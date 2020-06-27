/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package es.lab.activemq.amqreplier;

import java.util.Date;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.TextMessage;
import org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory;

/**
 *
 * @author fran
 */
public class AMQReplier implements MessageListener {
    private static final long _TIMEOUT_RECEIVE = 1000;
    private static final long _TIMEOUT_SLEEP   = 1000;

    private final ConnectionFactory _cf;
    private final String            _queueName;
    private final String            _username;
    private final String            _password;
    private       JMSContext        _context       = null;
    private       JMSProducer       _replyProducer = null;
    private       JMSConsumer       _consumer      = null;

    /***************************************************************************/
    /*                         Metodos Privados                                */
    /***************************************************************************/
    /**
     *
     * @param m
     * @return
     * @throws JMSException
     */
    private String _getText(final Message m) throws JMSException {
        return "(" + new Date() + ") " + Thread.currentThread().getName() +  " Recibido ------------> " + m.getJMSMessageID() + "|" + m.getJMSCorrelationID() + ": " + m.getBody(String.class);
    }

    /***************************************************************************/
    /*                         Metodos Protegidos                              */
    /***************************************************************************/

    /***************************************************************************/
    /*                            Constructores                                */
    /***************************************************************************/
    /**
     *
     * @param url
     * @param queueName
     * @param username
     * @param password
     */
    public AMQReplier(final String url, final String queueName, final String username, final String password) {
        _cf        = new ActiveMQJMSConnectionFactory(url);
        _queueName = queueName;
        _username  = username;
        _password  = password;
    }

    /***************************************************************************/
    /*                         Metodos Publicos                                */
    /***************************************************************************/
    /**
     *
     */
    public void start() {
        System.out.println("AMQReplier start.");

        //1.- Crea un contexto JMS
        _context = _cf.createContext(_username, _password, JMSContext.SESSION_TRANSACTED);

        //2.- Se crea el destino
        Queue queue = _context.createQueue(_queueName);

        //3.- Se crea un productor para la respuesta.
        _replyProducer = _context.createProducer();

        //4.- Se crea el consumidor
        _consumer = _context.createConsumer(queue);

        //5.- Establecemos el listener
        _consumer.setMessageListener(this);

        //6.- Start
        _context.start();
    }

    /**
     *
     */
    public void shutdown() {
        System.out.println("AMQReplier shutdown.");

        if (_context  != null) _context.stop();
        if (_consumer != null) _consumer.close();
        if (_context  != null) _context.close();
    }

    /**
     *
     * @param msg
     */
    @Override
    public void onMessage(final Message msg) {
        try
        {
            //1.- Mensaje recibido
            System.out.println(_getText(msg));

            final String body = msg.getBody(String.class);

            //2.- Destino para la respuesta
            final Destination replyDestination = msg.getJMSReplyTo();
            System.out.println("Reply to queue: " + replyDestination);

            //3.- Create the reply message
            final TextMessage replyMessage = _context.createTextMessage("Respuesta a: " + body);

            //4.- Establecemos el ID de correlacion
            replyMessage.setJMSCorrelationID(msg.getJMSMessageID());

            //5.- Enviamos la respuesta
            _replyProducer.send(replyDestination, replyMessage);

            //6.- Commit
            _context.commit();
        }
        catch (JMSException e)
        {
            _context.rollback();

            e.printStackTrace();
        }
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {
        if (args.length < 4)
        {
            System.err.println("Ejecuta: AMQReplier <url> <nombreCola> <username> <password> <timeoutSleep (s)> <timeoutReceive (s)>");
            System.exit(1);
        }

        final String url          = args[0];
        final String queueName    = args[1];
        final String username     = args[2];
        final String password     = args[3];
        final long   timeoutSleep = (args.length < 5) ? _TIMEOUT_SLEEP   : Integer.parseInt(args[4]) * 1000L;

        final AMQReplier server = new AMQReplier(url, queueName, username, password);

        server.start();

        Thread.sleep(timeoutSleep);

        server.shutdown();
    }
}
