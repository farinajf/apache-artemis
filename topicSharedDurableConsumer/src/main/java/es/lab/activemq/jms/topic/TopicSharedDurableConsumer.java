/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.lab.activemq.jms.topic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory;

/**
 *
 * @author fran
 */
public class TopicSharedDurableConsumer implements Runnable {
    private static final int _TIMEOUT_RECEIVE = 1000;
    private static final int _TIMEOUT_SLEEP   = 1000;

    final ConnectionFactory _cf;
    final String            _topicName;
    final String            _subscriptionName;
    final String            _username;
    final String            _password;
    final long              _timeoutSleep;
    final long              _timeoutReceive;

    /***************************************************************************/
    /*                         Metodos Privados                                */
    /***************************************************************************/
    private void _sleep() {
        try
        {
            Thread.currentThread().sleep(_timeoutSleep);
        }
        catch (InterruptedException e) {}
    }

    /**
     *
     * @param m
     * @return
     * @throws JMSException
     */
    private String _getText(final Message m) throws JMSException {
        return "(" + new Date() + ") " + Thread.currentThread().getName() +  " Recibido ------------> " + m.getJMSMessageID() + "|" + m.getJMSCorrelationID() + ": " + m.getBody(String.class);
    }

    /**
     *
     * @param context
     * @param consumer
     */
    private void _receive(final javax.jms.JMSContext context, final javax.jms.JMSConsumer consumer) {
        try
        {
            final Message m = (TextMessage) consumer.receive(_timeoutReceive);

            if (m != null)
            {
                System.out.println(_getText(m));

                context.commit();
            }

            _sleep();
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
    /**
     *
     * @param cf
     * @param topicName
     * @param subscriptionName
     * @param username
     * @param password
     * @param timeoutSleep
     * @param timeoutReceive
     */
    public TopicSharedDurableConsumer(final ConnectionFactory cf,
                                      final String            topicName,
                                      final String            subscriptionName,
                                      final String            username,
                                      final String            password,
                                      final long              timeoutSleep,
                                      final long              timeoutReceive) {
        _cf               = cf;
        _topicName        = topicName;
        _subscriptionName = subscriptionName;
        _username         = username;
        _password         = password;
        _timeoutSleep     = timeoutSleep;
        _timeoutReceive   = timeoutReceive;
    }

    /***************************************************************************/
    /*                         Metodos Publicos                                */
    /***************************************************************************/
    @Override
    public void run() {
        javax.jms.JMSContext context = null;

        try
        {
            //0.- Engancha con el destino
            final javax.jms.Topic t = ActiveMQJMSClient.createTopic(_topicName);

            //1.- Crea un contexto JMS
            context = _cf.createContext(_username, _username, JMSContext.SESSION_TRANSACTED);

            //2.- Crea un suscriptor
            final JMSConsumer topicSubscriber = context.createSharedDurableConsumer(t, _subscriptionName);

            //3.- Session start
            context.start();

            //4.- Receive
            while (true) _receive(context, topicSubscriber);
        }
        finally
        {
            if (context != null) context.stop();
            if (context != null) context.unsubscribe(_subscriptionName);
            if (context != null) context.close();
        }
    }

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 6)
        {
            System.err.println("Ejecuta: TopicSharedDurableConsumer <url> <nombreTopic> <subscriptionName> "
                             + "<username> <password> <timeoutSleep (s)> <timeoutReceive (s)> <threadNumber>");
            System.exit(1);
        }

        final String url              = args[0];
        final String topicName        = args[1];
        final String subscriptionName = args[2];
        final String username         = args[3];
        final String password         = args[4];
        final long   timeoutSleep     = (args.length < 6) ? _TIMEOUT_SLEEP   : Integer.parseInt(args[5]) * 1000L;
        final long   timeoutReceive   = (args.length < 7) ? _TIMEOUT_RECEIVE : Integer.parseInt(args[6]) * 1000L;
        final int    threadNumber     = (args.length < 8) ? 1                : Integer.parseInt(args[7]);

        System.out.println("Parametros:");
        System.out.println("\t - topic:            " + args[0]);
        System.out.println("\t - subscriptionName: " + subscriptionName);
        System.out.println("\t - timeout-sleep:    " + timeoutSleep);
        System.out.println("\t - timeout-receive:  " + timeoutReceive);
        System.out.println("\t - username:         " + username);
        System.out.println("\t - password:         " + password);

        final ConnectionFactory cf = new ActiveMQJMSConnectionFactory(url);

        final List<Thread> lista = new ArrayList<>();

        for (int i = 0; i < threadNumber; i++) lista.add(new Thread(new TopicSharedDurableConsumer(cf,
                                                                                                   topicName,
                                                                                                   subscriptionName,
                                                                                                   username,
                                                                                                   password,
                                                                                                   timeoutSleep,
                                                                                                   timeoutReceive)));

        for (int i = 0; i < threadNumber; i++) lista.get(i).start();
    }
}
