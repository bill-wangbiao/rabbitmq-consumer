package com.bill.test.zeroMQ;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicInteger;

import org.zeromq.ZMQ;

/**
 * push/pull模式
 * @author sara
 *
 */
public class Puller {

	public static void main(String[] args) {
		final AtomicInteger number = new AtomicInteger(0);  
		new Thread(
				new Runnable() {
					private int here=0;
					@Override
					public void run() {
						
						ZMQ.Context context=ZMQ.context(1);
						ZMQ.Socket pull=context.socket(ZMQ.PULL);
						
						pull.connect("ipc://bill");
						
						while(true) {
							String message="";
							try {
								message=new String(pull.recv(),"utf-8");
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
							System.out.println("*******接收到的数据"+message);
							int now=number.incrementAndGet();
							here++;
							if(now/10000==0) {
								 System.out.println(now + "  here is : " + here);  
							}
						}
						
					}
					
				}
				).start();
		

	}

}
