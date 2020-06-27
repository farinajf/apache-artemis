/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package es.lab.activemq.queueconsumertx;

import java.util.ArrayList;
import java.util.List;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory;

/**
 *
 * @author fran
 */
public class QueueConsumerTx implements Runnable {
    private static final long _TIMEOUT_RECEIVE = 1000;
    private static final long _TIMEOUT_SLEEP   = 1000;

    final ConnectionFactory _cf;
    final String            _queueName;
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
     * @param session
     * @param mc
     */
    private void _receive(final javax.jms.Session session, final javax.jms.MessageConsumer mc) {

        try
        {
            final TextMessage message = (TextMessage) mc.receive(_timeoutReceive);

            if (message != null)
            {
                System.out.println("Recibido ------------> " + message.getText());

                session.commit();
                //session.rollback();
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
    /*                         Metodos Protegidos                              */
    /***************************************************************************/

    /***************************************************************************/
    /*                            Constructores                                */
    /***************************************************************************/
    /**
     *
     * @param cf
     * @param queueName
     * @param username
     * @param password
     * @param timeoutSleep
     * @param timeoutReceive
     */
    public QueueConsumerTx(final ConnectionFactory cf, final String queueName, final String username, final String password, final long timeoutSleep, final long timeoutReceive) {
        _cf             = cf;
        _queueName      = queueName;
        _username       = username;
        _password       = password;
        _timeoutSleep   = timeoutSleep;
        _timeoutReceive = timeoutReceive;
    }

    /***************************************************************************/
    /*                         Metodos Publicos                                */
    /***************************************************************************/
    @Override
    public void run() {
        javax.jms.Connection connection = null;
        javax.jms.Session    session    = null;

        try
        {
            //1.- Crea una conexion JMS
            connection = _cf.createConnection(_username, _password);

            //2.- Crea una sesion
            session = connection.createSession(Session.SESSION_TRANSACTED);

            //3.- Se crea el destino
            Queue queue = session.createQueue(_queueName);

            //4.- Crea el consumidor
            final MessageConsumer mc = session.createConsumer(queue);

            //5.- Inicia la conexion
            connection.start();

            //6.- Consumimos mensajes
            while (true)
            {
                _receive(session, mc);
            }
        }
        catch (JMSException e) {e.printStackTrace();}
        finally
        {
            try {if (session    != null) session.close();}    catch (JMSException e) {}
            try {if (connection != null) connection.close();} catch (JMSException e) {}
        }
    }

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 4)
        {
            System.err.println("Ejecuta: QueueConsumer <url> <nombreCola> <username> <password> <timeoutSleep en seg> <timeoutReceive en seg> <threadNumber>");
            System.exit(1);
        }

        final String url            = args[0];
        final String queueName      = args[1];
        final String username       = args[2];
        final String password       = args[3];
        final long   timeoutSleep   = (args.length < 5) ? _TIMEOUT_SLEEP   : Integer.parseInt(args[4]) * 1000L;
        final long   timeoutReceive = (args.length < 6) ? _TIMEOUT_RECEIVE : Integer.parseInt(args[5]) * 1000L;
        final int    threadNumber   = (args.length < 7) ? 1                : Integer.parseInt(args[6]);

        final ConnectionFactory cf = new ActiveMQJMSConnectionFactory(url);

        final List<Thread> lista = new ArrayList<>();

        for (int i = 0; i < threadNumber; i++) lista.add(new Thread(new QueueConsumerTx(cf, queueName, username, password, timeoutSleep, timeoutReceive)));

        for (int i = 0; i < threadNumber; i++) lista.get(i).start();
    }
}
