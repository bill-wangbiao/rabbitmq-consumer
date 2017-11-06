package com.bill.test.mq;

import java.util.UUID;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
/**
 * RPC工作方式：
	1、当客户端启动时，会创建一个匿名的回调队列
	2、在RPC请求中，定义了两个属性：replyTo，表示回调队列的名称； correlationId，表示请求任务的唯一编号，用来区分不同请求的返回结果。
	3、将请求发送到rpc_queue队列中
	4、RPC服务器等待rpc_queue队列的请求，如果有消息，就处理，它将计算结果发送到请求中的回调队列里。
	5、客户端监听回调队列中的消息，如果有返回消息，它根据回调消息中的correlationid进行匹配计算结果。
 * @author sara
 *
 */
public class RPCClient {
	private Connection connection;
	private Channel channel;
	private String requestQueueName = "rpc_queue";
	private String replyQueueName;
	private QueueingConsumer consumer;

	public RPCClient() throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("192.168.1.129");
		connection = factory.newConnection();
		channel = connection.createChannel();

		replyQueueName = channel.queueDeclare().getQueue();
		System.out.println("******client端回调队列名称："+replyQueueName);

		/**回调队列，用于接收计算结果**/
		consumer = new QueueingConsumer(channel);
		channel.basicConsume(replyQueueName, true, consumer);
	}

	public String call(String message) throws Exception {
		String response = null;
		String corrId = UUID.randomUUID().toString();
		System.out.println("*********RPC客户端任务唯一编号："+corrId);
		BasicProperties props = new BasicProperties.Builder().correlationId(corrId).replyTo(replyQueueName).build();

		channel.basicPublish("", requestQueueName, props, message.getBytes("UTF-8"));

		while (true) {
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			if (delivery.getProperties().getCorrelationId().equals(corrId)) {
				response = new String(delivery.getBody(), "UTF-8");
				System.out.println("************返回的计算结果："+response);
				break;
			}
		}

		return response;
	}

	public void close() throws Exception {
		connection.close();
	}

	public static void main(String[] argv) {
		RPCClient fibonacciRpc = null;
		String response = null;
		try {
			fibonacciRpc = new RPCClient();

			System.out.println("RPCClient [x] Requesting fib(30)");
			response = fibonacciRpc.call("30");
			System.out.println("RPCClient [.] Got '" + response + "'");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fibonacciRpc != null) {
				try {
					fibonacciRpc.close();
				} catch (Exception ignore) {
				}
			}
		}
	}
}
