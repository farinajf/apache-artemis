/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.lab.activemq.jms.topic;

import java.util.ArrayList;
import java.util.List;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory;

/**
 *
 * @author fran
 */
public class TopicDurableConsumer implements Runnable {
    private static final int _TIMEOUT_RECEIVE = 1000;
    private static final int _TIMEOUT_SLEEP   = 1000;

    final ConnectionFactory _cf;
    final String            _topicName;
    final String            _subscriptionName;
    final String            _clientId;
    final String            _username;
    final String            _password;
    final long              _timeoutSleep;
    final long              _timeoutReceive;

    /***************************************************************************/
    /*                         Metodos Protegidos                              */
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
     * @param session
     * @param topicSubscriber
     */
    private void _receive(final javax.jms.Session session, final javax.jms.TopicSubscriber topicSubscriber) {

        try
        {
            final TextMessage message = (TextMessage) topicSubscriber.receive(_timeoutReceive);

            if (message != null)
            {
                System.out.println("Th-" + Thread.currentThread().getId() + " Recibido ------------> " + message.getText());
                session.commit();
            }
            else
            {
                System.out.println("Th-" + Thread.currentThread().getId() + " Recibido ------------> NULL.");
            }

            _sleep();
        }
        catch (JMSException e)
        {
            try {session.rollback();} catch (JMSException ex) {}

            e.printStackTrace();
        }
    }

    /***************************************************************************/
    /*                            Constructores                                */
    /***************************************************************************/
    /**
     *
     * @param cf
     * @param topicName
     * @param subscriptionName
     * @param clientId
     * @param username
     * @param password
     * @param timeoutSleep
     * @param timeoutReceive
     */
    public TopicDurableConsumer(final ConnectionFactory cf,
                                final String            topicName,
                                final String            subscriptionName,
                                final String            clientId,
                                final String            username,
                                final String            password,
                                final long              timeoutSleep,
                                final long              timeoutReceive) {
        _cf               = cf;
        _topicName        = topicName;
        _subscriptionName = subscriptionName;
        _clientId         = clientId;
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
        javax.jms.TopicSubscriber topicSubscriber = null;
        javax.jms.Connection      connection = null;
        javax.jms.Session         session    = null;

        try
        {
            //0.- Engancha con el destino
            final javax.jms.Topic t = ActiveMQJMSClient.createTopic(_topicName);

            //1.- Crea una conexion JMS
            connection = _cf.createConnection(_username, _password);

            //2.- Se establece el clientId de la conexion
            connection.setClientID(_clientId);

            //3.- Crea una sesion
            session = connection.createSession(javax.jms.Session.SESSION_TRANSACTED);

            //4.- Crea un Consumidor
            topicSubscriber = session.createDurableSubscriber(t, _subscriptionName);

            //5.- Se inicia la conexion
            connection.start();

            //6.- Consumimos mensajes
            while (true)
            {
                _receive(session, topicSubscriber);
            }
        }
        catch (JMSException e) {e.printStackTrace();}
        finally
        {
            try {if (session         != null) session.unsubscribe(_subscriptionName);} catch (JMSException e) {}

            try {if (topicSubscriber != null) topicSubscriber.close();} catch (JMSException e) {}
            try {if (session         != null) session.close();}         catch (JMSException e) {}
            try {if (connection      != null) connection.close();}      catch (JMSException e) {}
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
            System.err.println("Ejecuta: TopicDurableConsumer <url> <nombreTopic> <subscriptionName> <clientId> "
                             + "<username> <password> <timeoutSleep (s)> <timeoutReceive (s)> <threadNumber>");
            System.exit(1);
        }

        final String url              = args[0];
        final String topicName        = args[1];
        final String subscriptionName = args[2];
        final String clientId         = args[3];
        final String username         = args[4];
        final String password         = args[5];
        final long   timeoutSleep     = (args.length < 7) ? _TIMEOUT_SLEEP   : Integer.parseInt(args[6]) * 1000L;
        final long   timeoutReceive   = (args.length < 8) ? _TIMEOUT_RECEIVE : Integer.parseInt(args[7]) * 1000L;
        final int    threadNumber     = (args.length < 9) ? 1                : Integer.parseInt(args[8]);

        System.out.println("Parametros:");
        System.out.println("\t - topic:            " + args[0]);
        System.out.println("\t - clientId:         " + clientId);
        System.out.println("\t - subscriptionName: " + subscriptionName);
        System.out.println("\t - timeout-sleep:    " + timeoutSleep);
        System.out.println("\t - timeout-receive:  " + timeoutReceive);
        System.out.println("\t - username:         " + username);
        System.out.println("\t - password:         " + password);

        final ConnectionFactory cf = new ActiveMQJMSConnectionFactory(url);

        final List<Thread> lista = new ArrayList<>();

        for (int i = 0; i < threadNumber; i++) lista.add(new Thread(new TopicDurableConsumer(cf,
                                                                                             topicName,
                                                                                             subscriptionName,
                                                                                             clientId + "-" + i,
                                                                                             username,
                                                                                             password,
                                                                                             timeoutSleep,
                                                                                             timeoutReceive)));

        for (int i = 0; i < threadNumber; i++) lista.get(i).start();
    }
}
