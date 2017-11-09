package com.bill.test.zeroMQ;

import java.io.UnsupportedEncodingException;

import org.zeromq.ZMQ;
/**
 * publisher/subscriber模式
 * @author sara
 *
 */
public class Subscriber {

	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			new Thread(
					new Runnable() {

						@Override
						public void run() {
							ZMQ.Context  context=ZMQ.context(1);
							ZMQ.Socket subscriber=context.socket(ZMQ.SUB);
							subscriber.connect("tcp://127.0.0.1:5555");
							/**订阅bill的这个channel**/
							subscriber.subscribe("bill".getBytes());
							
							for (int j = 0; j < 10; j++) {
								byte[] message=subscriber.recv();
								try {
									System.out.println("********接收到的信息："+new String(message,"utf-8"));
								} catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							subscriber.close();
							context.term();
						}
						
					}
					).start();
		}
	}

}
